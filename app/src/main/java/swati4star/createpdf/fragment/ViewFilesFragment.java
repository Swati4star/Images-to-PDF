package swati4star.createpdf.fragment;

import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.SORTING_INDEX;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;
import static swati4star.createpdf.util.Constants.appName;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.Objects;

import swati4star.createpdf.R;
import swati4star.createpdf.activity.MainActivity;
import swati4star.createpdf.adapter.ViewFilesAdapter;
import swati4star.createpdf.databinding.FragmentViewFilesBinding;
import swati4star.createpdf.interfaces.EmptyStateChangeListener;
import swati4star.createpdf.interfaces.ItemSelectedListener;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.DirectoryUtils;
import swati4star.createpdf.util.FileSortUtils;
import swati4star.createpdf.util.MergeHelper;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.PopulateList;
import swati4star.createpdf.util.StringUtils;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

public class ViewFilesFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        EmptyStateChangeListener,
        ItemSelectedListener {

    private Activity mActivity;
    private ViewFilesAdapter mViewFilesAdapter;
    private DirectoryUtils mDirectoryUtils;
    private SearchView mSearchView;
    private int mCurrentSortingIndex;
    private SharedPreferences mSharedPreferences;
    private boolean mIsAllFilesSelected = false;
    private boolean mIsMergeRequired = false;
    private AlertDialog.Builder mAlertDialogBuilder;

    private FragmentViewFilesBinding mBinding;

    private int mCountFiles = 0;
    private MergeHelper mMergeHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = FragmentViewFilesBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();
        // Initialize variables
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mCurrentSortingIndex = mSharedPreferences.getInt(SORTING_INDEX, FileSortUtils.getInstance().NAME_INDEX);
        mViewFilesAdapter = new ViewFilesAdapter(mActivity, null, this, this);
        mAlertDialogBuilder = new AlertDialog.Builder(mActivity)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(root.getContext());
        mBinding.filesRecyclerView.setLayoutManager(mLayoutManager);
        mBinding.filesRecyclerView.setAdapter(mViewFilesAdapter);
        mBinding.filesRecyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(root.getContext()));
        mBinding.swipe.setOnRefreshListener(this);

        int dialogId;
        if (getArguments() != null) {
            dialogId = getArguments().getInt(BUNDLE_DATA);
            DialogUtils.getInstance().showFilesInfoDialog(mActivity, dialogId);
        }

        checkIfListEmpty();
        mMergeHelper = new MergeHelper(mActivity, mViewFilesAdapter);

        mBinding.getStarted.setOnClickListener(v -> {
            Fragment fragment = new HomeFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
            mActivity.setTitle(appName);
            //Set default item selected
            if (mActivity instanceof MainActivity) {
                ((MainActivity) mActivity).setNavigationViewSelection(R.id.nav_home);
            }
        });

        mBinding.providePermissions.setOnClickListener(v -> {
            if (!PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
                getRuntimePermissions();
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem menuItem;
        if (!mIsMergeRequired) {
            // menu to inflate the view where search and select all icon is there.
            inflater.inflate(R.menu.activity_view_files_actions, menu);
            MenuItem item = menu.findItem(R.id.action_search);
            menuItem = menu.findItem(R.id.select_all);
            mSearchView = (SearchView) item.getActionView();
            mSearchView.setQueryHint(getString(R.string.search_hint));
            mSearchView.setSubmitButtonEnabled(true);
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    setDataForQueryChange(s);
                    mSearchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    setDataForQueryChange(s);
                    return true;
                }
            });
            mSearchView.setOnCloseListener(() -> {
                populatePdfList(null);
                return false;
            });
            mSearchView.setIconifiedByDefault(true);
        } else {
            inflater.inflate(R.menu.activity_view_files_actions_if_selected, menu);
            MenuItem item = menu.findItem(R.id.item_merge);
            item.setVisible(mCountFiles > 1); //Show Merge icon when two or more files was selected
            menuItem = menu.findItem(R.id.select_all);
        }

        if (mIsAllFilesSelected) {
            menuItem.setIcon(R.drawable.ic_check_box_24dp);
        }

    }

    private void setDataForQueryChange(String s) {
        populatePdfList(s);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sort:
                displaySortDialog();
                break;
            case R.id.item_delete:
                if (mViewFilesAdapter.areItemsSelected())
                    deleteFiles();
                else
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
                break;
            case R.id.item_share:
                if (mViewFilesAdapter.areItemsSelected())
                    mViewFilesAdapter.shareFiles();
                else
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
                break;
            case R.id.select_all:
                if (mViewFilesAdapter.getItemCount() > 0) {
                    if (mIsAllFilesSelected) {
                        mViewFilesAdapter.unCheckAll();
                    } else {
                        mViewFilesAdapter.checkAll();
                    }
                } else {
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
                }
                break;
            case R.id.item_merge:
                if (mViewFilesAdapter.getItemCount() > 1) {
                    mMergeHelper.mergeFiles();
                }
                break;
        }
        return true;
    }

    private void deleteFiles() {
        mViewFilesAdapter.deleteFile();
    }

    /**
     * Checks if there are no elements in the list
     * and shows the icons appropriately
     */
    private void checkIfListEmpty() {
        onRefresh();
        final File[] files = mDirectoryUtils.getOrCreatePdfDirectory().listFiles();
        int count = 0;

        if (files == null) {
            showNoPermissionsView();
            return;
        }

        for (File file : files)
            if (!file.isDirectory()) {
                count++;
                break;
            }
        if (count == 0) {
            setEmptyStateVisible();
            mCountFiles = 0;
            updateToolbar();
        }
    }

    @Override
    public void onRefresh() {
        populatePdfList(null);
        mBinding.swipe.setRefreshing(false);
    }

    /**
     * populate pdf files with search query
     *
     * @param query to filter pdf files, {@code null} to get all
     */
    private void populatePdfList(@Nullable String query) {
        new PopulateList(mViewFilesAdapter, this,
                new DirectoryUtils(mActivity), mCurrentSortingIndex, query).execute();
    }

    private void displaySortDialog() {
        mAlertDialogBuilder.setTitle(R.string.sort_by_title)
                .setItems(R.array.sort_options, (dialog, which) -> {
                    mCurrentSortingIndex = which;
                    mSharedPreferences.edit().putInt(SORTING_INDEX, which).apply();
                    populatePdfList(null);
                });
        mAlertDialogBuilder.create().show();
    }

    @Override
    public void setEmptyStateVisible() {
        mBinding.emptyStatusView.setVisibility(View.VISIBLE);
        mBinding.noPermissionsView.setVisibility(View.GONE);
    }

    @Override
    public void setEmptyStateInvisible() {
        mBinding.emptyStatusView.setVisibility(View.GONE);
        mBinding.noPermissionsView.setVisibility(View.GONE);
    }

    @Override
    public void showNoPermissionsView() {
        mBinding.emptyStatusView.setVisibility(View.GONE);
        mBinding.noPermissionsView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoPermissionsView() {
        mBinding.noPermissionsView.setVisibility(View.GONE);
    }

    @Override
    public void filesPopulated() {
        //refresh everything and invalidate the menu.
        if (mIsMergeRequired) {
            mIsMergeRequired = false;
            mIsAllFilesSelected = false;
            mActivity.invalidateOptionsMenu();
        }
    }

    private void getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this,
                WRITE_PERMISSIONS,
                REQUEST_CODE_FOR_WRITE_PERMISSION);
    }

    /**
     * Called after user is asked to grant permissions
     *
     * @param requestCode  REQUEST Code for opening permissions
     * @param permissions  permissions asked to user
     * @param grantResults bool array indicating if permission is granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsUtils.getInstance().handleRequestPermissionsResult(mActivity, grantResults,
                requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION, this::onRefresh);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mDirectoryUtils = new DirectoryUtils(mActivity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void isSelected(Boolean isSelected, int countFiles) {
        mCountFiles = countFiles;
        updateToolbar();
    }

    /**
     * Updates the toolbar with respective the number of files selected.
     */
    private void updateToolbar() {
        AppCompatActivity activity = ((AppCompatActivity)
                Objects.requireNonNull(mActivity));
        ActionBar toolbar = activity.getSupportActionBar();
        if (toolbar != null) {
            mActivity.setTitle(mCountFiles == 0 ?
                    mActivity.getResources().getString(R.string.viewFiles)
                    : String.valueOf(mCountFiles));
            //When one or more than one files are selected refresh
            //ActionBar: set Merge option invisible or visible
            mIsMergeRequired = mCountFiles > 1;
            mIsAllFilesSelected = mCountFiles == mViewFilesAdapter.getItemCount();
            activity.invalidateOptionsMenu();
        }
    }
}
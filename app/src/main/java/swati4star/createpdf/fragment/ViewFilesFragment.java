package swati4star.createpdf.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.MainActivity;
import swati4star.createpdf.adapter.ViewFilesAdapter;
import swati4star.createpdf.interfaces.EmptyStateChangeListener;
import swati4star.createpdf.interfaces.ItemSelectedListener;
import swati4star.createpdf.util.DirectoryUtils;
import swati4star.createpdf.util.FileSortUtils;
import swati4star.createpdf.util.MoveFilesToDirectory;
import swati4star.createpdf.util.PopulateList;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.SORTING_INDEX;
import static swati4star.createpdf.util.Constants.appName;
import static swati4star.createpdf.util.DialogUtils.showFilesInfoDialog;
import static swati4star.createpdf.util.FileSortUtils.NAME_INDEX;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class ViewFilesFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        EmptyStateChangeListener,
        ItemSelectedListener {

    // Directory operations constants
    public static final int NEW_DIR = 1;
    public static final int EXISTING_DIR = 2;

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 10;

    @BindView(R.id.layout_main)
    public LinearLayout mainLayout;
    @BindView(R.id.getStarted)
    public Button getStarted;
    @BindView(R.id.filesRecyclerView)
    RecyclerView mViewFilesListRecyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipeView;
    @BindView(R.id.emptyStatusView)
    ConstraintLayout emptyView;
    @BindView(R.id.no_permissions_view)
    RelativeLayout noPermissionsLayout;

    private MenuItem mMenuItem;
    private Activity mActivity;
    private ViewFilesAdapter mViewFilesAdapter;

    private DirectoryUtils mDirectoryUtils;
    private SearchView mSearchView;
    private int mCurrentSortingIndex;
    private SharedPreferences mSharedPreferences;
    private boolean mIsChecked = false;
    private boolean mCheckBoxChanged = false;
    private AlertDialog.Builder mAlertDialogBuilder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_view_files, container, false);
        ButterKnife.bind(this, root);
        // Initialize variables
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mCurrentSortingIndex = mSharedPreferences.getInt(SORTING_INDEX, NAME_INDEX);
        mViewFilesAdapter = new ViewFilesAdapter(mActivity, null, this, this);
        mAlertDialogBuilder = new AlertDialog.Builder(mActivity)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(root.getContext());
        mViewFilesListRecyclerView.setLayoutManager(mLayoutManager);
        mViewFilesListRecyclerView.setAdapter(mViewFilesAdapter);
        mViewFilesListRecyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(root.getContext()));
        mSwipeView.setOnRefreshListener(this);

        int dialogId;
        if (getArguments() != null) {
            dialogId = getArguments().getInt(BUNDLE_DATA);
            showFilesInfoDialog(mActivity, dialogId);
        }

        checkIfListEmpty();
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!mCheckBoxChanged) {
            inflater.inflate(R.menu.activity_view_files_actions, menu);
            MenuItem item = menu.findItem(R.id.action_search);
            mMenuItem = menu.findItem(R.id.select_all);
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
                populatePdfList();
                return false;
            });
            mSearchView.setIconifiedByDefault(true);
        } else {
            inflater.inflate(R.menu.activity_view_files_actions_if_selected, menu);
        }
    }

    private void setDataForQueryChange(String s) {
        ArrayList<File> searchResult = mDirectoryUtils.searchPDF(s);
        mViewFilesAdapter.setData(searchResult);
        mViewFilesListRecyclerView.setAdapter(mViewFilesAdapter);
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
                    showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
                break;
            case R.id.item_share:
                if (mViewFilesAdapter.areItemsSelected())
                    mViewFilesAdapter.shareFiles();
                else
                    showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
                break;
            case R.id.select_all:
                if (mViewFilesAdapter.getItemCount() > 0) {
                    if (mIsChecked) {
                        mViewFilesAdapter.unCheckAll();
                        mMenuItem.setIcon(R.drawable.ic_check_box_outline_blank_24dp);
                    } else {
                        mViewFilesAdapter.checkAll();
                        mMenuItem.setIcon(R.drawable.ic_check_box_24dp);
                    }
                    mIsChecked = !mIsChecked;
                } else {
                    showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
                }
                break;
        }
        return true;
    }

    /**
     * Moves files from one directory to another
     *
     * @param operation - type of operation to be performed
     *                  (create new Directory or move to an existing directory)
     */
    private void moveFilesToDirectory(int operation) {
        LayoutInflater inflater = getLayoutInflater();
        View alertView = inflater.inflate(R.layout.directory_dialog, null);
        final ArrayList<String> filePath = mViewFilesAdapter.getSelectedFilePath();
        if (filePath == null) {
            showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
        } else {
            final EditText input = alertView.findViewById(R.id.directory_editText);
            TextView message = alertView.findViewById(R.id.directory_textView);
            if (operation == NEW_DIR) {
                message.setText(R.string.dialog_new_dir);
                mAlertDialogBuilder.setTitle(R.string.new_directory)
                        .setView(alertView)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                            String fileName = input.getText().toString();
                            new MoveFilesToDirectory(mActivity
                                    , filePath
                                    , fileName
                                    , MoveFilesToDirectory.MOVE_FILES)
                                    .execute();
                            populatePdfList();
                        });
            } else if (operation == EXISTING_DIR) {
                message.setText(R.string.dialog_dir);
                mAlertDialogBuilder.setTitle(R.string.directory)
                        .setView(alertView)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                            String fileName = input.getText().toString();
                            File directory = mDirectoryUtils.getDirectory(fileName);
                            if (directory != null) {
                                new MoveFilesToDirectory(mActivity, filePath, fileName, MoveFilesToDirectory.MOVE_FILES)
                                        .execute();
                                populatePdfList();
                            } else {
                                showSnackbar(mActivity, R.string.dir_does_not_exists);
                                dialogInterface.dismiss();
                            }
                        });
            }
            mAlertDialogBuilder.create().show();
        }
    }

    /**
     * Shows an alert to delete files
     * and delete files on positive response
     */
    private void deleteFiles() {
        AlertDialog.Builder dialogAlert = new AlertDialog.Builder(mActivity)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .setTitle(R.string.delete_alert)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    mViewFilesAdapter.deleteFiles();
                    checkIfListEmpty();
                });
        dialogAlert.create().show();
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
        if (count == 0)
            setEmptyStateVisible();
    }

    @Override
    public void onRefresh() {
        populatePdfList();
        mSwipeView.setRefreshing(false);
    }

    private void populatePdfList() {
        new PopulateList(mViewFilesAdapter, this,
                new DirectoryUtils(mActivity), mCurrentSortingIndex).execute();
    }

    private void displaySortDialog() {
        mAlertDialogBuilder.setTitle(R.string.sort_by_title)
                .setItems(R.array.sort_options, (dialog, which) -> {
                    ArrayList<File> allPdfs = mDirectoryUtils.getPdfFromOtherDirectories();
                    FileSortUtils.performSortOperation(which, allPdfs);
                    mViewFilesAdapter.setData(allPdfs);
                    mCurrentSortingIndex = which;
                    mSharedPreferences.edit().putInt(SORTING_INDEX, which).apply();
                });
        mAlertDialogBuilder.create().show();
    }

    @Override
    public void setEmptyStateVisible() {
        emptyView.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
        noPermissionsLayout.setVisibility(View.GONE);
    }

    @Override
    public void setEmptyStateInvisible() {
        emptyView.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        noPermissionsLayout.setVisibility(View.GONE);
    }

    @Override
    public void showNoPermissionsView() {
        emptyView.setVisibility(View.GONE);
        mainLayout.setVisibility(View.GONE);
        noPermissionsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoPermissionsView() {
        noPermissionsLayout.setVisibility(View.GONE);
    }

    // DIRECTORY OPERATIONS

    @OnClick(R.id.new_dir)
    void moveToNewDirectory() {
        if (mViewFilesAdapter.areItemsSelected())
            moveFilesToDirectory(NEW_DIR);
        else
            showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
    }

    @OnClick(R.id.move_to_dir)
    void moveToDirectory() {
        if (mViewFilesAdapter.areItemsSelected())
            moveFilesToDirectory(EXISTING_DIR);
        else
            showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
    }

    @OnClick(R.id.move_to_home_dir)
    void moveFilesToHomeDirectory() {
        if (!mViewFilesAdapter.areItemsSelected()) {
            showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
            return;
        }
        final ArrayList<String> filePath = mViewFilesAdapter.getSelectedFilePath();
        if (filePath == null) {
            showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
        } else {
            final File[] files = mDirectoryUtils.getOrCreatePdfDirectory().listFiles();
            for (File pdf : mDirectoryUtils.getPdfsFromPdfFolder(files)) {
                //remove the files already present in home directory
                filePath.remove(pdf.getPath());
            }
            new MoveFilesToDirectory(mActivity
                    , filePath
                    , null
                    , MoveFilesToDirectory.HOME_DIRECTORY)
                    .execute();
            populatePdfList();
        }
    }

    @OnClick(R.id.delete_dir)
    void deleteDirectory() {
        LayoutInflater inflater = getLayoutInflater();
        View alertView = inflater.inflate(R.layout.directory_dialog, null);
        final ArrayList<String> pdfFiles = new ArrayList<>();
        final EditText input = alertView.findViewById(R.id.directory_editText);
        TextView message = alertView.findViewById(R.id.directory_textView);
        message.setText(R.string.dialog_delete_dir);
        mAlertDialogBuilder.setTitle(R.string.delete_directory)
                .setView(alertView)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    final String dirName = input.getText().toString();
                    final File directory = mDirectoryUtils.getDirectory(dirName);
                    if (directory == null || dirName.trim().isEmpty()) {
                        showSnackbar(mActivity, R.string.dir_does_not_exists);
                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setTitle(R.string.delete)
                                .setMessage(R.string.delete_dialog)
                                .setCancelable(true)
                                .setPositiveButton(R.string.yes, (dialogInterface1, i12) -> {
                                    for (File pdf : directory.listFiles()) {
                                        pdfFiles.add(pdf.getPath());
                                    }
                                    new MoveFilesToDirectory(mActivity, pdfFiles,
                                            dirName, MoveFilesToDirectory.DELETE_DIRECTORY)
                                            .execute();
                                    populatePdfList();
                                })
                                .setNegativeButton(R.string.no, (dialog, i1) -> {
                                    dialog.dismiss();
                                    dialogInterface.dismiss();
                                });
                        builder.create().show();
                    }
                });
        mAlertDialogBuilder.create().show();
    }


    //When the "GET STARTED" button is clicked, the user is taken to home
    @OnClick(R.id.getStarted)
    public void loadHome() {
        Fragment fragment = new ImageToPdfFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        //Set default item selected
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).setDefaultMenuSelected(0);
        }
    }

    @OnClick(R.id.provide_permissions)
    public void providePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
        }
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

        if (grantResults.length < 1) {
            showSnackbar(mActivity, R.string.snackbar_insufficient_permissions);
            return;
        }
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showSnackbar(mActivity, R.string.snackbar_permissions_given);
                    onRefresh();
                } else
                    showSnackbar(mActivity, R.string.snackbar_insufficient_permissions);
            }
        }
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
        AppCompatActivity activity = ((AppCompatActivity)
                Objects.requireNonNull(getActivity()));
        ActionBar toolbar = activity.getSupportActionBar();

        if (toolbar != null) {
            if (countFiles == 0) {
                toolbar.setTitle(appName);
                if (mCheckBoxChanged) {
                    mCheckBoxChanged = false;
                    activity.invalidateOptionsMenu();
                }
            } else {
                toolbar.setTitle(String.valueOf(countFiles));
                if (!mCheckBoxChanged) {
                    mCheckBoxChanged = true;
                    activity.invalidateOptionsMenu();
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        AppCompatActivity activity = ((AppCompatActivity)
                Objects.requireNonNull(getActivity()));
        ActionBar toolbar = activity.getSupportActionBar();
        toolbar.setTitle(appName);
    }
}
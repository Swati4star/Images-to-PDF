package swati4star.createpdf.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.ViewFilesAdapter;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

import static swati4star.createpdf.R.string.search_hint;

public class ViewFilesFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, ViewFilesAdapter.EmptyStateChangeListener {

    private static final int NAME_INDEX = 0;
    private static final int DATE_INDEX = 1;
    private static final int SIZE_INCREASING_ORDER_INDEX = 2;
    private static final int SIZE_DECREASING_ORDER_INDEX = 3;

    private  Menu mMenuIcons;
    private Activity mActivity;
    private ViewFilesAdapter mViewFilesAdapter;
    @BindView(R.id.filesRecyclerView)
    RecyclerView mViewFilesListRecyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipeView;
    @BindView(R.id.emptyBackgroundImage)
    public ImageView backView;
    @BindView(R.id.emptyTextOverBgImage)
    public TextView TextOver;
    @BindView(R.id.getStarted)
    public TextView getStarted;
    @BindView(R.id.emptyTagLine)
    public TextView tagLine;

    private int mCurrentSortingIndex = -1;
    private FileUtils mFileUtils;
    private SearchView mSearchView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFileUtils = new FileUtils(mActivity);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_view_files, container, false);
        ButterKnife.bind(this, root);

        //Create/Open folder
        File folder = mFileUtils.getOrCreatePdfDirectory();

        // Initialize variables
        final ArrayList<File> pdfFiles = new ArrayList<>();
        final File[] files = folder.listFiles();
        if (files.length == 0) {
            setEmptyStateVisible();

        }

        mViewFilesAdapter = new ViewFilesAdapter(mActivity, pdfFiles, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(root.getContext());
        mViewFilesListRecyclerView.setLayoutManager(mLayoutManager);
        mViewFilesListRecyclerView.setAdapter(mViewFilesAdapter);
        mViewFilesListRecyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(root.getContext()));
        mSwipeView.setOnRefreshListener(this);

        // Populate data into listView
        populatePdfList();
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_view_files_actions, menu);
        mMenuIcons = menu;
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) item.getActionView();
        mSearchView.setQueryHint(getString(R.string.search_hint));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                ArrayList searchResult = mFileUtils.searchPDF(s);
                if (searchResult.isEmpty()) {
                    Toast.makeText(mActivity , R.string.no_result , Toast.LENGTH_LONG).show();
                } else {
                    mViewFilesAdapter.setData(searchResult);
                    mViewFilesListRecyclerView.setAdapter(mViewFilesAdapter);
                    mSearchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList searchResult = mFileUtils.searchPDF(s);
                mViewFilesAdapter.setData(searchResult);
                mViewFilesListRecyclerView.setAdapter(mViewFilesAdapter);
                return true;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                populatePdfList();
                return false;
            }
        });
        mSearchView.setIconifiedByDefault(true);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sort:
                displaySortDialog();
                break;
            case R.id.item_delete:
                if (mViewFilesAdapter.areItemsSelected()) {
                    deleteFiles();
                } else {
                    Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                            R.string.snackbar_no_pdfs_selected,
                            Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.item_share:
                if (mViewFilesAdapter.areItemsSelected()) {
                    mViewFilesAdapter.shareFiles();
                } else {
                    Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                            R.string.snackbar_no_pdfs_selected,
                            Snackbar.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void deleteFiles() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Do you want to delete all selected files?")
                .setNegativeButton("No", mDialogClickListener)
                .setPositiveButton("Yes", mDialogClickListener);
        builder.create().show();

    }

    private void checkIfListEmpty() {
        onRefresh();
        final File[] files = mFileUtils.getOrCreatePdfDirectory().listFiles();
        Log.d("after refresh", "yes");
        if (files == null || files.length == 0) {
            Log.d("after if", "done");
            setIconsInvisible();
        }
    }

    private final DialogInterface.OnClickListener mDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    mViewFilesAdapter.deleteFiles();
                    checkIfListEmpty();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };


    @Override
    public void onRefresh() {
        Log.v("refresh", "refreshing dta");
        populatePdfList();
        mSwipeView.setRefreshing(false);
    }

    private void populatePdfList() {
        new PopulateList().execute();
    }

    private void displaySortDialog() {
        final File folder = mFileUtils.getOrCreatePdfDirectory();
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Sort by")
                .setItems(R.array.sort_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<File> pdfsFromFolder = mFileUtils.getPdfsFromPdfFolder(folder.listFiles());
                        switch (which) {
                            case DATE_INDEX:
                                mFileUtils.sortFilesByDateNewestToOldest(pdfsFromFolder);
                                mViewFilesAdapter.setData(pdfsFromFolder);
                                mCurrentSortingIndex = DATE_INDEX;
                                break;
                            case NAME_INDEX:
                                mFileUtils.sortByNameAlphabetical(pdfsFromFolder);
                                mViewFilesAdapter.setData(pdfsFromFolder);
                                mCurrentSortingIndex = NAME_INDEX;
                                break;
                            case SIZE_INCREASING_ORDER_INDEX:
                                mFileUtils.sortFilesBySizeIncreasingOrder(pdfsFromFolder);
                                mViewFilesAdapter.setData(pdfsFromFolder);
                                mCurrentSortingIndex = SIZE_INCREASING_ORDER_INDEX;
                                break;
                            case SIZE_DECREASING_ORDER_INDEX:
                                mFileUtils.sortFilesBySizeDecreasingOrder(pdfsFromFolder);
                                mViewFilesAdapter.setData(pdfsFromFolder);
                                mCurrentSortingIndex = SIZE_DECREASING_ORDER_INDEX;
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    @Override
    public void setEmptyStateVisible() {
        backView.setVisibility(View.VISIBLE);
        TextOver.setVisibility(View.VISIBLE);
        getStarted.setVisibility(View.VISIBLE);
        tagLine.setVisibility(View.VISIBLE);
    }

    @Override
    public void setEmptyStateGone() {
        backView.setVisibility(View.GONE);
        TextOver.setVisibility(View.GONE);
        getStarted.setVisibility(View.GONE);
        tagLine.setVisibility(View.GONE);
    }

    private void setIconsInvisible() {
        mMenuIcons.findItem(R.id.item_delete).setVisible(false);
        mMenuIcons.findItem(R.id.item_sort).setVisible(false);
        mSearchView.setVisibility(View.GONE);
    }

    /**
    * AsyncTask used to populate the list of elements in the background
    */
    @SuppressLint("StaticFieldLeak")
    private class PopulateList extends AsyncTask<Void, Void, Void> {

        // Progress dialog
        MaterialDialog dialog;

        @Override
        protected Void doInBackground(Void... voids) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populateListView();
                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final MaterialDialog.Builder builder = new MaterialDialog.Builder(mActivity)
                    .title(mActivity.getResources().getString(R.string.please_wait))
                    .content(mActivity.getResources().getString(R.string.populating_list))
                    .cancelable(false)
                    .progress(true, 0);
            dialog = builder.build();
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            dialog.dismiss();
        }

        /**
         * Populate data into listView
         */
        private void populateListView() {
            ArrayList<File> pdfFiles = new ArrayList<>();
            final File[] files = mFileUtils.getOrCreatePdfDirectory().listFiles();
            if (files == null || files.length == 0) {
                setEmptyStateVisible();
                setIconsInvisible();
                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                        R.string.snackbar_no_pdfs,
                        Snackbar.LENGTH_LONG).show();
            } else {

                pdfFiles = mFileUtils.getPdfsFromPdfFolder(files);
            }
            Log.v("done", "adding");
            switch (mCurrentSortingIndex) {
                case NAME_INDEX:
                    mFileUtils.sortByNameAlphabetical(pdfFiles);
                    break;
                case DATE_INDEX:
                    mFileUtils.sortFilesByDateNewestToOldest(pdfFiles);
                    break;
                case SIZE_INCREASING_ORDER_INDEX:
                    mFileUtils.sortFilesBySizeIncreasingOrder(pdfFiles);
                    break;
                case SIZE_DECREASING_ORDER_INDEX:
                    mFileUtils.sortFilesBySizeDecreasingOrder(pdfFiles);
                    break;
            }
            mViewFilesAdapter.setData(pdfFiles);
            mViewFilesListRecyclerView.setAdapter(mViewFilesAdapter);
        }
    }
}
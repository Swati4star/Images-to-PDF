package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.ViewFilesAdapter;
import swati4star.createpdf.interfaces.EmptyStateChangeListener;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MoveFilesToDirectory;
import swati4star.createpdf.util.PopulateList;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

import static swati4star.createpdf.util.Constants.SORTING_INDEX;

public class ViewFilesFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, EmptyStateChangeListener {

    // Sorting order constants
    public static final int NAME_INDEX = 0;
    public static final int DATE_INDEX = 1;
    public static final int SIZE_INCREASING_ORDER_INDEX = 2;
    public static final int SIZE_DECREASING_ORDER_INDEX = 3;

    // Directory operations constants
    public static final int NEW_DIR = 1;
    public static final int EXISTING_DIR = 2;

    @BindView(R.id.layout_main)
    public LinearLayout mainLayout;
    @BindView(R.id.emptyBackgroundImage)
    public ImageView backView;
    @BindView(R.id.emptyTextOverBgImage)
    public TextView TextOver;
    @BindView(R.id.getStarted)
    public Button getStarted;
    @BindView(R.id.emptyTagLine)
    public TextView tagLine;
    @BindView(R.id.filesRecyclerView)
    RecyclerView mViewFilesListRecyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipeView;

    private MenuItem mMenuItem;
    private Activity mActivity;
    private ViewFilesAdapter mViewFilesAdapter;

    private FileUtils mFileUtils;
    private SearchView mSearchView;
    private int mCurrentSortingIndex;
    private SharedPreferences mSharedPreferences;
    private boolean mIsChecked = false;

    //When the "GET STARTED" button is clicked, the user is taken to home
    @OnClick(R.id.getStarted)
    public void loadHome() {
        Fragment fragment = new ImageToPdfFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mFileUtils = new FileUtils(mActivity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_view_files, container, false);
        ButterKnife.bind(this, root);

        // Initialize variables
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mCurrentSortingIndex = mSharedPreferences.getInt(SORTING_INDEX, NAME_INDEX);
        mViewFilesAdapter = new ViewFilesAdapter(mActivity, null, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(root.getContext());
        mViewFilesListRecyclerView.setLayoutManager(mLayoutManager);
        mViewFilesListRecyclerView.setAdapter(mViewFilesAdapter);
        mViewFilesListRecyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(root.getContext()));
        mSwipeView.setOnRefreshListener(this);

        checkIfListEmpty();
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_view_files_actions, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mMenuItem = menu.findItem(R.id.select_all);
        mSearchView = (SearchView) item.getActionView();
        mSearchView.setQueryHint(getString(R.string.search_hint));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                ArrayList<File> searchResult = mFileUtils.searchPDF(s);
                if (searchResult.isEmpty()) {
                    Toast.makeText(mActivity, R.string.no_result, Toast.LENGTH_LONG).show();
                } else {
                    mViewFilesAdapter.setData(searchResult);
                    mViewFilesListRecyclerView.setAdapter(mViewFilesAdapter);
                    mSearchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<File> searchResult = mFileUtils.searchPDF(s);
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
                if (mViewFilesAdapter.areItemsSelected())
                    deleteFiles();
                else
                    showSnack(R.string.snackbar_no_pdfs_selected);
                break;
            case R.id.item_share:
                if (mViewFilesAdapter.areItemsSelected())
                    mViewFilesAdapter.shareFiles();
                else
                    showSnack(R.string.snackbar_no_pdfs_selected);
                break;
            case R.id.select_all:
                if (mIsChecked) {
                    mViewFilesAdapter.unCheckAll();
                    mMenuItem.setIcon(R.drawable.ic_check_box_outline_blank_24dp);
                    mIsChecked = false;
                } else {
                    mViewFilesAdapter.checkAll();
                    mMenuItem.setIcon(R.drawable.ic_check_box_24dp);
                    mIsChecked = true;
                }
                break;
        }
        return true;
    }

    private void showSnack(int resID) {
        Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
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
            showSnack(R.string.snackbar_no_pdfs_selected);
        } else {
            final EditText input = alertView.findViewById(R.id.directory_editText);
            TextView message = alertView.findViewById(R.id.directory_textView);
            final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            if (operation == NEW_DIR) {
                message.setText(R.string.dialog_new_dir);
                builder.setTitle(R.string.new_directory)
                        .setView(alertView)
                        .setCancelable(true)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String fileName = input.getText().toString();
                                new MoveFilesToDirectory(mActivity
                                        , filePath
                                        , fileName
                                        , MoveFilesToDirectory.MOVE_FILES)
                                        .execute();
                                populatePdfList();
                            }
                        });
            } else if (operation == EXISTING_DIR) {
                message.setText(R.string.dialog_dir);
                builder.setTitle(R.string.directory)
                        .setView(alertView)
                        .setCancelable(true)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String fileName = input.getText().toString();
                                File directory = mFileUtils.getDirectory(fileName);
                                if (directory != null) {
                                    new MoveFilesToDirectory(mActivity
                                            , filePath
                                            , fileName
                                            , MoveFilesToDirectory.MOVE_FILES)
                                            .execute();
                                    populatePdfList();
                                } else {
                                    showSnack(R.string.dir_does_not_exists);
                                    dialogInterface.dismiss();
                                }

                            }
                        });
            }
            builder.create().show();
        }
    }

    /**
     * Shows an alert to delete files
     * and delete files on positive response
     */
    private void deleteFiles() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        DialogInterface.OnClickListener mDialogClickListener = new DialogInterface.OnClickListener() {
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
        builder.setTitle("Do you want to delete all selected files?")
                .setNegativeButton(R.string.no, mDialogClickListener)
                .setPositiveButton(R.string.yes, mDialogClickListener);
        builder.create().show();
    }

    /**
     * Checks if there are no elements in the list
     * and shows the icons appropriately
     */
    private void checkIfListEmpty() {
        onRefresh();
        final File[] files = mFileUtils.getOrCreatePdfDirectory().listFiles();
        int count = 0;
        for (File file : files)
            if (!file.isDirectory()) {
                count++; break;
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
        new PopulateList(mActivity, mViewFilesAdapter, this, mCurrentSortingIndex).execute();
    }

    private void displaySortDialog() {
        final File folder = mFileUtils.getOrCreatePdfDirectory();
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Sort by")
                .setItems(R.array.sort_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<File> pdfsFromFolder = mFileUtils.getPdfsFromPdfFolder(folder.listFiles());
                        ArrayList<File> pdfFromOtherDir = mFileUtils.getPdfFromOtherDirectories();
                        if (pdfFromOtherDir != null) {
                            pdfsFromFolder.addAll(pdfFromOtherDir);
                        }
                        switch (which) {
                            case DATE_INDEX:
                                mFileUtils.sortFilesByDateNewestToOldest(pdfsFromFolder);
                                break;
                            case NAME_INDEX:
                                mFileUtils.sortByNameAlphabetical(pdfsFromFolder);
                                break;
                            case SIZE_INCREASING_ORDER_INDEX:
                                mFileUtils.sortFilesBySizeIncreasingOrder(pdfsFromFolder);
                                break;
                            case SIZE_DECREASING_ORDER_INDEX:
                                mFileUtils.sortFilesBySizeDecreasingOrder(pdfsFromFolder);
                                break;
                            default:
                                break;
                        }
                        mViewFilesAdapter.setData(pdfsFromFolder);
                        mCurrentSortingIndex = which;
                        mSharedPreferences.edit().putInt(SORTING_INDEX, which).apply();
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
        mainLayout.setVisibility(View.GONE);
    }

    // DIRECTORY OPERATIONS

    @OnClick(R.id.new_dir)
    void moveToNewDirectory() {
        if (mViewFilesAdapter.areItemsSelected())
            moveFilesToDirectory(NEW_DIR);
        else
            showSnack(R.string.snackbar_no_pdfs_selected);
    }

    @OnClick(R.id.move_to_dir)
    void moveToDirectory() {
        if (mViewFilesAdapter.areItemsSelected())
            moveFilesToDirectory(EXISTING_DIR);
        else
            showSnack(R.string.snackbar_no_pdfs_selected);
    }

    @OnClick(R.id.move_to_home_dir)
    void moveFilesToHomeDirectory() {
        if (!mViewFilesAdapter.areItemsSelected()) {
            showSnack(R.string.snackbar_no_pdfs_selected);
            return;
        }
        final ArrayList<String> filePath = mViewFilesAdapter.getSelectedFilePath();
        if (filePath == null) {
            showSnack(R.string.snackbar_no_pdfs_selected);
        } else {
            final File[] files = mFileUtils.getOrCreatePdfDirectory().listFiles();
            for (File pdf : mFileUtils.getPdfsFromPdfFolder(files)) {
                if (filePath.contains(pdf.getPath())) {
                    //remove the files already present in home directory
                    filePath.remove(filePath.indexOf(pdf.getPath()));
                }
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.delete_directory)
                .setView(alertView)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        final String dirName = input.getText().toString();
                        final File directory = mFileUtils.getDirectory(dirName);
                        if (directory == null) {
                            showSnack(R.string.dir_does_not_exists);
                        } else {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                            builder.setTitle(R.string.delete)
                                    .setMessage(R.string.delete_dialog)
                                    .setCancelable(true)
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            for (File pdf : directory.listFiles()) {
                                                pdfFiles.add(pdf.getPath());
                                            }
                                            new MoveFilesToDirectory(mActivity
                                                    , pdfFiles
                                                    , dirName
                                                    , MoveFilesToDirectory.DELETE_DIRECTORY)
                                                    .execute();
                                            populatePdfList();
                                        }
                                    })
                                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            dialog.dismiss();
                                            dialogInterface.dismiss();
                                        }
                                    });
                            builder.create().show();
                        }
                    }
                });
        builder.create().show();
    }
}

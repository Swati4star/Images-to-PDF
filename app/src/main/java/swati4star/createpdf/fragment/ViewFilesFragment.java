package swati4star.createpdf.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import swati4star.createpdf.R;
import swati4star.createpdf.adapter.ViewFilesAdapter;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

public class ViewFilesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final int NAME_INDEX = 0;
    private static final int DATE_INDEX = 1;
    @SuppressLint("StaticFieldLeak")
    public static TextView emptyStatusTextView;
    private Activity activity;
    private ViewFilesAdapter mViewFilesAdapter;
    private RecyclerView viewFilesListRecyclerView;
    private SwipeRefreshLayout swipeView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_view_files, container, false);

        viewFilesListRecyclerView = root.findViewById(R.id.filesRecyclerView);
        swipeView = root.findViewById(R.id.swipe);
        emptyStatusTextView = root.findViewById(R.id.emptyStatusTextView);

        //Create/Open folder
        File folder = getOrCreatePdfDirectory();

        // Initialize variables
        final ArrayList<File> pdfFiles = new ArrayList<>();
        final File[] files = folder.listFiles();
        if (files.length == 0) {
            emptyStatusTextView.setVisibility(View.VISIBLE);
        }
        mViewFilesAdapter = new ViewFilesAdapter(activity, pdfFiles);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(root.getContext());
        viewFilesListRecyclerView.setLayoutManager(mLayoutManager);
        viewFilesListRecyclerView.setAdapter(mViewFilesAdapter);
        viewFilesListRecyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(root.getContext()));
        swipeView.setOnRefreshListener(this);

        // Populate data into listView
        populatePdfList();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_view_files_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sort:
                displaySortDialog();
                break;
            case R.id.item_delete:
                deleteFiles();
                break;
            default:
                break;
        }
        return true;
    }

    private void deleteFiles() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Do you want to delete all selected files?")
                .setNegativeButton("No", dialogClickListener)
                .setPositiveButton("Yes", dialogClickListener);
        builder.create().show();
    }

    private final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    mViewFilesAdapter.deleteFiles();
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
        swipeView.setRefreshing(false);
    }

    private void populatePdfList() {
        new PopulateList().execute();
    }

    private void displaySortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Sort by")
                .setItems(R.array.sort_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<File> pdfsFromFolder = getPdfsFromPdfFolder();
                        switch (which) {
                            case DATE_INDEX:
                                sortFilesByDateNewestToOldest(pdfsFromFolder);
                                mViewFilesAdapter.setData(pdfsFromFolder);
                                break;
                            case NAME_INDEX:
                                sortByNameAlphabetical(pdfsFromFolder);
                                mViewFilesAdapter.setData(pdfsFromFolder);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void sortByNameAlphabetical(ArrayList<File> pdfsFromFolder) {
        Collections.sort(pdfsFromFolder);
    }

    private void sortFilesByDateNewestToOldest(ArrayList<File> pdfsFromFolder) {
        Collections.sort(pdfsFromFolder, new Comparator<File>() {
            @Override
            public int compare(File file, File file2) {
                return Long.compare(file2.lastModified(), file.lastModified());
            }
        });
    }

    private ArrayList<File> getPdfsFromPdfFolder() {
        return getPdfsFromFolder(getOrCreatePdfDirectory().listFiles());
    }

    private File getOrCreatePdfDirectory() {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + getString(R.string.pdf_dir));
        if (!folder.exists()) {
            boolean isCreated = folder.mkdir();
        }
        return folder;
    }

    private ArrayList<File> getPdfsFromFolder(File[] files) {
        final ArrayList<File> pdfFiles = new ArrayList<>();
        for (File file : files) {
            if (!file.isDirectory() && file.getName().endsWith(getString(R.string.pdf_ext))) {
                pdfFiles.add(file);
                Log.v("adding", file.getName());
            }
        }
        return pdfFiles;
    }

    /**
     * AsyncTask used to populate the list of elements in the background
     */
    @SuppressLint("StaticFieldLeak")
    private class PopulateList extends AsyncTask<Void, Void, Void> {

        // Progress dialog
        final MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .title(activity.getResources().getString(R.string.please_wait))
                .content(activity.getResources().getString(R.string.populating_list))
                .cancelable(false)
                .progress(true, 0);
        final MaterialDialog dialog = builder.build();

        @Override
        protected Void doInBackground(Void... voids) {
            activity.runOnUiThread(new Runnable() {

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
            final File[] files = getOrCreatePdfDirectory().listFiles();
            if (files == null)
                Toast.makeText(activity, R.string.toast_no_pdfs, Toast.LENGTH_LONG).show();
            else {
                pdfFiles = getPdfsFromPdfFolder();
            }
            Log.v("done", "adding");
            mViewFilesAdapter.setData(pdfFiles);
            viewFilesListRecyclerView.setAdapter(mViewFilesAdapter);
        }
    }
}

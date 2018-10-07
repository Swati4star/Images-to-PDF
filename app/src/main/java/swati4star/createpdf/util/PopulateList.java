package swati4star.createpdf.util;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.util.ArrayList;

import swati4star.createpdf.adapter.ViewFilesAdapter;
import swati4star.createpdf.interfaces.EmptyStateChangeListener;

/**
 * AsyncTask used to populate the list of elements in the background
 */
public class PopulateList extends AsyncTask<Void, Void, Void> {

    private final int mCurrentSortingIndex;
    private final EmptyStateChangeListener mEmptyStateChangeListener;
    private final DirectoryUtils mDirectoryUtils;
    private final ViewFilesAdapter mAdapter;
    private final Handler mHandler;

    /**
     * Instantiates populate list object
     *
     * @param adapter - mAdapter to be notified with new data
     * @param emptyStateChangeListener - set appropriate view on no results
     * @param directoryUtils - directory utils object
     * @param index - sorting order
     */
    public PopulateList(ViewFilesAdapter adapter,
                        EmptyStateChangeListener emptyStateChangeListener,
                        DirectoryUtils directoryUtils, int index) {
        this.mAdapter = adapter;
        mCurrentSortingIndex = index;
        mEmptyStateChangeListener = emptyStateChangeListener;
        mHandler = new Handler(Looper.getMainLooper());
        mDirectoryUtils = directoryUtils;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        mHandler.post(this::populateListView);
        return null;
    }

    /**
     * Populate data into listView
     */
    private void populateListView() {
        ArrayList<File> pdfFiles = mDirectoryUtils.getPdfFromOtherDirectories();

        if (pdfFiles == null)
            mEmptyStateChangeListener.showNoPermissionsView();
        else if (pdfFiles.size() == 0) {
            mEmptyStateChangeListener.setEmptyStateVisible();
        } else {
            mEmptyStateChangeListener.hideNoPermissionsView();
            FileSortUtils.performSortOperation(mCurrentSortingIndex, pdfFiles);
            mAdapter.setData(pdfFiles);
        }
    }
}
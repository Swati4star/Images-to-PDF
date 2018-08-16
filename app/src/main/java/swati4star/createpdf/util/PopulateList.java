package swati4star.createpdf.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.adapter.ViewFilesAdapter;
import swati4star.createpdf.interfaces.EmptyStateChangeListener;


/**
 * AsyncTask used to populate the list of elements in the background
 */
public class PopulateList extends AsyncTask<Void, Void, Void> {

    private MaterialDialog mDialog;
    private final Activity mActivity;
    private final int mCurrentSortingIndex;
    private final EmptyStateChangeListener mEmptyStateChangeListener;
    private final DirectoryUtils mDirectoryUtils;
    private final ViewFilesAdapter mAdapter;
    private final Handler mHandler;

    /**
     * Instantiates populate list object
     *
     * @param activity - activity instance
     * @param adapter - mAdapter to be notified with new data
     * @param emptyStateChangeListener - set appropriate view on no results
     * @param index - sorting order
     */
    public PopulateList(Activity activity, ViewFilesAdapter adapter,
                        EmptyStateChangeListener emptyStateChangeListener, int index) {
        mActivity = activity;
        this.mAdapter = adapter;
        mCurrentSortingIndex = index;
        mEmptyStateChangeListener = emptyStateChangeListener;
        mDirectoryUtils = new DirectoryUtils(mActivity);
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        mHandler.post(this::populateListView);
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
        mDialog = builder.build();
        mDialog.show();
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        mDialog.dismiss();
    }

    /**
     * Populate data into listView
     */
    private void populateListView() {
        ArrayList<File> pdfFiles = new ArrayList<>();
        ArrayList<File> pdfFromOtherDir = mDirectoryUtils.getPdfFromOtherDirectories();
        final File[] files = mDirectoryUtils.getOrCreatePdfDirectory().listFiles();
        if ((files == null || files.length == 0) && pdfFromOtherDir == null) {
            mEmptyStateChangeListener.setEmptyStateVisible();
        } else {

            pdfFiles = mDirectoryUtils.getPdfsFromPdfFolder(files);
            if (pdfFromOtherDir != null) {
                pdfFiles.addAll(pdfFromOtherDir);
            }
        }
        Log.v("done", "adding");
        FileSortUtils.performSortOperation(mCurrentSortingIndex, pdfFiles);
        mAdapter.setData(pdfFiles);
    }

}
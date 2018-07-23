package swati4star.createpdf.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.adapter.ViewFilesAdapter;
import swati4star.createpdf.interfaces.EmptyStateChangeListener;

import static swati4star.createpdf.fragment.ViewFilesFragment.DATE_INDEX;
import static swati4star.createpdf.fragment.ViewFilesFragment.NAME_INDEX;
import static swati4star.createpdf.fragment.ViewFilesFragment.SIZE_DECREASING_ORDER_INDEX;
import static swati4star.createpdf.fragment.ViewFilesFragment.SIZE_INCREASING_ORDER_INDEX;

/**
 * AsyncTask used to populate the list of elements in the background
 */
public class PopulateList extends AsyncTask<Void, Void, Void> {

    MaterialDialog dialog;
    private Activity mActivity;
    private int mCurrentSortingIndex;
    private EmptyStateChangeListener mEmptyStateChangeListener;
    private FileUtils mFileUtils;
    ViewFilesAdapter adapter;

    /**
     * Instantiates populate list object
     *
     * @param activity - activity instance
     * @param adapter - adapter to be notified with new data
     * @param emptyStateChangeListener - set appropriate view on no results
     * @param index - sorting order
     */
    public PopulateList(Activity activity, ViewFilesAdapter adapter,
                        EmptyStateChangeListener emptyStateChangeListener, int index) {
        mActivity = activity;
        this.adapter = adapter;
        mCurrentSortingIndex = index;
        mEmptyStateChangeListener = emptyStateChangeListener;
        mFileUtils = new FileUtils(mActivity);
    }

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
        ArrayList<File> pdfFromOtherDir = mFileUtils.getPdfFromOtherDirectories();
        final File[] files = mFileUtils.getOrCreatePdfDirectory().listFiles();
        if ((files == null || files.length == 0) && pdfFromOtherDir == null) {
            mEmptyStateChangeListener.setEmptyStateVisible();
        } else {

            pdfFiles = mFileUtils.getPdfsFromPdfFolder(files);
            if (pdfFromOtherDir != null) {
                pdfFiles.addAll(pdfFromOtherDir);
                mFileUtils.sortFilesByDateNewestToOldest(pdfFiles);
            }
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
        adapter.setData(pdfFiles);
    }

}
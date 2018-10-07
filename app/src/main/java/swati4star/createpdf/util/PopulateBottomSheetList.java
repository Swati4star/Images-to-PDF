package swati4star.createpdf.util;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import swati4star.createpdf.interfaces.BottomSheetPopulate;

/**
 * AsyncTask used to populate the list of elements in the background
 */
public class PopulateBottomSheetList extends AsyncTask<Void, Void, Void> {

    private final BottomSheetPopulate mOnLoadListener;
    private final Handler mHandler;
    private final DirectoryUtils mDirectoryUtils;

    PopulateBottomSheetList(BottomSheetPopulate listener,
                                   DirectoryUtils directoryUtils) {
        mOnLoadListener = listener;
        mDirectoryUtils = directoryUtils;
        mHandler = new Handler(Looper.getMainLooper());

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
        ArrayList<String> paths = mDirectoryUtils.getAllPDFsOnDevice();
        mOnLoadListener.onPopulate(paths);
    }
}
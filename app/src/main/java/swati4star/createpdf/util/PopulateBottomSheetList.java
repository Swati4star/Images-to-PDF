package swati4star.createpdf.util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.ArrayList;

import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.adapter.ViewFilesAdapter;
import swati4star.createpdf.interfaces.BottomSheetPopulate;
import swati4star.createpdf.interfaces.EmptyStateChangeListener;

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
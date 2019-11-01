package swati4star.createpdf.util;

import android.os.AsyncTask;

import java.util.ArrayList;

import swati4star.createpdf.interfaces.BottomSheetPopulate;

/**
 * AsyncTask used to populate the list of elements in the background
 */
class PopulateBottomSheetListWithExcelFiles extends AsyncTask<Void, Void, ArrayList<String>> {

    private final BottomSheetPopulate mOnLoadListener;
    private final DirectoryUtils mDirectoryUtils;

    PopulateBottomSheetListWithExcelFiles(BottomSheetPopulate listener,
                            DirectoryUtils directoryUtils) {
        mOnLoadListener = listener;
        mDirectoryUtils = directoryUtils;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        return mDirectoryUtils.getAllExcelDocumentsOnDevice();
    }

    @Override
    protected void onPostExecute(ArrayList<String> paths) {
        super.onPostExecute(paths);
        mOnLoadListener.onPopulate(paths);
    }

}
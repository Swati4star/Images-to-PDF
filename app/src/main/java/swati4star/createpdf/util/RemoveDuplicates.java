package swati4star.createpdf.util;

import android.os.AsyncTask;

import java.util.ArrayList;

import swati4star.createpdf.interfaces.OnPDFCreatedInterface;

public class RemoveDuplicates extends AsyncTask<Void, Void, Void> {
    private String mPath;
    private ArrayList<String> mOutputFilePaths;
    private OnPDFCreatedInterface mOnPDFCreatedInterface;

    public RemoveDuplicates(String mPath, OnPDFCreatedInterface onPDFCreatedInterface) {
        this.mPath = mPath;

        mOutputFilePaths = new ArrayList<>();
        this.mOnPDFCreatedInterface = onPDFCreatedInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mOnPDFCreatedInterface.onPDFCreationStarted();

    }


    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

}

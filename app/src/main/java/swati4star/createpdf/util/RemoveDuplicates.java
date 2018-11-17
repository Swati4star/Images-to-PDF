package swati4star.createpdf.util;

import android.os.AsyncTask;

import java.util.ArrayList;

import swati4star.createpdf.interfaces.ExtractImagesListener;
import swati4star.createpdf.interfaces.OnPDFCreatedInterface;

public class RemoveDuplicates extends AsyncTask<Void,Void,Void> {
    private String mPath;

    private ArrayList<String> mOutputFilePaths;

    public RemoveDuplicates(String mPath, OnPDFCreatedInterface onPDFCreatedInterface) {
        this.mPath = mPath;

        mOutputFilePaths = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }
}

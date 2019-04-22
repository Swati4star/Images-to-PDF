package swati4star.createpdf.util;

import android.os.AsyncTask;
import swati4star.createpdf.interfaces.OnPDFCreatedInterface;
import swati4star.createpdf.model.TextToPDFOptions;

public class TextToPdfAsync extends AsyncTask<Object, Object, Object> {
    private PDFUtils mFileUtil;
    private TextToPDFOptions mTextToPdfOptions;
    private String mRealPath;
    private String mFileExtension;
    private final OnPDFCreatedInterface mOnPDFCreatedInterface;
    private boolean mSuccess;

    public TextToPdfAsync(String realpath, PDFUtils fileutil, TextToPDFOptions textToPDFOptions,
                          String fileextension, OnPDFCreatedInterface onPDFCreatedInterface) {
        this.mFileUtil = fileutil;
        this.mRealPath = realpath;
        this.mTextToPdfOptions = textToPDFOptions;
        this.mFileExtension = fileextension;
        this.mOnPDFCreatedInterface = onPDFCreatedInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mSuccess = true;
        mOnPDFCreatedInterface.onPDFCreationStarted();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            mFileUtil.createPdf(mTextToPdfOptions, mFileExtension);
        } catch (Exception e) {
            mSuccess = false;
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        mOnPDFCreatedInterface.onPDFCreated(mSuccess, mRealPath);
    }
}

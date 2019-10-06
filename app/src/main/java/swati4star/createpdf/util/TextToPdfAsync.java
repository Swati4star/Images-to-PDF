package swati4star.createpdf.util;

import android.os.AsyncTask;

import java.io.File;

import swati4star.createpdf.interfaces.OnTextToPdfInterface;
import swati4star.createpdf.model.TextToPDFOptions;

public class TextToPdfAsync extends AsyncTask<Object, Object, Object> {
    private TextToPDFUtils mTexttoPDFUtil;
    private TextToPDFOptions mTextToPdfOptions;
    private String mRealPath;
    private String mFileExtension;
    private String mParentPath;
    private final OnTextToPdfInterface mOnPDFCreatedInterface;
    private boolean mSuccess;

    /**
     * This is a public constructor responsible for initializing the path of the actual
     * file, the PDFUtils instance for the file, the options for text to Pdf, the file
     * extension, and the OnTextToPdfInterface instance.
     * @param realpath is the path of the actual text file.
     * @param parentPath is the directory path where the PDF will be stored.
     * @param fileutil is the PDFUtils instance for the file.
     * @param textToPDFOptions is the options for text to Pdf.
     * @param fileextension is the file extension name string.
     * @param onPDFCreatedInterface is the OnTextToPdfInterface instance.
     */
    public TextToPdfAsync(String realpath, String parentPath, TextToPDFUtils fileutil,
                          TextToPDFOptions textToPDFOptions, String fileextension,
                          OnTextToPdfInterface onPDFCreatedInterface) {
        this.mTexttoPDFUtil = fileutil;
        this.mRealPath = realpath;
        this.mTextToPdfOptions = textToPDFOptions;
        this.mFileExtension = fileextension;
        this.mOnPDFCreatedInterface = onPDFCreatedInterface;
        this.mParentPath = parentPath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mSuccess = true;
        mOnPDFCreatedInterface.onPDFCreationStarted();
    }

    private void createParentDir() {
        File folder = new File(mParentPath);
        if (!folder.exists())
            folder.mkdir();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            createParentDir();
            mTexttoPDFUtil.createPdfFromTextFile(mTextToPdfOptions, mFileExtension);
        } catch (Exception e) {
            mSuccess = false;
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        mOnPDFCreatedInterface.onPDFCreated(mSuccess);
    }

}

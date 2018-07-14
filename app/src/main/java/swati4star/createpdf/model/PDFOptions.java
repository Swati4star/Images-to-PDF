package swati4star.createpdf.model;

import com.itextpdf.text.Rectangle;

import java.util.ArrayList;

public class PDFOptions {

    private final String mFileName;
    private final boolean mPasswordProtected;
    private final String mPassword;
    private final String mQualityString;
    private final ArrayList<String> mImagesUri;
    private String mPath;
    private Rectangle mPageSize;

    public PDFOptions(String mFileName, Boolean isPasswordProtected, String mPassword,
                      String mQualityString, ArrayList<String> mImagesUri, String mPath, Rectangle mPageSize) {
        this.mFileName = mFileName;
        this.mPasswordProtected = isPasswordProtected;
        this.mPassword = mPassword;
        this.mQualityString = mQualityString;
        this.mImagesUri = mImagesUri;
        this.mPath = mPath;
        this.mPageSize = mPageSize;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getQualityString() {
        return mQualityString;
    }

    public ArrayList<String> getImagesUri() {
        return mImagesUri;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public Rectangle getPageSize() {
        return mPageSize;
    }

    public void setPageSize(Rectangle mPageSize) {
        this.mPageSize = mPageSize;
    }

    public boolean isPasswordProtected() {
        return mPasswordProtected;
    }
}

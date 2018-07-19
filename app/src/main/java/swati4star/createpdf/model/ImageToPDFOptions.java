package swati4star.createpdf.model;

import com.itextpdf.text.Rectangle;

import java.util.ArrayList;

public class ImageToPDFOptions extends PDFOptions {
    private final boolean mPasswordProtected;
    private final String mPassword;
    private final String mQualityString;
    private final ArrayList<String> mImagesUri;
    private final Rectangle mPageSize;

    public ImageToPDFOptions(String mFileName, boolean mPasswordProtected, String mPassword, String mQualityString,
                             ArrayList<String> mImagesUri, Rectangle mPageSize) {
        super(mFileName);
        this.mPasswordProtected = mPasswordProtected;
        this.mPassword = mPassword;
        this.mQualityString = mQualityString;
        this.mImagesUri = mImagesUri;
        this.mPageSize = mPageSize;
    }

    public boolean isPasswordProtected() {
        return mPasswordProtected;
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

    public Rectangle getPageSize() {
        return mPageSize;
    }
}

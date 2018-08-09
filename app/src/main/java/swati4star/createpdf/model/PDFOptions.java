package swati4star.createpdf.model;

import com.itextpdf.text.Rectangle;

public class PDFOptions {

    private final String mOutFileName;
    private final Rectangle mPageSize;
    private final boolean mPasswordProtected;
    private final String mPassword;


    PDFOptions(String mFileName, Rectangle mPageSize, boolean mPasswordProtected, String mPassword) {
        this.mOutFileName = mFileName;
        this.mPageSize = mPageSize;
        this.mPasswordProtected = mPasswordProtected;
        this.mPassword = mPassword;
    }

    public String getOutFileName() {
        return mOutFileName;
    }

    public Rectangle getPageSize() {
        return mPageSize;
    }

    public boolean isPasswordProtected() {
        return mPasswordProtected;
    }

    public String getPassword() {
        return mPassword;
    }
}

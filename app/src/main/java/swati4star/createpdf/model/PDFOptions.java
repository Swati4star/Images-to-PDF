package swati4star.createpdf.model;

public class PDFOptions {

    private final String mOutFileName;
    private final boolean mPasswordProtected;
    private final String mPassword;
    private final String mPageSize;
    private final int mBorderWidth;

    PDFOptions(String mFileName, String mPageSize, boolean mPasswordProtected, String mPassword, int mBorderWidth) {
        this.mOutFileName = mFileName;
        this.mPageSize = mPageSize;
        this.mPasswordProtected = mPasswordProtected;
        this.mPassword = mPassword;
        this.mBorderWidth = mBorderWidth;
    }

    public String getOutFileName() {
        return mOutFileName;
    }

    public String getPageSize() {
        return mPageSize;
    }

    public boolean isPasswordProtected() {
        return mPasswordProtected;
    }

    public String getPassword() {
        return mPassword;
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }
}

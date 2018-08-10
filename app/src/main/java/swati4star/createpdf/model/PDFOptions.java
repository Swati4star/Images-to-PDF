package swati4star.createpdf.model;

public class PDFOptions {

    private final String mOutFileName;
    private final boolean mPasswordProtected;
    private final String mPassword;
    private final String mPageSize;


    PDFOptions(String mFileName, String mPageSize, boolean mPasswordProtected, String mPassword) {
        this.mOutFileName = mFileName;
        this.mPageSize = mPageSize;
        this.mPasswordProtected = mPasswordProtected;
        this.mPassword = mPassword;
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
}

package swati4star.createpdf.model;

public class PDFOptions {

    private String mOutFileName;
    private boolean mPasswordProtected;
    private String mPassword;
    private String mPageSize;
    private int mBorderWidth;
    private boolean mWatermarkAdded;
    private Watermark mWatermark;

    PDFOptions() {

    }

    PDFOptions(String mFileName, String mPageSize, boolean mPasswordProtected, String mPassword,
               int mBorderWidth, boolean mWatermarkAdded, Watermark mWatermark) {
        this.mOutFileName = mFileName;
        this.mPageSize = mPageSize;
        this.mPasswordProtected = mPasswordProtected;
        this.mPassword = mPassword;
        this.mBorderWidth = mBorderWidth;
        this.mWatermarkAdded = mWatermarkAdded;
        this.mWatermark = mWatermark;
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

    public void setOutFileName(String mOutFileName) {
        this.mOutFileName = mOutFileName;
    }

    public void setPasswordProtected(boolean mPasswordProtected) {
        this.mPasswordProtected = mPasswordProtected;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public void setPageSize(String mPageSize) {
        this.mPageSize = mPageSize;
    }

    public void setBorderWidth(int mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
    }

    public boolean isWatermarkAdded() {
        return mWatermarkAdded;
    }

    public void setWatermarkAdded(boolean mWatermarkAdded) {
        this.mWatermarkAdded = mWatermarkAdded;
    }

    public Watermark getWatermark() {
        return this.mWatermark;
    }

    public void setWatermark(Watermark mWatermark) {
        this.mWatermark = mWatermark;
    }


}

package swati4star.createpdf.model;

import java.util.ArrayList;

public class ImageToPDFOptions extends PDFOptions {

    private String mQualityString;
    private ArrayList<String> mImagesUri;
    private int mMarginTop = 0;
    private int mMarginBottom = 0;
    private int mMarginRight = 0;
    private int mMarginLeft = 0;
    private String mImageScaleType;
    private String mPageNumStyle;
    private String mMasterPwd;

    public ImageToPDFOptions() {
        super();
        setPasswordProtected(false);
        setWatermarkAdded(false);
        setBorderWidth(0);
    }

    public ImageToPDFOptions(String mFileName, String mPageSize, boolean mPasswordProtected,
                             String mPassword, String mQualityString, int mBorderWidth,
                             String masterPwd, ArrayList<String> mImagesUri,
                             boolean mWatermarkAdded, Watermark mWatermark, int pageColor) {
        super(mFileName, mPageSize, mPasswordProtected, mPassword, mBorderWidth, mWatermarkAdded, mWatermark,
                pageColor);
        this.mQualityString = mQualityString;
        this.mImagesUri = mImagesUri;
        this.mMasterPwd = masterPwd;
    }

    public String getQualityString() {
        return mQualityString;
    }

    public ArrayList<String> getImagesUri() {
        return mImagesUri;
    }

    public void setQualityString(String mQualityString) {
        this.mQualityString = mQualityString;
    }

    public void setImagesUri(ArrayList<String> mImagesUri) {
        this.mImagesUri = mImagesUri;
    }

    public void setMargins(int top, int bottom, int right, int left) {
        mMarginTop = top;
        mMarginBottom = bottom;
        mMarginRight = right;
        mMarginLeft = left;
    }

    public void setMasterPwd(String pwd) {
        this.mMasterPwd = pwd;
    }

    public int getMarginTop() {
        return mMarginTop;
    }

    public int getMarginBottom() {
        return mMarginBottom;
    }

    public int getMarginRight() {
        return mMarginRight;
    }

    public int getMarginLeft() {
        return mMarginLeft;
    }

    public String getImageScaleType() {
        return mImageScaleType;
    }

    public void setImageScaleType(String mImageScaleType) {
        this.mImageScaleType = mImageScaleType;
    }
    public String getPageNumStyle() {
        return mPageNumStyle;
    }

    public void setPageNumStyle(String mPageNumStyle) {
        this.mPageNumStyle = mPageNumStyle;
    }

    public String getMasterPwd() {
        return mMasterPwd;
    }
}

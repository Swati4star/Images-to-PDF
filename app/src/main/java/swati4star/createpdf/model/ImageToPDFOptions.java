package swati4star.createpdf.model;

import androidx.annotation.NonNull;

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

    public ImageToPDFOptions(@NonNull String mFileName, @NonNull String mPageSize, boolean mPasswordProtected,
                             @NonNull String mPassword, @NonNull String mQualityString, int mBorderWidth,
                             @NonNull String masterPwd, @NonNull ArrayList<String> mImagesUri,
                             boolean mWatermarkAdded, @NonNull Watermark mWatermark, int pageColor) {
        super(mFileName, mPageSize, mPasswordProtected, mPassword, mBorderWidth, mWatermarkAdded, mWatermark,
                pageColor);
        this.mQualityString = mQualityString;
        this.mImagesUri = mImagesUri;
        this.mMasterPwd = masterPwd;
    }

    @NonNull
    public String getQualityString() {
        return mQualityString;
    }

    public void setQualityString(@NonNull String mQualityString) {
        this.mQualityString = mQualityString;
    }

    @NonNull
    public ArrayList<String> getImagesUri() {
        return mImagesUri;
    }

    public void setImagesUri(@NonNull ArrayList<String> mImagesUri) {
        this.mImagesUri = mImagesUri;
    }

    public void setMargins(int top, int bottom, int right, int left) {
        mMarginTop = top;
        mMarginBottom = bottom;
        mMarginRight = right;
        mMarginLeft = left;
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

    @NonNull
    public String getImageScaleType() {
        return mImageScaleType;
    }

    public void setImageScaleType(@NonNull String mImageScaleType) {
        this.mImageScaleType = mImageScaleType;
    }

    @NonNull
    public String getPageNumStyle() {
        return mPageNumStyle;
    }

    public void setPageNumStyle(@NonNull String mPageNumStyle) {
        this.mPageNumStyle = mPageNumStyle;
    }

    @NonNull
    public String getMasterPwd() {
        return mMasterPwd;
    }

    public void setMasterPwd(@NonNull String pwd) {
        this.mMasterPwd = pwd;
    }
}

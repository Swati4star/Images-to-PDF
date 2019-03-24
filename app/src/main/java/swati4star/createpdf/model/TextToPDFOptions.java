package swati4star.createpdf.model;

import android.net.Uri;

import com.itextpdf.text.Font;

public class TextToPDFOptions extends PDFOptions {
    private final Uri mInFileUri;
    private final int mFontSize;
    private final Font.FontFamily mFontFamily;
    private final int mFontColor;
    private final int mPageColor;

    public TextToPDFOptions(String mFileName, String mPageSize, boolean mPasswordProtected,
                            String mPassword, Uri mInFileUri, int mFontSize, Font.FontFamily mFontFamily, int color,
                            int pageColor) {
        super(mFileName, mPageSize, mPasswordProtected, mPassword, 0, false, null);
        this.mInFileUri = mInFileUri;
        this.mFontSize = mFontSize;
        this.mFontFamily = mFontFamily;
        this.mFontColor = color;
        this.mPageColor = pageColor;
    }

    public Uri getInFileUri() {
        return mInFileUri;
    }

    public int getFontSize() {
        return mFontSize;
    }

    public Font.FontFamily getFontFamily() {
        return mFontFamily;
    }

    public int getmFontColor() {
        return mFontColor;
    }

    public int getmPageColor() {
        return mPageColor;
    }
}

package swati4star.createpdf.model;

import android.net.Uri;

public class TextToPDFOptions extends PDFOptions {
    private final Uri mInFileUri;
    private final int mFontSize;

    public TextToPDFOptions(String mFileName, Uri mInFileUri, int mFontSize) {
        super(mFileName);
        this.mInFileUri = mInFileUri;
        this.mFontSize = mFontSize;
    }

    public Uri getInFileUri() {
        return mInFileUri;
    }

    public int getFontSize() {
        return mFontSize;
    }
}

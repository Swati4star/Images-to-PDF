package swati4star.createpdf.model;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.itextpdf.text.Font;

import swati4star.createpdf.preferences.TextToPdfPreferences;

public class TextToPDFOptions extends PDFOptions {
    private final Uri mInFileUri;
    private final int mFontSize;
    private final Font.FontFamily mFontFamily;
    private final int mFontColor;

    public TextToPDFOptions(@NonNull String mFileName, @NonNull String mPageSize, boolean mPasswordProtected,
                            @NonNull String mPassword, @NonNull Uri mInFileUri, int mFontSize, @NonNull Font.FontFamily mFontFamily, int color,
                            int pageColor) {
        super(mFileName, mPageSize, mPasswordProtected, mPassword, 0, false, null, pageColor);
        this.mInFileUri = mInFileUri;
        this.mFontSize = mFontSize;
        this.mFontFamily = mFontFamily;
        this.mFontColor = color;
    }

    @NonNull
    public Uri getInFileUri() {
        return mInFileUri;
    }

    public int getFontSize() {
        return mFontSize;
    }

    @NonNull
    public Font.FontFamily getFontFamily() {
        return mFontFamily;
    }

    public int getFontColor() {
        return mFontColor;
    }

    public static class Builder {

        private String mFileName;
        private String mPageSize;
        private boolean mPasswordProtected;
        private String mPassword;
        private int mPageColor;
        private Uri mInFileUri;
        private int mFontSize;
        private Font.FontFamily mFontFamily;
        private int mFontColor;
        private String mFontSizeTitle;

        public Builder(@NonNull Context context) {
            final TextToPdfPreferences preferences = new TextToPdfPreferences(context);
            mPageSize = preferences.getPageSize();
            mPasswordProtected = false;
            mFontColor = preferences.getFontColor();
            mFontFamily = Font.FontFamily.valueOf(preferences.getFontFamily());
            mFontSize = preferences.getFontSize();
            mPageColor = preferences.getPageColor();
        }

        @NonNull
        public String getFileName() {
            return mFileName;
        }

        @NonNull
        public Builder setFileName(@NonNull String fileName) {
            mFileName = fileName;
            return this;
        }

        @NonNull
        public String getPageSize() {
            return mPageSize;
        }

        @NonNull
        public Builder setPageSize(@NonNull String pageSize) {
            mPageSize = pageSize;
            return this;
        }

        public boolean isPasswordProtected() {
            return mPasswordProtected;
        }

        @NonNull
        public Builder setPasswordProtected(boolean passwordProtected) {
            mPasswordProtected = passwordProtected;
            return this;
        }

        @NonNull
        public String getPassword() {
            return mPassword;
        }

        @NonNull
        public Builder setPassword(@NonNull String password) {
            mPassword = password;
            return this;
        }

        public int getPageColor() {
            return mPageColor;
        }

        @NonNull
        public Builder setPageColor(int pageColor) {
            mPageColor = pageColor;
            return this;
        }

        @NonNull
        public Uri getInFileUri() {
            return mInFileUri;
        }

        @NonNull
        public Builder setInFileUri(@NonNull Uri inFileUri) {
            mInFileUri = inFileUri;
            return this;
        }

        public int getFontSize() {
            return mFontSize;
        }

        @NonNull
        public Builder setFontSize(int fontSize) {
            mFontSize = fontSize;
            return this;
        }

        @NonNull
        public Font.FontFamily getFontFamily() {
            return mFontFamily;
        }

        @NonNull
        public Builder setFontFamily(@NonNull Font.FontFamily fontFamily) {
            mFontFamily = fontFamily;
            return this;
        }

        public int getFontColor() {
            return mFontColor;
        }

        @NonNull
        public Builder setFontColor(int fontColor) {
            mFontColor = fontColor;
            return this;
        }

        @NonNull
        public String getFontSizeTitle() {
            return mFontSizeTitle;
        }

        @NonNull
        public Builder setFontSizeTitle(@NonNull String fontSizeTitle) {
            mFontSizeTitle = fontSizeTitle;
            return this;
        }

        @NonNull
        public TextToPDFOptions build() {
            return new TextToPDFOptions(mFileName, mPageSize, mPasswordProtected,
                    mPassword, mInFileUri, mFontSize, mFontFamily, mFontColor, mPageColor);
        }


    }
}

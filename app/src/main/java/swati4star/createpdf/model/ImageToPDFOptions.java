package swati4star.createpdf.model;

import com.itextpdf.text.Rectangle;

import java.util.ArrayList;

public class ImageToPDFOptions extends PDFOptions {
    private final boolean mPasswordProtected;
    private final String mPassword;
    private final String mQualityString;
    private final ArrayList<String> mImagesUri;

    public ImageToPDFOptions(String mFileName, Rectangle mPageSize, boolean mPasswordProtected,
                             String mPassword, String mQualityString, ArrayList<String> mImagesUri) {
        super(mFileName, mPageSize);
        this.mPasswordProtected = mPasswordProtected;
        this.mPassword = mPassword;
        this.mQualityString = mQualityString;
        this.mImagesUri = mImagesUri;
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

}

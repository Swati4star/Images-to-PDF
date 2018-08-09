package swati4star.createpdf.model;

import com.itextpdf.text.Rectangle;

import java.util.ArrayList;

public class ImageToPDFOptions extends PDFOptions {
    private final String mQualityString;
    private final ArrayList<String> mImagesUri;

    public ImageToPDFOptions(String mFileName, Rectangle mPageSize, boolean mPasswordProtected,
                             String mPassword, String mQualityString, ArrayList<String> mImagesUri) {
        super(mFileName, mPageSize, mPasswordProtected, mPassword);
        this.mQualityString = mQualityString;
        this.mImagesUri = mImagesUri;
    }

    public String getQualityString() {
        return mQualityString;
    }

    public ArrayList<String> getImagesUri() {
        return mImagesUri;
    }

}

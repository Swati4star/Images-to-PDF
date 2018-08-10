package swati4star.createpdf.model;

import java.util.ArrayList;

public class ImageToPDFOptions extends PDFOptions {
    private final String mQualityString;
    private final ArrayList<String> mImagesUri;

    public ImageToPDFOptions(String mFileName, String mPageSize, boolean mPasswordProtected,
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

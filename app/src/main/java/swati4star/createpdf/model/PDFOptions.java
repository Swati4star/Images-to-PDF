package swati4star.createpdf.model;

import com.itextpdf.text.Rectangle;

public class PDFOptions {

    private final String mOutFileName;
    private final Rectangle mPageSize;


    PDFOptions(String mFileName, Rectangle mPageSize) {
        this.mOutFileName = mFileName;
        this.mPageSize = mPageSize;
    }

    public String getOutFileName() {
        return mOutFileName;
    }

    public Rectangle getPageSize() {
        return mPageSize;
    }
}

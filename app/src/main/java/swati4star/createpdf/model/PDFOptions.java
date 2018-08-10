package swati4star.createpdf.model;

public class PDFOptions {

    private final String mOutFileName;
    private final String mPageSize;


    PDFOptions(String mFileName, String mPageSize) {
        this.mOutFileName = mFileName;
        this.mPageSize = mPageSize;
    }

    public String getOutFileName() {
        return mOutFileName;
    }

    public String getPageSize() {
        return mPageSize;
    }
}

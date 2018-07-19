package swati4star.createpdf.model;

public class PDFOptions {

    protected final String mOutFileName;

    public PDFOptions(String mFileName) {
        this.mOutFileName = mFileName;
    }

    public String getOutFileName() {
        return mOutFileName;
    }
}

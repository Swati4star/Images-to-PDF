package swati4star.createpdf.model;

public class PDFOptions {

    private final String mOutFileName;

    PDFOptions(String mFileName) {
        this.mOutFileName = mFileName;
    }

    public String getOutFileName() {
        return mOutFileName;
    }
}

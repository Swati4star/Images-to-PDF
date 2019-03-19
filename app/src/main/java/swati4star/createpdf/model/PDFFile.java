package swati4star.createpdf.model;

import java.io.File;

public class PDFFile {
    private File mPdfFile;
    private boolean mIsEncrypted;

    public PDFFile(File mPdfFile, boolean mIsEncrypted) {
        this.mPdfFile = mPdfFile;
        this.mIsEncrypted = mIsEncrypted;
    }

    public File getPdfFile() {
        return mPdfFile;
    }

    public void setPdfFile(File mPdfFile) {
        this.mPdfFile = mPdfFile;
    }

    public boolean isEncrypted() {
        return mIsEncrypted;
    }

    public void setEncrypted(boolean mIsEncrypted) {
        this.mIsEncrypted = mIsEncrypted;
    }
}

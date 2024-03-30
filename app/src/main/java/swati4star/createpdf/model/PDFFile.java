package swati4star.createpdf.model;

import androidx.annotation.NonNull;

import java.io.File;

public class PDFFile {
    private File mPdfFile;
    private boolean mIsEncrypted;

    public PDFFile(@NonNull File mPdfFile, boolean mIsEncrypted) {
        this.mPdfFile = mPdfFile;
        this.mIsEncrypted = mIsEncrypted;
    }

    @NonNull
    public File getPdfFile() {
        return mPdfFile;
    }

    public void setPdfFile(@NonNull File mPdfFile) {
        this.mPdfFile = mPdfFile;
    }

    public boolean isEncrypted() {
        return mIsEncrypted;
    }

    public void setEncrypted(boolean mIsEncrypted) {
        this.mIsEncrypted = mIsEncrypted;
    }
}

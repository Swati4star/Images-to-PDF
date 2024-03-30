package swati4star.createpdf.interfaces;

import androidx.annotation.NonNull;

public interface OnPDFCompressedInterface {
    void pdfCompressionStarted();

    void pdfCompressionEnded(@NonNull String path, @NonNull Boolean success);
}

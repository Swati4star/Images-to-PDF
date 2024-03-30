package swati4star.createpdf.interfaces;

import androidx.annotation.NonNull;

public interface OnPDFCreatedInterface {
    void onPDFCreationStarted();

    void onPDFCreated(boolean success, @NonNull String path);
}

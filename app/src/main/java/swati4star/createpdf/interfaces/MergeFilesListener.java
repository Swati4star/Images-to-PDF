package swati4star.createpdf.interfaces;

import androidx.annotation.NonNull;

public interface MergeFilesListener {
    void resetValues(boolean isPDFMerged, @NonNull String path);

    void mergeStarted();
}

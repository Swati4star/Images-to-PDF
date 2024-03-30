package swati4star.createpdf.interfaces;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public interface ExtractImagesListener {
    void resetView();

    void extractionStarted();

    void updateView(int imageCount, @NonNull ArrayList<String> outputFilePaths);
}
package swati4star.createpdf.interfaces;

import java.util.ArrayList;

public interface ExtractImagesListener {
    void resetView();
    void extractionStarted();
    void updateView(int imagecount, ArrayList<String> outputFilePaths);
}
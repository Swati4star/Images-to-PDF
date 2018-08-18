package swati4star.createpdf.interfaces;

import java.util.ArrayList;

public interface ExtractImagesListener {
    void resetView();

    void updateView(String text, ArrayList<String> outputFilePaths);
}
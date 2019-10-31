package swati4star.createpdf.interfaces;

import android.graphics.Bitmap;

import java.util.List;

public interface OnPdfReorderedInterface {

    /**
    * Marks the initiation of pdf reorder operation
    */
    void onPdfReorderStarted();

    /**
    * Called when PdfReorder is complete
    * @param bitmaps All the pages of the pdf as bitmap .
    */
    void onPdfReorderCompleted(List<Bitmap> bitmaps);

    /*
    * Called when the pdf reorder operation fails.
    */
    void onPdfReorderFailed();
}

package swati4star.createpdf.util;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import swati4star.createpdf.interfaces.OnPDFCreatedInterface;

import static android.os.ParcelFileDescriptor.MODE_READ_ONLY;

public class RemoveDuplicates extends AsyncTask<Void, Void, Void> {
    private String mPath;
    private final OnPDFCreatedInterface mOnPDFCreatedInterface;
    private final ArrayList<Bitmap> mBitmaps;
    private final StringBuilder mSequence;
    private Boolean mIsNewPDFCreated;

    public RemoveDuplicates(String mPath, OnPDFCreatedInterface onPDFCreatedInterface) {
        this.mPath = mPath;
        mSequence = new StringBuilder();
        mBitmaps = new ArrayList<>();
        this.mOnPDFCreatedInterface = onPDFCreatedInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mOnPDFCreatedInterface.onPDFCreationStarted();
        mIsNewPDFCreated = false;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        // Render pdf pages as bitmap
        ParcelFileDescriptor fileDescriptor = null;
        try {
            if (mPath != null)
                // resolve pdf file path based on relative path
                fileDescriptor = ParcelFileDescriptor.open(new File(mPath), MODE_READ_ONLY);

            if (fileDescriptor != null) {
                PdfRenderer renderer = new PdfRenderer(fileDescriptor);
                final int pageCount = renderer.getPageCount();

                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = renderer.openPage(i);
                    // generate bitmaps for individual pdf pages
                    Bitmap currentBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(),
                            Bitmap.Config.ARGB_8888);
                    // say we render for showing on the screen
                    page.render(currentBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    // close the page
                    page.close();

                    //Adding bitmap to arrayList if not same
                    checkAndAddIfBitmapExists(currentBitmap, i);

                }

                // close the renderer
                renderer.close();

                if (mBitmaps.size() == pageCount) {
                    //No repetition found
                    return null;
                } else {
                    String mPages = mSequence.toString();
                    String outputPath = mPath.replace(".pdf", "_edited_" + mPages + ".pdf");
                    if (createPDF(mPath, outputPath, mPages)) {
                        mPath = outputPath;
                        mIsNewPDFCreated = true;
                    }
                }

            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
            mIsNewPDFCreated = false;
        }

        return null;
    }


    private void checkAndAddIfBitmapExists(Bitmap bitmap, int position) {

        boolean add = true;
        for (Bitmap b : mBitmaps) {
            if (b.sameAs(bitmap))
                add = false;
        }
        if (add) {
            mBitmaps.add(bitmap);
            mSequence.append(position).append(",");
        }
    }

    @Override
    protected void onPostExecute(Void  avoid) {
        // execution of result of Long time consuming operation
        super.onPostExecute(avoid);
        mOnPDFCreatedInterface.onPDFCreated(mIsNewPDFCreated, mPath);
    }

    private boolean createPDF(String inputPath, String output, String pages) {
        try {
            PdfReader reader = new PdfReader(inputPath);
            reader.selectPages(pages);
            PdfStamper pdfStamper = new PdfStamper(reader,
                    new FileOutputStream(output));
            pdfStamper.close();
            return true;

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            return false;
        }
    }

}
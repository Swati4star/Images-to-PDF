package swati4star.createpdf.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import swati4star.createpdf.interfaces.OnPDFCreatedInterface;

import static android.os.ParcelFileDescriptor.MODE_READ_ONLY;

public class InvertPdf extends AsyncTask<Void, Void, Void> {
    private String mPath;
    private OnPDFCreatedInterface mOnPDFCreatedInterface;
    private ArrayList<Bitmap> mBitmaps;
    private StringBuilder mSequence;
    private Boolean mIsNewPDFCreated;

    public InvertPdf(String mPath, OnPDFCreatedInterface onPDFCreatedInterface) {
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

                    //Inverting current Bitmap and adding it.
                    Bitmap invertedBitmap = invertAndAdd(currentBitmap);
                    mBitmaps.add(invertedBitmap);
                }
                // close the renderer
                renderer.close();
                String outputPath = mPath.replace(".pdf", "inverted" + ".pdf");
                if (createPDF(outputPath, mBitmaps)) {
                    mPath = outputPath;
                    mIsNewPDFCreated = true;
                }
            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
            mIsNewPDFCreated = false;
        }

        return null;
    }

    public Bitmap invertAndAdd(Bitmap src) {
        int height = src.getHeight();
        int width = src.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

//        ColorMatrix matrixGrayscale = new ColorMatrix();
//        matrixGrayscale.setSaturation(0);

        ColorMatrix matrixInvert = new ColorMatrix();
        matrixInvert.set(new float[]
                {-1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                });
//        matrixInvert.preConcat(matrixGrayscale);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixInvert);
        paint.setColorFilter(filter);
        canvas.drawBitmap(src, 0, 0, paint);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Void avoid) {
        // execution of result of Long time consuming operation
        super.onPostExecute(avoid);
        mOnPDFCreatedInterface.onPDFCreated(mIsNewPDFCreated, mPath);
    }

    private boolean createPDF(String output, ArrayList<Bitmap> bitmaps) {
//        try {
//            PdfReader reader = new PdfReader(inputPath);
//            reader.selectPages(pages);
//            PdfStamper pdfStamper = new PdfStamper(reader,
//                    new FileOutputStream(output));
//            pdfStamper.close();
//            return true;
//
//        } catch (IOException | DocumentException e) {
//            e.printStackTrace();
//            return false;
//        }
        try {
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(output));
            document.open();


            for (Bitmap b : mBitmaps) {
                Image image = Image.getInstance(getByteArray(b));
                document.add(image);
                document.newPage();
            }
            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public byte[] getByteArray(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 1, bos);
        return bos.toByteArray();
    }

}
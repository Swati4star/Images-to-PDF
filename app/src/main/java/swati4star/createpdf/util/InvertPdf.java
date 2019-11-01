package swati4star.createpdf.util;

import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import swati4star.createpdf.interfaces.OnPDFCreatedInterface;

import static android.os.ParcelFileDescriptor.MODE_READ_ONLY;
import static swati4star.createpdf.util.Constants.pdfExtension;

public class InvertPdf extends AsyncTask<Void, Void, Void> {
    private String mPath;
    private final OnPDFCreatedInterface mOnPDFCreatedInterface;
    private Boolean mIsNewPDFCreated;

    /**
     * Invert PDF constructor
     * @param path - path of input file
     * @param onPDFCreatedInterface - interface implementation to handle pre & post
     */
    public InvertPdf(String path, OnPDFCreatedInterface onPDFCreatedInterface) {
        this.mPath = path;
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
        ParcelFileDescriptor fileDescriptor = null;
        try {
            if (mPath != null)
                fileDescriptor = ParcelFileDescriptor.open(new File(mPath), MODE_READ_ONLY);
            if (fileDescriptor != null) {
                String outputPath = mPath.replace(".pdf", "_inverted" + pdfExtension);
                if (createPDF(mPath, outputPath)) {
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


    /**
     * invokes invert method passing stamper as parameter
     *
     * @param mPath original file path
     * @param outputPath output file path
     */
    private boolean createPDF(String mPath, String outputPath) {

        try {
            PdfReader reader = new PdfReader(mPath);
            OutputStream os = new FileOutputStream(outputPath);
            PdfStamper stamper = new PdfStamper(reader, os);
            invert(stamper);
            stamper.close();
            os.close();
            return true;
        } catch (Exception er) {
            er.printStackTrace();
            return false;
        }

    }

    /**
     * Inverts PDF page by page
     * @param stamper - PDF stamper
     */
    private void invert(PdfStamper stamper) {
        for (int i = stamper.getReader().getNumberOfPages(); i > 0; i--) {
            invertPage(stamper, i);
        }
    }

    /**
     * Draws a white rectangle in blend mode DIFFERENCE
     * over the page and then draws a white rectangle
     * under the page for compatibility for all acrobat versions.
     *
     * @param stamper - PDF stamper
     * @param page - PDF page index
     */
    private void invertPage(PdfStamper stamper, int page) {
        Rectangle rect = stamper.getReader().getPageSize(page);
        PdfContentByte cb = stamper.getOverContent(page);
        PdfGState gs = new PdfGState();
        gs.setBlendMode(PdfGState.BM_DIFFERENCE);
        cb.setGState(gs);
        cb.setColorFill(new GrayColor(1.0f));
        cb.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
        cb.fill();

        cb = stamper.getUnderContent(page);
        cb.setColorFill(new GrayColor(1.0f));
        cb.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
        cb.fill();
    }

    @Override
    protected void onPostExecute(Void avoid) {
        // Execution of result of Long time consuming operation
        super.onPostExecute(avoid);
        mOnPDFCreatedInterface.onPDFCreated(mIsNewPDFCreated, mPath);
    }
}
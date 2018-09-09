package swati4star.createpdf.util;

import android.os.AsyncTask;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import java.io.FileOutputStream;

import swati4star.createpdf.interfaces.MergeFilesListener;

import static swati4star.createpdf.util.Constants.pdfExtension;

public class MergePdf extends AsyncTask<String, Void, Void> {

    private String mFinPath;
    private Boolean mIsPDFMerged;
    private String mFilename;
    private final MergeFilesListener mMergeFilesListener;

    public MergePdf(String fileName, String homePath,
                    MergeFilesListener mergeFilesListener) {
        mFilename = fileName;
        mMergeFilesListener = mergeFilesListener;
        mFinPath = homePath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mIsPDFMerged = false;
        mMergeFilesListener.mergeStarted();
    }

    @Override
    protected Void doInBackground(String... pdfpaths) {
        try {
            PdfReader pdfreader;
            for (String p : pdfpaths)
                pdfreader = new PdfReader(p);
            // Create document object
            Document document = new Document();
            // Create pdf copy object to copy current document to the output mergedresult file
            mFilename = mFilename + pdfExtension;
            mFinPath = mFinPath + mFilename;
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(mFinPath));
            // Open the document
            document.open();
            int numOfPages;
            for (String pdfPath : pdfpaths) {
                // Create pdf reader object to read each input pdf file
                pdfreader = new PdfReader(pdfPath);
                // Get the number of pages of the pdf file
                numOfPages = pdfreader.getNumberOfPages();
                for (int page = 1; page <= numOfPages; page++) {
                    // Import all pages from the file to PdfCopy
                    copy.addPage(copy.getImportedPage(pdfreader, page));
                }
            }
            mIsPDFMerged = true;
            document.close(); // close the document
        } catch (Exception e) {
            mIsPDFMerged = false;
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mMergeFilesListener.resetValues(mIsPDFMerged, mFinPath);
    }
}
package swati4star.createpdf.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import java.io.FileOutputStream;
import java.util.Objects;

import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.MergeFilesListener;

@SuppressLint("StaticFieldLeak")
public class MergePdf extends AsyncTask<String, Void, Void> {

    private String mFinPath;
    private Boolean mIsPDFMerged;
    private MaterialDialog mMaterialDialog;
    private Activity mActivity;
    private LottieAnimationView mAnimationView;
    private String mFilename;
    private MergeFilesListener mMergeFilesListener;

    public MergePdf(Activity activity, String fileName, MergeFilesListener mergeFilesListener) {
        mActivity = activity;
        mFilename = fileName;
        mMergeFilesListener = mergeFilesListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mMaterialDialog = new MaterialDialog.Builder(mActivity)
                .customView(R.layout.lottie_anim_dialog, false)
                .build();
        mAnimationView = mMaterialDialog.getCustomView().findViewById(R.id.animation_view);
        mAnimationView.playAnimation();
        mIsPDFMerged = false;
        mMaterialDialog.show();
    }

    @Override
    protected Void doInBackground(String... pdfpaths) {
        try {
            // Create document object
            Document document = new Document();
            // Create pdf copy object to copy current document to the output mergedresult file
            String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    mActivity.getString(R.string.pdf_dir);
            mFilename = mFilename + mActivity.getString(R.string.pdf_ext);
            mFinPath = mPath + mFilename;
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(mFinPath));
            // Open the document
            document.open();
            PdfReader pdfreader;
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
        mAnimationView.cancelAnimation();
        mMaterialDialog.dismiss();
        mMergeFilesListener.resetValues();
        if (mIsPDFMerged)
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                    R.string.pdf_merged, Snackbar.LENGTH_LONG).setAction(R.string.snackbar_viewAction, v -> {
                        FileUtils fileUtils = new FileUtils(mActivity);
                        fileUtils.openFile(mFinPath);
                    }).show();
        else
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                    R.string.pdf_merge_error, Snackbar.LENGTH_LONG).show();
    }
}
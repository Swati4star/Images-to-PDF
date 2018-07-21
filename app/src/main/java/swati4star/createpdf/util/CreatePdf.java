package swati4star.createpdf.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.OnPDFCreatedInterface;
import swati4star.createpdf.model.ImageToPDFOptions;

import static swati4star.createpdf.util.Constants.DEFAULT_COMPRESSION;

/**
 * An async task that converts selected images to Pdf
 */
public class CreatePdf extends AsyncTask<String, String, String> {

    private final String mFileName;
    private final String mPassword;
    private final String mQualityString;
    private final ArrayList<String> mImagesUri;
    private final Activity mContext;
    private final OnPDFCreatedInterface mOnPDFCreatedInterface;
    private LottieAnimationView mAnimationView;
    private boolean mSuccess;
    private String mPath;
    private final Rectangle mPageSize;
    private MaterialDialog mMaterialDialog;
    private final boolean mPasswordProtected;

    public CreatePdf(Activity context, ImageToPDFOptions mImageToPDFOptions, OnPDFCreatedInterface onPDFCreated) {
        this.mImagesUri = mImageToPDFOptions.getImagesUri();
        this.mFileName = mImageToPDFOptions.getOutFileName();
        this.mPassword = mImageToPDFOptions.getPassword();
        this.mQualityString = mImageToPDFOptions.getQualityString();
        this.mContext = context;
        this.mOnPDFCreatedInterface = onPDFCreated;
        this.mPageSize = mImageToPDFOptions.getPageSize();
        this.mPasswordProtected = mImageToPDFOptions.isPasswordProtected();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mSuccess = true;
        mMaterialDialog = new MaterialDialog.Builder(mContext)
                .customView(R.layout.lottie_anim_dialog, false)
                .build();
        mAnimationView = mMaterialDialog.getCustomView().findViewById(R.id.animation_view);
        mAnimationView.playAnimation();
        mMaterialDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        mPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                mContext.getString(R.string.pdf_dir);
        File folder = new File(mPath);
        if (!folder.exists()) {
            mSuccess = folder.mkdir();
            if (!mSuccess) {
                return null;
            }
        }

        mPath = mPath + mFileName + mContext.getString(R.string.pdf_ext);

        Log.v("stage 1", "store the pdf in sd card");

        Document document = new Document(mPageSize, 38, 38, 50, 38);

        Log.v("stage 2", "Document Created");

        Rectangle documentRect = document.getPageSize();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(mPath));

            Log.v("Stage 3", "Pdf writer");

            if (mPasswordProtected) {
                writer.setEncryption(mPassword.getBytes(),
                        mContext.getString(R.string.app_name).getBytes(),
                        PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY,
                        PdfWriter.ENCRYPTION_AES_128);

                Log.v("Stage 3.1", "Set Encryption");
            }

            document.open();

            Log.v("Stage 4", "Document opened");

            for (int i = 0; i < mImagesUri.size(); i++) {
                int quality;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
                quality = preferences.getInt(DEFAULT_COMPRESSION, 30);

                if (StringUtils.isNotEmpty(mQualityString)) {
                    quality = Integer.parseInt(mQualityString);
                }
                Image image = Image.getInstance(mImagesUri.get(i));
                image.setCompressionLevel(100 - quality);

                Log.v("Stage 5", "Image compressed");


                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(mImagesUri.get(i), bmOptions);

                Rectangle imageSize = ImageUtils.calculateFitSize(bitmap.getWidth(), bitmap.getHeight(), documentRect);
                image.scaleAbsolute(imageSize);

                Log.v("Stage 6", "Image mPath adding");

                image.setAbsolutePosition(
                        (documentRect.getWidth() - image.getScaledWidth()) / 2,
                        (documentRect.getHeight() - image.getScaledHeight()) / 2);
                Log.v("Stage 7", "Image Alignments");

                document.add(image);

                document.newPage();
            }

            Log.v("Stage 8", "Image adding");

            document.close();

            Log.v("Stage 7", "Document Closed" + mPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        document.close();
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mAnimationView.cancelAnimation();
        //mAnimationView.setVisibility(View.GONE);
        mMaterialDialog.dismiss();
        if (!mSuccess) {
            Snackbar.make(Objects.requireNonNull(mContext).findViewById(android.R.id.content),
                    R.string.snackbar_folder_not_created,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        mOnPDFCreatedInterface.onPDFCreated(mSuccess, mPath);

        Snackbar.make(Objects.requireNonNull(mContext).findViewById(android.R.id.content)
                , R.string.snackbar_pdfCreated
                , Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_viewAction, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FileUtils fileUtils = new FileUtils(mContext);
                        fileUtils.openFile(mPath);
                    }
                }).show();
    }
}



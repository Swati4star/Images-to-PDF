package swati4star.createpdf.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfImageObject;

import java.io.IOException;
import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.ExtractImagesListener;

import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class ExtractImages extends AsyncTask<Void, Void, Void> {
    private final Activity mActivity;
    private String mPath;
    private FileUtils mFileUtils;
    private ExtractImagesListener mExtractImagesListener;
    private MaterialDialog mMaterialDialog;
    private boolean mSuccess;
    private int mImagesCount = 0;
    private ArrayList<String> mOutputFilePaths;

    public ExtractImages(Activity mActivity, String mPath, ExtractImagesListener mExtractImagesListener) {
        this.mActivity = mActivity;
        this.mPath = mPath;
        this.mExtractImagesListener = mExtractImagesListener;
        mFileUtils = new FileUtils(mActivity);
        mOutputFilePaths = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mMaterialDialog = new MaterialDialog.Builder(mActivity)
                .customView(R.layout.lottie_anim_dialog, false)
                .build();
        mMaterialDialog.show();
        mSuccess = false;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        mOutputFilePaths = new ArrayList<>();
        mImagesCount = 0;
        try {
            PdfReader reader = new PdfReader(mPath);
            PdfObject obj;
            for (int i = 1; i <= reader.getXrefSize(); i++) {
                obj = reader.getPdfObject(i);
                if (obj != null && obj.isStream()) {
                    PRStream stream = (PRStream) obj;
                    PdfObject type = stream.get(PdfName.SUBTYPE); //get the object type
                    if (type != null && type.toString().equals(PdfName.IMAGE.toString())) {
                        PdfImageObject pio = new PdfImageObject(stream);
                        byte[] image = pio.getImageAsBytes();
                        Bitmap bmp = BitmapFactory.decodeByteArray(image, 0,
                                image.length);
                        mImagesCount++;
                        mOutputFilePaths.add(mFileUtils.saveImage(bmp));
                    }
                }
            }
            mSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mImagesCount == 0) {
            showSnackbar(mActivity, R.string.extract_images_failed);
        }
        if (mSuccess) {
            String text = String.format(mActivity.getString(R.string.extract_images_success), mImagesCount);
            showSnackbar(mActivity, text);
            mExtractImagesListener.updateView(text, mOutputFilePaths);
        }
        mMaterialDialog.dismiss();
        mExtractImagesListener.resetView();
    }
}
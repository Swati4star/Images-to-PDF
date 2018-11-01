package swati4star.createpdf.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import swati4star.createpdf.interfaces.OnPDFCreatedInterface;
import swati4star.createpdf.model.ImageToPDFOptions;
import swati4star.createpdf.model.Watermark;

import static swati4star.createpdf.util.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static swati4star.createpdf.util.Constants.pdfExtension;

/**
 * An async task that converts selected images to Pdf
 */
public class CreatePdf extends AsyncTask<String, String, String> {

    private final String mFileName;
    private final String mPassword;
    private final String mQualityString;
    private final ArrayList<String> mImagesUri;
    private final int mBorderWidth;
    private final OnPDFCreatedInterface mOnPDFCreatedInterface;
    private boolean mSuccess;
    private String mPath;
    private final String mPageSize;
    private final boolean mPasswordProtected;
    private Boolean mWatermarkAdded;
    private Watermark mWatermark;
    private int mMarginTop;
    private int mMarginBottom;
    private int mMarginRight;
    private int mMarginLeft;
    private String mImagescaleType;
    private String mPageNumStyle;
    private String mMasterPwd;

    public CreatePdf(ImageToPDFOptions mImageToPDFOptions, String parentPath,
                     OnPDFCreatedInterface onPDFCreated) {
        this.mImagesUri = mImageToPDFOptions.getImagesUri();
        this.mFileName = mImageToPDFOptions.getOutFileName();
        this.mPassword = mImageToPDFOptions.getPassword();
        this.mQualityString = mImageToPDFOptions.getQualityString();
        this.mOnPDFCreatedInterface = onPDFCreated;
        this.mPageSize = mImageToPDFOptions.getPageSize();
        this.mPasswordProtected = mImageToPDFOptions.isPasswordProtected();
        this.mBorderWidth = mImageToPDFOptions.getBorderWidth();
        this.mWatermarkAdded = mImageToPDFOptions.isWatermarkAdded();
        this.mWatermark = mImageToPDFOptions.getWatermark();
        this.mMarginTop = mImageToPDFOptions.getMarginTop();
        this.mMarginBottom = mImageToPDFOptions.getMarginBottom();
        this.mMarginRight = mImageToPDFOptions.getMarginRight();
        this.mMarginLeft = mImageToPDFOptions.getMarginLeft();
        this.mImagescaleType = mImageToPDFOptions.getImageScaleType();
        this.mPageNumStyle = mImageToPDFOptions.getPageNumStyle();
        this.mMasterPwd = mImageToPDFOptions.getMasterPwd();
        mPath = parentPath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mSuccess = true;
        mOnPDFCreatedInterface.onPDFCreationStarted();
    }

    private void setFilePath() {
        File folder = new File(mPath);
        if (!folder.exists())
            folder.mkdir();
        mPath = mPath + mFileName + pdfExtension;
    }

    @Override
    protected String doInBackground(String... params) {

        setFilePath();

        Log.v("stage 1", "store the pdf in sd card");

        Document document = new Document(PageSize.getRectangle(mPageSize),
                mMarginLeft, mMarginRight, mMarginTop, mMarginBottom);
        Log.v("stage 2", "Document Created");
        document.setMargins(mMarginLeft, mMarginRight, mMarginTop, mMarginBottom);
        Rectangle documentRect = document.getPageSize();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(mPath));

            Log.v("Stage 3", "Pdf writer");

            if (mPasswordProtected) {
                writer.setEncryption(mPassword.getBytes(), mMasterPwd.getBytes(),
                        PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY,
                        PdfWriter.ENCRYPTION_AES_128);

                Log.v("Stage 3.1", "Set Encryption");
            }

            if (mWatermarkAdded) {
                WatermarkPageEvent watermarkPageEvent = new WatermarkPageEvent();
                watermarkPageEvent.setWatermark(mWatermark);
                writer.setPageEvent(watermarkPageEvent);
            }

            document.open();

            Log.v("Stage 4", "Document opened");

            for (int i = 0; i < mImagesUri.size(); i++) {
                int quality;
                quality = 30;
                if (StringUtils.isNotEmpty(mQualityString)) {
                    quality = Integer.parseInt(mQualityString);
                }
                Image image = Image.getInstance(mImagesUri.get(i));
                // compressionLevel is a value between 0 (best speed) and 9 (best compression)
                double qualtyMod = quality * 0.09;
                image.setCompressionLevel((int) qualtyMod);
                image.setBorder(Rectangle.BOX);
                image.setBorderWidth(mBorderWidth);

                Log.v("Stage 5", "Image compressed " + qualtyMod);

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(mImagesUri.get(i), bmOptions);

                Log.v("Stage 6", "Image path adding");

                float pageWidth = document.getPageSize().getWidth() - (mMarginLeft + mMarginRight);
                float pageHeight = document.getPageSize().getHeight() - (mMarginBottom + mMarginTop);
                if (mImagescaleType.equals(IMAGE_SCALE_TYPE_ASPECT_RATIO))
                    image.scaleToFit(pageWidth, pageHeight);
                else
                    image.scaleAbsolute(pageWidth, pageHeight);

                image.setAbsolutePosition(
                        (documentRect.getWidth() - image.getScaledWidth()) / 2,
                        (documentRect.getHeight() - image.getScaledHeight()) / 2);

                Log.v("Stage 7", "Image Alignments");
                addPageNumber(documentRect, writer);
                document.add(image);

                document.newPage();
            }

            Log.v("Stage 8", "Image adding");

            document.close();

            Log.v("Stage 7", "Document Closed" + mPath);

            Log.v("Stage 8", "Record inserted in database");

        } catch (Exception e) {
            e.printStackTrace();
            mSuccess = false;
        }

        return null;
    }

    private void addPageNumber(Rectangle documentRect, PdfWriter writer) {
        if (mPageNumStyle != null) {
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_BOTTOM,
                    getPhrase(writer, mPageNumStyle, mImagesUri.size()),
                    ((documentRect.getRight() + documentRect.getLeft()) / 2),
                    documentRect.getBottom() + 25, 0);
        }
    }

    @NonNull
    private Phrase getPhrase(PdfWriter writer, String pageNumStyle, int size) {
        Phrase phrase;
        switch (pageNumStyle) {
            case Constants.PG_NUM_STYLE_PAGE_X_OF_N:
                phrase = new Phrase(String.format("Page %d of %d", writer.getPageNumber(), size));
                break;
            case Constants.PG_NUM_STYLE_X_OF_N:
                phrase = new Phrase(String.format("%d of %d", writer.getPageNumber(), size));
                break;
            default:
                phrase = new Phrase(String.format("%d", writer.getPageNumber()));
                break;
        }
        return phrase;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mOnPDFCreatedInterface.onPDFCreated(mSuccess, mPath);
    }
}



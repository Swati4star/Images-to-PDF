package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.customviews.MyCardView;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.interfaces.OnPDFCreatedInterface;
import swati4star.createpdf.model.ImageToPDFOptions;
import swati4star.createpdf.model.TextToPDFOptions;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.PageSizeUtils;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.StringUtils;
import swati4star.createpdf.util.TextToPDFUtils;

import static swati4star.createpdf.util.Constants.DEFAULT_BORDER_WIDTH;
import static swati4star.createpdf.util.Constants.DEFAULT_COMPRESSION;
import static swati4star.createpdf.util.Constants.DEFAULT_IMAGE_BORDER_TEXT;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_COLOR;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_SIZE;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_SIZE_TEXT;
import static swati4star.createpdf.util.Constants.DEFAULT_QUALITY_VALUE;
import static swati4star.createpdf.util.Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT;
import static swati4star.createpdf.util.Constants.READ_WRITE_CAMERA_PERMISSIONS;

import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;

public class QrBarcodeScanFragment extends Fragment implements View.OnClickListener, OnPDFCreatedInterface {
    private final String mTempFileName = "scan_result_temp.txt";

    private SharedPreferences mSharedPreferences;
    private Activity mActivity;
    private MaterialDialog mMaterialDialog;
    private String mPath;
    private FileUtils mFileUtils;
    private Font.FontFamily mFontFamily;
    private int mFontColor;

    @BindView(R.id.scan_qrcode)
    MyCardView scanQrcode;
    @BindView(R.id.scan_barcode)
    MyCardView scanBarcode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_qrcode_barcode, container, false);
        // Initialize variables
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);

        ButterKnife.bind(this, rootview);
        scanQrcode.setOnClickListener(this);
        scanBarcode.setOnClickListener(this);
        mFontFamily = Font.FontFamily.valueOf(mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY));
        mFontColor = mSharedPreferences.getInt(Constants.DEFAULT_FONT_COLOR_TEXT,
                Constants.DEFAULT_FONT_COLOR);
        PageSizeUtils.mPageSize = mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT,
                Constants.DEFAULT_PAGE_SIZE);

        getRuntimePermissions();

        return rootview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result == null || result.getContents() == null)
            StringUtils.getInstance().showSnackbar(mActivity, R.string.scan_cancelled);
        else {
            Toast.makeText(mActivity, " " + result.getContents(), Toast.LENGTH_SHORT).show();

            File mDir = mActivity.getCacheDir();
            File mTempFile = new File(mDir.getPath() + "/" + mTempFileName);
            PrintWriter mWriter;
            try {
                mWriter = new PrintWriter(mTempFile);
                mWriter.print("");
                mWriter.append(result.getContents());
                mWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Uri uri = Uri.fromFile(mTempFile);
            resultToTextPdf(uri);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_qrcode:
                openScanner(IntentIntegrator.QR_CODE_TYPES, R.string.scan_qrcode);
                break;
            case R.id.scan_barcode:
                openScanner(IntentIntegrator.ONE_D_CODE_TYPES, R.string.scan_barcode);
                break;
        }
    }

    /**
     * Open scanner
     *
     * @param scannerType - type (qr code/bar code)
     * @param promptId    - string resource id for prompt
     */
    private void openScanner(Collection<String> scannerType, int promptId) {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        // use forSupportFragment or forFragment method to use fragments instead of activity
        integrator.setDesiredBarcodeFormats(scannerType);
        integrator.setPrompt(mActivity.getString(promptId));
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();
    }

    /**
     * Generate Result to PDF
     *
     * @param uri - uri where text is located
     */
    private void resultToTextPdf(Uri uri) {
        String ext = getString(R.string.pdf_ext);
        mFileUtils.openSaveDialog(null, ext, filename -> createPdf(filename, uri));
    }

    /**
     * function to create PDF
     *
     * @param mFilename name of file to be created.
     * @param uri       - uri where text is located
     */
    private void createPdf(String mFilename, Uri uri) {
        mPath = mSharedPreferences.getString(STORAGE_LOCATION,
                StringUtils.getInstance().getDefaultStorageLocation());
        mPath = mPath + mFilename + mActivity.getString(R.string.pdf_ext);
        try {
            TextToPDFUtils fileUtil = new TextToPDFUtils(mActivity);
            int fontSize = mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, Constants.DEFAULT_FONT_SIZE);
            fileUtil.createPdfFromTextFile(new TextToPDFOptions(mFilename, PageSizeUtils.mPageSize, false,
                    "", uri, fontSize, mFontFamily, mFontColor, DEFAULT_PAGE_COLOR),
                    Constants.textExtension);
            final String finalMPath = mPath;
            StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction,
                            v -> mFileUtils.openFile(finalMPath, FileUtils.FileType.e_PDF)).show();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mFileUtils = new FileUtils(mActivity);
    }

    /**
     * Resets pdf creation related values & show enhancement options
     */
    private void resetValues() {
        ImageToPDFOptions imageToPDFOptions = new ImageToPDFOptions();
        imageToPDFOptions.setBorderWidth(mSharedPreferences.getInt(DEFAULT_IMAGE_BORDER_TEXT,
                DEFAULT_BORDER_WIDTH));
        imageToPDFOptions.setQualityString(
                Integer.toString(mSharedPreferences.getInt(DEFAULT_COMPRESSION,
                        DEFAULT_QUALITY_VALUE)));
        imageToPDFOptions.setPageSize(mSharedPreferences.getString(DEFAULT_PAGE_SIZE_TEXT,
                DEFAULT_PAGE_SIZE));
        imageToPDFOptions.setPasswordProtected(false);
    }

    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, String path) {
        mMaterialDialog.dismiss();
        if (!success) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_folder_not_created);
            return;
        }
        new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
        StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                .setAction(R.string.snackbar_viewAction,
                        v -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();
        mPath = path;
        resetValues();
    }

    /***
     * check runtime permission in Android M
     ***/
    private void getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this,
                READ_WRITE_CAMERA_PERMISSIONS,
                PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
    }
}
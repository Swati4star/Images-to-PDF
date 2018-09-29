package swati4star.createpdf.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.customviews.MyCardView;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.interfaces.OnPDFCreatedInterface;
import swati4star.createpdf.model.ImageToPDFOptions;
import swati4star.createpdf.model.TextToPDFOptions;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.PDFUtils;
import swati4star.createpdf.util.PageSizeUtils;
import swati4star.createpdf.util.StringUtils;

import static swati4star.createpdf.util.Constants.DEFAULT_BORDER_WIDTH;
import static swati4star.createpdf.util.Constants.DEFAULT_COMPRESSION;
import static swati4star.createpdf.util.Constants.DEFAULT_IMAGE_BORDER_TEXT;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_SIZE;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_SIZE_TEXT;
import static swati4star.createpdf.util.Constants.DEFAULT_QUALITY_VALUE;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.DialogUtils.createAnimationDialog;
import static swati4star.createpdf.util.DialogUtils.createOverwriteDialog;
import static swati4star.createpdf.util.StringUtils.getDefaultStorageLocation;
import static swati4star.createpdf.util.StringUtils.getSnackbarwithAction;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class QrBarcodeScanFragment extends Fragment implements View.OnClickListener, OnPDFCreatedInterface {
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private final String mTempFileName = "scan_result_temp.txt";

    private ImageToPDFOptions mPdfOptions;
    private SharedPreferences mSharedPreferences;
    private Activity mActivity;
    private MaterialDialog mMaterialDialog;
    private String mPath;
    private FileUtils mFileUtils;
    private Font.FontFamily mFontFamily;

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
        PageSizeUtils.mPageSize = mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT ,
                Constants.DEFAULT_PAGE_SIZE);

        getRuntimePermissions();

        return rootview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(mActivity, mActivity.getResources().
                        getString(R.string.scan_cancelled), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mActivity, "Scan Result: " +  result.getContents(), Toast.LENGTH_SHORT).show();

                File mDir = mActivity.getCacheDir();
                File mTempFile = new File(mDir.getPath() + "/" + mTempFileName);
                PrintWriter mWriter;
                try {
                    mWriter = new PrintWriter(mTempFile);
                    mWriter.print("");
                    mWriter.append("Result : ").append(result.getContents());
                    mWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.fromFile(mTempFile);
                resultToTextPdf(uri);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_qrcode:
                openCameraForQrcode();
                break;
            case R.id.scan_barcode:
                openCameraForBarcode();
                break;
        }
    }

    /**
     * Open camera for Barcode Scan
     */
    public void openCameraForBarcode() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        // use forSupportFragment or forFragment method to use fragments instead of activity
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt(mActivity.getString(R.string.scan_barcode));
        integrator.setScanningRectangle(1000, 450);
        integrator.setResultDisplayDuration(0); // milliseconds to display result on screen after scan
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();
    }

    /**
     * Open camera for QRCode Scan
     */
    public void openCameraForQrcode() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        // use forSupportFragment or forFragment method to use fragments instead of activity
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt(mActivity.getString(R.string.scan_qrcode));
        integrator.setScanningRectangle(600, 600);
        integrator.setResultDisplayDuration(0); // milliseconds to display result on screen after scan
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();
    }

    /**
     * Generate Result to PDF
     * @param uri - uri where text is located
     */
    private void resultToTextPdf(Uri uri) {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, (dialog, input) -> {
                    if (StringUtils.isEmpty(input)) {
                        showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                    } else {
                        final String inputName = input.toString();
                        if (!mFileUtils.isFileExist(inputName + getString(R.string.pdf_ext))) {
                            createPdf(inputName, uri);
                        } else {
                            MaterialDialog.Builder builder = createOverwriteDialog(mActivity);
                            builder.onPositive((dialog12, which) -> createPdf(inputName, uri))
                                    .onNegative((dialog1, which) -> resultToTextPdf(uri))
                                    .show();
                        }
                    }
                })
                .show();
    }


    /**
     * function to create PDF
     *
     * @param mFilename name of file to be created.
     * @param uri - uri where text is located
     */
    private void createPdf(String mFilename, Uri uri) {
        mPath = mSharedPreferences.getString(STORAGE_LOCATION,
                getDefaultStorageLocation());
        mPath = mPath + mFilename + mActivity.getString(R.string.pdf_ext);
        try {
            PDFUtils fileUtil = new PDFUtils(mActivity);
            int fontSize = mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, Constants.DEFAULT_FONT_SIZE);
            fileUtil.createPdf(new TextToPDFOptions(mFilename, PageSizeUtils.mPageSize, false,
                    "", uri, fontSize, mFontFamily));
            final String finalMPath = mPath;
            getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction, v -> mFileUtils.openFile(finalMPath)).show();
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
        mPdfOptions = new ImageToPDFOptions();
        mPdfOptions.setBorderWidth(mSharedPreferences.getInt(DEFAULT_IMAGE_BORDER_TEXT,
                DEFAULT_BORDER_WIDTH));
        mPdfOptions.setQualityString(
                Integer.toString(mSharedPreferences.getInt(DEFAULT_COMPRESSION,
                        DEFAULT_QUALITY_VALUE)));
        mPdfOptions.setPageSize(mSharedPreferences.getString(DEFAULT_PAGE_SIZE_TEXT,
                DEFAULT_PAGE_SIZE));
        mPdfOptions.setPasswordProtected(false);
    }


    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, String path) {
        mMaterialDialog.dismiss();
        if (!success) {
            showSnackbar(mActivity, R.string.snackbar_folder_not_created);
            return;
        }
        new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
        getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                .setAction(R.string.snackbar_viewAction, v -> mFileUtils.openFile(mPath)).show();
        mPath = path;
        resetValues();
    }

    /**
     * check runtime permission in Android M
     */
    private void getRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
            }
        }
    }
}

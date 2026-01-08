package swati4star.createpdf.fragment;

import static swati4star.createpdf.util.Constants.DEFAULT_BORDER_WIDTH;
import static swati4star.createpdf.util.Constants.DEFAULT_COMPRESSION;
import static swati4star.createpdf.util.Constants.DEFAULT_IMAGE_BORDER_TEXT;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_COLOR;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_SIZE;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_SIZE_TEXT;
import static swati4star.createpdf.util.Constants.DEFAULT_QUALITY_VALUE;
import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

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

public class QrBarcodeScanFragment extends Fragment implements View.OnClickListener, OnPDFCreatedInterface {
    private static final int REQUEST_CODE_FOR_QR_CODE = 1;
    private static final int REQUEST_CODE_FOR_BARCODE = 2;

    // Added further code for new feature
    private static final int REQUEST_CODE_FOR_QR_CODE_IMAGE = 11;

    private static final int REQUEST_CODE_FOR_BARCODE_IMAGE = 11;

    private final String mTempFileName = "scan_result_temp.txt";
    @BindView(R.id.scan_qrcode)
    MyCardView scanQrcode;
    @BindView(R.id.scan_barcode)
    MyCardView scanBarcode;


    private SharedPreferences mSharedPreferences;
    private Activity mActivity;
    private MaterialDialog mMaterialDialog;
    private String mPath;
    private FileUtils mFileUtils;
    private Font.FontFamily mFontFamily;
    private int mFontColor;

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
        return rootview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!(resultCode == Activity.RESULT_OK && data != null && data.getData() != null)) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.scan_cancelled);
            return;
        }

        if (requestCode == REQUEST_CODE_FOR_QR_CODE_IMAGE || requestCode == REQUEST_CODE_FOR_BARCODE_IMAGE) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), data.getData());
                if (bitmap == null) return;

                Result result = decodeBitmap(bitmap, requestCode);
                if (result != null) {
                    writeResultToFile(result.getText());
                } else {
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.null_code);
                }

            } catch (Exception e) {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.null_code);
            }
        } else {
            processCode(requestCode, resultCode, data);
        }
    }


    /**
     * Processes the result of the scanning operation
     * @param requestCode - the instruction code
     * @param resultCode - the code indication a valid scan or not
     * @param data - Intent data
     */
    private void processCode(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result == null || result.getContents() == null) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.scan_cancelled);
        } else {
            Toast.makeText(mActivity, " " + result.getContents(), Toast.LENGTH_SHORT).show();
            writeResultToFile(result.getContents());
        }
    }


    private void writeResultToFile(String resultContent) {
        File mDir = mActivity.getCacheDir();
        File mTempFile = new File(mDir.getPath() + "/" + mTempFileName);
        try (PrintWriter mWriter = new PrintWriter(mTempFile)) {
            mWriter.print("");
            mWriter.append(resultContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.fromFile(mTempFile);
        resultToTextPdf(uri);
    }

    /**
     * Decodes the information from a given Bitmap image containing a barcode or QR code.
     *
     * @param bitmap - The bitmap image to be decoded
     * @param requestCode - instruction code
     * @return
     * @throws NotFoundException
     * @throws ChecksumException
     * @throws FormatException
     */

    private Result decodeBitmap(Bitmap bitmap, int requestCode) throws NotFoundException, ChecksumException, FormatException {
        // reading image
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        Reader reader = new MultiFormatReader();
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);

        // handle diff request code to make sure the code is doing its own stuff
        if (requestCode == REQUEST_CODE_FOR_QR_CODE_IMAGE) {
            hints.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.of(BarcodeFormat.QR_CODE));
        } else if (requestCode == REQUEST_CODE_FOR_BARCODE_IMAGE) {
            // double check with owner if she likes to modify it to support more stuff
            hints.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.of(BarcodeFormat.EAN_13, BarcodeFormat.EAN_8, BarcodeFormat.UPC_A));
        }

        return reader.decode(binaryBitmap, hints);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_qrcode:
                PopupMenu popupMenu = new PopupMenu(mActivity, scanQrcode);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.popup_code_select, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked
                        if (menuItem.getItemId() == R.id.camera) {

                            if (Build.VERSION.SDK_INT >= 23) {
                                if (isCameraPermissionGranted()) {
                                    if (PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
                                        openScanner(IntentIntegrator.QR_CODE_TYPES, R.string.scan_qrcode);
                                    } else {
                                        getRuntimePermissions();
                                    }
                                } else {
                                    requestCameraPermissionForQrCodeScan();
                                }
                            }
                        } else if (menuItem.getItemId() == R.id.image) {
                            selectImage(REQUEST_CODE_FOR_QR_CODE_IMAGE);
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();

                break;
            case R.id.scan_barcode:

                PopupMenu popupMenu2 = new PopupMenu(mActivity, scanBarcode);

                // Inflating popup menu from popup_menu.xml file
                popupMenu2.getMenuInflater().inflate(R.menu.popup_code_select, popupMenu2.getMenu());
                popupMenu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked
                        if (menuItem.getItemId() == R.id.camera) {

                            if (Build.VERSION.SDK_INT >= 23) {
                                if (isCameraPermissionGranted()) {
                                    if (PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
                                        openScanner(IntentIntegrator.ONE_D_CODE_TYPES, R.string.scan_barcode);
                                    } else {
                                        getRuntimePermissions();
                                    }
                                }
                            }
                        } else if (menuItem.getItemId() == R.id.image) {
                            selectImage(REQUEST_CODE_FOR_BARCODE_IMAGE);
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu2.show();

                break;
        }


    }

    /**
     * opens android dialog for selecting an image to retrieve code from.
     * @param requestCode - instruction code
     */

    private void selectImage(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
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

    private boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermissionForQrCodeScan() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_FOR_QR_CODE);
    }

    private void requestCameraPermissionForBarCodeScan() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_FOR_BARCODE);
    }

    private void getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this,
                WRITE_PERMISSIONS,
                REQUEST_CODE_FOR_WRITE_PERMISSION);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if ((requestCode == REQUEST_CODE_FOR_QR_CODE || requestCode == REQUEST_CODE_FOR_BARCODE || requestCode == REQUEST_CODE_FOR_WRITE_PERMISSION) && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == REQUEST_CODE_FOR_QR_CODE) {
                    if (PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
                        openScanner(IntentIntegrator.QR_CODE_TYPES, R.string.scan_qrcode);
                    } else {
                        getRuntimePermissions();
                    }
                } else if (requestCode == REQUEST_CODE_FOR_BARCODE) {
                    if (PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
                        openScanner(IntentIntegrator.ONE_D_CODE_TYPES, R.string.scan_barcode);
                    } else {
                        getRuntimePermissions();
                    }
                }
            } else {
                showPermissionDenyDialog(requestCode);
            }
        }
    }

    private void showPermissionDenyDialog(int requestCode) {
        String scanType, permissionType;
        if (requestCode == REQUEST_CODE_FOR_QR_CODE) {
            scanType = "QR-Code";
            permissionType = "Camera";
        } else if (requestCode == REQUEST_CODE_FOR_BARCODE) {
            scanType = "Bar-Code";
            permissionType = "Camera";
        } else if (requestCode == REQUEST_CODE_FOR_WRITE_PERMISSION) {
            scanType = "and create pdf";
            permissionType = "Storage";
        } else {
            scanType = "unknown";
            permissionType = "unknown";
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Permission Denied")
                    .setMessage(permissionType + " permission is needed to scan " + scanType)
                    .setPositiveButton("Re-try", (dialog, which) -> {
                        if (requestCode == REQUEST_CODE_FOR_QR_CODE) {
                            requestCameraPermissionForQrCodeScan();
                        } else if (requestCode == REQUEST_CODE_FOR_BARCODE) {
                            requestCameraPermissionForBarCodeScan();
                        } else if (requestCode == REQUEST_CODE_FOR_WRITE_PERMISSION) {
                            getRuntimePermissions();
                        }
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    }).show();
        } else if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Permission Denied")
                    .setMessage("You have chosen to never ask the permission again, but " + permissionType + " permission is needed to scan " + scanType)
                    .setPositiveButton("Enable from settings", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    }).show();
        }
    }
}
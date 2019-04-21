package swati4star.createpdf.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.OnPDFCreatedInterface;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.ExcelToPDFAsync;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.RealPathUtil;
import swati4star.createpdf.util.StringUtils;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.DialogUtils.createAnimationDialog;
import static swati4star.createpdf.util.DialogUtils.createOverwriteDialog;
import static swati4star.createpdf.util.StringUtils.getDefaultStorageLocation;
import static swati4star.createpdf.util.StringUtils.getSnackbarwithAction;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class ExceltoPdfFragment extends Fragment implements OnPDFCreatedInterface {
    private Activity mActivity;
    private FileUtils mFileUtils;
    private Uri mExcelFileUri;
    private String mRealPath;
    private String mFileExtension;
    private String mPath;

    @BindView(R.id.tv_excel_file_name_bottom)
    TextView mTextView;
    @BindView(R.id.create_excel_to_pdf)
    MorphingButton mCreateExcelPdf;

    private SharedPreferences mSharedPreferences;
    private MorphButtonUtility mMorphButtonUtility;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private boolean mPermissionGranted = false;
    private boolean mButtonClicked = false;
    private final int mFileSelectCode = 0;
    private MaterialDialog mMaterialDialog;

    public ExceltoPdfFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_excelto_pdf, container,
                false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        ButterKnife.bind(this, rootview);
        mMorphButtonUtility.morphToGrey(mCreateExcelPdf, mMorphButtonUtility.integer());
        mCreateExcelPdf.setEnabled(false);
        return rootview;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mFileUtils = new FileUtils(mActivity);
    }

    @OnClick(R.id.select_excel_file)
    public void selectExcelFile() {
        if (!mButtonClicked) {
            Uri uri = Uri.parse(Environment.getRootDirectory() + "/");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setDataAndType(uri, "*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(
                        Intent.createChooser(intent, String.valueOf(R.string.select_file)),
                        mFileSelectCode);
            } catch (android.content.ActivityNotFoundException ex) {
                showSnackbar(mActivity, R.string.install_file_manager);
            }
            mButtonClicked = true;
        }
    }

    /**
     * This function opens a dialog to enter the file name of
     * the converted file.
     */
    @OnClick(R.id.create_excel_to_pdf)
    public void openExcelToPdf() {
        if (!mPermissionGranted)
            getRuntimePermissions();

        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, (dialog, input) -> {
                    if (StringUtils.isEmpty(input)) {
                        showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                    } else {
                        final String inputName = input.toString();
                        if (!mFileUtils.isFileExist(inputName + getString(R.string.pdf_ext))) {
                            convertToPdf(inputName);
                        } else {
                            MaterialDialog.Builder builder = createOverwriteDialog(mActivity);
                            builder.onPositive((dialog12, which) -> convertToPdf(inputName))
                                    .onNegative((dialog1, which) -> openExcelToPdf())
                                    .show();
                        }
                    }
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mButtonClicked = false;
        switch (requestCode) {
            case mFileSelectCode:
                if (resultCode == RESULT_OK) {
                    mExcelFileUri = data.getData();
                    mRealPath = RealPathUtil.getRealPath(getContext(), mExcelFileUri);
                    showSnackbar(mActivity, getResources().getString(R.string.excel_selected));
                    String fileName = mFileUtils.getFileName(mExcelFileUri);
                    if (fileName != null) {
                        if (fileName.endsWith(Constants.excelExtension))
                            mFileExtension = Constants.excelExtension;
                        else if (fileName.endsWith(Constants.excelWorkbookExtension))
                            mFileExtension = Constants.excelWorkbookExtension;
                        else {
                            showSnackbar(mActivity, R.string.extension_not_supported);
                            return;
                        }
                    }
                    fileName = getResources().getString(R.string.excel_selected)
                            + fileName;
                    mTextView.setText(fileName);
                    mTextView.setVisibility(View.VISIBLE);
                    mCreateExcelPdf.setEnabled(true);
                    mMorphButtonUtility.morphToSquare(mCreateExcelPdf, mMorphButtonUtility.integer());
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length < 1)
            return;
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionGranted = true;
                    openExcelToPdf();
                    showSnackbar(mActivity, R.string.snackbar_permissions_given);
                } else
                    showSnackbar(mActivity, R.string.snackbar_insufficient_permissions);
            }
        }
    }

    /**
     * This function is required to convert the chosen excel file
     * to PDF.
     *
     * @param mFilename
     */
    private void convertToPdf(String mFilename) {
        String mStorePath = mSharedPreferences.getString(STORAGE_LOCATION,
                getDefaultStorageLocation());
        mPath = mStorePath + mFilename + mActivity.getString(R.string.pdf_ext);

        new ExcelToPDFAsync(mRealPath, mPath, ExceltoPdfFragment.this).execute();

    }

    private void getRuntimePermissions() {
        boolean permission = PermissionsUtils.checkRuntimePermissions(mActivity,
                PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission)
            mPermissionGranted = true;
    }

    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, String path) {
        if (mMaterialDialog != null && mMaterialDialog.isShowing())
            mMaterialDialog.dismiss();
        if (!success) {
            showSnackbar(mActivity, R.string.error_occurred);
            mTextView.setVisibility(View.GONE);
            mMorphButtonUtility.morphToGrey(mCreateExcelPdf, mMorphButtonUtility.integer());
            mCreateExcelPdf.setEnabled(false);
            mExcelFileUri = null;
            return;
        }
        getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                .setAction(R.string.snackbar_viewAction, v -> mFileUtils.openFile(mPath))
                .show();
        mTextView.setVisibility(View.GONE);
        mMorphButtonUtility.morphToGrey(mCreateExcelPdf, mMorphButtonUtility.integer());
        mCreateExcelPdf.setEnabled(false);
        mExcelFileUri = null;
    }
}

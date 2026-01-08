package swati4star.createpdf.fragment;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Objects;

import swati4star.createpdf.R;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.databinding.FragmentExceltoPdfBinding;
import swati4star.createpdf.interfaces.BottomSheetPopulate;
import swati4star.createpdf.interfaces.OnItemClickListener;
import swati4star.createpdf.interfaces.OnPDFCreatedInterface;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.util.BottomSheetCallback;
import swati4star.createpdf.util.BottomSheetUtils;
import swati4star.createpdf.util.CommonCodeUtils;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.DefaultTextWatcher;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.ExcelToPDFAsync;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MergePdfEnhancementOptionsUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.RealPathUtil;
import swati4star.createpdf.util.StringUtils;

public class ExceltoPdfFragment extends Fragment implements MergeFilesAdapter.OnClickListener,
        OnPDFCreatedInterface, OnItemClickListener, BottomSheetPopulate {
    private final int mFileSelectCode = 0;
    private Activity mActivity;
    private FileUtils mFileUtils;
    private Uri mExcelFileUri;
    private String mRealPath;
    private String mPath;
    private BottomSheetBehavior mSheetBehavior;
    private StringUtils mStringUtils;
    private SharedPreferences mSharedPreferences;
    private MorphButtonUtility mMorphButtonUtility;
    private BottomSheetUtils mBottomSheetUtils;
    private boolean mButtonClicked = false;
    private MaterialDialog mMaterialDialog;
    private ArrayList<EnhancementOptionsEntity> mEnhancementOptionsEntityArrayList;
    private EnhancementOptionsAdapter mEnhancementOptionsAdapter;
    private boolean mPasswordProtected = false;
    private String mPassword;
    private FragmentExceltoPdfBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentExceltoPdfBinding.inflate(inflater, container, false);
        View rootView = mBinding.getRoot();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        showEnhancementOptions();
        mMorphButtonUtility.morphToGrey(mBinding.createExcelToPdf, mMorphButtonUtility.integer());
        mBinding.createExcelToPdf.setEnabled(false);

        LinearLayout layoutBottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mBinding.bottomSheet.upArrow, isAdded()));
        mBinding.bottomSheet.lottieProgress.setVisibility(View.VISIBLE);
        mBottomSheetUtils.populateBottomSheetWithExcelFiles(this);

        mBinding.selectExcelFile.setOnClickListener(v -> {
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
                    mStringUtils.showSnackbar(mActivity, R.string.install_file_manager);
                }
                mButtonClicked = true;
            }
        });

        mBinding.createExcelToPdf.setOnClickListener(v -> {
            openExcelToPdf();
        });

        mBinding.openPdf.setOnClickListener(v -> {
            mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF);
        });

        return rootView;
    }

    private void showEnhancementOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 2);
        mBinding.enhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        mEnhancementOptionsEntityArrayList = MergePdfEnhancementOptionsUtils.getInstance()
                .getEnhancementOptions(mActivity);
        mEnhancementOptionsAdapter = new EnhancementOptionsAdapter(this, mEnhancementOptionsEntityArrayList);
        mBinding.enhancementOptionsRecycleView.setAdapter(mEnhancementOptionsAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mFileUtils = new FileUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
        mStringUtils = StringUtils.getInstance();
    }

    /**
     * This function opens a dialog to enter the file name of
     * the converted file.
     */
    public void openExcelToPdf() {
        PermissionsUtils.getInstance().checkStoragePermissionAndProceed(getContext(), this::openExcelToPdf_);
    }

    private void openExcelToPdf_() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, (dialog, input) -> {
                    if (mStringUtils.isEmpty(input)) {
                        mStringUtils.showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                    } else {
                        final String inputName = input.toString();
                        if (!mFileUtils.isFileExist(inputName + getString(R.string.pdf_ext))) {
                            convertToPdf(inputName);
                        } else {
                            MaterialDialog.Builder builder = DialogUtils.getInstance().createOverwriteDialog(mActivity);
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
        if (requestCode == mFileSelectCode) {
            if (resultCode == RESULT_OK) {
                mExcelFileUri = data.getData();
                mRealPath = RealPathUtil.getInstance().getRealPath(getContext(), mExcelFileUri);
                processUri();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this,
                WRITE_PERMISSIONS,
                REQUEST_CODE_FOR_WRITE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsUtils.getInstance().handleRequestPermissionsResult(mActivity, grantResults,
                requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION, this::openExcelToPdf_);
    }

    private void processUri() {
        mStringUtils.showSnackbar(mActivity, getResources().getString(R.string.excel_selected));
        String fileName = mFileUtils.getFileName(mExcelFileUri);
        if (fileName != null && !fileName.endsWith(Constants.excelExtension) &&
                !fileName.endsWith(Constants.excelWorkbookExtension)) {
            mStringUtils.showSnackbar(mActivity, R.string.extension_not_supported);
            return;
        }

        fileName = getResources().getString(R.string.excel_selected)
                + fileName;
        mBinding.tvExcelFileNameBottom.setText(fileName);
        mBinding.tvExcelFileNameBottom.setVisibility(View.VISIBLE);
        mBinding.createExcelToPdf.setEnabled(true);
        mBinding.createExcelToPdf.unblockTouch();
        mMorphButtonUtility.morphToSquare(mBinding.createExcelToPdf, mMorphButtonUtility.integer());
        mBinding.openPdf.setVisibility(View.GONE);
    }

    /**
     * This function is required to convert the chosen excel file
     * to PDF.
     *
     * @param mFilename - output PDF name
     */
    private void convertToPdf(String mFilename) {
        String mStorePath = mSharedPreferences.getString(STORAGE_LOCATION,
                mStringUtils.getDefaultStorageLocation());
        mPath = mStorePath + mFilename + mActivity.getString(R.string.pdf_ext);
        new ExcelToPDFAsync(mRealPath, mPath, ExceltoPdfFragment.this, mPasswordProtected, mPassword).execute();

    }

    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, String path) {
        if (mMaterialDialog != null && mMaterialDialog.isShowing())
            mMaterialDialog.dismiss();
        if (!success) {
            mStringUtils.showSnackbar(mActivity, R.string.error_pdf_not_created);
            mBinding.tvExcelFileNameBottom.setVisibility(View.GONE);
            mMorphButtonUtility.morphToGrey(mBinding.createExcelToPdf, mMorphButtonUtility.integer());
            mBinding.createExcelToPdf.setEnabled(false);
            mExcelFileUri = null;
            return;
        }
        mStringUtils.getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                .setAction(R.string.snackbar_viewAction,
                        v -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF))
                .show();
        new DatabaseHelper(mActivity).insertRecord(mPath, mActivity.getString(R.string.created));
        mBinding.tvExcelFileNameBottom.setVisibility(View.GONE);
        mBinding.openPdf.setVisibility(View.VISIBLE);
        mMorphButtonUtility.morphToSuccess(mBinding.createExcelToPdf);
        mBinding.createExcelToPdf.blockTouch();
        mMorphButtonUtility.morphToGrey(mBinding.createExcelToPdf, mMorphButtonUtility.integer());
        mExcelFileUri = null;
        mPasswordProtected = false;
        showEnhancementOptions();
    }

    @Override
    public void onItemClick(int position) {
        if (!mBinding.createExcelToPdf.isEnabled()) {
            mStringUtils.showSnackbar(mActivity, R.string.no_excel_file);
            return;
        }
        if (position == 0) {
            setPassword();
        }
    }

    private void setPassword() {
        MaterialDialog.Builder builder = DialogUtils.getInstance()
                .createCustomDialogWithoutContent(mActivity, R.string.set_password);
        final MaterialDialog dialog = builder
                .customView(R.layout.custom_dialog, true)
                .neutralText(R.string.remove_dialog)
                .build();

        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        final View neutralAction = dialog.getActionButton(DialogAction.NEUTRAL);
        final EditText passwordInput = Objects.requireNonNull(dialog.getCustomView()).findViewById(R.id.password);
        passwordInput.setText(mPassword);
        passwordInput.addTextChangedListener(watcherImpl(positiveAction));
        if (mStringUtils.isNotEmpty(mPassword)) {
            neutralAction.setOnClickListener(v -> {
                mPassword = null;
                setPasswordIcon(R.drawable.baseline_enhanced_encryption_24);
                mPasswordProtected = false;
                dialog.dismiss();
                mStringUtils.showSnackbar(mActivity, R.string.password_remove);
            });
        }
        dialog.show();
        positiveAction.setEnabled(false);
    }

    private DefaultTextWatcher watcherImpl(View positiveAction) {
        return new DefaultTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable input) {
                if (mStringUtils.isEmpty(input)) {
                    mStringUtils.showSnackbar(mActivity, R.string.snackbar_password_cannot_be_blank);
                } else {
                    mPassword = input.toString();
                    mPasswordProtected = true;
                    setPasswordIcon(R.drawable.baseline_done_24);
                }
            }
        };
    }

    private void setPasswordIcon(int drawable) {
        mEnhancementOptionsEntityArrayList.get(0).setImage(mActivity.getResources().getDrawable(drawable));
        mEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(String path) {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mExcelFileUri = Uri.parse("file://" + path);
        mRealPath = path;
        processUri();
    }

    @Override
    public void onPopulate(ArrayList<String> paths) {
        CommonCodeUtils.getInstance().populateUtil(mActivity, paths,
                this, mBinding.bottomSheet.layout, mBinding.bottomSheet.lottieProgress, mBinding.enhancementOptionsRecycleView);
    }

}

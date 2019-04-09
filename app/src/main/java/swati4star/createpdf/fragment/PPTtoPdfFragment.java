package swati4star.createpdf.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.dd.morphingbutton.MorphingButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.RealPathUtil;
import swati4star.createpdf.util.StringUtils;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.DialogUtils.createOverwriteDialog;
import static swati4star.createpdf.util.StringUtils.getDefaultStorageLocation;
import static swati4star.createpdf.util.StringUtils.getSnackbarwithAction;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class PPTtoPdfFragment extends Fragment {

    @BindView(R.id.create_PPTPdf)
    MorphingButton mCreatePdf;
    @BindView(R.id.tv_ppt_file_name)
    TextView mTextView;

    private Activity mActivity;
    private FileUtils mFileUtils;
    private Uri mPPTFileUri;
    private String mRealPath;
    private String mFileExtension;

    private SharedPreferences mSharedPreferences;
    private MorphButtonUtility mMorphButtonUtility;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private boolean mPermissionGranted = false;
    private boolean mButtonClicked = false;
    private final int mFileSelectCode = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_pptto_pdf, container, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        ButterKnife.bind(this, rootview);
        mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
        mCreatePdf.setEnabled(false);
        return rootview;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mFileUtils = new FileUtils(mActivity);
    }

    @OnClick(R.id.select_pptFile)
    public void selectPPTFile() {
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
    @OnClick(R.id.create_PPTPdf)
    public void openPPTtoPdf() {
        if (!mPermissionGranted) {
            getRuntimePermissions();
            return;
        }
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
                                    .onNegative((dialog1, which) -> openPPTtoPdf())
                                    .show();
                        }
                    }
                }).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mButtonClicked = false;
        switch (requestCode) {
            case mFileSelectCode:
                if (resultCode == RESULT_OK) {
                    mPPTFileUri = data.getData();
                    mRealPath = RealPathUtil.getRealPath(getContext(), mPPTFileUri);
                    showSnackbar(mActivity, getResources().getString(R.string.ppt_selected));
                    String fileName = mFileUtils.getFileName(mPPTFileUri);
                    if (fileName != null) {
                        if (fileName.endsWith(Constants.pptExtension))
                            mFileExtension = Constants.pptExtension;
                        else if (fileName.endsWith(Constants.pptExtensionNew))
                            mFileExtension = Constants.pptExtensionNew;
                        else {
                            showSnackbar(mActivity, R.string.extension_not_supported);
                            return;
                        }
                    }
                    fileName = getResources().getString(R.string.ppt_tv_view_text)
                            + fileName;
                    mTextView.setText(fileName);
                    mTextView.setVisibility(View.VISIBLE);
                    mCreatePdf.setEnabled(true);
                    mMorphButtonUtility.morphToSquare(mCreatePdf,
                            mMorphButtonUtility.integer());
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This function converts the PPT to PDF.
     * @param mFilename
     */
    private void convertToPdf(String mFilename) {
        String destinationPath = mSharedPreferences.getString(STORAGE_LOCATION,
                getDefaultStorageLocation());
        String mPath = destinationPath + mFilename
                + mActivity.getString(R.string.pdf_ext);
        AsyncTask.execute(() -> {
            try {
                Presentation presentation = new Presentation(mRealPath);
                presentation.save(mPath, SaveFormat.Pdf);
                getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                        .setAction(R.string.snackbar_viewAction, v -> mFileUtils.openFile(mPath)).show();
                mTextView.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
                showSnackbar(mActivity, R.string.error_occurred);
            } finally {
                mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
                mCreatePdf.setEnabled(false);
                mPPTFileUri = null;
            }
        });

    }

    private void getRuntimePermissions() {
        boolean permission = PermissionsUtils.checkRuntimePermissions(mActivity,
                PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission)
            mPermissionGranted = true;
    }
}

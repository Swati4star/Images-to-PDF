package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.dd.morphingbutton.MorphingButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.RearrangePdfPages;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.interfaces.BottomSheetPopulate;
import swati4star.createpdf.interfaces.OnBackPressedInterface;
import swati4star.createpdf.interfaces.OnPDFCompressedInterface;
import swati4star.createpdf.interfaces.OnPdfReorderedInterface;
import swati4star.createpdf.util.BottomSheetCallback;
import swati4star.createpdf.util.BottomSheetUtils;
import swati4star.createpdf.util.CommonCodeUtils;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PDFEncryptionUtility;
import swati4star.createpdf.util.PDFUtils;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.RealPathUtil;
import swati4star.createpdf.util.StringUtils;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.Constants.ADD_PWD;
import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.COMPRESS_PDF;
import static swati4star.createpdf.util.Constants.REMOVE_PAGES;
import static swati4star.createpdf.util.Constants.REMOVE_PWd;
import static swati4star.createpdf.util.Constants.REORDER_PAGES;
import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.RESULT;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;
import static swati4star.createpdf.util.FileInfoUtils.getFormattedSize;

public class RemovePagesFragment extends Fragment implements MergeFilesAdapter.OnClickListener,
        OnPDFCompressedInterface, BottomSheetPopulate, OnBackPressedInterface, OnPdfReorderedInterface {

    private Activity mActivity;
    private String mPath;
    private MorphButtonUtility mMorphButtonUtility;
    private FileUtils mFileUtils;
    private BottomSheetUtils mBottomSheetUtils;
    private PDFUtils mPDFUtils;
    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    private static final int INTENT_REQUEST_REARRANGE_PDF = 11;
    private String mOperation;
    private MaterialDialog mMaterialDialog;
    private BottomSheetBehavior mSheetBehavior;

    @BindView(R.id.lottie_progress)
    LottieAnimationView mLottieProgress;
    @BindView(R.id.selectFile)
    MorphingButton selectFileButton;
    @BindView(R.id.pdfCreate)
    MorphingButton createPdf;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    @BindView(R.id.downArrow)
    ImageView mDownArrow;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    @BindView(R.id.pages)
    EditText pagesInput;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    @BindView(R.id.infoText)
    TextView mInfoText;
    @BindView(R.id.compressionInfoText)
    TextView mCompressionInfoText;
    @BindView(R.id.view_pdf)
    Button mViewPdf;
    private Uri mUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_remove_pages, container, false);
        ButterKnife.bind(this, rootview);
        mSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, isAdded()));
        mOperation = getArguments().getString(BUNDLE_DATA);
        mLottieProgress.setVisibility(View.VISIBLE);
        mBottomSheetUtils.populateBottomSheetWithPDFs(this);
        getRuntimePermissions();

        resetValues();
        return rootview;
    }

    @OnClick(R.id.viewFiles)
    void onViewFilesClick(View view) {
        mBottomSheetUtils.showHideSheet(mSheetBehavior);
    }

    /**
     * Displays file chooser intent
     */
    @OnClick(R.id.selectFile)
    public void showFileChooser() {
        startActivityForResult(mFileUtils.getFileChooser(),
                INTENT_REQUEST_PICKFILE_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) throws NullPointerException {
        if (data == null || resultCode != RESULT_OK)
            return;
        if (requestCode == INTENT_REQUEST_PICKFILE_CODE) {
            mUri = data.getData();
            //Getting Absolute Path
            String path = RealPathUtil.getInstance().getRealPath(getContext(), data.getData());
            setTextAndActivateButtons(path);
        } else if (requestCode == INTENT_REQUEST_REARRANGE_PDF) {
            String pages = data.getStringExtra(RESULT);
            boolean sameFile = data.getBooleanExtra("SameFile", false);

            if (mPath == null)
                return;
            String outputPath;

            outputPath = setPath(pages);
            if (mPDFUtils.isPDFEncrypted(mPath)) {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.encrypted_pdf);
                return;
            }

            if (!sameFile) {
                if (mPDFUtils.reorderRemovePDF(mPath, outputPath, pages)) {
                    viewPdfButton(outputPath);
                }
            } else {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.file_order);
            }
            resetValues();
        }
    }

    /**
     * This method returns new  pdf name.
     * @param pages The pages String that contains page numbers
     * @return Returns the new pdf name
     */
    private String setPath(String pages) {
        String outputPath;
        if (pages.length() > 50) {
            outputPath = mPath.replace(mActivity.getString(R.string.pdf_ext),
                    "_edited" + mActivity.getString(R.string.pdf_ext));
        } else {
            outputPath = mPath.replace(mActivity.getString(R.string.pdf_ext),
                    "_edited" + pages + mActivity.getString(R.string.pdf_ext));

        }
        return outputPath;
    }

    @OnClick(R.id.pdfCreate)
    public void parse() {
        StringUtils.getInstance().hideKeyboard(mActivity);
        if (mOperation.equals(COMPRESS_PDF)) {
            compressPDF();
            return;
        }

        PDFEncryptionUtility pdfEncryptionUtility = new PDFEncryptionUtility(mActivity);
        if (mOperation.equals(ADD_PWD)) {
            if (!mPDFUtils.isPDFEncrypted(mPath)) {
                pdfEncryptionUtility.setPassword(mPath, null);
            } else {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.encrypted_pdf);
            }
            return;
        }

        if (mOperation.equals(REMOVE_PWd)) {
            if (mPDFUtils.isPDFEncrypted(mPath)) {
                pdfEncryptionUtility.removePassword(mPath, null);
            } else {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.not_encrypted);
            }
            return;
        }

        mPDFUtils.reorderPdfPages(mUri, mPath, this);
    }

    private void compressPDF() {
        String input = pagesInput.getText().toString();
        int check;
        try {
            check = Integer.parseInt(input);
            if (check > 100 || check <= 0 || mPath == null) {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
            } else {
                String outputPath = mPath.replace(mActivity.getString(R.string.pdf_ext),
                        "_edited" + check + mActivity.getString(R.string.pdf_ext));
                mPDFUtils.compressPDF(mPath, outputPath, 100 - check, this);
            }
        } catch (NumberFormatException e) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
        }
    }

    private void resetValues() {
        mPath = null;
        pagesInput.setText(null);
        mMorphButtonUtility.initializeButton(selectFileButton, createPdf);
        switch (mOperation) {
            case REORDER_PAGES:
            case REMOVE_PAGES:
            case ADD_PWD:
            case REMOVE_PWd:
                mInfoText.setVisibility(View.GONE);
                pagesInput.setVisibility(View.GONE);
                break;
            case COMPRESS_PDF:
                mInfoText.setText(R.string.compress_pdf_prompt);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mPDFUtils = new PDFUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
    }

    @Override
    public void onItemClick(String path) {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setTextAndActivateButtons(path);
    }

    private void setTextAndActivateButtons(String path) {
        if (path == null) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.file_access_error);
            resetValues();
            return;
        }
        mPath = path;
        mCompressionInfoText.setVisibility(View.GONE);
        mViewPdf.setVisibility(View.GONE);
        mMorphButtonUtility.setTextAndActivateButtons(path,
                selectFileButton, createPdf);
    }

    @Override
    public void pdfCompressionStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void pdfCompressionEnded(String path, Boolean success) {
        mMaterialDialog.dismiss();
        if (success && path != null && mPath != null) {
            StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction,
                            v -> mFileUtils.openFile(path, FileUtils.FileType.e_PDF)).show();
            new DatabaseHelper(mActivity).insertRecord(path,
                    mActivity.getString(R.string.created));
            File input = new File(mPath);
            File output = new File(path);
            viewPdfButton(path);
            mCompressionInfoText.setVisibility(View.VISIBLE);
            mCompressionInfoText.setText(String.format(mActivity.getString(R.string.compress_info),
                    getFormattedSize(input),
                    getFormattedSize(output)));
        } else {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.encrypted_pdf);
        }
        resetValues();
    }

    private void viewPdfButton(String path) {
        mViewPdf.setVisibility(View.VISIBLE);
        mViewPdf.setOnClickListener(v -> mFileUtils.openFile(path, FileUtils.FileType.e_PDF));
    }

    @Override
    public void onPopulate(ArrayList<String> paths) {
        CommonCodeUtils.getInstance().populateUtil(mActivity, paths,
                this, mLayout, mLottieProgress, mRecyclerViewFiles);
    }

    @Override
    public void closeBottomSheet() {
        CommonCodeUtils.getInstance().closeBottomSheetUtil(mSheetBehavior);
    }

    @Override
    public boolean checkSheetBehaviour() {
        return CommonCodeUtils.getInstance().checkSheetBehaviourUtil(mSheetBehavior);
    }

    @Override
    public void onPdfReorderStarted() {
        mMaterialDialog = DialogUtils
                .getInstance()
                .createCustomAnimationDialog(mActivity, mActivity.getString(R.string.reordering_pages_dialog));
        mMaterialDialog.show();
    }

    @Override
    public void onPdfReorderCompleted(List<Bitmap> bitmaps) {
        mMaterialDialog.dismiss();
        RearrangePdfPages.mImages = new ArrayList<>(bitmaps);
        bitmaps.clear(); //releasing memory
        startActivityForResult(RearrangePdfPages.getStartIntent(mActivity),
                INTENT_REQUEST_REARRANGE_PDF);
    }

    @Override
    public void onPdfReorderFailed() {
        mMaterialDialog.dismiss();
        StringUtils.getInstance().showSnackbar(mActivity, R.string.file_access_error);
    }

    /***
     * check runtime permissions for storage and camera
     ***/
    private void getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this,
                    WRITE_PERMISSIONS,
                    REQUEST_CODE_FOR_WRITE_PERMISSION);
    }
}
package swati4star.createpdf.fragment;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.PDF_TO_IMAGES;
import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.activity.ImagesPreviewActivity;
import swati4star.createpdf.adapter.ExtractImagesAdapter;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.databinding.FragmentPdfToImageBinding;
import swati4star.createpdf.interfaces.BottomSheetPopulate;
import swati4star.createpdf.interfaces.ExtractImagesListener;
import swati4star.createpdf.interfaces.OnBackPressedInterface;
import swati4star.createpdf.util.BottomSheetCallback;
import swati4star.createpdf.util.BottomSheetUtils;
import swati4star.createpdf.util.CommonCodeUtils;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.ExtractImages;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PDFUtils;
import swati4star.createpdf.util.PdfToImages;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.RealPathUtil;
import swati4star.createpdf.util.StringUtils;

public class PdfToImageFragment extends Fragment implements BottomSheetPopulate, MergeFilesAdapter.OnClickListener,
        ExtractImagesListener, ExtractImagesAdapter.OnFileItemClickedListener, OnBackPressedInterface {

    private static final int INTENT_REQUEST_PICK_FILE_CODE = 10;
    private Activity mActivity;
    private String mPath;
    private Uri mUri;
    private MorphButtonUtility mMorphButtonUtility;
    private FileUtils mFileUtils;
    private BottomSheetBehavior mSheetBehavior;
    private BottomSheetUtils mBottomSheetUtils;
    private ArrayList<String> mOutputFilePaths;
    private ArrayList<String> mOutputFileUris;
    private MaterialDialog mMaterialDialog;
    private String mOperation;
    private Context mContext;
    private PDFUtils mPDFUtils;
    private String[] mInputPassword;
    private FragmentPdfToImageBinding mBinding;

    /**
     * inflates the layout for the fragment
     *
     * @param inflater           reference to inflater object
     * @param container          parent for the inflated view
     * @param savedInstanceState bundle with saved data, if any
     * @return inflated view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentPdfToImageBinding.inflate(inflater, container, false);
        View rootView = mBinding.getRoot();
        mOperation = getArguments().getString(BUNDLE_DATA);
        LinearLayout layoutBottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mBinding.bottomSheet.upArrow, isAdded()));
        mBinding.bottomSheet.lottieProgress.setVisibility(View.VISIBLE);
        mBottomSheetUtils.populateBottomSheetWithPDFs(this);
        resetView();
        getRuntimePermissions();

        mBinding.viewImagesInGallery.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri imagesUri = Uri.parse("content:///storage/emulated/0/PDFfiles/");
            intent.setDataAndType(imagesUri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });

        mBinding.shareImages.setOnClickListener(v -> {
            if (mOutputFilePaths != null) {
                ArrayList<File> fileArrayList = new ArrayList<>();
                for (String path : mOutputFilePaths) {
                    fileArrayList.add(new File(path));
                }
                mFileUtils.shareMultipleFiles(fileArrayList);
            }
        });

        mBinding.bottomSheet.viewFiles.setOnClickListener(v -> {
            mBottomSheetUtils.showHideSheet(mSheetBehavior);
        });

        mBinding.viewImages.setOnClickListener(v -> {
            mActivity.startActivity(ImagesPreviewActivity.getStartIntent(mActivity, mOutputFileUris));
        });

        mBinding.selectFile.setOnClickListener(v -> {
            startActivityForResult(mFileUtils.getFileChooser(),
                    INTENT_REQUEST_PICK_FILE_CODE);
        });

        mBinding.createImages.setOnClickListener(v -> {
            if (mPDFUtils.isPDFEncrypted(mPath)) {
                mInputPassword = new String[1];
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.enter_password)
                        .content(R.string.decrypt_protected_file)
                        .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .input(null, null, (dialog, input) -> {
                            if (StringUtils.getInstance().isEmpty(input)) {
                                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                            } else {
                                final String inputName = input.toString();
                                mInputPassword[0] = inputName;
                                pdfToImage(mInputPassword);
                            }
                        })
                        .show();
            } else {
                pdfToImage(mInputPassword);
            }
        });

        return rootView;
    }


    /**
     * receives intent response for selecting a pdf file
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) throws NullPointerException {
        if (data == null || resultCode != RESULT_OK || data.getData() == null)
            return;
        if (requestCode == INTENT_REQUEST_PICK_FILE_CODE) {
            mUri = data.getData();
            //Getting Absolute Path
            String path = RealPathUtil.getInstance().getRealPath(getContext(), data.getData());
            setTextAndActivateButtons(path);

        }
    }

    /**
     * Thia method handles the call to the Async process of conversion
     * from PDF to Image.
     *
     * @param mInputPassword - the password if the file is encrypted.
     */
    private void pdfToImage(String[] mInputPassword) {
        if (mOperation.equals(PDF_TO_IMAGES)) {
            new PdfToImages(mContext, mInputPassword, mPath, mUri, this)
                    .execute();
        } else
            new ExtractImages(mPath, this).execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
        mContext = context;
        mPDFUtils = new PDFUtils(mActivity);
    }

    /**
     * handles choosing a file from bottom sheet list
     *
     * @param path path of the file on the device
     */
    @Override
    public void onItemClick(String path) {
        mUri = null;
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setTextAndActivateButtons(path);
    }

    /**
     * handles text and button states
     *
     * @param path path of the file on the device
     */
    private void setTextAndActivateButtons(String path) {
        if (path == null) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.error_path_not_found);
            resetView();
            return;
        }
        mBinding.createdImages.setVisibility(View.GONE);
        mBinding.options.setVisibility(View.GONE);
        mBinding.pdfToImagesText.setVisibility(View.GONE);
        mPath = path;
        mMorphButtonUtility.setTextAndActivateButtons(path,
                mBinding.selectFile, mBinding.createImages);
    }

    /**
     * handles opening a generated image for the given pdf
     *
     * @param path path of the file on the device
     */
    @Override
    public void onFileItemClick(String path) {
        mFileUtils.openImage(path);
    }

    /**
     * initializes interactive views
     */
    @Override
    public void resetView() {
        mPath = null;
        mMorphButtonUtility.initializeButton(mBinding.selectFile, mBinding.createImages);
    }

    /**
     * displays progress indicator
     */
    @Override
    public void extractionStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    /**
     * updates recycler view list items based with the generated images
     *
     * @param imageCount      number of generated images
     * @param outputFilePaths path for each generated image
     */
    @Override
    public void updateView(int imageCount, ArrayList<String> outputFilePaths, ArrayList<String> outputFileUris) {

        mMaterialDialog.dismiss();
        resetView();
        mOutputFilePaths = outputFilePaths;
        mOutputFileUris = outputFileUris;

        CommonCodeUtils.getInstance().updateView(mActivity, imageCount, outputFilePaths,
                mBinding.pdfToImagesText, mBinding.options, mBinding.createdImages, this);
    }

    /**
     * populates bottom sheet list with pdf files
     *
     * @param paths paths for pdf files on the device
     */
    @Override
    public void onPopulate(ArrayList<String> paths) {
        CommonCodeUtils.getInstance().populateUtil(mActivity, paths,
                this, mBinding.bottomSheet.layout,
                mBinding.bottomSheet.lottieProgress, mBinding.bottomSheet.recyclerViewFiles);
    }

    @Override
    public void closeBottomSheet() {
        CommonCodeUtils.getInstance().closeBottomSheetUtil(mSheetBehavior);
    }

    @Override
    public boolean checkSheetBehaviour() {
        return CommonCodeUtils.getInstance().checkSheetBehaviourUtil(mSheetBehavior);
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
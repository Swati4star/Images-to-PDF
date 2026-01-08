package swati4star.createpdf.fragment;

import static swati4star.createpdf.util.Constants.ADD_IMAGES;
import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.databinding.FragmentAddImagesBinding;
import swati4star.createpdf.interfaces.BottomSheetPopulate;
import swati4star.createpdf.interfaces.OnBackPressedInterface;
import swati4star.createpdf.util.BottomSheetCallback;
import swati4star.createpdf.util.BottomSheetUtils;
import swati4star.createpdf.util.CommonCodeUtils;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.FileUriUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PDFUtils;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.StringUtils;

public class AddImagesFragment extends Fragment implements BottomSheetPopulate,
        MergeFilesAdapter.OnClickListener, OnBackPressedInterface {

    private static final int INTENT_REQUEST_PICK_FILE_CODE = 10;
    private static final ArrayList<String> mImagesUri = new ArrayList<>();
    private Activity mActivity;
    private String mPath;
    private MorphButtonUtility mMorphButtonUtility;
    private FileUtils mFileUtils;
    private BottomSheetUtils mBottomSheetUtils;
    private PDFUtils mPDFUtils;
    private String mOperation;
    private BottomSheetBehavior mSheetBehavior;
    private FragmentAddImagesBinding mBinding;
    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentAddImagesBinding.inflate(inflater, container, false);
        View rootView = mBinding.getRoot();
        LinearLayout layoutBottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mBinding.bottomSheet.upArrow, isAdded()));
        mOperation = getArguments().getString(BUNDLE_DATA);
        mBinding.bottomSheet.lottieProgress.setVisibility(View.VISIBLE);
        mBottomSheetUtils.populateBottomSheetWithPDFs(this);

        pickMultipleMedia = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(100), uris -> {
            mImagesUri.clear();
            if (!uris.isEmpty()) {
                ArrayList<String> stringUris = new ArrayList<>();
                for (Uri uri : uris) {
                    stringUris.add(uri.toString());
                }
                mImagesUri.addAll(stringUris);
                mBinding.tvNoOfImages.setText(String.format(mActivity.getResources()
                        .getString(R.string.images_selected), mImagesUri.size()));
                mBinding.tvNoOfImages.setVisibility(View.VISIBLE);
                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_images_added);
                mBinding.pdfCreate.setEnabled(true);
            } else {
                mBinding.tvNoOfImages.setVisibility(View.GONE);
            }

            mMorphButtonUtility.morphToSquare(mBinding.pdfCreate, mMorphButtonUtility.integer());
        });

        resetValues();

        mBinding.selectFile.setOnClickListener(v -> {
            startActivityForResult(mFileUtils.getFileChooser(),
                    INTENT_REQUEST_PICK_FILE_CODE);
        });

        mBinding.bottomSheet.viewFiles.setOnClickListener(v -> {
            mBottomSheetUtils.showHideSheet(mSheetBehavior);
        });

        mBinding.addImages.setOnClickListener(v -> {
            PermissionsUtils.getInstance().checkStoragePermissionAndProceed(getContext(), this::selectImages);
        });

        mBinding.pdfCreate.setOnClickListener(v -> {
            StringUtils.getInstance().hideKeyboard(mActivity);
            if (mOperation.equals(ADD_IMAGES))
                getFileName();
        });

        return rootView;
    }

    /**
     * Called after Activity is called
     *
     * @param requestCode REQUEST Code for opening Activity
     * @param resultCode  result code of the process
     * @param data        Data of the image selected
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        switch (requestCode) {

            case INTENT_REQUEST_PICK_FILE_CODE:
                setTextAndActivateButtons(FileUriUtils.getInstance().getFilePath(data.getData()));
                break;
        }
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
                requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION, this::selectImages);
    }

    private void getFileName() {
        MaterialDialog.Builder builder = DialogUtils.getInstance().createCustomDialog(mActivity,
                R.string.creating_pdf, R.string.enter_file_name);
        builder.input(getString(R.string.example), null, (dialog, input) -> {
            if (StringUtils.getInstance().isEmpty(input)) {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
            } else {
                final String filename = input.toString();
                FileUtils utils = new FileUtils(mActivity);
                if (!utils.isFileExist(filename + getString(R.string.pdf_ext))) {
                    this.addImagesToPdf(filename);
                } else {
                    MaterialDialog.Builder builder2 = DialogUtils.getInstance().createOverwriteDialog(mActivity);
                    builder2.onPositive((dialog2, which) ->
                            this.addImagesToPdf(filename)).onNegative((dialog1, which) -> getFileName()).show();
                }
            }
        }).show();
    }

    /**
     * Adds images to existing PDF
     *
     * @param output - path of output PDF
     */
    private void addImagesToPdf(String output) {
        int index = mPath.lastIndexOf("/");
        String outputPath = mPath.replace(mPath.substring(index + 1),
                output + mActivity.getString(R.string.pdf_ext));

        if (mImagesUri.size() > 0) {
            MaterialDialog progressDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
            progressDialog.show();
            mPDFUtils.addImagesToPdf(mPath, outputPath, mImagesUri);
            mMorphButtonUtility.morphToSuccess(mBinding.pdfCreate);
            resetValues();
            progressDialog.dismiss();
        } else {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.no_images_selected);
        }
    }

    private void resetValues() {
        mPath = null;
        mImagesUri.clear();
        mMorphButtonUtility.initializeButton(mBinding.selectFile, mBinding.pdfCreate);
        mMorphButtonUtility.initializeButton(mBinding.selectFile, mBinding.addImages);
        mBinding.tvNoOfImages.setVisibility(View.GONE);
    }

    /**
     * Opens PickVisualMedia activity to select Images
     */
    private void selectImages() {
        pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
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
        mPath = path;
        mMorphButtonUtility.setTextAndActivateButtons(path,
                mBinding.selectFile, mBinding.addImages);

    }

    @Override
    public void onPopulate(ArrayList<String> paths) {
        CommonCodeUtils.getInstance().populateUtil(mActivity, paths,
                this, mBinding.bottomSheet.layout, mBinding.bottomSheet.lottieProgress, mBinding.bottomSheet.recyclerViewFiles);
    }

    @Override
    public void closeBottomSheet() {
        CommonCodeUtils.getInstance().closeBottomSheetUtil(mSheetBehavior);
    }

    @Override
    public boolean checkSheetBehaviour() {
        return CommonCodeUtils.getInstance().checkSheetBehaviourUtil(mSheetBehavior);
    }
}

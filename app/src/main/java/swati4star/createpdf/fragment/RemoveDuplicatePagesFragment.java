package swati4star.createpdf.fragment;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.adapter.FilesListAdapter;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.databinding.FragmentRemoveDuplicatePagesBinding;
import swati4star.createpdf.interfaces.BottomSheetPopulate;
import swati4star.createpdf.interfaces.OnBackPressedInterface;
import swati4star.createpdf.interfaces.OnPDFCreatedInterface;
import swati4star.createpdf.util.BottomSheetCallback;
import swati4star.createpdf.util.BottomSheetUtils;
import swati4star.createpdf.util.CommonCodeUtils;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.RealPathUtil;
import swati4star.createpdf.util.RemoveDuplicates;
import swati4star.createpdf.util.StringUtils;

public class RemoveDuplicatePagesFragment extends Fragment
        implements MergeFilesAdapter.OnClickListener, FilesListAdapter.OnFileItemClickedListener,
        BottomSheetPopulate, OnPDFCreatedInterface, OnBackPressedInterface {

    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    BottomSheetBehavior mSheetBehavior;
    private Activity mActivity;
    private String mPath;
    private MorphButtonUtility mMorphButtonUtility;
    private FileUtils mFileUtils;
    private BottomSheetUtils mBottomSheetUtils;
    private MaterialDialog mMaterialDialog;

    private FragmentRemoveDuplicatePagesBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentRemoveDuplicatePagesBinding.inflate(inflater, container, false);
        View rootview = mBinding.getRoot();
        LinearLayout layoutBottomSheet = rootview.findViewById(R.id.bottom_sheet);
        mSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mBinding.bottomSheet.upArrow, isAdded()));
        mBinding.bottomSheet.lottieProgress.setVisibility(View.VISIBLE);
        mBottomSheetUtils.populateBottomSheetWithPDFs(this);
        getRuntimePermissions();
        resetValues();

        mBinding.bottomSheet.viewFiles.setOnClickListener(v -> {
            mBottomSheetUtils.showHideSheet(mSheetBehavior);
        });

        mBinding.selectFile.setOnClickListener(v -> {
            startActivityForResult(mFileUtils.getFileChooser(), INTENT_REQUEST_PICKFILE_CODE);
        });

        mBinding.remove.setOnClickListener(v -> {
            new RemoveDuplicates(mPath, this).execute();
        });

        return rootview;
    }


    // Refactor onActivityResult() method to handle possible NullPointerExceptions
    // when accessing data.getData(). Added try-catch blocks to handle exceptions
    // in a better way(imo) to prevent app crashes.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            return;
        }

        if (requestCode == INTENT_REQUEST_PICKFILE_CODE) {
            try {
                // Attempt to get the real path of the selected file and update the UI
                String path = RealPathUtil.getInstance().getRealPath(getContext(), data.getData());
                setTextAndActivateButtons(path);
            } catch (NullPointerException e) {
                // If a NullPointerException occurs, log it to the console and continue
                e.printStackTrace();
            }
        }
    }

    private void resetValues() {
        mPath = null;
        mMorphButtonUtility.initializeButton(mBinding.selectFile, mBinding.remove);
    }

    private void setTextAndActivateButtons(String path) {
        mPath = path;
        mMorphButtonUtility.setTextAndActivateButtons(path, mBinding.selectFile, mBinding.remove);
    }

    @Override
    public void onPopulate(ArrayList<String> paths) {
        CommonCodeUtils.getInstance().populateUtil(mActivity,
                paths, this, mBinding.bottomSheet.layout,
                mBinding.bottomSheet.lottieProgress,
                mBinding.bottomSheet.recyclerViewFiles);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
    }

    @Override
    public void onItemClick(String path) {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setTextAndActivateButtons(path);
    }

    @Override
    public void onFileItemClick(String path) {
        mFileUtils.openFile(path, FileUtils.FileType.e_PDF);
    }

    private void viewPdfButton(String path) {
        mBinding.viewPdf.setVisibility(View.VISIBLE);
        mBinding.viewPdf.setOnClickListener(v -> mFileUtils.openFile(path, FileUtils.FileType.e_PDF));
    }

    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean isNewPdfCreated, String path) {
        mMaterialDialog.dismiss();
        if (!isNewPdfCreated) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_no_duplicate_pdf);
            // Hiding View PDF button
            mBinding.viewPdf.setVisibility(View.GONE);
            return;
        }
        new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
        StringUtils.getInstance().getSnackbarwithAction(mActivity,
                        R.string.snackbar_duplicate_removed)
                .setAction(R.string.snackbar_viewAction, v -> mFileUtils.openFile(path, FileUtils.FileType.e_PDF)).show();
        viewPdfButton(path);
        resetValues();
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
        PermissionsUtils.getInstance().requestRuntimePermissions(this, WRITE_PERMISSIONS, REQUEST_CODE_FOR_WRITE_PERMISSION);
    }
}

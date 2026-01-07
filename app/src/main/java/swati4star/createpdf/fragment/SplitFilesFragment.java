package swati4star.createpdf.fragment;

import static android.app.Activity.RESULT_OK;
import static android.os.ParcelFileDescriptor.MODE_READ_ONLY;
import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.adapter.FilesListAdapter;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.databinding.FragmentSplitFilesBinding;
import swati4star.createpdf.interfaces.BottomSheetPopulate;
import swati4star.createpdf.interfaces.OnBackPressedInterface;
import swati4star.createpdf.util.BottomSheetCallback;
import swati4star.createpdf.util.BottomSheetUtils;
import swati4star.createpdf.util.CommonCodeUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.RealPathUtil;
import swati4star.createpdf.util.SplitPDFUtils;
import swati4star.createpdf.util.StringUtils;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

public class SplitFilesFragment extends Fragment implements MergeFilesAdapter.OnClickListener,
        FilesListAdapter.OnFileItemClickedListener, BottomSheetPopulate, OnBackPressedInterface {

    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    private Activity mActivity;
    private String mPath;
    private MorphButtonUtility mMorphButtonUtility;
    private FileUtils mFileUtils;
    private SplitPDFUtils mSplitPDFUtils;
    private BottomSheetUtils mBottomSheetUtils;
    private BottomSheetBehavior mSheetBehavior;
    private FragmentSplitFilesBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSplitFilesBinding.inflate(inflater, container, false);
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

        mBinding.selectFile.setOnClickListener( v -> {
            startActivityForResult(mFileUtils.getFileChooser(),
                    INTENT_REQUEST_PICKFILE_CODE);
        });

        mBinding.splitFiles.setOnClickListener(v -> {
            StringUtils.getInstance().hideKeyboard(mActivity);

            ArrayList<String> outputFilePaths = mSplitPDFUtils.splitPDFByConfig(mPath,
                    mBinding.splitConfig.getText().toString());
            int numberOfPages = outputFilePaths.size();
            if (numberOfPages == 0) {
                return;
            }
            String output = String.format(mActivity.getString(R.string.split_success), numberOfPages);
            StringUtils.getInstance().showSnackbar(mActivity, output);
            mBinding.splitfilesText.setVisibility(View.VISIBLE);
            mBinding.splitfilesText.setText(output);

            FilesListAdapter splitFilesAdapter = new FilesListAdapter(mActivity, outputFilePaths, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
            mBinding.splittedFiles.setVisibility(View.VISIBLE);
            mBinding.splittedFiles.setLayoutManager(mLayoutManager);
            mBinding.splittedFiles.setAdapter(splitFilesAdapter);
            mBinding.splittedFiles.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
            resetValues();
        });

        return rootview;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) throws NullPointerException {
        if (data == null || resultCode != RESULT_OK || data.getData() == null)
            return;
        if (requestCode == INTENT_REQUEST_PICKFILE_CODE) {
            //Getting Absolute Path
            String path = RealPathUtil.getInstance().getRealPath(getContext(), data.getData());
            setTextAndActivateButtons(path);
        }
    }

    private void resetValues() {
        mPath = null;
        mMorphButtonUtility.initializeButton(mBinding.selectFile, mBinding.splitFiles);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mSplitPDFUtils = new SplitPDFUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
    }

    @Override
    public void onItemClick(String path) {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setTextAndActivateButtons(path);
    }

    private void setTextAndActivateButtons(String path) {
        mBinding.splittedFiles.setVisibility(View.GONE);
        mBinding.splitfilesText.setVisibility(View.GONE);
        mPath = path;
        String defaultSplitConfig = getDefaultSplitConfig(mPath);
        if (defaultSplitConfig != null) {
            mMorphButtonUtility.setTextAndActivateButtons(path,
                    mBinding.selectFile, mBinding.splitFiles);
            mBinding.splitConfig.setVisibility(View.VISIBLE);
            mBinding.splitConfig.setText(defaultSplitConfig);
        } else
            resetValues();
    }

    /**
     * Gets the total number of pages in the
     * selected PDF and returns them in
     * default format for single page pdfs
     * (1,2,3,4,5,)
     */
    private String getDefaultSplitConfig(String mPath) {
        StringBuilder splitConfig = new StringBuilder();
        ParcelFileDescriptor fileDescriptor = null;
        try {
            if (mPath != null)
                fileDescriptor = ParcelFileDescriptor.open(new File(mPath), MODE_READ_ONLY);
            if (fileDescriptor != null) {
                PdfRenderer renderer = new PdfRenderer(fileDescriptor);
                final int pageCount = renderer.getPageCount();
                for (int i = 1; i <= pageCount; i++) {
                    splitConfig.append(i).append(",");
                }
            }
        } catch (Exception er) {
            er.printStackTrace();
            StringUtils.getInstance().showSnackbar(mActivity, R.string.encrypted_pdf);
            return null;
        }
        return splitConfig.toString();
    }

    @Override
    public void onFileItemClick(String path) {
        mFileUtils.openFile(path, FileUtils.FileType.e_PDF);
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

    /***
     * check runtime permissions for storage and camera
     ***/
    private void getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this,
                WRITE_PERMISSIONS,
                REQUEST_CODE_FOR_WRITE_PERMISSION);
    }
}
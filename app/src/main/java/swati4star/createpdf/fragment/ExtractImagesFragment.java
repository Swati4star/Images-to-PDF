package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.ImagesPreviewActivity;
import swati4star.createpdf.adapter.ExtractImagesAdapter;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.interfaces.ExtractImagesListener;
import swati4star.createpdf.util.BottomSheetCallback;
import swati4star.createpdf.util.BottomSheetUtils;
import swati4star.createpdf.util.ExtractImages;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.DialogUtils.createAnimationDialog;
import static swati4star.createpdf.util.FileUriUtils.getFilePath;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class ExtractImagesFragment extends Fragment implements MergeFilesAdapter.OnClickListener,
        ExtractImagesAdapter.OnFileItemClickedListener, ExtractImagesListener {

    private Activity mActivity;
    private String mPath;
    private MorphButtonUtility mMorphButtonUtility;
    private FileUtils mFileUtils;
    private BottomSheetUtils mBottomSheetUtils;
    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    private ArrayList<String> mOutFilePaths;
    private MaterialDialog mMaterialDialog;

    @BindView(R.id.selectFile)
    MorphingButton selectFileButton;
    @BindView(R.id.extractImages)
    MorphingButton extractImagesButton;
    BottomSheetBehavior sheetBehavior;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    @BindView(R.id.downArrow)
    ImageView mDownArrow;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    @BindView(R.id.extracted_files)
    RecyclerView mExtractedFiles;
    @BindView(R.id.extractedimages_text)
    TextView extractImagesSuccessText;
    @BindView(R.id.options)
    LinearLayout options;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_extract_images, container, false);
        ButterKnife.bind(this, rootview);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, mDownArrow));
        mBottomSheetUtils.populateBottomSheetWithPDFs(mLayout,
                mRecyclerViewFiles, this);
        resetView();
        return rootview;
    }

    @OnClick(R.id.share_files)
    void onShareFilesClick(View view) {
        if (mOutFilePaths != null) {
            ArrayList<File> fileArrayList = new ArrayList<>();
            for (String path : mOutFilePaths) {
                fileArrayList.add(new File(path));
            }
            mFileUtils.shareMultipleFiles(fileArrayList);
        }
    }

    @OnClick(R.id.viewFiles)
    void onViewFilesClick(View view) {
        mBottomSheetUtils.showHideSheet(sheetBehavior);
    }

    @OnClick(R.id.view_images)
    void onViewImagesClicked(View view) {
        mActivity.startActivity(ImagesPreviewActivity.getStartIntent(mActivity, mOutFilePaths));
    }

    @OnClick(R.id.selectFile)
    public void showFileChooser() {
        startActivityForResult(mFileUtils.getFileChooser(),
                INTENT_REQUEST_PICKFILE_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) throws NullPointerException {
        if (data == null || resultCode != RESULT_OK || data.getData() == null)
            return;
        if (requestCode == INTENT_REQUEST_PICKFILE_CODE)
            setTextAndActivateButtons(getFilePath(data.getData()));
    }

    @OnClick(R.id.extractImages)
    public void parse() {
        new ExtractImages(mPath, this).execute();
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
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setTextAndActivateButtons(path);
    }

    private void setTextAndActivateButtons(String path) {
        mExtractedFiles.setVisibility(View.GONE);
        options.setVisibility(View.GONE);
        extractImagesSuccessText.setVisibility(View.GONE);
        mPath = path;
        mMorphButtonUtility.setTextAndActivateButtons(path,
                selectFileButton, extractImagesButton);
    }

    @Override
    public void onFileItemClick(String path) {
        mFileUtils.openImage(path);
    }

    @Override
    public void resetView() {
        mPath = null;
        mMorphButtonUtility.initializeButton(selectFileButton, extractImagesButton);
    }

    @Override
    public void extractionStarted() {
        mMaterialDialog = createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void updateView(int imagecount, ArrayList<String> outputFilePaths) {

        mMaterialDialog.dismiss();
        resetView();
        if (imagecount == 0) {
            showSnackbar(mActivity, R.string.extract_images_failed);
            return;
        }

        String text = String.format(mActivity.getString(R.string.extract_images_success), imagecount);
        showSnackbar(mActivity, text);
        extractImagesSuccessText.setVisibility(View.VISIBLE);
        options.setVisibility(View.VISIBLE);
        mOutFilePaths = outputFilePaths;
        ExtractImagesAdapter extractImagesAdapter = new ExtractImagesAdapter(mActivity, outputFilePaths, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        extractImagesSuccessText.setText(text);
        mExtractedFiles.setVisibility(View.VISIBLE);
        mExtractedFiles.setLayoutManager(mLayoutManager);
        mExtractedFiles.setAdapter(extractImagesAdapter);
        mExtractedFiles.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
    }

}

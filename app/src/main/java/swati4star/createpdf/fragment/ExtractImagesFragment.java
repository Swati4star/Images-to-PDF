package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.morphingbutton.MorphingButton;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.FilesListAdapter;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.interfaces.ExtractImagesListener;
import swati4star.createpdf.util.DirectoryUtils;
import swati4star.createpdf.util.ExtractImages;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.FileUriUtils.getFilePath;

public class ExtractImagesFragment extends Fragment implements MergeFilesAdapter.OnClickListener,
        FilesListAdapter.OnFileItemClickedListener, ExtractImagesListener {

    private Activity mActivity;
    private String mPath;
    private MorphButtonUtility mMorphButtonUtility;
    private FileUtils mFileUtils;
    private DirectoryUtils mDirectoryUtils;
    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    private ArrayList<String> mOutFilePaths;

    @BindView(R.id.selectFile)
    Button selectFileButton;
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
    @BindView(R.id.share_files)
    Button mShareFiles;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_extract_images, container, false);
        ButterKnife.bind(this, rootview);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setBottomSheetCallback(new ExtractImagesFragment.BottomSheetCallback());

        ArrayList<String> mAllFilesPaths = mDirectoryUtils.getAllFilePaths();
        if (mAllFilesPaths == null || mAllFilesPaths.size() == 0)
            mLayout.setVisibility(View.GONE);
        else {
            // Init recycler view
            MergeFilesAdapter mergeFilesAdapter = new MergeFilesAdapter(mActivity, mAllFilesPaths, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
            mRecyclerViewFiles.setLayoutManager(mLayoutManager);
            mRecyclerViewFiles.setAdapter(mergeFilesAdapter);
            mRecyclerViewFiles.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
        }

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
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    /**
     * Displays file chooser intent
     */
    @OnClick(R.id.selectFile)
    public void showFileChooser() {
        String folderPath = Environment.getExternalStorageDirectory() + "/";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Uri myUri = Uri.parse(folderPath);
        intent.setDataAndType(myUri, getString(R.string.pdf_type));
        Intent intentChooser = Intent.createChooser(intent, getString(R.string.merge_file_select));
        startActivityForResult(intentChooser, INTENT_REQUEST_PICKFILE_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) throws NullPointerException {
        if (data == null || resultCode != RESULT_OK || data.getData() == null)
            return;
        if (requestCode == INTENT_REQUEST_PICKFILE_CODE)
            setTextAndActivateButtons(getFilePath(mActivity, data.getData()));
    }

    @OnClick(R.id.extractImages)
    public void parse() {
        new ExtractImages(mActivity, mPath, this).execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mDirectoryUtils = new DirectoryUtils(mActivity);
    }

    @Override
    public void onItemClick(String path) {
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setTextAndActivateButtons(path);
    }

    private void setTextAndActivateButtons(String path) {
        mExtractedFiles.setVisibility(View.GONE);
        mShareFiles.setVisibility(View.GONE);
        extractImagesSuccessText.setVisibility(View.GONE);
        mPath = path;
        selectFileButton.setText(mPath);
        selectFileButton.setBackgroundColor(getResources().getColor(R.color.mb_green_dark));
        extractImagesButton.setEnabled(true);
        mMorphButtonUtility.morphToSquare(extractImagesButton, mMorphButtonUtility.integer());
    }

    @Override
    public void onFileItemClick(String path) {
        mFileUtils.openImage(path);
    }

    @Override
    public void resetView() {
        mPath = null;
        selectFileButton.setText(R.string.merge_file_select);
        selectFileButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        mMorphButtonUtility.morphToGrey(extractImagesButton, mMorphButtonUtility.integer());
        extractImagesButton.setEnabled(false);
    }

    @Override
    public void updateView(String text, ArrayList<String> outputFilePaths) {
        extractImagesSuccessText.setVisibility(View.VISIBLE);
        mShareFiles.setVisibility(View.VISIBLE);
        mOutFilePaths = outputFilePaths;
        FilesListAdapter splitFilesAdapter = new FilesListAdapter(mActivity, outputFilePaths, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        extractImagesSuccessText.setText(text);
        mExtractedFiles.setVisibility(View.VISIBLE);
        mExtractedFiles.setLayoutManager(mLayoutManager);
        mExtractedFiles.setAdapter(splitFilesAdapter);
        mExtractedFiles.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
    }

    private class BottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch (newState) {
                case BottomSheetBehavior.STATE_EXPANDED:
                    mUpArrow.setVisibility(View.GONE);
                    mDownArrow.setVisibility(View.VISIBLE);
                    break;
                case BottomSheetBehavior.STATE_COLLAPSED:
                    mUpArrow.setVisibility(View.VISIBLE);
                    mDownArrow.setVisibility(View.GONE);
                    break;
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    }
}

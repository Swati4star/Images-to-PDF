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

import com.dd.morphingbutton.MorphingButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.FilesListAdapter;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.util.BottomSheetCallback;
import swati4star.createpdf.util.BottomSheetUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PDFUtils;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.FileUriUtils.getFilePath;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class SplitFilesFragment extends Fragment implements MergeFilesAdapter.OnClickListener,
        FilesListAdapter.OnFileItemClickedListener {

    private Activity mActivity;
    private String mPath;
    private MorphButtonUtility mMorphButtonUtility;
    private FileUtils mFileUtils;
    private PDFUtils mPDFUtils;
    private BottomSheetUtils mBottomSheetUtils;
    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;

    @BindView(R.id.selectFile)
    MorphingButton selectFileButton;
    @BindView(R.id.splitFiles)
    MorphingButton splitFilesButton;
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
    @BindView(R.id.splitted_files)
    RecyclerView mSplittedFiles;
    @BindView(R.id.splitfiles_text)
    TextView splitFilesSuccessText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_split_files, container, false);
        ButterKnife.bind(this, rootview);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, mDownArrow));
        mBottomSheetUtils.populateBottomSheetWithPDFs(mLayout,
                mRecyclerViewFiles, this);
        resetValues();
        return rootview;
    }

    @OnClick(R.id.viewFiles)
    void onViewFilesClick(View view) {
        mBottomSheetUtils.showHideSheet(sheetBehavior);
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
        if (data == null || resultCode != RESULT_OK || data.getData() == null)
            return;
        if (requestCode == INTENT_REQUEST_PICKFILE_CODE)
            setTextAndActivateButtons(getFilePath(data.getData()));
    }

    @OnClick(R.id.splitFiles)
    public void parse() {
        ArrayList<String> outputFilePaths = mPDFUtils.splitPDF(mPath);
        int numberOfPages = outputFilePaths.size();
        if (numberOfPages > 0) {
            String output = String.format(mActivity.getString(R.string.split_success), numberOfPages);
            showSnackbar(mActivity, output);
            splitFilesSuccessText.setVisibility(View.VISIBLE);
            splitFilesSuccessText.setText(output);

            FilesListAdapter splitFilesAdapter = new FilesListAdapter(mActivity, outputFilePaths, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
            mSplittedFiles.setVisibility(View.VISIBLE);
            mSplittedFiles.setLayoutManager(mLayoutManager);
            mSplittedFiles.setAdapter(splitFilesAdapter);
            mSplittedFiles.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
            resetValues();
        }
    }

    private void resetValues() {
        mPath = null;
        mMorphButtonUtility.initializeButton(selectFileButton, splitFilesButton);
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
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setTextAndActivateButtons(path);
    }

    private void setTextAndActivateButtons(String path) {
        mSplittedFiles.setVisibility(View.GONE);
        splitFilesSuccessText.setVisibility(View.GONE);
        mPath = path;
        mMorphButtonUtility.setTextAndActivateButtons(path,
                selectFileButton, splitFilesButton);
    }

    @Override
    public void onFileItemClick(String path) {
        mFileUtils.openFile(path);
    }
}

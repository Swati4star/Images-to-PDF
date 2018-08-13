package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.interfaces.MergeFilesListener;
import swati4star.createpdf.util.DirectoryUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MergePdf;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.StringUtils;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

import static android.app.Activity.RESULT_OK;

public class MergeFilesFragment extends Fragment implements MergeFilesAdapter.OnClickListener, MergeFilesListener {
    private Activity mActivity;
    private boolean mSuccess;
    private String mCheckbtClickTag = "";
    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    private String mRealPath;
    private String mDisplayName;
    private DirectoryUtils mDirectoryUtils;
    private MorphButtonUtility mMorphButtonUtility;
    private FileUtils mFileUtils;
    String firstFilePath;
    String secondFilePath;

    @BindView(R.id.fileonebtn)
    Button addFileOne;
    @BindView(R.id.filetwobtn)
    Button addFileTwo;
    @BindView(R.id.mergebtn)
    MorphingButton mergeBtn;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    @BindView(R.id.viewFiles)
    TextView mViewFiles;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    @BindView(R.id.downArrow)
    ImageView mDownArrow;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    BottomSheetBehavior sheetBehavior;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    public MergeFilesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_merge_files, container, false);
        ButterKnife.bind(this, root);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mDirectoryUtils = new DirectoryUtils(mActivity);
        mFileUtils = new FileUtils(mActivity);

        ArrayList<String> mAllFilesPaths = mDirectoryUtils.getAllFilePaths();
        if (mAllFilesPaths == null || mAllFilesPaths.size() == 0) {
            mLayout.setVisibility(View.GONE);
        }

        // Init recycler view
        MergeFilesAdapter mergeFilesAdapter = new MergeFilesAdapter(mActivity, mAllFilesPaths, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerViewFiles.setLayoutManager(mLayoutManager);
        mRecyclerViewFiles.setAdapter(mergeFilesAdapter);
        mRecyclerViewFiles.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));

        sheetBehavior.setBottomSheetCallback(new BottomSheetCallback());
        mMorphButtonUtility.morphToGrey(mergeBtn, mMorphButtonUtility.integer());
        mergeBtn.setEnabled(false);

        return root;
    }

    @OnClick(R.id.viewFiles)
    void onViewFilesClick(View view) {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @OnClick({R.id.fileonebtn, R.id.filetwobtn})
    void startAddingPDF(View v) {
        mCheckbtClickTag = (v).getTag().toString();
        showFileChooser();
    }

    @OnClick(R.id.mergebtn)
    void mergeFiles(final View v) {
        String[] pdfpaths = {firstFilePath, secondFilePath};
        if (firstFilePath == null || secondFilePath == null || !mSuccess) {
            showSnackbar(R.string.snackbar_no_pdfs_selected);
            return;
        }
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, (dialog, input) -> {
                    if (StringUtils.isEmpty(input)) {
                        showSnackbar(R.string.snackbar_name_not_blank);
                    } else {
                        new MergePdf(mActivity, input.toString(), this).execute(pdfpaths);
                    }
                })
                .show();
    }

    /**
     * Displays file chooser intent
     */
    private void showFileChooser() {
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
        if (requestCode == INTENT_REQUEST_PICKFILE_CODE) {
            Uri uri = data.getData();
            Log.v("file", uri + " ");
            mRealPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            //Check if First button is clicked from mCheckbtClickTag
            if (addFileOne.getTag().toString().equals(mCheckbtClickTag)) {
                firstFilePath = getFilePath(uri);
                Log.v("File path 1", firstFilePath + mSuccess);
                addFileOne.setText(firstFilePath);
                addFileOne.setBackgroundColor(getResources().getColor(R.color.mb_green_dark));
            } else {
                secondFilePath = getFilePath(uri);
                Log.v("File path 2", secondFilePath);
                addFileTwo.setText(secondFilePath);
                addFileTwo.setBackgroundColor(getResources().getColor(R.color.mb_green_dark));
            }
            if (firstFilePath != null && secondFilePath != null) {
                mergeBtn.setEnabled(true);
                mMorphButtonUtility.morphToSquare(mergeBtn, mMorphButtonUtility.integer());
            }
        }
    }

    //Returns the complete filepath of the PDF as a string
    private String getFilePath(Uri uri) {
        String uriString = uri.toString();
        File file = new File(uri.toString());
        String path = file.getPath();
        if (uriString.startsWith("content://") && uriString.contains("com.google.android.")) {
            mSuccess = false;
        } else {
            mSuccess = true;
            mDisplayName = mFileUtils.getFileName(uri);
        }
        if (mSuccess) {
            String folname = mDirectoryUtils.getParentFolder(path);
            if (folname != null) {
                String c = getString(R.string.path_seperator);
                mRealPath = mRealPath + c + folname + c + mDisplayName;
            }
        }
        return mRealPath;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mCheckbtClickTag = savedInstanceState.getString("savText");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.btn_sav_text), mCheckbtClickTag);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    private void showSnackbar(int resID) {
        Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(String path) {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.select_as)
                .items(R.array.select_as_options)
                .itemsCallback((dialog, itemView, position, text) -> {
                    switch (position) {
                        case 0:
                            firstFilePath = path;
                            addFileOne.setText(path);
                            addFileOne.setBackgroundColor(getResources().getColor(R.color.mb_green_dark));
                            break;
                        case 1:
                            addFileTwo.setText(path);
                            secondFilePath = path;
                            addFileTwo.setBackgroundColor(getResources().getColor(R.color.mb_green_dark));
                            mSuccess = true;
                            break;
                    }
                    if (firstFilePath != null && secondFilePath != null) {
                        mergeBtn.setEnabled(true);
                        mMorphButtonUtility.morphToSquare(mergeBtn, mMorphButtonUtility.integer());
                    }
                })
                .show();
    }

    /**
     * resets fragment to initial stage
     */
    @Override
    public void resetValues() {
        firstFilePath = "";
        secondFilePath = "";
        addFileOne.setText(R.string.file_one);
        addFileTwo.setText(R.string.file_two);
        addFileTwo.setBackgroundColor(getResources().getColor(R.color.colorGray));
        addFileOne.setBackgroundColor(getResources().getColor(R.color.colorGray));
        mMorphButtonUtility.morphToGrey(mergeBtn, mMorphButtonUtility.integer());
        mergeBtn.setEnabled(false);
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

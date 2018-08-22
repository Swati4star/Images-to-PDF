package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.adapter.MergeSelectedFilesAdapter;
import swati4star.createpdf.interfaces.MergeFilesListener;
import swati4star.createpdf.util.DirectoryUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MergePdf;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.StringUtils;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.FileUriUtils.getFilePath;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class MergeFilesFragment extends Fragment implements MergeFilesAdapter.OnClickListener, MergeFilesListener,
        MergeSelectedFilesAdapter.OnFileItemClickListener {
    private Activity mActivity;
    private String mCheckbtClickTag = "";
    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    private MorphButtonUtility mMorphButtonUtility;
    private ArrayList<String> mFilePaths;
    private FileUtils mFileUtils;
    private MergeSelectedFilesAdapter mMergeSelectedFilesAdapter;

    @BindView(R.id.mergebtn)
    MorphingButton mergeBtn;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    @BindView(R.id.downArrow)
    ImageView mDownArrow;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    BottomSheetBehavior sheetBehavior;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    @BindView(R.id.selectFiles)
    Button mSelectFiles;
    @BindView(R.id.selected_files)
    RecyclerView mSelectedFiles;

    public MergeFilesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_merge_files, container, false);
        ButterKnife.bind(this, root);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mFilePaths = new ArrayList<>();
        mMergeSelectedFilesAdapter = new MergeSelectedFilesAdapter(mActivity, mFilePaths, this);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        DirectoryUtils directoryUtils = new DirectoryUtils(mActivity);

        ArrayList<String> mAllFilesPaths = directoryUtils.getAllFilePaths();
        if (mAllFilesPaths == null || mAllFilesPaths.size() == 0) {
            mLayout.setVisibility(View.GONE);
        }

        // Init recycler view
        MergeFilesAdapter mergeFilesAdapter = new MergeFilesAdapter(mActivity, mAllFilesPaths, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerViewFiles.setLayoutManager(mLayoutManager);
        mRecyclerViewFiles.setAdapter(mergeFilesAdapter);
        mRecyclerViewFiles.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));

        mSelectedFiles.setAdapter(mMergeSelectedFilesAdapter);
        mSelectedFiles.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));

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

    @OnClick(R.id.selectFiles)
    void startAddingPDF(View v) {
        showFileChooser();
    }

    @OnClick(R.id.mergebtn)
    void mergeFiles(final View view) {
        String[] pdfpaths = mFilePaths.toArray(new String[0]);
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, (dialog, input) -> {
                    if (StringUtils.isEmpty(input)) {
                        showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                    } else {
                        final String inputName = input.toString();
                        if (!mFileUtils.isFileExist(inputName + getString(R.string.pdf_ext))) {
                            new MergePdf(mActivity, input.toString(), this).execute(pdfpaths);
                        } else {
                            new MaterialDialog.Builder(mActivity)
                                    .title(R.string.warning)
                                    .content(R.string.overwrite_message)
                                    .positiveText(android.R.string.ok)
                                    .negativeText(android.R.string.cancel)
                                    .onPositive((dialog12, which) -> new MergePdf(mActivity, input.toString(),
                                            this).execute(pdfpaths))
                                    .onNegative((dialog1, which) -> mergeFiles(view))
                                    .show();
                        }
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
            mFilePaths.add(getFilePath(mActivity, uri));
            mMergeSelectedFilesAdapter.notifyDataSetChanged();
            showSnackbar(mActivity, getString(R.string.pdf_added_to_list));
            if (mFilePaths.size() > 1 && !mergeBtn.isEnabled()) {
                mergeBtn.setEnabled(true);
                mMorphButtonUtility.morphToSquare(mergeBtn, mMorphButtonUtility.integer());
            }
        }
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
        mFileUtils = new FileUtils(mActivity);
    }

    @Override
    public void onItemClick(String path) {
        mFilePaths.add(path);
        mMergeSelectedFilesAdapter.notifyDataSetChanged();
        if (mFilePaths.size() > 1 && !mergeBtn.isEnabled()) {
            mergeBtn.setEnabled(true);
            mMorphButtonUtility.morphToSquare(mergeBtn, mMorphButtonUtility.integer());
        }
        showSnackbar(mActivity, getString(R.string.pdf_added_to_list));
    }

    /**
     * resets fragment to initial stage
     */
    @Override
    public void resetValues() {
        mMorphButtonUtility.morphToGrey(mergeBtn, mMorphButtonUtility.integer());
        mergeBtn.setEnabled(false);
        mFilePaths.clear();
        mMergeSelectedFilesAdapter.notifyDataSetChanged();
    }


    @Override
    public void viewFile(String path) {
        mFileUtils.openFile(path);
    }

    @Override
    public void removeFile(String path) {
        mFilePaths.remove(path);
        mMergeSelectedFilesAdapter.notifyDataSetChanged();
        showSnackbar(mActivity, getString(R.string.pdf_removed_from_list));
        if (mFilePaths.size() < 2 && mergeBtn.isEnabled()) {
            mergeBtn.setEnabled(false);
            mMorphButtonUtility.morphToGrey(mergeBtn, mMorphButtonUtility.integer());
        }
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

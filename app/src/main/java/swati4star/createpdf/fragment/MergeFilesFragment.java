package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.dd.morphingbutton.MorphingButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.adapter.MergeSelectedFilesAdapter;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.interfaces.BottomSheetPopulate;
import swati4star.createpdf.interfaces.MergeFilesListener;
import swati4star.createpdf.interfaces.OnItemClickListner;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.util.BottomSheetCallback;
import swati4star.createpdf.util.BottomSheetUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MergePdf;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.StringUtils;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.Constants.MASTER_PWD_STRING;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.appName;
import static swati4star.createpdf.util.DialogUtils.createAnimationDialog;
import static swati4star.createpdf.util.DialogUtils.createCustomDialogWithoutContent;
import static swati4star.createpdf.util.DialogUtils.createOverwriteDialog;
import static swati4star.createpdf.util.FileUriUtils.getFilePath;
import static swati4star.createpdf.util.MergePdfEnhancementOptionsUtils.getEnhancementOptions;
import static swati4star.createpdf.util.StringUtils.getDefaultStorageLocation;
import static swati4star.createpdf.util.StringUtils.getSnackbarwithAction;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class MergeFilesFragment extends Fragment implements MergeFilesAdapter.OnClickListener, MergeFilesListener,
        MergeSelectedFilesAdapter.OnFileItemClickListener, OnItemClickListner, BottomSheetPopulate {
    private Activity mActivity;
    private String mCheckbtClickTag = "";
    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    private MorphButtonUtility mMorphButtonUtility;
    private ArrayList<String> mFilePaths;
    private FileUtils mFileUtils;
    private BottomSheetUtils mBottomSheetUtils;
    private MergeSelectedFilesAdapter mMergeSelectedFilesAdapter;
    private MaterialDialog mMaterialDialog;
    private String mHomePath;
    private ArrayList<EnhancementOptionsEntity> mEnhancementOptionsEntityArrayList;
    private EnhancementOptionsAdapter mEnhancementOptionsAdapter;
    private boolean mPasswordProtected = false;
    private String mPassword;
    private SharedPreferences mSharedPrefs;

    @BindView(R.id.lottie_progress)
    LottieAnimationView mLottieProgress;
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
    @BindView(R.id.enhancement_options_recycle_view)
    RecyclerView mEnhancementOptionsRecycleView;

    public MergeFilesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_merge_files, container, false);
        ButterKnife.bind(this, root);
        showEnhancementOptions();
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mFilePaths = new ArrayList<>();
        mMergeSelectedFilesAdapter = new MergeSelectedFilesAdapter(mActivity, mFilePaths, this);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mHomePath = mSharedPrefs.getString(STORAGE_LOCATION,
                getDefaultStorageLocation());
        mLottieProgress.setVisibility(View.VISIBLE);
        mBottomSheetUtils.populateBottomSheetWithPDFs(this);

        mSelectedFiles.setAdapter(mMergeSelectedFilesAdapter);
        mSelectedFiles.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));

        sheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, isAdded()));
        setMorphingButtonState(false);

        return root;
    }

    /**
     * Function to show the enhancement options.
     */
    private void showEnhancementOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 2);
        mEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        mEnhancementOptionsEntityArrayList = getEnhancementOptions(mActivity);
        mEnhancementOptionsAdapter = new EnhancementOptionsAdapter(this, mEnhancementOptionsEntityArrayList);
        mEnhancementOptionsRecycleView.setAdapter(mEnhancementOptionsAdapter);
    }

    @Override
    public void onItemClick(int position) {
        if (mFilePaths.size() == 0) {
            showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
            return;
        }
        switch (position) {
            case 0:
                setPassword();
                break;
        }
    }

    private void setPassword() {
        MaterialDialog.Builder builder = createCustomDialogWithoutContent(mActivity,
                R.string.set_password);
        final MaterialDialog dialog = builder
                .customView(R.layout.custom_dialog, true)
                .neutralText(R.string.remove_dialog)
                .build();

        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        final View neutralAction = dialog.getActionButton(DialogAction.NEUTRAL);
        final EditText passwordInput = Objects.requireNonNull(dialog.getCustomView()).findViewById(R.id.password);
        passwordInput.setText(mPassword);
        passwordInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        positiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable input) {
                        if (StringUtils.isEmpty(input)) {
                            showSnackbar(mActivity, R.string.snackbar_password_cannot_be_blank);
                        } else {
                            mPassword = input.toString();
                            mPasswordProtected = true;
                            onPasswordAdded();
                        }
                    }
                });
        if (StringUtils.isNotEmpty(mPassword)) {
            neutralAction.setOnClickListener(v -> {
                mPassword = null;
                onPasswordRemoved();
                mPasswordProtected = false;
                dialog.dismiss();
                showSnackbar(mActivity, R.string.password_remove);
            });
        }
        dialog.show();
        positiveAction.setEnabled(false);
    }

    private void onPasswordAdded() {
        mEnhancementOptionsEntityArrayList.get(0)
                .setImage(mActivity.getResources()
                        .getDrawable(R.drawable.baseline_done_24));
        mEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    private void onPasswordRemoved() {
        mEnhancementOptionsEntityArrayList.get(0)
                .setImage(mActivity.getResources()
                        .getDrawable(R.drawable.baseline_enhanced_encryption_24));
        mEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.viewFiles)
    void onViewFilesClick(View view) {
        mBottomSheetUtils.showHideSheet(sheetBehavior);
    }

    @OnClick(R.id.selectFiles)
    void startAddingPDF(View v) {
        startActivityForResult(mFileUtils.getFileChooser(),
                INTENT_REQUEST_PICKFILE_CODE);
    }

    @OnClick(R.id.mergebtn)
    void mergeFiles(final View view) {
        String[] pdfpaths = mFilePaths.toArray(new String[0]);
        String masterpwd = mSharedPrefs.getString(MASTER_PWD_STRING, appName);
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, (dialog, input) -> {
                    if (StringUtils.isEmpty(input)) {
                        showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                    } else {
                        if (!mFileUtils.isFileExist(input + getString(R.string.pdf_ext))) {
                            new MergePdf(input.toString(), mHomePath, mPasswordProtected,
                                    mPassword, this, masterpwd).execute(pdfpaths);
                        } else {
                            MaterialDialog.Builder builder = createOverwriteDialog(mActivity);
                            builder.onPositive((dialog12, which) -> new MergePdf(input.toString(),
                                    mHomePath, mPasswordProtected, mPassword,
                                    this, masterpwd).execute(pdfpaths))
                                    .onNegative((dialog1, which) -> mergeFiles(view)).show();
                        }
                    }
                })
                .show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || resultCode != RESULT_OK || data.getData() == null)
            return;
        if (requestCode == INTENT_REQUEST_PICKFILE_CODE) {
            mFilePaths.add(getFilePath(data.getData()));
            mMergeSelectedFilesAdapter.notifyDataSetChanged();
            showSnackbar(mActivity, getString(R.string.pdf_added_to_list));
            if (mFilePaths.size() > 1 && !mergeBtn.isEnabled())
                setMorphingButtonState(true);
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
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
    }

    @Override
    public void onItemClick(String path) {
        if (mFilePaths.contains(path)) {
            mFilePaths.remove(path);
            showSnackbar(mActivity, getString(R.string.pdf_removed_from_list));
        } else {
            mFilePaths.add(path);
            showSnackbar(mActivity, getString(R.string.pdf_added_to_list));
        }

        mMergeSelectedFilesAdapter.notifyDataSetChanged();
        if (mFilePaths.size() > 1) {
            if (!mergeBtn.isEnabled()) setMorphingButtonState(true);
        } else {
            if (mergeBtn.isEnabled()) setMorphingButtonState(false);
        }
    }

    /**
     * resets fragment to initial stage
     */
    @Override
    public void resetValues(boolean isPDFMerged, String path) {
        mMaterialDialog.dismiss();

        if (isPDFMerged) {
            getSnackbarwithAction(mActivity, R.string.pdf_merged)
                    .setAction(R.string.snackbar_viewAction, v -> mFileUtils.openFile(path)).show();
            new DatabaseHelper(mActivity).insertRecord(path,
                    mActivity.getString(R.string.created));
        } else
            showSnackbar(mActivity, R.string.pdf_merge_error);

        setMorphingButtonState(false);
        mFilePaths.clear();
        mMergeSelectedFilesAdapter.notifyDataSetChanged();
    }

    @Override
    public void mergeStarted() {
        mMaterialDialog = createAnimationDialog(mActivity);
        mMaterialDialog.show();
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
        if (mFilePaths.size() < 2 && mergeBtn.isEnabled())
            setMorphingButtonState(false);
    }

    @Override
    public void moveUp(int position) {
        Collections.swap(mFilePaths, position, position - 1);
        mMergeSelectedFilesAdapter.notifyDataSetChanged();
    }

    @Override
    public void moveDown(int position) {
        Collections.swap(mFilePaths, position, position + 1);
        mMergeSelectedFilesAdapter.notifyDataSetChanged();
    }

    void setMorphingButtonState(Boolean enabled) {
        if (enabled)
            mMorphButtonUtility.morphToSquare(mergeBtn, mMorphButtonUtility.integer());
        else
            mMorphButtonUtility.morphToGrey(mergeBtn, mMorphButtonUtility.integer());

        mergeBtn.setEnabled(enabled);
    }

    @Override
    public void onPopulate(ArrayList<String> paths) {
        if (paths == null || paths.size() == 0) {
            mLayout.setVisibility(View.GONE);
        } else {
            // Init recycler view
            mRecyclerViewFiles.setVisibility(View.VISIBLE);
            MergeFilesAdapter mergeFilesAdapter = new MergeFilesAdapter(mActivity,
                    paths, true, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
            mRecyclerViewFiles.setLayoutManager(mLayoutManager);
            mRecyclerViewFiles.setAdapter(mergeFilesAdapter);
            mRecyclerViewFiles.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
        }
        mLottieProgress.setVisibility(View.GONE);
    }
}

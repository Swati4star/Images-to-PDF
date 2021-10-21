package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.dd.morphingbutton.MorphingButton;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.interfaces.BottomSheetPopulate;
import swati4star.createpdf.interfaces.OnBackPressedInterface;
import swati4star.createpdf.interfaces.OnItemClickListener;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.util.AddTextEnhancementOptionsUtils;
import swati4star.createpdf.util.BottomSheetCallback;
import swati4star.createpdf.util.BottomSheetUtils;
import swati4star.createpdf.util.CommonCodeUtils;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.RealPathUtil;
import swati4star.createpdf.util.StringUtils;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;
import static swati4star.createpdf.util.Constants.pdfExtension;

public class AddTextFragment extends Fragment implements MergeFilesAdapter.OnClickListener,
        BottomSheetPopulate, OnBackPressedInterface, OnItemClickListener {
    private Activity mActivity;
    private static String mPdfpath;
    private String mFontTitle;
    private static String mTextPath;
    private FileUtils mFileUtils;
    private MorphButtonUtility mMorphButtonUtility;
    private BottomSheetUtils mBottomSheetUtils;
    private SharedPreferences mSharedPreferences;
    private boolean mPermissionGranted;
    private int mFontSize = 0;
    private static final int INTENT_REQUEST_PICK_PDF_FILE_CODE = 10;
    private static final int INTENT_REQUEST_PICK_TEXT_FILE_CODE = 0;
    private BottomSheetBehavior mSheetBehavior;

    @BindView(R.id.select_pdf_file)
    MorphingButton mSelectPDF;
    @BindView(R.id.select_text_file)
    MorphingButton mSelectText;
    @BindView(R.id.create_pdf_added_text)
    MorphingButton mCreateTextPDF;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    @BindView(R.id.downArrow)
    ImageView mDownArrow;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    @BindView(R.id.lottie_progress)
    LottieAnimationView mLottieProgress;
    @BindView(R.id.enhancement_options_recycle_view_text)
    RecyclerView mTextEnhancementOptionsRecycleView;

    private ArrayList<EnhancementOptionsEntity> mTextEnhancementOptionsEntityArrayList;
    private EnhancementOptionsAdapter mTextEnhancementOptionsAdapter;
    private Font.FontFamily mFontFamily;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_text, container, false);
        ButterKnife.bind(this, rootView);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mFontTitle = String.format(getString(R.string.edit_font_size),
                mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, Constants.DEFAULT_FONT_SIZE));
        mFontFamily = Font.FontFamily.valueOf(mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY));
        mFontSize = mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, Constants.DEFAULT_FONT_SIZE);
        mSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mBottomSheetUtils.populateBottomSheetWithPDFs(this);
        showEnhancementOptions();
        mLottieProgress.setVisibility(View.VISIBLE);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, isAdded()));
        resetView();
        return rootView;
    }

    /**
     * Function to show the enhancement options.
     */
    private void showEnhancementOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 2);
        mTextEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        mTextEnhancementOptionsEntityArrayList = AddTextEnhancementOptionsUtils.getInstance()
                .getEnhancementOptions(mActivity, mFontTitle, mFontFamily);
        mTextEnhancementOptionsAdapter = new EnhancementOptionsAdapter(this, mTextEnhancementOptionsEntityArrayList);
        mTextEnhancementOptionsRecycleView.setAdapter(mTextEnhancementOptionsAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
    }

    @OnClick(R.id.select_pdf_file)
    public void showPdfFileChooser() {
        try {
            startActivityForResult(mFileUtils.getFileChooser(),
                    INTENT_REQUEST_PICK_PDF_FILE_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.install_file_manager);
        }
    }

    @OnClick(R.id.select_text_file)
    public void showTextFileChooser() {
        Uri uri = Uri.parse(Environment.getRootDirectory() + "/");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(uri, "*/*");
        String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/msword", getString(R.string.text_type)};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, String.valueOf(R.string.select_file)),
                    INTENT_REQUEST_PICK_TEXT_FILE_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.install_file_manager);
        }
    }

    @OnClick(R.id.create_pdf_added_text)
    public void openPdfNameDialog() {
        if (PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
            openPdfNameDialog_();
        } else {
            getRuntimePermissions();
        }
    }

    private void openPdfNameDialog_() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, (dialog, input) -> {
                    if (StringUtils.getInstance().isEmpty(input)) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                    } else {
                        final String inputName = input.toString();
                        if (!mFileUtils.isFileExist(inputName + getString(R.string.pdf_ext))) {
                            addText(inputName, mFontSize, mFontFamily);
                        } else {
                            MaterialDialog.Builder builder = DialogUtils.getInstance().createOverwriteDialog(mActivity);
                            builder.onPositive((dialog12, which) -> addText(inputName, mFontSize, mFontFamily))
                                    .onNegative((dialog1, which) -> openPdfNameDialog())
                                    .show();
                        }
                    }
                })
                .show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) throws NullPointerException {
        if (data == null || resultCode != RESULT_OK || data.getData() == null)
            return;
        if (requestCode == INTENT_REQUEST_PICK_PDF_FILE_CODE) {
            mPdfpath = RealPathUtil.getInstance().getRealPath(getContext(), data.getData());
            StringUtils.getInstance().showSnackbar(mActivity, getResources().getString(R.string.snackbar_pdfselected));
        }
        if (requestCode == INTENT_REQUEST_PICK_TEXT_FILE_CODE) {
            mTextPath = RealPathUtil.getInstance().getRealPath(getContext(), data.getData());
            StringUtils.getInstance().showSnackbar(mActivity, getResources().getString(R.string.snackbar_txtselected));
        }
        if (mPdfpath != null && mTextPath != null)
            setTextAndActivateButtons(mPdfpath, mTextPath);
    }

    private void setTextAndActivateButtons(String pdfPath, String textPath) {
        if (pdfPath == null || textPath == null) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.error_path_not_found);
            resetView();
            return;
        }
        mMorphButtonUtility.setTextAndActivateButtons(pdfPath,
                mSelectPDF, mCreateTextPDF);
        mMorphButtonUtility.setTextAndActivateButtons(textPath,
                mSelectText, mCreateTextPDF);
    }

    @OnClick(R.id.viewFiles)
    void onViewFilesClick(View view) {
        mBottomSheetUtils.showHideSheet(mSheetBehavior);
    }

    /**
     * Resets view after successful conversion
     */
    private void resetView() {
        mPdfpath = mTextPath = null;
        mMorphButtonUtility.morphToGrey(mCreateTextPDF, mMorphButtonUtility.integer());
        mCreateTextPDF.setEnabled(false);
        mFontSize = mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, Constants.DEFAULT_FONT_SIZE);
        mFontFamily = Font.FontFamily.valueOf(mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY));
        showFontSize();
        showFontFamily();
    }

    /**
     * This method is used to add append the text to an existing PDF file and
     * make a final new pdf with the appended text to the old pdf content.
     *
     * @param fileName - the name of the new pdf that is to be created.
     */
    private void addText(String fileName, int fontSize, Font.FontFamily fontFamily) {
        String mStorePath = mSharedPreferences.getString(STORAGE_LOCATION,
                StringUtils.getInstance().getDefaultStorageLocation());
        String mPath = mStorePath + fileName + pdfExtension;
        try {
            StringBuilder text = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(mTextPath));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

            OutputStream fos = new FileOutputStream(new File(mPath));

            PdfReader pdfReader = new PdfReader(mPdfpath);

            Document document = new Document(pdfReader.getPageSize(1));
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fos);
            document.open();
            PdfContentByte cb = pdfWriter.getDirectContent();
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                PdfImportedPage page = pdfWriter.getImportedPage(pdfReader, i);

                document.newPage();
                cb.addTemplate(page, 0, 0);
            }
            document.setPageSize(pdfReader.getPageSize(1));
            document.newPage();
            document.add(new Paragraph(new Paragraph(text.toString(),
                    FontFactory.getFont(fontFamily.name(), fontSize))));
            document.close();

            StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction,
                            v -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF))
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mMorphButtonUtility.initializeButtonForAddText(mSelectPDF, mSelectText, mCreateTextPDF);
            resetView();
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
                requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION, this::openPdfNameDialog_);
    }

    @Override
    public void onItemClick(String path) {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mPdfpath = path;
        StringUtils.getInstance().showSnackbar(mActivity, getResources().getString(R.string.snackbar_pdfselected));
    }

    @Override
    public void onPopulate(ArrayList<String> paths) {
        CommonCodeUtils.getInstance().populateUtil(mActivity, paths,
                this, mLayout, mLottieProgress, mRecyclerViewFiles);
    }

    @Override
    public void closeBottomSheet() {
        CommonCodeUtils.getInstance().closeBottomSheetUtil(mSheetBehavior);
    }

    @Override
    public boolean checkSheetBehaviour() {
        return CommonCodeUtils.getInstance().checkSheetBehaviourUtil(mSheetBehavior);
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                editFontSize();
                break;
            case 1:
                changeFontFamily();
                break;
        }

    }

    /**
     * Function to take the font size of pdf as user input
     */
    private void editFontSize() {
        new MaterialDialog.Builder(mActivity)
                .title(mFontTitle)
                .customView(R.layout.dialog_font_size, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    final EditText fontInput = dialog.getCustomView().findViewById(R.id.fontInput);
                    final CheckBox cbSetDefault = dialog.getCustomView().findViewById(R.id.cbSetFontDefault);
                    try {
                        int check = Integer.parseInt(String.valueOf(fontInput.getText()));
                        if (check > 1000 || check < 0) {
                            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                        } else {
                            mFontSize = check;
                            showFontSize();
                            StringUtils.getInstance().showSnackbar(mActivity, R.string.font_size_changed);
                            if (cbSetDefault.isChecked()) {
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putInt(Constants.DEFAULT_FONT_SIZE_TEXT, mFontSize);
                                editor.apply();
                                mFontTitle = String.format(getString(R.string.edit_font_size),
                                        mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT,
                                                Constants.DEFAULT_FONT_SIZE));
                            }
                        }
                    } catch (NumberFormatException e) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                    }
                })
                .show();
    }

    /**
     * Displays font size in UI
     */
    private void showFontSize() {
        mTextEnhancementOptionsEntityArrayList.get(0)
                .setName(String.format(getString(R.string.font_size), String.valueOf(mFontSize)));
        mTextEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    /**
     * Shows dialog to change font size
     */
    private void changeFontFamily() {
        String fontFamily = mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY);
        int ordinal = Font.FontFamily.valueOf(fontFamily).ordinal();
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .title(String.format(getString(R.string.default_font_family_text), fontFamily))
                .customView(R.layout.dialog_font_family, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    View view = dialog.getCustomView();
                    RadioGroup radioGroup = view.findViewById(R.id.radio_group_font_family);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = view.findViewById(selectedId);
                    String fontFamily1 = radioButton.getText().toString();
                    mFontFamily = Font.FontFamily.valueOf(fontFamily1);
                    final CheckBox cbSetDefault = view.findViewById(R.id.cbSetDefault);
                    if (cbSetDefault.isChecked()) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(Constants.DEFAULT_FONT_FAMILY_TEXT, fontFamily1);
                        editor.apply();
                    }
                    showFontFamily();
                })
                .build();
        RadioGroup radioGroup = materialDialog.getCustomView().findViewById(R.id.radio_group_font_family);
        RadioButton rb = (RadioButton) radioGroup.getChildAt(ordinal);
        rb.setChecked(true);
        materialDialog.show();
    }


    /**
     * Displays font family in UI
     */
    private void showFontFamily() {
        mTextEnhancementOptionsEntityArrayList.get(1)
                .setName(getString(R.string.font_family_text) + mFontFamily.name());
        mTextEnhancementOptionsAdapter.notifyDataSetChanged();
    }


}


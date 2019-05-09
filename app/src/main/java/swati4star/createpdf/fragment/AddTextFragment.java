package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.dd.morphingbutton.MorphingButton;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

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
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.interfaces.BottomSheetPopulate;
import swati4star.createpdf.interfaces.OnBackPressedInterface;
import swati4star.createpdf.util.BottomSheetCallback;
import swati4star.createpdf.util.BottomSheetUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.RealPathUtil;
import swati4star.createpdf.util.StringUtils;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.CommonCodeUtils.checkSheetBehaviourUtil;
import static swati4star.createpdf.util.CommonCodeUtils.closeBottomSheetUtil;
import static swati4star.createpdf.util.CommonCodeUtils.populateUtil;
import static swati4star.createpdf.util.Constants.READ_WRITE_PERMISSIONS;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.pdfExtension;
import static swati4star.createpdf.util.Constants.textExtension;
import static swati4star.createpdf.util.DialogUtils.createOverwriteDialog;
import static swati4star.createpdf.util.StringUtils.getDefaultStorageLocation;
import static swati4star.createpdf.util.StringUtils.getSnackbarwithAction;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class AddTextFragment extends Fragment implements MergeFilesAdapter.OnClickListener,
        BottomSheetPopulate, OnBackPressedInterface {
    private Activity mActivity;
    private String mPdfpath;
    private String mTextPath;
    private Uri mPdfUri;
    private Uri mTextUri;
    private FileUtils mFileUtils;
    private MorphButtonUtility mMorphButtonUtility;
    private BottomSheetUtils mBottomSheetUtils;
    private SharedPreferences mSharedPreferences;
    private boolean mPermissionGranted;
    private static final int INTENT_REQUEST_PICK_PDF_FILE_CODE = 10;
    private static final int INTENT_REQUEST_PICK_TEXT_FILE_CODE = 0;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;

    @BindView(R.id.select_pdf_file)
    MorphingButton mSelectPDF;
    @BindView(R.id.select_text_file)
    MorphingButton mSelectText;
    @BindView(R.id.create_pdf_added_text)
    MorphingButton mCreateTextPDF;
    BottomSheetBehavior sheetBehavior;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_text, container, false);
        ButterKnife.bind(this, rootView);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mBottomSheetUtils.populateBottomSheetWithPDFs(this);
        mLottieProgress.setVisibility(View.VISIBLE);
        sheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, isAdded()));
        resetView();
        return rootView;
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
            showSnackbar(mActivity, R.string.install_file_manager);
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
            showSnackbar(mActivity, R.string.install_file_manager);
        }
    }

    @OnClick(R.id.create_pdf_added_text)
    public void openPdfNameDialog() {
        if (!mPermissionGranted) {
            PermissionsUtils.requestRuntimePermissions(this,
                    READ_WRITE_PERMISSIONS,
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
        }
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, (dialog, input) -> {
                    if (StringUtils.isEmpty(input)) {
                        showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                    } else {
                        final String inputName = input.toString();
                        if (!mFileUtils.isFileExist(inputName + getString(R.string.pdf_ext))) {
                            addText(inputName);
                        } else {
                            MaterialDialog.Builder builder = createOverwriteDialog(mActivity);
                            builder.onPositive((dialog12, which) -> addText(inputName))
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
            mPdfUri = data.getData();
            mPdfpath = RealPathUtil.getRealPath(getContext(), data.getData());
            showSnackbar(mActivity, getResources().getString(R.string.snackbar_pdfselected));
            return;
        }
        if (requestCode == INTENT_REQUEST_PICK_TEXT_FILE_CODE) {
            mTextUri = data.getData();
            mTextPath = RealPathUtil.getRealPath(getContext(), data.getData());
            showSnackbar(mActivity, getResources().getString(R.string.snackbar_txtselected));
        }
        setTextAndActivateButtons(mPdfpath, mTextPath);
    }

    private void setTextAndActivateButtons(String pdfPath, String textPath) {
        if (pdfPath == null || textPath == null) {
            showSnackbar(mActivity, R.string.error_occurred);
            resetView();
            return;
        }
        mMorphButtonUtility.setTextAndActivateButtons(pdfPath,
                mSelectPDF, mCreateTextPDF);
        mMorphButtonUtility.setTextAndActivateButtons(textPath,
                mSelectText, mCreateTextPDF);
    }

    public void resetView() {
        mPdfpath = mTextPath = null;
        mPdfUri = mTextUri = null;
        mMorphButtonUtility.morphToGrey(mCreateTextPDF, mMorphButtonUtility.integer());
        mCreateTextPDF.setEnabled(false);
    }

    private void addText(String fileName) {
        String mStorePath = mSharedPreferences.getString(STORAGE_LOCATION,
                getDefaultStorageLocation());
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
            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

            PdfContentByte pdfContentByte = pdfStamper.getOverContent(pdfReader.getNumberOfPages());

            // Add text in existing PDF
            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont
                            (BaseFont.TIMES_ROMAN, //Font name
                                    BaseFont.CP1257, //Font encoding
                                    BaseFont.EMBEDDED //Font embedded
                            )
                    , 12); // set font and size
            pdfContentByte.setTextMatrix(10, 10); // set x and y co-ordinates
            //0, 800 will write text on TOP LEFT of pdf page
            //0, 0 will write text on BOTTOM LEFT of pdf page
            pdfContentByte.showText(text.toString());
            pdfContentByte.endText();

            pdfStamper.close(); //close pdfStamper

            getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction, v -> mFileUtils.openFile(mPath))
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mMorphButtonUtility.initializeButtonForAddText(mSelectPDF, mSelectText, mCreateTextPDF);
            resetView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length < 1)
            return;
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionGranted = true;
                } else
                    showSnackbar(mActivity, R.string.snackbar_insufficient_permissions);
            }
        }
    }

    @Override
    public void onItemClick(String path) {
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mPdfpath = path;
        showSnackbar(mActivity, getResources().getString(R.string.snackbar_pdfselected));
    }

    @Override
    public void onPopulate(ArrayList<String> paths) {
        populateUtil(mActivity, paths, this, mLayout, mLottieProgress, mRecyclerViewFiles);
    }

    @Override
    public void closeBottomSheet() {
        closeBottomSheetUtil(sheetBehavior);
    }

    @Override
    public boolean checkSheetBehaviour() {
        return checkSheetBehaviourUtil(sheetBehavior);
    }
}

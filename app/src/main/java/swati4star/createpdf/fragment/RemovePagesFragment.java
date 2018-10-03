package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.RearrangePdfPages;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.interfaces.OnPDFCompressedInterface;
import swati4star.createpdf.util.BottomSheetCallback;
import swati4star.createpdf.util.BottomSheetUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PDFUtils;

import static android.app.Activity.RESULT_OK;
import static android.os.ParcelFileDescriptor.MODE_READ_ONLY;
import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.COMPRESS_PDF;
import static swati4star.createpdf.util.Constants.REMOVE_PAGES;
import static swati4star.createpdf.util.Constants.REORDER_PAGES;
import static swati4star.createpdf.util.Constants.RESULT;
import static swati4star.createpdf.util.DialogUtils.createAnimationDialog;
import static swati4star.createpdf.util.FileUriUtils.getFilePath;
import static swati4star.createpdf.util.FileUtils.getFormattedSize;
import static swati4star.createpdf.util.StringUtils.getSnackbarwithAction;
import static swati4star.createpdf.util.StringUtils.hideKeyboard;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class RemovePagesFragment extends Fragment implements MergeFilesAdapter.OnClickListener,
        OnPDFCompressedInterface {

    private Activity mActivity;
    private String mPath;
    private MorphButtonUtility mMorphButtonUtility;
    private FileUtils mFileUtils;
    private BottomSheetUtils mBottomSheetUtils;
    private PDFUtils mPDFUtils;
    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    private static final int INTENT_REQUEST_REARRANGE_PDF = 11;
    private String mOperation;
    private MaterialDialog mMaterialDialog;

    @BindView(R.id.selectFile)
    MorphingButton selectFileButton;
    @BindView(R.id.pdfCreate)
    MorphingButton createPdf;
    BottomSheetBehavior sheetBehavior;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    @BindView(R.id.downArrow)
    ImageView mDownArrow;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    @BindView(R.id.pages)
    EditText pagesInput;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    @BindView(R.id.infoText)
    TextView mInfoText;
    @BindView(R.id.compressionInfoText)
    TextView mCompressionInfoText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_remove_pages, container, false);
        ButterKnife.bind(this, rootview);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, mDownArrow));
        mOperation = getArguments().getString(BUNDLE_DATA);
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
        if (data == null || resultCode != RESULT_OK )
            return;
        if (requestCode == INTENT_REQUEST_PICKFILE_CODE)
            setTextAndActivateButtons(getFilePath(data.getData()));
        else if (requestCode == INTENT_REQUEST_REARRANGE_PDF) {
            String pages = data.getStringExtra(RESULT);
            Log.v("output", pages + " ");
            String outputPath = mPath.replace(mActivity.getString(R.string.pdf_ext),
                    "_edited" + pages + mActivity.getString(R.string.pdf_ext));
            if (mPDFUtils.isPDFEncrypted(mPath)) {
                showSnackbar(mActivity, R.string.encrypted_pdf);
                return;
            }

            mPDFUtils.reorderRemovePDF(mPath, outputPath, pages);
            resetValues();
        }
    }

    @OnClick(R.id.pdfCreate)
    public void parse() {
        hideKeyboard(mActivity);
        if (mOperation.equals(COMPRESS_PDF)) {
            compressPDF();
            return;
        }

        // Render pdf pages as bitmap
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        ParcelFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = ParcelFileDescriptor.open(new File(mPath), MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(fileDescriptor);
            final int pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);

                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(),
                        Bitmap.Config.ARGB_8888);
                // say we render for showing on the screen
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                // do stuff with the bitmap
                bitmaps.add(bitmap);
                // close the page
                page.close();
            }

            // close the renderer
            renderer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmaps.size() < 1) {
            showSnackbar(mActivity, R.string.file_access_error);
        } else {
            RearrangePdfPages.mImages = bitmaps;
            startActivityForResult(RearrangePdfPages.getStartIntent(mActivity),
                    INTENT_REQUEST_REARRANGE_PDF);
        }

    }

    private void compressPDF() {
        String input = pagesInput.getText().toString();
        int check;
        try {
            check = Integer.parseInt(String.valueOf(input));
            if (check > 100 || check <= 0) {
                showSnackbar(mActivity, R.string.invalid_entry);
            } else {
                String outputPath = mPath.replace(mActivity.getString(R.string.pdf_ext),
                        "_edited" + check + mActivity.getString(R.string.pdf_ext));
                mPDFUtils.compressPDF(mPath, outputPath, 100 - check, this);
            }
        } catch (NumberFormatException e) {
            showSnackbar(mActivity, R.string.invalid_entry);
        }
    }

    private void resetValues() {
        mPath = null;
        pagesInput.setText(null);
        mMorphButtonUtility.initializeButton(selectFileButton, createPdf);
        switch (mOperation) {
            case REORDER_PAGES:
            case REMOVE_PAGES:
                mInfoText.setVisibility(View.GONE);
                pagesInput.setVisibility(View.GONE);
                break;
            case COMPRESS_PDF:
                mInfoText.setText(R.string.compress_pdf_prompt);
                break;
        }
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
        mPath = path;
        mCompressionInfoText.setVisibility(View.GONE);
        mMorphButtonUtility.setTextAndActivateButtons(path,
                selectFileButton, createPdf);
    }

    @Override
    public void pdfCompressionStarted() {
        mMaterialDialog = createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void pdfCompressionEnded(String path, Boolean success) {
        mMaterialDialog.dismiss();
        if (success) {
            getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction, v -> mFileUtils.openFile(path)).show();
            new DatabaseHelper(mActivity).insertRecord(path,
                    mActivity.getString(R.string.created));
            File input = new File(mPath);
            File output = new File(path);
            mCompressionInfoText.setVisibility(View.VISIBLE);
            mCompressionInfoText.setText(String.format(mActivity.getString(R.string.compress_info),
                    getFormattedSize(input),
                    getFormattedSize(output)));
        } else {
            showSnackbar(mActivity, R.string.encrypted_pdf);
        }
        resetValues();
    }
}

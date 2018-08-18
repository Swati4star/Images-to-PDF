package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.morphingbutton.MorphingButton;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.util.DirectoryUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PDFUtils;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.REORDER_PAGES;
import static swati4star.createpdf.util.FileUriUtils.getFilePath;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class RemovePagesFragment extends Fragment implements MergeFilesAdapter.OnClickListener {

    private Activity mActivity;
    private String mPath;
    private MorphButtonUtility mMorphButtonUtility;
    private FileUtils mFileUtils;
    private PDFUtils mPDFUtils;
    private DirectoryUtils mDirectoryUtils;
    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;

    @BindView(R.id.selectFile)
    Button selectFileButton;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_remove_pages, container, false);
        ButterKnife.bind(this, rootview);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setBottomSheetCallback(new RemovePagesFragment.BottomSheetCallback());

        String argument = getArguments().getString(BUNDLE_DATA);
        if (argument.equals(REORDER_PAGES))
            mInfoText.setText(R.string.reorder_pages_text);
        else
            mInfoText.setText(R.string.remove_pages_text);


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

        resetValues();

        return rootview;
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


    @OnClick(R.id.pdfCreate)
    public void parse() {
        String pages = pagesInput.getText().toString();
        String outputPath = mPath.replace(mActivity.getString(R.string.pdf_ext),
                "_edited" + pages + mActivity.getString(R.string.pdf_ext));
        hideKeyboard(mActivity);
        if (mPDFUtils.isPDFEncrypted(mPath)) {
            showSnackbar(mActivity, R.string.encrypted_pdf);
            return;
        }

        try {
            PdfReader reader = new PdfReader(mPath);
            reader.selectPages(pages);
            if (reader.getNumberOfPages() == 0) {
                showSnackbar(mActivity, R.string.remove_pages_error);
                return;
            }
            Log.e("pages", reader.getNumberOfPages() + " ");
            //if (reader.getNumberOfPages() )
            PdfStamper pdfStamper = new PdfStamper(reader,
                    new FileOutputStream(outputPath));
            pdfStamper.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            showSnackbar(mActivity, R.string.remove_pages_error);
            return;
        }
        Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content)
                , R.string.snackbar_pdfCreated, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_viewAction, v -> mFileUtils.openFile(outputPath)).show();
        new DatabaseHelper(mActivity).insertRecord(outputPath,
                mActivity.getString(R.string.created));
        resetValues();
    }

    private void resetValues() {
        mPath = null;
        pagesInput.setText(null);
        selectFileButton.setText(R.string.merge_file_select);
        selectFileButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        mMorphButtonUtility.morphToGrey(createPdf, mMorphButtonUtility.integer());
        createPdf.setEnabled(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mDirectoryUtils = new DirectoryUtils(mActivity);
        mPDFUtils = new PDFUtils(mActivity);
    }

    @Override
    public void onItemClick(String path) {
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setTextAndActivateButtons(path);
    }

    private void setTextAndActivateButtons(String path) {
        mPath = path;
        selectFileButton.setText(mPath);
        selectFileButton.setBackgroundColor(getResources().getColor(R.color.mb_green_dark));
        createPdf.setEnabled(true);
        mMorphButtonUtility.morphToSquare(createPdf, mMorphButtonUtility.integer());
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

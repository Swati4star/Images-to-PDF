package swati4star.createpdf.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.util.DirectoryUtils;
import swati4star.createpdf.util.StringUtils;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

import static android.app.Activity.RESULT_OK;

public class MergeFilesFragment extends Fragment implements MergeFilesAdapter.OnClickListener {
    private Activity mActivity;
    private boolean mSuccess;
    private String mFilename;
    private String mCheckbtClickTag = "";
    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    private String mRetfoldername;
    private String mRealPath;
    private String mDisplayName;
    private MaterialDialog mMaterialDialog;
    private LottieAnimationView mAnimationView;
    private MergeFilesAdapter mMergeFilesAdapter;
    private DirectoryUtils mDirectoryUtils;
    private ArrayList<String> mAllFilesPaths;
    @BindView(R.id.textView)
    TextView nosupport;
    @BindView(R.id.fileonebtn)
    Button addFileOne;
    @BindView(R.id.filetwobtn)
    Button addFileTwo;
    @BindView(R.id.mergebtn)
    Button mergeBtn;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    @BindView(R.id.viewFiles)
    TextView mViewFiles;
    @BindView(R.id.tableLayout)
    ConstraintLayout mTableLayout;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    @BindView(R.id.downArrow)
    ImageView mDownArrow;
    String firstFilePath;
    String secondFilePath;
    private boolean mFilesShowing = true;


    public MergeFilesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_merge_files, container, false);
        ButterKnife.bind(this, root);
        mDirectoryUtils = new DirectoryUtils(mActivity);
        mAllFilesPaths = getAllFilePaths();
        mMergeFilesAdapter = new MergeFilesAdapter(mActivity, mAllFilesPaths, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerViewFiles.setLayoutManager(mLayoutManager);
        mRecyclerViewFiles.setAdapter(mMergeFilesAdapter);
        mRecyclerViewFiles.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));

        return root;
    }

    @OnClick(R.id.viewFiles)
    void onViewFilesClick(View view) {
        if (mFilesShowing) {
            addFileOne.setVisibility(View.VISIBLE);
            addFileTwo.setVisibility(View.VISIBLE);
            mergeBtn.setVisibility(View.VISIBLE);
            mRecyclerViewFiles.setVisibility(View.GONE);
            mUpArrow.setVisibility(View.VISIBLE);
            mDownArrow.setVisibility(View.GONE);
            mFilesShowing = !mFilesShowing;
        } else {
            addFileOne.setVisibility(View.GONE);
            addFileTwo.setVisibility(View.GONE);
            mergeBtn.setVisibility(View.GONE);
            mRecyclerViewFiles.setVisibility(View.VISIBLE);
            mUpArrow.setVisibility(View.GONE);
            mDownArrow.setVisibility(View.VISIBLE);
            mFilesShowing = !mFilesShowing;
        }
    }

    @OnClick(R.id.fileonebtn)
    void startAddingPDF(View v) {
        mCheckbtClickTag = (v).getTag().toString();
        showFileChooser();
    }

    @OnClick(R.id.filetwobtn)
    void startAddingPDF2(View v) {
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
                        mFilename = input.toString();
                        new MergePdf().execute(pdfpaths);
                    }
                })
                .show();
    }

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
        if (data != null) {

            if (requestCode == INTENT_REQUEST_PICKFILE_CODE) {
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uri.toString());
                    String path = myFile.getPath();
                    mRealPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    //Check if First button is clicked from mCheckbtClickTag
                    if (addFileOne.getTag().toString().equals(mCheckbtClickTag)) {
                        firstFilePath = getFilePath(uriString, uri, myFile, path);
                        addFileOne.setText(firstFilePath);
                        addFileOne.setBackgroundColor(getResources().getColor(R.color.mb_green_dark));
                    } else {
                        secondFilePath = getFilePath(uriString, uri, myFile, path);
                        addFileTwo.setText(secondFilePath);
                        addFileTwo.setBackgroundColor(getResources().getColor(R.color.mb_green_dark));
                    }

                }
            }
        }
    }

    //Returns the complete filepath of the PDF as a string
    private String getFilePath(String uriString, Uri uri, File myFile, String path) {
        String filepath = null;
        if (uriString.startsWith("content://")) {
            mDisplayName = getFileName(uri);
            mSuccess = true;
        } else if (uriString.startsWith("file://")) {
            mDisplayName = myFile.getName();
            mSuccess = true;
        } else if (uriString.startsWith("content://") && uriString.contains("com.google.android.")) {
            mSuccess = false;
        }
        if (mSuccess) {
            String folname = getParentFolder(path);
            filepath = setPathontextview(folname);
        }
        return filepath;
    }

    private String getParentFolder(String p) {
        try {
            //Get Name of Parent Folder of File
            // Folder Name found between first occurance of string %3A and %2F from path
            // of content://...
            if (p.contains("%3A")) {
                int beg = p.indexOf("%3A") + 3;
                mRetfoldername = p.substring(beg, p.indexOf("%2F"));
                Log.d("img", mRetfoldername);
            } else {
                mRetfoldername = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mRetfoldername;
    }

    private String getFileName(Uri uri) {
        Cursor cursor;
        try {
            cursor = mActivity.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                mDisplayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
            }
            mSuccess = true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return mDisplayName;
    }

    //Returns the folder and file name as string
    private String setPathontextview(String folname) {
        if (folname != null) {
            String c = getString(R.string.path_seperator);
            mRealPath = mRealPath + c + folname + c + mDisplayName;
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

    private ArrayList<String> getAllFilePaths() {
        ArrayList<String> pdfPaths = new ArrayList<>();
        ArrayList<File> pdfFiles;
        ArrayList<File> pdfFromOtherDir = mDirectoryUtils.getPdfFromOtherDirectories();
        final File[] files = mDirectoryUtils.getOrCreatePdfDirectory().listFiles();
        if ((files == null || files.length == 0) && pdfFromOtherDir == null) {
            return null;
        } else {

            pdfFiles = mDirectoryUtils.getPdfsFromPdfFolder(files);
            if (pdfFromOtherDir != null) {
                pdfFiles.addAll(pdfFromOtherDir);
            }
        }
        if (pdfFiles != null) {
            for (File pdf : pdfFiles) {
                pdfPaths.add(pdf.getAbsolutePath());
            }
        }
        return pdfPaths;
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
                            break;
                        case 1:
                            addFileTwo.setText(path);
                            secondFilePath = path;
                            mSuccess = true;
                            break;
                    }
                })
                .show();

    }

    @SuppressLint("StaticFieldLeak")
    private class MergePdf extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMaterialDialog = new MaterialDialog.Builder(mActivity)
                    .customView(R.layout.lottie_anim_dialog, false)
                    .build();
            mAnimationView = mMaterialDialog.getCustomView().findViewById(R.id.animation_view);
            mAnimationView.playAnimation();
            mMaterialDialog.show();
        }

        @Override
        protected Void doInBackground(String... pdfpaths) {
            try {
                // Create document object
                Document document = new Document();
                // Create pdf copy object to copy current document to the output mergedresult file
                String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        MergeFilesFragment.this.getString(R.string.pdf_dir);
                mFilename = mFilename + getString(R.string.pdf_ext);
                String finPath = mPath + mFilename;
                PdfCopy copy = new PdfCopy(document, new FileOutputStream(finPath));
                // Open the document
                document.open();
                PdfReader pdfreader;
                int numOfPages;
                for (String pdfPath : pdfpaths) {
                    // Create pdf reader object to read each input pdf file
                    pdfreader = new PdfReader(pdfPath);
                    // Get the number of pages of the pdf file
                    numOfPages = pdfreader.getNumberOfPages();
                    for (int page = 1; page <= numOfPages; page++) {
                        // Import all pages from the file to PdfCopy
                        copy.addPage(copy.getImportedPage(pdfreader, page));
                    }
                }
                document.close(); // close the document
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAnimationView.cancelAnimation();
            mMaterialDialog.dismiss();
            showSnackbar(R.string.pdf_merged);
        }
    }
}

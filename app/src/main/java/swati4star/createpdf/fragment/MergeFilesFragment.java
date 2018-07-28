package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.util.StringUtils;

import static android.app.Activity.RESULT_OK;
public class MergeFilesFragment extends Fragment {
    private Activity mActivity;
    private boolean mSuccess;
    private String mFilename;
    private String mCheckbtClickTag = "";
    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    private String mRetfoldername;
    private String mRealPath;
    private String mDisplayName;
    @BindView(R.id.textView)
    TextView nosupport;
    @BindView(R.id.fileonebtn)
    Button addFileOne;
    @BindView(R.id.filetwobtn)
    Button addFileTwo;
    @BindView(R.id.mergebtn)
    Button mergeBtn;
    String firstFilePath;
    String secondFilePath;


    public MergeFilesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_merge_files, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @OnClick(R.id.fileonebtn)
    void startAddingPDF(View v) {
        Log.d("img", "startAddingPDF: ");
        mCheckbtClickTag = (v).getTag().toString();
        showFileChooser();
    }

    @OnClick(R.id.filetwobtn)
    void startAddingPDF2(View v) {
        Log.d("img", "startAddingPDF: ");
        mCheckbtClickTag = (v).getTag().toString();
        showFileChooser();
    }

    @OnClick(R.id.mergebtn)
    void mergeFiles(final View v) {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (StringUtils.isEmpty(input)) {
                            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                    R.string.snackbar_name_not_blank,
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            mFilename = input.toString();
                            mergePdfFiles();
                        }
                    }
                })
                .show();
    }

    private void mergePdfFiles() {
        try {

            String[] pdfpaths = {firstFilePath, secondFilePath};

            if (firstFilePath.isEmpty() || secondFilePath.isEmpty() || !mSuccess) {
                mergeBtn.setEnabled(false);
                Toast.makeText(this.getContext(), getString(R.string.pdf_merge_error), Toast.LENGTH_SHORT).show();
            } else {

                mergeBtn.setEnabled(true);
                mergePdf(pdfpaths);
                Toast.makeText(this.getContext(), getString(R.string.pdf_merge), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mergePdf(String[] pdfpaths) {
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
            int numopages;
            for (String pdfpath : pdfpaths) {
                // Create pdf reader object to read each input pdf file
                pdfreader = new PdfReader(pdfpath);
                // Get the number of pages of the pdf file
                numopages = pdfreader.getNumberOfPages();
                for (int page = 1; page <= numopages; page++) {
                    // Import all pages from the file to PdfCopy
                    copy.addPage(copy.getImportedPage(pdfreader, page));
                }
            }
            document.close(); // close the document
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFileChooser() {

        String folderPath = Environment.getExternalStorageDirectory() + "/";
        Intent intent = new Intent();
        intent.setAction( Intent.ACTION_GET_CONTENT);
        Uri myUri = Uri.parse(folderPath);
        intent.setDataAndType( myUri , getString(R.string.pdf_type));
        Intent intentChooser = Intent.createChooser(intent , getString(R.string.merge_file_select));
        startActivityForResult(intentChooser , INTENT_REQUEST_PICKFILE_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) throws NullPointerException {
        if (data != null) {

            if (requestCode == INTENT_REQUEST_PICKFILE_CODE ) {
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uri.toString());
                    String path = myFile.getPath();
                    mRealPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    //Check if First button is clicked from mCheckbtClickTag
                    if (addFileOne.getTag().toString().equals(mCheckbtClickTag)) {
                        firstFilePath = getFilePath(uriString , uri , myFile, path);
                        addFileOne.setText(firstFilePath);
                        if (firstFilePath.length()>15) {
                            addFileOne.setTextSize(10);
                        }

                        addFileOne.setBackgroundColor(getResources().getColor(R.color.mb_green_dark));
                    } else {
                        secondFilePath = getFilePath(uriString , uri, myFile, path);
                        addFileTwo.setText(secondFilePath);
                        if (secondFilePath.length()>15) {
                            addFileTwo.setTextSize(10);
                        }

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

    private String  getParentFolder(String p) {
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
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                mDisplayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
            cursor.close();
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
    public void onSaveInstanceState(@NonNull  Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.btn_sav_text), mCheckbtClickTag);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }
}

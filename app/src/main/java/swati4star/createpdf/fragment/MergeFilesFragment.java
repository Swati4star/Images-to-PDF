package swati4star.createpdf.fragment;

import android.Manifest;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

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
    private final Context mContext;
    private String mPath;
    public String filepath1, filepath2;
    boolean success;
    String mFilename;
    public String checkbtClickTag = "";
    public static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    String retfoldername;
    String realPath , displayName;
    @BindView(R.id.textView)
    TextView nosupport;
    @BindView(R.id.fileonebtn)
    Button addFileOne;
    @BindView(R.id.filetwobtn)
    Button addFileTwo;
    @BindView(R.id.mergebtn)
    Button mergeBtn;
    @BindView(R.id.txtfirstpdf)
    TextView filepathtv1;
    @BindView(R.id.txtsecondpdf)
    TextView filepathtv2;


    public MergeFilesFragment() {
        // Required empty public constructor
        this.mContext = this.getContext();
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
        checkbtClickTag = (v).getTag().toString();
        showFileChooser();
    }

    @OnClick(R.id.filetwobtn)
    void startAddingPDF2(View v) {
        Log.d("img", "startAddingPDF: ");
        checkbtClickTag = (v).getTag().toString();
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
                            mergePdfFiles(v);
                        }
                    }
                })
                .show();
    }

    public void mergePdfFiles(View view) {
        try {

            filepath1 = filepathtv1.getText().toString();
            filepath2 = filepathtv2.getText().toString();
            String[] pdfpaths = { filepath1 , filepath2 };

            if (filepath1.isEmpty() || filepath2.isEmpty() || !success) {
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

    public void mergePdf(String[] pdfpaths) {
        try {
            // Create document object
            Document document = new Document();
            // Create pdf copy object to copy current document to the output mergedresult file
            mPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    MergeFilesFragment.this.getString(R.string.pdf_dir);
            mFilename = mFilename + getString(R.string.pdf_ext);
            String finPath = mPath + mFilename;
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(finPath));
            // Open the document
            document.open();
            PdfReader pdfreader;
            int numopages;
            for (int i = 0; i < pdfpaths.length; i++) {
                // Create pdf reader object to read each input pdf file
                pdfreader = new PdfReader(pdfpaths[i]);
                // Get the number of pages of the pdf file
                numopages = pdfreader.getNumberOfPages();
                for (int page = 1; page <= numopages; page++) {
                    // Import all pages from the file to PdfCopy
                    copy.addPage( copy.getImportedPage(pdfreader , page));
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
                    realPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    //Check if First button is clicked from checkbtClickTag
                    if (addFileOne.getTag().toString().equals(checkbtClickTag)) {
                        if (uriString.startsWith("content://")) {        //If file path is of content:// type
                            displayName = getFileName(uri);
                            success = true;
                        } else if (uriString.startsWith("file://")) {
                            //If file path is of file:// type
                            displayName = myFile.getName();
                            Log.d("img", displayName);
                            success = true;
                        } else if (uriString.startsWith("content://") && uriString.contains("com.google.android.")) {
                            //If file path is of content:// type and is a google drive document
                            success = false;
                        }
                        //If success is true, get full path
                        if (success) {
                            String folname = getParentFolder(path);
                            setPathontextview(folname , filepathtv1);
                        }
                    } else {
                        if (uriString.startsWith("content://")) {
                            displayName = getFileName(uri);
                            success = true;

                        } else if (uriString.startsWith("file://")) {
                            displayName = myFile.getName();
                            success = true;
                        } else if (uriString.startsWith("content://") && uriString.contains("com.google.android.")) {
                            success = false;
                        }
                        if (success) {
                            String folname = getParentFolder(path);
                            setPathontextview(folname , filepathtv2);
                        }

                    }

                }
            }
        }
    }


    public String  getParentFolder(String p) {
        try {
            //Get Name of Parent Folder of File
            // Folder Name found between first occurance of string %3A and %2F from path
            // of content://...
            if (p.contains("%3A")) {
                int beg = p.indexOf("%3A") + 3;
                retfoldername = p.substring(beg, p.indexOf("%2F"));
                Log.d("img", retfoldername);
            } else {
                retfoldername = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retfoldername;
    }

    public String getFileName(Uri uri) {
        Cursor cursor;
        try {
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
            cursor.close();
            success = true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return displayName;
    }

    public void setPathontextview(String folname , TextView t1) {
        if (folname != null) {
            String c = getString(R.string.path_seperator);
            realPath = realPath + c + folname + c + displayName;
            //Set value to textview2
            t1.setText(realPath);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            checkbtClickTag = savedInstanceState.getString("savText");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull  Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.btn_sav_text), checkbtClickTag);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }
}

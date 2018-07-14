package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.util.Objects;

import swati4star.createpdf.R;
import swati4star.createpdf.util.PDFUtils;
import swati4star.createpdf.util.StringUtils;

import static android.app.Activity.RESULT_OK;

public class TextToPdfFragment extends Fragment {

    private  final int mFileSelectCode = 0;
    private  Uri mTextFileUri = null;
    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.fragment_text_to_pdf, container, false);

        Button selectButton = rootview.findViewById(R.id.selectFile);
        selectButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textFileSelect();
                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                        R.string.text_file_selected,
                        Snackbar.LENGTH_LONG).show();
            }
        });
        Button createButton = rootview.findViewById(R.id.createtextpdf);
        createButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openCreateTextPdf();
            }
        });
        return rootview;
    }
    public void openCreateTextPdf() {
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
                            String mFilename = input.toString();
                            try {
                                PDFUtils.createPdf(mTextFileUri, mFilename);
                                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                        R.string.snackbar_pdfCreated,
                                        Snackbar.LENGTH_LONG).show();
                            } catch (DocumentException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .show();
    }
    /**
     * Create a file picker to get text file.
     */
    private void textFileSelect() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, String.valueOf(R.string.select_file)),
                    mFileSelectCode);
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                    R.string.install_file_manager,
                    Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case mFileSelectCode:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    mTextFileUri = uri;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }
}

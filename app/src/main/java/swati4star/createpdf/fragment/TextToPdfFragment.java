package swati4star.createpdf.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import swati4star.createpdf.R;
import swati4star.createpdf.util.StringUtils;

import static android.app.Activity.RESULT_OK;

public class TextToPdfFragment extends Fragment {
    private static final int FILE_SELECT_CODE = 0;
    private static Uri TEXT_FILE_URI = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.fragment_text_to_pdf, container, false);

        Button selectButton = (Button) rootview.findViewById(R.id.selectFile);
        selectButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textFileSelect();
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content),
                        R.string.text_file_selected,
                        Snackbar.LENGTH_LONG).show();
            }
        });
        Button createButton = (Button) rootview.findViewById(R.id.createtextpdf);
        createButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openCreateTextPdf();
            }
        });
        return rootview;
    }
    public void openCreateTextPdf() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (StringUtils.isEmpty(input)) {
                            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content),
                                    R.string.snackbar_name_not_blank,
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            String mFilename = input.toString();
                            try {
                                createPdf(TEXT_FILE_URI, mFilename);
                                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content),
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
    public void createPdf(Uri fileURI, String outputFile)
            throws DocumentException, IOException {

        Document document = new Document();
        String finalOutput = Environment.getExternalStorageDirectory() + "/" + "PDFfiles" + "/" + outputFile + ".pdf";
        PdfWriter.getInstance(document, new FileOutputStream(finalOutput)).setPdfVersion(PdfWriter.VERSION_1_7);

        document.open();
        Font myfont = new Font();
        myfont.setStyle(Font.NORMAL);
        myfont.setSize(11);

        document.add(new Paragraph("\n"));
        readTextFile(fileURI, document, myfont);
        document.close();
    }
    private void readTextFile(Uri uri, Document document, Font myfont) {
        InputStream inputStream;
        try {
            inputStream = getActivity().getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                Paragraph para = new Paragraph(line + "\n", myfont);
                para.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(para);
            }
            reader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void textFileSelect() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content),
                    R.string.install_file_manager,
                    Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    TEXT_FILE_URI = uri;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }
}

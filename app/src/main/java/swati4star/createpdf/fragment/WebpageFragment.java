package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Objects;

import swati4star.createpdf.R;
import swati4star.createpdf.util.StringUtils;


public class WebpageFragment extends Fragment {

    private Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_webpage, container, false);
        Button urlToPdf = (Button)rootView.findViewById(R.id.createpdf_url);
        urlToPdf.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText urlText = (EditText)rootView.findViewById(R.id.edittext_url);
                String url =  urlText.getText().toString();
                createPdf(url);
            }
        });
        return rootView;
    }
    private void createPdf(final String url) {
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content),
                    R.string.invalid_url,
                    Snackbar.LENGTH_LONG).show();
            return;
        }
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String code = null;
                        if (StringUtils.isEmpty(input)) {
                            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                    R.string.snackbar_name_not_blank,
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            String filename = input.toString();
                            getCode(url, filename);
                        }
                    }
                })
                .show();
    }
    private void getCode(String url, final String path) {
        Ion.with(mActivity).load(url).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                try {
                    getPdf(path, result);
                } catch (IOException | DocumentException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
    private void getPdf(String path, String sourceCode) throws IOException, DocumentException {
        Log.e("log","CODE : " + sourceCode);
        String finalOutputString = "/storage/emulated/0/PDFfiles/" + path + ".pdf";
        OutputStream file = new FileOutputStream(new File(finalOutputString));
        Document document = new Document();
        PdfWriter.getInstance(document, file);
        document.open();
        HTMLWorker htmlWorker = new HTMLWorker(document);
        htmlWorker.parse(new StringReader(sourceCode));
        document.close();
        file.close();
        }
    }

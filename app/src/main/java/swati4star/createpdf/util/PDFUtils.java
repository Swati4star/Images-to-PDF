package swati4star.createpdf.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import swati4star.createpdf.R;

public class PDFUtils {

    private Activity mContext;
    String mPassword;
    String outputfile;

    public PDFUtils(Activity context) {
        this.mContext = context;
    }


    /**
     * Opens the password dialog to set Password for an existing PDF file.
     *
     * @param filePath Path of file to be encrypted
     * @return String - path of output file
     */
    public String setPassword(final String filePath) {
        final MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title(R.string.set_password)
                .customView(R.layout.custom_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .build();


        final View mPositiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        EditText mPasswordInput = dialog.getCustomView().findViewById(R.id.password);
        mPasswordInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        mPositiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable input) {
                        if (StringUtils.isEmpty(input)) {
                            Snackbar.make(Objects.requireNonNull(mContext).findViewById(android.R.id.content),
                                    R.string.snackbar_password_cannot_be_blank,
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            mPassword = input.toString();

                        }
                    }
                });
        dialog.show();
        mPositiveAction.setEnabled(false);
        mPositiveAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    outputfile = doEncryption(filePath, mPassword);
                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                Snackbar.make(Objects.requireNonNull(mContext).findViewById(android.R.id.content),
                        R.string.password_added,
                        Snackbar.LENGTH_LONG).show();
            }
        });
        return outputfile;
    }

    /**
     * Uses PDF Reader to set encryption in pdf file.
     *
     * @param path - Path of pdf file to be encrypted
     * @param password - password to be encrypted with
     * @return string - path of output file
     */
    public String  doEncryption(String path, String password) throws IOException, DocumentException {
        String finalOutputFile = path.replace(".pdf", mContext.getString(R.string.encrypted_file));
        Log.e("Log", finalOutputFile);
        PdfReader reader = new PdfReader(path);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
        stamper.setEncryption(password.getBytes(), mContext.getString(R.string.app_name).getBytes(),
                PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY, PdfWriter.ENCRYPTION_AES_256);
        stamper.close();
        reader.close();
        return finalOutputFile;
    }

    /**
     * Creates a dialog with details of given PDF file
     * @param name - file name
     * @param path - file path
     * @param size - file size
     * @param lastModDate - file's last modified date
     */
    public void showDetails(String name, String path, String size, String lastModDate) {
        TextView message = new TextView(mContext);
        TextView title = new TextView(mContext);
        message.setText("\n  File Name : " + name
                + "\n\n  Path : " + path
                + "\n\n  Size : " + size
                + " \n\n  Date Modified : " + lastModDate);
        message.setTextIsSelectable(true);
        title.setText(R.string.details);
        title.setPadding(20, 10, 10, 10);
        title.setTextSize(30);
        title.setTextColor(mContext.getResources().getColor(R.color.black_54));
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.create();
        builder.setView(message);
        builder.setCustomTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

}

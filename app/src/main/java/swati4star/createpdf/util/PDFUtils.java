package swati4star.createpdf.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.DataSetChanged;

public class PDFUtils {

    private final Activity mContext;
    private String mPassword;

    public PDFUtils(Activity context) {
        this.mContext = context;
    }


    /**
     * Opens the password dialog to set Password for an existing PDF file.
     *
     * @param filePath Path of file to be encrypted
     */
    public void setPassword(final String filePath, final DataSetChanged dataSetChanged) {
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
                            showSnackbar(R.string.snackbar_password_cannot_be_blank);
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
                    doEncryption(filePath, mPassword);
                    dataSetChanged.updateDataset();
                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                showSnackbar(R.string.password_added);
            }
        });
    }

    /**
     * Uses PDF Reader to set encryption in pdf file.
     *
     * @param path - Path of pdf file to be encrypted
     * @param password - password to be encrypted with
     * @return string - path of output file
     */
    private String  doEncryption(String path, String password) throws IOException, DocumentException {
        String finalOutputFile = path.replace(".pdf", mContext.getString(R.string.encrypted_file));
        PdfReader reader = new PdfReader(path);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
        stamper.setEncryption(password.getBytes(), mContext.getString(R.string.app_name).getBytes(),
                PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY, PdfWriter.ENCRYPTION_AES_128);
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
    /**
     * Uses PDF Reader to decrypt the PDF.
     *
     * @param file Path of pdf file to be decrypted
     */
    public void removePassword(final String file, final DataSetChanged dataSetChanged, final ArrayList<File> FileList) {

        PdfReader reader = null;
        try {
            reader = new PdfReader(file, mContext.getString(R.string.app_name).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Check if PDF is encrypted or not.
        if (!reader.isEncrypted()) {
            showSnackbar(R.string.not_encrypted);
            return;
        }
        final String[] input_password = new String[1];
        final MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title(R.string.enter_password)
                .customView(R.layout.custom_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .build();

        final View mPositiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        final EditText mPasswordInput = dialog.getCustomView().findViewById(R.id.password);
        TextView text = dialog.getCustomView().findViewById(R.id.enter_password);
        text.setText(R.string.decrypt_message);
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
                        input_password[0] = input.toString();
                    }
                });
        dialog.show();
        mPositiveAction.setEnabled(false);
        mPositiveAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String finalOutputFile;
                PdfReader reader = null;
                try {
                    reader = new PdfReader(file, mContext.getString(R.string.app_name).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] password;
                finalOutputFile = file.replace(".pdf", mContext.getString(R.string.decrypted_file));
                for (int i = 0; i < FileList.size(); i++) {

                    if(finalOutputFile.equals(FileList.get(i).getPath())) {
                        int append = checkRepeat(finalOutputFile, FileList);
                        finalOutputFile = finalOutputFile.replace(".pdf",append+".pdf");
                        break;
                    }
                }
                password = reader.computeUserPassword();
                byte[] input = input_password[0].getBytes();
                if (Arrays.equals(input, password)) {
                    PdfStamper stamper = null;
                    try {
                        stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
                    } catch (DocumentException | IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        stamper.close();
                    } catch (DocumentException | IOException e) {
                        e.printStackTrace();
                    }
                    showSnackbar(R.string.password_remove);
                    reader.close();
                    dialog.dismiss();
                    dataSetChanged.updateDataset();
                } else {
                    showSnackbar(R.string.incorrect_passowrd);
                    dialog.dismiss();
                }

            }
        });
    }
    /**
     * Checks if the new decrypted file already exists.
     *
     * @param finalOutputFile Path of pdf file to check
     * @param File File List of all PDFs
     * @return Number to be added finally in the name
     */
    private int checkRepeat(String finalOutputFile, final ArrayList<File> File) {
        int flag =1;
        int append = 1;
        while(flag == 1) {
            for (int i=0; i < File.size(); i++) {
                flag =0;
                if(finalOutputFile.equals(File.get(i).getPath())) {
                    flag = 1;
                    append++;
                    break;
                }
            }
            finalOutputFile = finalOutputFile.replace(".pdf",append+".pdf");
        }
        return append;
    }
    /**
     * Creates Snackbar
     */
    private void showSnackbar(int input) {
        Snackbar.make(Objects.requireNonNull(mContext).findViewById(android.R.id.content),
                input,
                Snackbar.LENGTH_LONG).show();
    }
}

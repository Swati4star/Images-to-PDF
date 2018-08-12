package swati4star.createpdf.util;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.exceptions.BadPasswordException;
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

public class PDFEncryptionUtility {

    private final Activity mContext;
    private final FileUtils mFileUtils;
    private String mPassword;

    final MaterialDialog dialog;

    public PDFEncryptionUtility(Activity context) {
        this.mContext = context;
        this.mFileUtils = new FileUtils(context);
        dialog = new MaterialDialog.Builder(mContext)
                .customView(R.layout.custom_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .build();
    }

    /**
     * Opens the password dialog to set Password for an existing PDF file.
     *
     * @param filePath Path of file to be encrypted
     */
    public void setPassword(final String filePath, final DataSetChanged dataSetChanged,
                            final ArrayList<File> mFileList) {

        dialog.setTitle(R.string.set_password);
        final View mPositiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        assert dialog.getCustomView() != null;
        EditText mPasswordInput = dialog.getCustomView().findViewById(R.id.password);
        mPasswordInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        mPositiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable input) {
                        if (StringUtils.isEmpty(input))
                            showSnackbar(R.string.snackbar_password_cannot_be_blank);
                        else
                            mPassword = input.toString();
                    }
                });
        dialog.show();
        mPositiveAction.setEnabled(false);
        mPositiveAction.setOnClickListener(v -> {
            try {
                doEncryption(filePath, mPassword, mFileList);
                dataSetChanged.updateDataset();
                showSnackbar(R.string.password_added);
            } catch (BadPasswordException e) {
                e.printStackTrace();
                showSnackbar(R.string.cannot_add_password);
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });
    }

    /**
     * Uses PDF Reader to set encryption in pdf file.
     *
     * @param path     - Path of pdf file to be encrypted
     * @param password - password to be encrypted with
     * @return string - path of output file
     */
    private String doEncryption(String path, String password,
                                final ArrayList<File> mFileList) throws IOException, DocumentException {
        String finalOutputFile = path.replace(mContext.getString(R.string.pdf_ext),
                mContext.getString(R.string.encrypted_file));

        if (mFileUtils.isFileExist(finalOutputFile)) {
            int append = mFileUtils.checkRepeat(finalOutputFile, mFileList);
            finalOutputFile = finalOutputFile.replace(mContext.getString(R.string.pdf_ext),
                    append + mContext.getResources().getString(R.string.pdf_ext));
        }

        PdfReader reader = new PdfReader(path);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
        stamper.setEncryption(password.getBytes(), mContext.getString(R.string.app_name).getBytes(),
                PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY, PdfWriter.ENCRYPTION_AES_128);
        stamper.close();
        reader.close();
        return finalOutputFile;
    }

    /**
     * Checks if PDf is encrpyted
     * @param file - path of PDF file
     * @return true, if PDF is encrypted, otherwise false
     */
    private boolean isPDFEncrypted(final String file) {
        PdfReader reader = null;
        try {
            reader = new PdfReader(file, mContext.getString(R.string.app_name).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Check if PDF is encrypted or not.
        if (!reader.isEncrypted()) {
            showSnackbar(R.string.not_encrypted);
            return false;
        }
        return true;
    }

    /**
     * Uses PDF Reader to decrypt the PDF.
     *
     * @param file Path of pdf file to be decrypted
     */
    public void removePassword(final String file,
                               final DataSetChanged dataSetChanged,
                               final ArrayList<File> mFileList) {

        if (!isPDFEncrypted(file))
            return;

        final String[] input_password = new String[1];
        dialog.setTitle(R.string.enter_password);
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
        mPositiveAction.setOnClickListener(v -> {
            String finalOutputFile;
            PdfReader reader = null;
            try {
                reader = new PdfReader(file, mContext.getString(R.string.app_name).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] password;
            finalOutputFile = file.replace(mContext.getResources().getString(R.string.pdf_ext),
                    mContext.getString(R.string.decrypted_file));

            if (mFileUtils.isFileExist(finalOutputFile)) {
                int append = mFileUtils.checkRepeat(finalOutputFile, mFileList);
                finalOutputFile = finalOutputFile.replace(mContext.getResources().getString(R.string.pdf_ext),
                        append + mContext.getResources().getString(R.string.pdf_ext));
            }

            password = reader.computeUserPassword();
            byte[] input = input_password[0].getBytes();
            if (Arrays.equals(input, password)) {
                try {
                    PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
                    stamper.close();
                } catch (DocumentException | IOException e) {
                    e.printStackTrace();
                }
                showSnackbar(R.string.password_remove);
                reader.close();
                dataSetChanged.updateDataset();
            } else {
                showSnackbar(R.string.incorrect_passowrd);
            }
            dialog.dismiss();
        });
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

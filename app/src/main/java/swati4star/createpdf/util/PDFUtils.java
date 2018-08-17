package swati4star.createpdf.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Environment;
import android.text.InputType;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import swati4star.createpdf.R;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.model.TextToPDFOptions;

import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class PDFUtils {

    private final Activity mContext;
    private final FileUtils mFileUtils;

    public PDFUtils(Activity context) {
        this.mContext = context;
        this.mFileUtils = new FileUtils(mContext);
    }

    /**
     * Creates a mDialog with details of given PDF file
     *
     * @param file - file name
     */
    public void showDetails(File file) {

        String name = file.getName();
        String path = file.getPath();
        String size = FileUtils.getFormattedSize(file);
        String lastModDate = FileUtils.getFormattedSize(file);

        TextView message = new TextView(mContext);
        TextView title = new TextView(mContext);
        message.setText(String.format
                (mContext.getResources().getString(R.string.file_info), name, path, size, lastModDate));
        message.setTextIsSelectable(true);
        title.setText(R.string.details);
        title.setPadding(20, 10, 10, 10);
        title.setTextSize(30);
        title.setTextColor(mContext.getResources().getColor(R.color.black_54));
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.create();
        builder.setView(message);
        builder.setCustomTitle(title);
        builder.setPositiveButton(mContext.getResources().getString(R.string.ok),
                (dialogInterface, i) -> dialog.dismiss());
        builder.create();
        builder.show();
    }


    /**
     * Create a PDF from a Text File
     *
     * @param mTextToPDFOptions TextToPDFOptions Object
     */
    public void createPdf(TextToPDFOptions mTextToPDFOptions)
            throws DocumentException, IOException {

        Document document = new Document(PageSize.getRectangle(mTextToPDFOptions.getPageSize()));
        String finalOutput = Environment.getExternalStorageDirectory() + "/" + "PDFfiles" + "/" +
                mTextToPDFOptions.getOutFileName() + ".pdf";
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(finalOutput));
        writer.setPdfVersion(PdfWriter.VERSION_1_7);
        if (mTextToPDFOptions.isPasswordProtected()) {
            writer.setEncryption(mTextToPDFOptions.getPassword().getBytes(),
                    mContext.getString(R.string.app_name).getBytes(),
                    PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY,
                    PdfWriter.ENCRYPTION_AES_128);
        }

        document.open();
        Font myfont = new Font(mTextToPDFOptions.getFontFamily());
        myfont.setStyle(Font.NORMAL);
        myfont.setSize(mTextToPDFOptions.getFontSize());

        document.add(new Paragraph("\n"));
        readTextFile(mTextToPDFOptions.getInFileUri(), document, myfont);
        document.close();

        new DatabaseHelper(mContext).insertRecord(finalOutput, mContext.getString(R.string.created));
    }

    /**
     * Read the Text File and put it in document
     *
     * @param uri      URL to create PDF
     * @param document PDF Document
     * @param myfont   Font style in PDF
     */
    private void readTextFile(Uri uri, Document document, Font myfont) {
        InputStream inputStream;
        try {
            inputStream = mContext.getContentResolver().openInputStream(uri);
            if (inputStream == null)
                return;
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

    /**
     * Check if a PDF at given path is encrypted
     *
     * @param path - path of PDF
     * @return true - if encrypted otherwise false
     */
    public boolean isPDFEncrypted(String path) {
        boolean isEncrypted = false;
        try {
            new PdfReader(path);
        } catch (IOException e) {
            isEncrypted = true;
        }
        return isEncrypted;
    }

    /**
     * Show the dialog for angle of rotation of pdf pages
     * @param sourceFilePath - path of file to be rotated
     */
    public void rotatePages(String sourceFilePath) {
        new MaterialDialog.Builder(mContext)
                .title(R.string.rotate_pages)
                .content(R.string.enter_rotation_angle)
                .input(null, null, false, (dialog, input) -> {
                    String destFilePath = mFileUtils.getFileDirectoryPath(sourceFilePath);
                    String fileName = mFileUtils.getFileName(sourceFilePath);
                    destFilePath += String.format(mContext.getString(R.string.rotated_file_name),
                            fileName.substring(0, fileName.lastIndexOf('.')), input,
                            mContext.getString(R.string.pdf_ext));
                    boolean result = rotatePDFPages(input.toString(), sourceFilePath, destFilePath);
                    if (result) {
                        new DatabaseHelper(mContext).insertRecord(destFilePath,
                                mContext.getString(R.string.rotated));
                    }
                })
                .inputType(InputType.TYPE_NUMBER_FLAG_SIGNED)
                .inputRange(1, 4)
                .show();
    }


    /**
     * Rotates pages in pdf
     *
     * @param input          rotation angle
     * @param sourceFilePath source file path
     * @param destFilePath   destination file path
     * @return true if no error else false
     */
    private boolean rotatePDFPages(String input, String sourceFilePath, String destFilePath) {
        try {
            int angle = Integer.parseInt(input);
            PdfReader reader = new PdfReader(sourceFilePath);
            int n = reader.getNumberOfPages();
            PdfDictionary page;
            PdfNumber rotate;
            for (int p = 1; p <= n; p++) {
                page = reader.getPageN(p);
                rotate = page.getAsNumber(PdfName.ROTATE);
                if (rotate == null) {
                    page.put(PdfName.ROTATE, new PdfNumber(angle));
                } else {
                    page.put(PdfName.ROTATE, new PdfNumber((rotate.intValue() + angle) % 360));
                }
            }
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destFilePath));
            stamper.close();
            reader.close();
            showSnackbar(mContext, R.string.snackbar_pdfCreated);
            return true;
        } catch (NumberFormatException e) {
            showSnackbar(mContext, R.string.invalid_entry);
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(mContext, R.string.error_occurred);
        }
        return false;
    }
}

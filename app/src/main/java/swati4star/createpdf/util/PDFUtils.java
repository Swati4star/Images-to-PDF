package swati4star.createpdf.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Environment;
import android.widget.TextView;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfReader;
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

public class PDFUtils {

    private final Activity mContext;

    public PDFUtils(Activity context) {
        this.mContext = context;
    }

    /**
     * Creates a dialog with details of given PDF file
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
}

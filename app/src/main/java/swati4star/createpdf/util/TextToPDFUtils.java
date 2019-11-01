package swati4star.createpdf.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import swati4star.createpdf.R;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.model.TextToPDFOptions;

import static swati4star.createpdf.util.Constants.MASTER_PWD_STRING;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.appName;

public class TextToPDFUtils {

    private final Activity mContext;
    private final SharedPreferences mSharedPreferences;

    public TextToPDFUtils(Activity context) {
        mContext = context;
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
    }
    /**
     * Create a PDF from a Text File
     *
     * @param mTextToPDFOptions TextToPDFOptions Object
     * @param fileExtension     file extension represented as string
     */
    public void createPdfFromTextFile(TextToPDFOptions mTextToPDFOptions, String fileExtension)
            throws DocumentException, IOException {

        String masterpwd = mSharedPreferences.getString(MASTER_PWD_STRING, appName);

        Rectangle pageSize = new Rectangle(PageSize.getRectangle(mTextToPDFOptions.getPageSize()));
        pageSize.setBackgroundColor(getBaseColor(mTextToPDFOptions.getPageColor()));
        Document document = new Document(pageSize);

        String finalOutput = mSharedPreferences.getString(STORAGE_LOCATION,
                StringUtils.getInstance().getDefaultStorageLocation()) +
                mTextToPDFOptions.getOutFileName() + ".pdf";
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(finalOutput));
        writer.setPdfVersion(PdfWriter.VERSION_1_7);
        if (mTextToPDFOptions.isPasswordProtected()) {
            writer.setEncryption(mTextToPDFOptions.getPassword().getBytes(),
                    masterpwd.getBytes(),
                    PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY,
                    PdfWriter.ENCRYPTION_AES_128);
        }

        document.open();

        Font myfont = new Font(mTextToPDFOptions.getFontFamily());
        myfont.setStyle(Font.NORMAL);
        myfont.setSize(mTextToPDFOptions.getFontSize());
        myfont.setColor(getBaseColor(mTextToPDFOptions.getFontColor()));

        document.add(new Paragraph("\n"));

        if (fileExtension == null)
            throw new DocumentException();

        switch (fileExtension) {
            case Constants.docExtension:
                readDocFile(mTextToPDFOptions.getInFileUri(), document, myfont);
                break;
            case Constants.docxExtension:
                readDocxFile(mTextToPDFOptions.getInFileUri(), document, myfont);
                break;
            default:
                readTextFile(mTextToPDFOptions.getInFileUri(), document, myfont);
                break;
        }
        document.close();

        new DatabaseHelper(mContext).insertRecord(finalOutput, mContext.getString(R.string.created));
    }

    /**
     * Read the BaseColor of passed color
     *
     * @param color value of color in int
     */
    private BaseColor getBaseColor(int color) {
        return new BaseColor(
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

    /**
     * Read the .docx file and put it in document
     *
     * @param uri      URL to create PDF
     * @param document PDF Document
     * @param myfont   Font style in PDF
     */
    private void readDocxFile(Uri uri, Document document, Font myfont) {
        InputStream inputStream;

        try {
            inputStream = mContext.getContentResolver().openInputStream(uri);
            if (inputStream == null)
                return;

            XWPFDocument doc = new XWPFDocument(inputStream);
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            String fileData = extractor.getText();

            Paragraph documentParagraph = new Paragraph(fileData + "\n", myfont);
            documentParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
            document.add(documentParagraph);
            inputStream.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the .doc file and put it in document
     *
     * @param uri      URL to create PDF
     * @param document PDF Document
     * @param myfont   Font style in PDF
     */
    private void readDocFile(Uri uri, Document document, Font myfont) {
        InputStream inputStream;

        try {
            inputStream = mContext.getContentResolver().openInputStream(uri);
            if (inputStream == null)
                return;

            HWPFDocument doc = new HWPFDocument(inputStream);
            WordExtractor extractor = new WordExtractor(doc);
            String fileData = extractor.getText();

            Paragraph documentParagraph = new Paragraph(fileData + "\n", myfont);
            documentParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
            document.add(documentParagraph);
            inputStream.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
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
                System.out.println("line = " + line);
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

}

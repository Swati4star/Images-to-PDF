package swati4star.createpdf.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

import swati4star.createpdf.R;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.model.TextToPDFOptions;

import static swati4star.createpdf.util.Constants.MASTER_PWD_STRING;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.appName;

public class TextToPDFUtils {

    private final Activity mContext;
    private final SharedPreferences mSharedPreferences;
    private final TextFileReader mTextFileReader;
    private final DocFileReader mDocFileReader;
    private final DocxFileReader mDocxFileReader;

    public TextToPDFUtils(Activity context) {
        mContext = context;
        mTextFileReader = new TextFileReader(mContext);
        mDocFileReader = new DocFileReader(mContext);
        mDocxFileReader = new DocxFileReader(mContext);
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
        addContentToDocument(mTextToPDFOptions, fileExtension, document, myfont);
        document.close();

        new DatabaseHelper(mContext).insertRecord(finalOutput, mContext.getString(R.string.created));
    }

    private void addContentToDocument(TextToPDFOptions mTextToPDFOptions, String fileExtension,
                                      Document document, Font myfont) throws DocumentException {
        if (fileExtension == null)
            throw new DocumentException();

        switch (fileExtension) {
            case Constants.docExtension:
                mDocFileReader.read(mTextToPDFOptions.getInFileUri(), document, myfont);
                break;
            case Constants.docxExtension:
                mDocxFileReader.read(mTextToPDFOptions.getInFileUri(), document, myfont);
                break;
            default:
                mTextFileReader.read(mTextToPDFOptions.getInFileUri(), document, myfont);
                break;
        }
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
}

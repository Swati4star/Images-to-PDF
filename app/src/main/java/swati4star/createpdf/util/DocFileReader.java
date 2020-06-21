package swati4star.createpdf.util;

import android.content.Context;
import android.net.Uri;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.InputStream;

public class DocFileReader extends FileReader {

    public DocFileReader(Context context) {
        super(context);
    }

    @Override
    protected void createDocumentFromStream(
            Uri uri, Document document, Font myfont, InputStream inputStream) throws Exception {
        HWPFDocument doc = new HWPFDocument(inputStream);
        WordExtractor extractor = new WordExtractor(doc);
        String fileData = extractor.getText();

        Paragraph documentParagraph = new Paragraph(fileData + "\n", myfont);
        documentParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(documentParagraph);
    }

}
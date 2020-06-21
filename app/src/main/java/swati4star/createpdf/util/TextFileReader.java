package swati4star.createpdf.util;

import android.content.Context;
import android.net.Uri;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextFileReader extends FileReader {

    public TextFileReader(Context context) {
        super(context);
    }

    @Override
    protected void createDocumentFromStream(
            Uri uri, Document document, Font myfont, InputStream inputStream) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("line = " + line);
            Paragraph para = new Paragraph(line + "\n", myfont);
            para.setAlignment(Element.ALIGN_JUSTIFIED);
            document.add(para);
        }
        reader.close();
    }
}
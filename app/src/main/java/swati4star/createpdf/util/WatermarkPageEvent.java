package swati4star.createpdf.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class WatermarkPageEvent extends PdfPageEventHelper {

    private final Phrase mWatermark = new Phrase("WATERMARKWATERMARKWATERMARKWATERMARKWAT" +
            "ERMARKWATERMARKWATERMARKWATERMARKWATERMARKWATERMARKWATERMARK",
            new Font(Font.FontFamily.HELVETICA, 20, Font.NORMAL, BaseColor.GRAY));

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContent();
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, mWatermark, 337, 500, 0);
    }
}
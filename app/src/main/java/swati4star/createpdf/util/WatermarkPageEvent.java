package swati4star.createpdf.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import swati4star.createpdf.model.Watermark;

class WatermarkPageEvent extends PdfPageEventHelper {
    private Watermark mWatermark;
    private Phrase mPhrase;

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContent();
        float x = (document.getPageSize().getLeft() + document.getPageSize().getRight()) / 2;
        float y = (document.getPageSize().getTop() + document.getPageSize().getBottom()) / 2;
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, mPhrase, x, y, mWatermark.getRotationAngle());
    }

    public Watermark getWatermark() {
        return mWatermark;
    }

    public void setWatermark(Watermark watermark) {
        this.mWatermark = watermark;
        this.mPhrase = new Phrase(mWatermark.getWatermarkText(),
                new Font(mWatermark.getFontFamily(), mWatermark.getTextSize(),
                        mWatermark.getFontStyle(), mWatermark.getTextColor()));
    }
}
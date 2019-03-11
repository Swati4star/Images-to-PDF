package swati4star.createpdf.util;

import com.itextpdf.text.Rectangle;

import org.junit.Test;

public class ImageUtilsTest {

    @Test
    public void testcalculateFitSize() {
        float testWidth = 8.0f;
        float testHeight = 12.0f;
        Rectangle testDocumentSize = new Rectangle(5.0f, 5.0f, 5.0f, 5.0f);

        new ImageUtils().calculateFitSize(testWidth, testHeight, testDocumentSize);
    }
}
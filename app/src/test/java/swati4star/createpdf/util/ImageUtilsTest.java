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

        float testWidthTwo = 8.0f;
        float testHeightTwo = 12.0f;
        Rectangle testDocumentSizeTwo = new Rectangle(-5.0f, 5.0f, -5.0f, 5.0f);
        new ImageUtils().calculateFitSize(testWidthTwo, testHeightTwo, testDocumentSizeTwo);

        float negWidth = -8.0f;
        float negHeight = -12.0f;
        Rectangle testDocumentSizeThree = new Rectangle(5.0f, 5.0f, 5.0f, 5.0f);
        new ImageUtils().calculateFitSize(negWidth, negHeight, testDocumentSizeThree);

        float testWidthThree = 8.0f;
        float testHeightThree = 12.0f;
        Rectangle testDocumentSizePoint = new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
        new ImageUtils().calculateFitSize(testWidthThree, testHeightThree, testDocumentSizePoint);

        float testWidthFour = 8.0f;
        float testHeightFour = 12.0f;
        Rectangle testDocumentSizeLine = new Rectangle(5.0f, 5.0f, 0.0f, 0.0f);
        new ImageUtils().calculateFitSize(testWidthFour, testHeightFour, testDocumentSizeLine);

    }
}
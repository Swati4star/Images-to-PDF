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

        float testWidth_two = 8.0f;
        float testHeight_two = 12.0f;
        Rectangle testDocumentSize_two = new Rectangle(-5.0f, 5.0f, -5.0f, 5.0f);
        new ImageUtils().calculateFitSize(testWidth_two, testHeight_two, testDocumentSize_two);

        float neg_width = -8.0f;
        float neg_height = -12.0f;
        Rectangle testDocumentSize_three = new Rectangle(5.0f, 5.0f, 5.0f, 5.0f);
        new ImageUtils().calculateFitSize(neg_width, neg_height, testDocumentSize_three);

        float testWidth_three = 8.0f;
        float testHeight_three = 12.0f;
        Rectangle testDocumentSize_point = new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
        new ImageUtils().calculateFitSize(testWidth_three, testHeight_three, testDocumentSize_point);

        float testWidth_four = 8.0f;
        float testHeight_four = 12.0f;
        Rectangle testDocumentSize_line = new Rectangle(5.0f, 5.0f, 0.0f, 0.0f);
        new ImageUtils().calculateFitSize(testWidth_four, testHeight_four, testDocumentSize_line);

    }
}
package swati4star.createpdf.util;

import com.itextpdf.text.Rectangle;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ImageUtilsTest {
    private static final Rectangle NO_NORMAL_ERROR =
            new Rectangle(5.0f, 5.0f, 5.0f, 5.0f);
    private static final Rectangle NO_INVALID_ERROR =
            new Rectangle(-5.0f, 5.0f, -5.0f, 5.0f);
    private static final Rectangle NO_NEGATIVE_LENGTH_ERROR =
            new Rectangle(5.0f, 5.0f, 5.0f, 5.0f);
    private static final Rectangle NO_POINT_ERROR =
            new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
    private static final Rectangle NO_LINE_ERROR =
            new Rectangle(5.0f, 5.0f, 0.0f, 0.0f);

    @Test
    public void testcalculateFitSize() {
        float testWidth = 8.0f;
        float testHeight = 12.0f;
        Rectangle testDocumentSize = new Rectangle(5.0f, 5.0f, 5.0f, 5.0f);
        assertThat(ImageUtils.calculateFitSize(testWidth, testHeight, testDocumentSize),
                is(NO_NORMAL_ERROR));

        float testWidthTwo = 8.0f;
        float testHeightTwo = 12.0f;
        Rectangle testDocumentSizeTwo = new Rectangle(-5.0f, 5.0f, -5.0f, 5.0f);
        assertThat(ImageUtils.calculateFitSize(testWidthTwo, testHeightTwo, testDocumentSizeTwo),
                is(NO_INVALID_ERROR));

        float negWidth = -8.0f;
        float negHeight = -12.0f;
        Rectangle testDocumentSizeThree = new Rectangle(5.0f, 5.0f, 5.0f, 5.0f);
        assertThat(ImageUtils.calculateFitSize(negWidth, negHeight, testDocumentSizeThree),
                is(NO_NEGATIVE_LENGTH_ERROR));

        float testWidthThree = 8.0f;
        float testHeightThree = 12.0f;
        Rectangle testDocumentSizePoint = new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
        assertThat(ImageUtils.calculateFitSize(testWidthThree, testHeightThree,
                testDocumentSizePoint), is(NO_POINT_ERROR));

        float testWidthFour = 8.0f;
        float testHeightFour = 12.0f;
        Rectangle testDocumentSizeLine = new Rectangle(5.0f, 5.0f, 0.0f, 0.0f);
        assertThat(ImageUtils.calculateFitSize(testWidthFour, testHeightFour, testDocumentSizeLine),
                is(NO_LINE_ERROR));

    }
}
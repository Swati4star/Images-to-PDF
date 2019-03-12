package swati4star.createpdf.util;

import com.itextpdf.text.Rectangle;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class ImageUtilsTest {
    private static final int NO_NORMAL_ERROR = 1;
    private static final int NO_INVALID_ERROR = 2;
    private static final int NO_NEGATIVE_LENGTH_ERROR = 3;
    private static final int NO_POINT_ERROR = 4;
    private static final int NO_LINE_ERROR = 5;

    @Test
    public void testCalculateFitSizeMethodForErrors() {
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
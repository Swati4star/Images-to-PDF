package swati4star.createpdf.util;

import com.itextpdf.text.Rectangle;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ImageUtilsTest {
    private static final Rectangle NO_NORMAL_ERROR =
            new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
    private static final Rectangle NO_POINT_ERROR =
            new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
    private static final Rectangle NO_LINE_ERROR =
            new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);

    @Test
    public void testCalculateFitSize() {
        float testWidth = 8.0f;
        float testHeight = 12.0f;
        Rectangle testDocumentSize = new Rectangle(5.0f, 5.0f, 5.0f, 5.0f);
        assertEquals(ImageUtils.calculateFitSize(testWidth, testHeight,
                testDocumentSize).getLeft(), NO_NORMAL_ERROR.getLeft());
        assertEquals(ImageUtils.calculateFitSize(testWidth, testHeight,
                testDocumentSize).getRight(), NO_NORMAL_ERROR.getRight());

        float testWidthTwo = 8.0f;
        float testHeightTwo = 12.0f;
        Rectangle testDocumentSizeTwo = new Rectangle(-5.0f, 5.0f, -5.0f, 5.0f);
        assertEquals(ImageUtils.calculateFitSize(testWidthTwo, testHeightTwo,
                testDocumentSizeTwo).getLeft(), NO_NORMAL_ERROR.getLeft());
        assertEquals(ImageUtils.calculateFitSize(testWidthTwo, testHeightTwo,
                testDocumentSizeTwo).getRight(), NO_NORMAL_ERROR.getRight());

        float negWidth = -8.0f;
        float negHeight = -12.0f;
        Rectangle testDocumentSizeThree = new Rectangle(5.0f, 5.0f, 5.0f, 5.0f);
        assertEquals(ImageUtils.calculateFitSize(negWidth, negHeight,
                testDocumentSizeThree).getLeft(), NO_NORMAL_ERROR.getLeft());
        assertEquals(ImageUtils.calculateFitSize(negWidth, negHeight,
                testDocumentSizeThree).getRight(), NO_NORMAL_ERROR.getRight());

        float testWidthThree = 8.0f;
        float testHeightThree = 12.0f;
        Rectangle testDocumentSizePoint = new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
        assertThat(ImageUtils.calculateFitSize(testWidthThree, testHeightThree,
                testDocumentSizePoint), is(NO_POINT_ERROR));

        float testWidthFour = 8.0f;
        float testHeightFour = 12.0f;
        Rectangle testDocumentSizeLine = new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
        assertEquals(ImageUtils.calculateFitSize(testWidthFour, testHeightFour,
                testDocumentSizeLine).getLeft(), NO_LINE_ERROR.getLeft());
        assertEquals(ImageUtils.calculateFitSize(testWidthFour, testHeightFour,
                testDocumentSizeLine).getRight(), NO_LINE_ERROR.getRight());

    }
}
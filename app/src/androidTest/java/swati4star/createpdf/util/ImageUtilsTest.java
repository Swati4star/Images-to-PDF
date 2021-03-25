package swati4star.createpdf.util;


import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.itextpdf.text.Rectangle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ImageUtilsTest {
    private static final Rectangle NO_NORMAL_ERROR =
            new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
    private static final Rectangle NO_POINT_ERROR =
            new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
    private static final Rectangle NO_LINE_ERROR =
            new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);

    ImageUtils imageUtils;

    @Before
    public void setUp() throws Exception {
        imageUtils = ImageUtils.getInstance();
    }

    @Test
    public void testCalculateFitSize() {

        float testWidth = 8.0f;
        float testHeight = 12.0f;
        Rectangle testDocumentSize = new Rectangle(5.0f, 5.0f, 5.0f, 5.0f);
        assertEquals(imageUtils.calculateFitSize(testWidth, testHeight,
                testDocumentSize).getLeft(), NO_NORMAL_ERROR.getLeft());
        assertEquals(imageUtils.calculateFitSize(testWidth, testHeight,
                testDocumentSize).getRight(), NO_NORMAL_ERROR.getRight());

        float testWidthTwo = 8.0f;
        float testHeightTwo = 12.0f;
        Rectangle testDocumentSizeTwo = new Rectangle(-5.0f, 5.0f, -5.0f, 5.0f);
        assertEquals(imageUtils.calculateFitSize(testWidthTwo, testHeightTwo,
                testDocumentSizeTwo).getLeft(), NO_NORMAL_ERROR.getLeft());
        assertEquals(imageUtils.calculateFitSize(testWidthTwo, testHeightTwo,
                testDocumentSizeTwo).getRight(), NO_NORMAL_ERROR.getRight());

        float negWidth = -8.0f;
        float negHeight = -12.0f;
        Rectangle testDocumentSizeThree = new Rectangle(5.0f, 5.0f, 5.0f, 5.0f);
        assertEquals(imageUtils.calculateFitSize(negWidth, negHeight,
                testDocumentSizeThree).getLeft(), NO_NORMAL_ERROR.getLeft());
        assertEquals(imageUtils.calculateFitSize(negWidth, negHeight,
                testDocumentSizeThree).getRight(), NO_NORMAL_ERROR.getRight());

        float testWidthThree = 8.0f;
        float testHeightThree = 12.0f;
        Rectangle testDocumentSizePoint = new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
        assertThat(imageUtils.calculateFitSize(testWidthThree, testHeightThree,
                testDocumentSizePoint), is(NO_POINT_ERROR));

        float testWidthFour = 8.0f;
        float testHeightFour = 12.0f;
        Rectangle testDocumentSizeLine = new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
        assertEquals(imageUtils.calculateFitSize(testWidthFour, testHeightFour,
                testDocumentSizeLine).getLeft(), NO_LINE_ERROR.getLeft());
        assertEquals(imageUtils.calculateFitSize(testWidthFour, testHeightFour,
                testDocumentSizeLine).getRight(), NO_LINE_ERROR.getRight());

    }

    @Test
    public void testgetRoundBitmap() {
        int width = 500, height = 100;
        Bitmap bitmap500x100 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap500x100.eraseColor(Color.RED);
        Bitmap actualBmp = imageUtils.getRoundBitmap(bitmap500x100);

        int actualWidth = actualBmp.getWidth();
        int actualHeight = actualBmp.getHeight();
        assertEquals(100, actualHeight);
        assertEquals(100, actualWidth);

        int rgb = actualBmp.getPixel(50, 50);
        String hexColor = String.format("#%06X", (0xFFFFFF & rgb));
        assertEquals("#FF0000", hexColor);

    }

    @Test
    public void testtoGrayscale() {
        int width = 100, height = 100;
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.RED);
        Bitmap actualBmp = imageUtils.toGrayscale(bmp);
        String actualColor = String.format("#%06X", (0xFFFFFF & actualBmp.getPixel(0, 0)));
        //Gray Of Red
        assertEquals("#363636", actualColor);

        bmp.eraseColor(Color.GREEN);
        actualBmp = imageUtils.toGrayscale(bmp);
        actualColor = String.format("#%06X", (0xFFFFFF & actualBmp.getPixel(0, 0)));
        //Gray Of Green
        assertEquals("#B6B6B6", actualColor);

        bmp.eraseColor(Color.BLUE);
        actualBmp = imageUtils.toGrayscale(bmp);
        actualColor = String.format("#%06X", (0xFFFFFF & actualBmp.getPixel(0, 0)));
        //Gray Of Blue
        assertEquals("#121212", actualColor);

        bmp.eraseColor(Color.GRAY);
        actualBmp = imageUtils.toGrayscale(bmp);
        actualColor = String.format("#%06X", (0xFFFFFF & actualBmp.getPixel(0, 0)));
        assertEquals(String.format("#%06X", (0xFFFFFF & Color.GRAY)), actualColor);

    }

}
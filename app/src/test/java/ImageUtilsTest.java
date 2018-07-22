import com.itextpdf.text.Rectangle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import swati4star.createpdf.util.ImageUtils;

import static org.mockito.Mockito.when;

public class ImageUtilsTest {

    ImageUtils imageUtils;

    @Mock
    Rectangle returnRec;

    @Mock
    Rectangle rectangleMock;


    @Before
    public void setupImageUtilsTest() {
        MockitoAnnotations.initMocks(this);
        imageUtils = new ImageUtils();
    }

    @Test
    public void calculateFitSizeTest() {
        when(rectangleMock.getWidth()).thenReturn((float) 1);
        when(rectangleMock.getHeight()).thenReturn((float) 1);

        returnRec = imageUtils.calculateFitSize(5, 10, rectangleMock);

        Assert.assertEquals((int)0.5,returnRec.getWidth(),0.0001);
        Assert.assertEquals( 1, returnRec.getHeight(),0.0001);
    }
}

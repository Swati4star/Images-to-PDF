package swati4star.createpdf.util;

import android.graphics.Color;
import android.os.Build;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;

@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.O_MR1)
public class ColorUtilsTest {

    @Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(
                new Object[][] {
                        {Color.rgb(100, 69, 100), Color.rgb(100, 100, 100), false},
                        {Color.rgb(100, 71, 100), Color.rgb(100, 100, 100), true},
                        {Color.rgb(100, 100, 100), Color.rgb(100, 100, 100), true},
                        {Color.rgb(100, 129, 100), Color.rgb(100, 100, 100), true},
                        {Color.rgb(100, 131, 100), Color.rgb(100, 100, 100), false},
                        {Color.rgb(110, 110, 110), Color.rgb(100, 100, 100), true},
                        {Color.rgb(120, 120, 115), Color.rgb(100, 100, 100), false},
                        {Color.rgb(100, 120, 120), Color.rgb(100, 100, 100), true}
                });
    }

    private final int mColor1;
    private final int mColor2;
    private final boolean mIsColorSimilar;

    public ColorUtilsTest(int mColor1, int mColor2, boolean mIsColorSimilar) {
        this.mColor1 = mColor1;
        this.mColor2 = mColor2;
        this.mIsColorSimilar = mIsColorSimilar;
    }

    @Test
    public void colorSimilarCheck() {
        ColorUtils colorUtils = ColorUtils.getInstance();

        Assert.assertEquals(
                mIsColorSimilar,
                colorUtils.colorSimilarCheck(mColor1, mColor2)
        );
    }
}
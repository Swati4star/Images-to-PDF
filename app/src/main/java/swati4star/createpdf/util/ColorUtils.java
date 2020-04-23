package swati4star.createpdf.util;

import android.graphics.Color;

public class ColorUtils {

    private static final double COLOR_DIFF_THRESHOLD = 30.0;

    private ColorUtils() {
    }

    private static class SingletonHolder {
        static final ColorUtils INSTANCE = new ColorUtils();
    }

    public static ColorUtils getInstance() {
        return ColorUtils.SingletonHolder.INSTANCE;
    }

    /**
     * Every RGB color consists three components: red, green and blue. That's why can we put 2 colors in a 3D coordinate
     * system and calculate distance between them. When distance is lower than COLOR_DIFF_THRESHOLD it means that these
     * colors are similar.
     * @see <a href="https://www.engineeringtoolbox.com/distance-relationship-between-two-points-d_1854.html">
     *     Distance between 2 points in 3D
     *     </a>
     * @return true for similar colors
     */
    public boolean colorSimilarCheck(int color1, int color2) {
        double colorDiff = Math.sqrt(
                Math.pow(Color.red(color1) - Color.red(color2), 2) +
                Math.pow(Color.green(color1) - Color.green(color2), 2) +
                Math.pow(Color.blue(color1) - Color.blue(color2), 2)
        );
        return colorDiff < COLOR_DIFF_THRESHOLD;
    }
}

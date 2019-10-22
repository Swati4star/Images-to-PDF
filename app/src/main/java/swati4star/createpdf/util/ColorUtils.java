package swati4star.createpdf.util;

import android.graphics.Color;

public class ColorUtils {
    public boolean colorSimilarCheck(int color1, int color2) {
        double colorDif = Math.sqrt((Math.pow((Color.red(color1) - Color.red(color2)), 2) +
                Math.pow((Color.blue(color1) - Color.blue(color2)), 2)
                + Math.pow((Color.green(color1) - Color.green(color2)), 2)));
        if (colorDif < 30) {
            return true;
        }
        return false;
    }

    private static class SingletonHolder {
        static final ColorUtils INSTANCE = new ColorUtils();
    }

    public static ColorUtils getInstance() {
        return ColorUtils.SingletonHolder.INSTANCE;
    }
}

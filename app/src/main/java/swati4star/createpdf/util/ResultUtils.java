package swati4star.createpdf.util;

import android.app.Activity;
import android.content.Intent;

public class ResultUtils {

    public static boolean checkResultValidity(int resultCode, Intent data) {

        return resultCode == Activity.RESULT_OK && data != null && data.getData() != null;
    }
}

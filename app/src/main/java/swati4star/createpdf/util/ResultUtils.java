package swati4star.createpdf.util;

import android.app.Activity;
import android.content.Intent;

public class ResultUtils {

    private ResultUtils() {

    }

    public boolean checkResultValidity(int resultCode, Intent data) {

        return resultCode == Activity.RESULT_OK && data != null && data.getData() != null;
    }

    private static class SingletonHolder {
        static final ResultUtils INSTANCE = new ResultUtils();
    }

    public static ResultUtils getInstance() {
        return ResultUtils.SingletonHolder.INSTANCE;
    }
}

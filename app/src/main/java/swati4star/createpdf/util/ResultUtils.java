package swati4star.createpdf.util;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

public class ResultUtils {

    private ResultUtils() {
    }

    @NonNull
    public static ResultUtils getInstance() {
        return ResultUtils.SingletonHolder.INSTANCE;
    }

    public boolean checkResultValidity(int resultCode, @NonNull Intent data) {

        return resultCode == Activity.RESULT_OK && data != null && data.getData() != null;
    }

    private static class SingletonHolder {
        static final ResultUtils INSTANCE = new ResultUtils();
    }
}

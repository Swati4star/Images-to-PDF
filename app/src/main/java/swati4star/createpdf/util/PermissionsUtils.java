package swati4star.createpdf.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class PermissionsUtils {
    private static final String TAG = "PermissionsUtils";

    /**
     * checkRuntimePermissions takes in multiple permissions or an array of permissions
     * and checks for if the permission is granted and if not it requests for the desired
     * permissions. The try and catch block checks for invalid permissions passed.
     *
     * @param activity
     * @param permissions
     * @return
     */
    public static boolean checkRuntimePermissions(Activity activity, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                try {
                    if ((ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                            permissions[i])
                            != PackageManager.PERMISSION_GRANTED)) {
                        requestRuntimePermissions(activity, permissions[i]);
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Faulty permission passed");
                }
            }
        }
        return true;
    }

    /**
     * requestRuntimePermissions request for the required permission
     * if not granted.
     *
     * @param permission
     */
    public static void requestRuntimePermissions(Activity activity, String permission) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
    }
}

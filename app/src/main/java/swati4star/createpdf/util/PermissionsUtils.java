package swati4star.createpdf.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
    public static boolean checkRuntimePermissions(Activity activity, int requestCode, String... permissions) {
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                if ((ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                        permissions[i])
                        != PackageManager.PERMISSION_GRANTED)) {
                    listPermissionsNeeded.add(permissions[i]);
                }
            }
            try {
                requestRuntimePermissions(activity, listPermissionsNeeded, requestCode);
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Faulty permission passed");
            }
        }
        return true;
    }

    /**
     * requestRuntimePermissions request for the required permission
     * if not granted.
     *
     * @param permissions
     */
    public static void requestRuntimePermissions(Activity activity, List<String> permissions,
                                                 int requestCode) {
        ActivityCompat.requestPermissions(activity,
                permissions.toArray(new String[permissions.size()]), requestCode);
    }
}

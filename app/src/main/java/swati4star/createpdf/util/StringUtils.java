package swati4star.createpdf.util;

import static swati4star.createpdf.util.Constants.PATH_SEPERATOR;
import static swati4star.createpdf.util.Constants.pdfDirectory;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.Objects;
//import static swati4star.createpdf.util.Constants.pdfDirectory;

/**
 * Created by anandparmar on 18/06/18.
 */

public class StringUtils {

    private StringUtils() {
    }

    @NonNull
    public static StringUtils getInstance() {
        return StringUtils.SingletonHolder.INSTANCE;
    }

    public boolean isEmpty(@NonNull CharSequence s) {
        return s == null || s.toString().trim().equals("");
    }

    public boolean isNotEmpty(@NonNull CharSequence s) {
        return s != null && !s.toString().trim().equals("");
    }

    public void showSnackbar(@NonNull Activity context, int resID) {
        Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }

    public void showSnackbar(@NonNull Activity context, @NonNull String resID) {
        Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }

    @NonNull
    public Snackbar showIndefiniteSnackbar(@NonNull Activity context, @NonNull String resID) {
        return Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_INDEFINITE);
    }

    @NonNull
    public Snackbar getSnackbarwithAction(@NonNull Activity context, int resID) {
        return Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG);
    }

    public void hideKeyboard(@NonNull Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @NonNull
    public String getDefaultStorageLocation() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                pdfDirectory);
        if (!dir.exists()) {
            boolean isDirectoryCreated = dir.mkdir();
            if (!isDirectoryCreated) {
                Log.e("Error", "Directory could not be created");
            }
        }
        return dir.getAbsolutePath() + PATH_SEPERATOR;
    }

    /**
     * if text is empty according to {@link StringUtils#isEmpty(CharSequence)} returns the default,
     * if text is not empty, parses the text according to {@link Integer#parseInt(String)}
     *
     * @param text the input text
     * @param def  the default value
     * @return the text parsed to an int or the default value
     * @throws NumberFormatException if the text is not empty and not formatted as an int
     */
    public int parseIntOrDefault(@NonNull CharSequence text, int def) throws NumberFormatException {
        if (isEmpty(text))
            return def;
        else
            return Integer.parseInt(text.toString());
    }

    private static class SingletonHolder {
        static final StringUtils INSTANCE = new StringUtils();
    }
}

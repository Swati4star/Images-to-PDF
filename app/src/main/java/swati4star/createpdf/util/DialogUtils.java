package swati4star.createpdf.util;

import android.app.Activity;

import com.afollestad.materialdialogs.MaterialDialog;

import swati4star.createpdf.R;

public class DialogUtils {

    /**
     * Creates a material dialog with `Warning` title
     * @param activity - activity instance
     * @param content - content resource id
     * @return - material dialog builder
     */
    public static MaterialDialog.Builder createWarningDialog(Activity activity,
                                                             int content) {
        return new MaterialDialog.Builder(activity)
                .title(R.string.warning)
                .content(content)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    /**
     * Creates a material dialog with `warning title` and overwrite message as content
     * @param activity - activity instance
     * @return - material dialog builder
     */
    public static MaterialDialog.Builder createOverwriteDialog(Activity activity) {
        return new MaterialDialog.Builder(activity)
                .title(R.string.warning)
                .content(R.string.overwrite_message)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    /**
     * Creates a material dialog with given title & content
     * @param activity - activity instance
     * @param title - dialog title resource id
     * @param content - content resource id
     * @return - material dialog builder
     */
    public static MaterialDialog.Builder createCustomDialog(Activity activity,
                                                            int title, int content) {
        return new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    /**
     * Creates a material dialog with given title
     * @param activity - activity instance
     * @param title - dialog title resource id
     * @return - material dialog builder
     */
    public static MaterialDialog.Builder createCustomDialogWithoutContent(Activity activity,
                                                            int title) {
        return new MaterialDialog.Builder(activity)
                .title(title)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    /**
     * Creates dialog with animation
     * @param activity - activity instance
     * @return - material dialog
     */
    public static MaterialDialog createAnimationDialog(Activity activity) {
        return new MaterialDialog.Builder(activity)
                .customView(R.layout.lottie_anim_dialog, false)
                .build();
    }
}

package swati4star.createpdf.util;

import static swati4star.createpdf.util.Constants.LAUNCH_COUNT;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import swati4star.createpdf.R;

public class FeedbackUtils {

    private final Activity mContext;
    private final SharedPreferences mSharedPreferences;

    public FeedbackUtils(Activity context) {
        this.mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Share application's playstore link
     */
    public void shareApplication() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.rate_us_text));
        openMailIntent(intent);
    }

    public void openMailIntent(Intent intent) {
        try {
            mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.share_chooser)));
        } catch (android.content.ActivityNotFoundException ex) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_no_share_app);
        }
    }

    /**
     * Open application in play store, so that user can rate
     */
    public void rateUs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.rate_title))
                .setMessage(mContext.getString(R.string.rate_dialog_text))
                .setNegativeButton(mContext.getString(R.string.rate_negative),
                        (dialogInterface, i) -> {
                            mSharedPreferences.edit().putInt(LAUNCH_COUNT, 0).apply();
                            dialogInterface.cancel();
                        })
                .setPositiveButton(mContext.getString(R.string.rate_positive),
                        (dialogInterface, i) -> {
                            try {
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=" +
                                                mContext.getApplicationContext().getPackageName())));
                            } catch (Exception e) {
                                openWebPage("https://play.google.com/store/apps/details?id=swati4star.createpdf");
                            }
                            mSharedPreferences.edit().putInt(LAUNCH_COUNT, -1).apply();
                            dialogInterface.dismiss();

                        })
                .setNeutralButton(mContext.getString(R.string.rate_us_never), (dialogInterface, i) -> {
                    mSharedPreferences.edit().putInt(LAUNCH_COUNT, -1).apply();
                    dialogInterface.cancel();
                });
        builder.create().show();
    }


    /**
     * Opens given web page in browser
     *
     * @param url - web page to open up
     */
    public void openWebPage(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(mContext.getPackageManager()) != null)
            mContext.startActivity(intent);
    }
}

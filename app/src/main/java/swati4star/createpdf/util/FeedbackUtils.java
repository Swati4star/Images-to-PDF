package swati4star.createpdf.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;

import java.util.Objects;

import swati4star.createpdf.R;

public class FeedbackUtils {

    private Activity mContext;

    public FeedbackUtils(Activity context) {
        this.mContext = context;
    }


    /**
     * Get feedback from users via email
     */
    public void getFeedback() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"swati4star@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.feedback_subject));
        intent.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.feedback_text));
        try {
            mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.feedback_chooser)));
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(Objects.requireNonNull(mContext).findViewById(android.R.id.content),
                    R.string.snackbar_no_email_clients,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Share application's playstore link
     */
    public void shareApplication() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT , mContext.getResources().getString(R.string.rate_us_text));
        try {
            mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.share_chooser)));
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(Objects.requireNonNull(mContext).findViewById(android.R.id.content),
                    R.string.snackbar_no_share_app,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Open application in play store, so that user can rate
     */
    public void rateUs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.rate_title))
                .setMessage(mContext.getString(R.string.rate_dialog_text))
                .setNegativeButton(mContext.getString(R.string.rate_negative), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton(mContext.getString(R.string.rate_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=" +
                                            mContext.getApplicationContext().getPackageName())));
                        } catch (Exception e) {
                            Snackbar.make(Objects.requireNonNull(mContext).findViewById(android.R.id.content),
                                    R.string.playstore_not_installed,
                                    Snackbar.LENGTH_LONG).show();
                        }
                        dialogInterface.dismiss();

                    }
                });
        builder.create().show();
    }
}

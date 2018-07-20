package swati4star.createpdf.util;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;

import java.util.Objects;

import swati4star.createpdf.R;

public class FeedbackUtils {

    private final Activity mContext;

    public FeedbackUtils(Activity context) {
        this.mContext = context;
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
}

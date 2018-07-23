package swati4star.createpdf.util;

import android.app.Activity;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;

import com.dd.morphingbutton.MorphingButton;

import swati4star.createpdf.R;

public class MorphButtonUtility {

    private final Activity mActivity;

    public MorphButtonUtility(Activity activity) {
        mActivity = activity;
    }
    public int integer() {
        return mActivity.getResources().getInteger(R.integer.mb_animation);
    }

    private int dimen(@DimenRes int resId) {
        return (int) mActivity.getResources().getDimension(resId);
    }

    public int color(@ColorRes int resId) {
        return mActivity.getResources().getColor(resId);
    }

    /**
     * Converts morph button ot square shape
     *
     * @param btnMorph the button to be converted
     * @param duration time period of transition
     */
    public void morphToSquare(final MorphingButton btnMorph, int duration) {
        MorphingButton.Params square = defaultButton(duration);
        square.color(color(R.color.mb_blue));
        square.colorPressed(color(R.color.mb_blue_dark));
        btnMorph.morph(square);
    }

    /**
     * Converts morph button into success shape
     *
     * @param btnMorph the button to be converted
     */
    public void morphToSuccess(final MorphingButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(integer())
                .cornerRadius(dimen(R.dimen.mb_height_56))
                .width(dimen(R.dimen.mb_height_56))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_green))
                .colorPressed(color(R.color.mb_green_dark))
                .icon(R.drawable.ic_done);
        btnMorph.morph(circle);
    }

    /**
     * Converts morph button ot square shape
     *
     * @param btnMorph the button to be converted
     * @param duration time period of transition
     */
    public void morphToGrey(final MorphingButton btnMorph, int duration) {
        MorphingButton.Params square = defaultButton(duration);
        square.color(color(R.color.mb_gray));
        square.colorPressed(color(R.color.mb_gray));
        btnMorph.morph(square);
    }

    /**
     * Return morphing button params with default values
     * @param duration - duration of transition
     * @return - params objefct
     */
    private MorphingButton.Params defaultButton(int duration) {
        return MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(dimen(R.dimen.mb_corner_radius_2))
                .width(dimen(R.dimen.mb_width_328))
                .height(dimen(R.dimen.mb_height_48))
                .text(mActivity.getString(R.string.mb_button));
    }
}

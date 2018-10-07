package swati4star.createpdf.util;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.ImageView;


public class BottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {

    private ImageView mUpArrow;
    private boolean mIsAdded;

    public BottomSheetCallback(ImageView mUpArrow, boolean mIsFragmentAdded) {
        this.mUpArrow = mUpArrow;
        this.mIsAdded = mIsFragmentAdded;
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        //ensure the fragment is currently added to its activity
        if (mIsAdded) {
            animateBottomSheetArrow(slideOffset);
        }
    }

    /**
     * animates the upArrow icon when sliding the bottom sheet
     *
     * @param slideOffset offset to use to sync the animation with bottom sheet sliding
     */
    private void animateBottomSheetArrow(float slideOffset) {
        if (slideOffset >= 0 && slideOffset <= 1) {
            mUpArrow.setRotation(slideOffset * -180);
        } else if (slideOffset >= -1 && slideOffset < 0) {
            mUpArrow.setRotation(slideOffset * 180);
        }
    }
}
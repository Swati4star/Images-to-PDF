package swati4star.createpdf.util;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.ImageView;


public class BottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {

    private ImageView mUpArrow;
    private ImageView mDownArrow;

    public BottomSheetCallback(ImageView mUpArrow, ImageView mDownArrow) {
        this.mDownArrow = mDownArrow;
        this.mUpArrow = mUpArrow;
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        switch (newState) {
            case BottomSheetBehavior.STATE_EXPANDED:
                mUpArrow.setVisibility(View.GONE);
                mDownArrow.setVisibility(View.VISIBLE);
                break;
            case BottomSheetBehavior.STATE_COLLAPSED:
                mUpArrow.setVisibility(View.VISIBLE);
                mDownArrow.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
    }
}
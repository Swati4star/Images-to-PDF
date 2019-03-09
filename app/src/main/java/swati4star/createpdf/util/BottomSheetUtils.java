package swati4star.createpdf.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetBehavior;
import static swati4star.createpdf.util.Constants.stateBottomSheet;

import swati4star.createpdf.interfaces.BottomSheetPopulate;

public class BottomSheetUtils  {

    private Activity mContext;

    public BottomSheetUtils(Activity context) {
        this.mContext = context;
    }

    public void showHideSheet(BottomSheetBehavior sheetBehavior) {
        SharedPreferences mSharedPreferences;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            mSharedPreferences.edit().putBoolean(stateBottomSheet, true).apply();
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            mSharedPreferences.edit().putBoolean(stateBottomSheet, false).apply();
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void populateBottomSheetWithPDFs(BottomSheetPopulate listener) {
        new PopulateBottomSheetList(listener, new DirectoryUtils(mContext)).execute();
    }

}

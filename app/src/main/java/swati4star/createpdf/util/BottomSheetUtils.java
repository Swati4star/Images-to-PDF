package swati4star.createpdf.util;

import android.app.Activity;
import android.support.design.widget.BottomSheetBehavior;

import swati4star.createpdf.interfaces.BottomSheetPopulate;

public class BottomSheetUtils  {

    private Activity mContext;
    private static BottomSheetUtils instance = null;

    private BottomSheetUtils(Activity context) {
        this.mContext = context;
    }

    public static BottomSheetUtils getInstance(Activity context) {
        if (instance == null) {
            //added synchronized block to control simultaneous access
            synchronized (BottomSheetUtils.class) {
                // double check locking
                if (instance == null)
                    instance = new BottomSheetUtils(context);
            }
        }
        return instance;
    }

    public void showHideSheet(BottomSheetBehavior sheetBehavior) {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void populateBottomSheetWithPDFs(BottomSheetPopulate listener) {
        new PopulateBottomSheetList(listener, new DirectoryUtils(mContext)).execute();
    }

    /**
     * Retrieves a list of available excel files on the device
     * @param listener a bottom sheet listener used to inform the caller when the list of files
     * is available
     */
    public void populateBottomSheetWithExcelFiles(BottomSheetPopulate listener) {
        new PopulateBottomSheetListWithExcelFiles(listener, new DirectoryUtils(mContext)).execute();
    }

}

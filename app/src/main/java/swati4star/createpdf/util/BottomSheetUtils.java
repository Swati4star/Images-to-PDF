package swati4star.createpdf.util;

import android.app.Activity;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import swati4star.createpdf.adapter.MergeFilesAdapter;

public class BottomSheetUtils {

    private Activity mContext;

    public BottomSheetUtils(Activity context) {
        this.mContext = context;
    }

    public void showHideSheet(BottomSheetBehavior sheetBehavior) {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void populateBottomSheetWithPDFs(RelativeLayout bottomSheetLayout,
                                            RecyclerView recyclerView,
                                            MergeFilesAdapter.OnClickListener listener) {
        DirectoryUtils directoryUtils = new DirectoryUtils(mContext);
        ArrayList<String> paths = directoryUtils.getAllPDFsOnDevice();
        if (paths == null || paths.size() == 0) {
            bottomSheetLayout.setVisibility(View.GONE);
        } else {
            // Init recycler view
            MergeFilesAdapter mergeFilesAdapter = new MergeFilesAdapter(mContext,
                    paths, listener);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(mergeFilesAdapter);
            recyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(mContext));
        }
    }
}

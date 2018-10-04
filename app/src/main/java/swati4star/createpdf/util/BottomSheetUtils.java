package swati4star.createpdf.util;

import android.app.Activity;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.interfaces.BottomSheetPopulate;

public class BottomSheetUtils implements BottomSheetPopulate {

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

    private View mFetchingFilesLabel;
    RelativeLayout bottomSheetLayout;
    RecyclerView recyclerView;
    MergeFilesAdapter.OnClickListener listener;

    public void populateBottomSheetWithPDFs(RelativeLayout bottomSheetLayout,
                                            RecyclerView recyclerView,
                                            MergeFilesAdapter.OnClickListener listener) {
        this.bottomSheetLayout = bottomSheetLayout;
        this.recyclerView = recyclerView;
        this.listener = listener;
        mFetchingFilesLabel = bottomSheetLayout.getRootView().findViewById(R.id.fetching);

        new PopulateBottomSheetList(this, new DirectoryUtils(mContext)).execute();

    }

    @Override
    public void onPopulate(ArrayList<String> paths) {

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
        mFetchingFilesLabel.setVisibility(View.GONE);
    }
}

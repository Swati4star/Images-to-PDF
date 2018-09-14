package swati4star.createpdf.interfaces;

import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import swati4star.createpdf.adapter.MergeFilesAdapter;

public interface BottomSheetPopulate {
    void onPopulate(ArrayList<String> paths);
}

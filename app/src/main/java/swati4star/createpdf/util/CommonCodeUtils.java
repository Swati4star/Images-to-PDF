package swati4star.createpdf.util;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.adapter.ExtractImagesAdapter;
import swati4star.createpdf.adapter.MergeFilesAdapter;

import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class CommonCodeUtils {

    /**
     * updates the output recycler view if paths.size > 0
     * else gides the main view
     */
    public static void populateUtil(Activity mActivity, ArrayList<String> paths,
                                    MergeFilesAdapter.OnClickListener onClickListener,
                                    RelativeLayout layout, LottieAnimationView animationView,
                                    RecyclerView recyclerView) {

        if (paths == null || paths.size() == 0) {
            layout.setVisibility(View.GONE);
        } else {
            // Init recycler view
            recyclerView.setVisibility(View.VISIBLE);
            MergeFilesAdapter mergeFilesAdapter = new MergeFilesAdapter(mActivity,
                    paths, false, onClickListener);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(mergeFilesAdapter);
            recyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
        }
        animationView.setVisibility(View.GONE);
    }


    /**
     * sets the appropriate text to successtextview & display images in adapter
     */
    public static void updateView(Activity mActivity, int imageCount, ArrayList<String> outputFilePaths,
                                  TextView successTextView, LinearLayout options, RecyclerView mCreatedImages,
                                  ExtractImagesAdapter.OnFileItemClickedListener listener) {

        if (imageCount == 0) {
            showSnackbar(mActivity, R.string.extract_images_failed);
            return;
        }

        String text = String.format(mActivity.getString(R.string.extract_images_success), imageCount);
        showSnackbar(mActivity, text);
        successTextView.setVisibility(View.VISIBLE);
        options.setVisibility(View.VISIBLE);
        ExtractImagesAdapter extractImagesAdapter = new ExtractImagesAdapter(mActivity, outputFilePaths, listener);
        // init recycler view for displaying generated image list
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        successTextView.setText(text);
        mCreatedImages.setVisibility(View.VISIBLE);
        mCreatedImages.setLayoutManager(mLayoutManager);
        // set up adapter
        mCreatedImages.setAdapter(extractImagesAdapter);
        mCreatedImages.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
    }
}

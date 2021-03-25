package swati4star.createpdf.util;

import android.app.Activity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.ExtractImagesAdapter;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.model.HomePageItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static swati4star.createpdf.R.drawable.baseline_crop_rotate_24;
import static swati4star.createpdf.R.drawable.ic_add_black_24dp;
import static swati4star.createpdf.R.drawable.ic_branding_watermark_black_24dp;
import static swati4star.createpdf.R.drawable.ic_broken_image_black_24dp;
import static swati4star.createpdf.R.drawable.ic_call_split_black_24dp;
import static swati4star.createpdf.R.drawable.ic_compress_image;
import static swati4star.createpdf.R.drawable.ic_excel;
import static swati4star.createpdf.R.drawable.ic_history_black_24dp;
import static swati4star.createpdf.R.drawable.ic_image_black_24dp;
import static swati4star.createpdf.R.drawable.ic_invert_color_24dp;
import static swati4star.createpdf.R.drawable.ic_lock_black_24dp;
import static swati4star.createpdf.R.drawable.ic_lock_open_black_24dp;
import static swati4star.createpdf.R.drawable.ic_menu_camera;
import static swati4star.createpdf.R.drawable.ic_menu_gallery;
import static swati4star.createpdf.R.drawable.ic_merge_type_black_24dp;
import static swati4star.createpdf.R.drawable.ic_qrcode_24dp;
import static swati4star.createpdf.R.drawable.ic_rearrange;
import static swati4star.createpdf.R.drawable.ic_remove_circle_black_24dp;
import static swati4star.createpdf.R.drawable.ic_text_format_black_24dp;
import static swati4star.createpdf.R.drawable.ic_zip_to_pdf;
import static swati4star.createpdf.R.id.add_images;
import static swati4star.createpdf.R.id.add_images_fav;
import static swati4star.createpdf.R.id.add_password;
import static swati4star.createpdf.R.id.add_password_fav;
import static swati4star.createpdf.R.id.add_text_fav;
import static swati4star.createpdf.R.id.add_watermark;
import static swati4star.createpdf.R.id.add_watermark_fav;
import static swati4star.createpdf.R.id.compress_pdf;
import static swati4star.createpdf.R.id.compress_pdf_fav;
import static swati4star.createpdf.R.id.excel_to_pdf;
import static swati4star.createpdf.R.id.excel_to_pdf_fav;
import static swati4star.createpdf.R.id.extract_images;
import static swati4star.createpdf.R.id.extract_images_fav;
import static swati4star.createpdf.R.id.extract_text;
import static swati4star.createpdf.R.id.extract_text_fav;
import static swati4star.createpdf.R.id.images_to_pdf_fav;
import static swati4star.createpdf.R.id.invert_pdf_fav;
import static swati4star.createpdf.R.id.merge_pdf;
import static swati4star.createpdf.R.id.merge_pdf_fav;
import static swati4star.createpdf.R.id.nav_add_images;
import static swati4star.createpdf.R.id.nav_add_password;
import static swati4star.createpdf.R.id.nav_add_text;
import static swati4star.createpdf.R.id.nav_add_watermark;
import static swati4star.createpdf.R.id.nav_camera;
import static swati4star.createpdf.R.id.nav_compress_pdf;
import static swati4star.createpdf.R.id.nav_excel_to_pdf;
import static swati4star.createpdf.R.id.nav_extract_images;
import static swati4star.createpdf.R.id.nav_gallery;
import static swati4star.createpdf.R.id.nav_history;
import static swati4star.createpdf.R.id.nav_invert_pdf;
import static swati4star.createpdf.R.id.nav_merge;
import static swati4star.createpdf.R.id.nav_pdf_to_images;
import static swati4star.createpdf.R.id.nav_qrcode;
import static swati4star.createpdf.R.id.nav_rearrange_pages;
import static swati4star.createpdf.R.id.nav_remove_duplicate_pages;
import static swati4star.createpdf.R.id.nav_remove_pages;
import static swati4star.createpdf.R.id.nav_remove_password;
import static swati4star.createpdf.R.id.nav_split;
import static swati4star.createpdf.R.id.nav_text_extract;
import static swati4star.createpdf.R.id.nav_text_to_pdf;
import static swati4star.createpdf.R.id.nav_zip_to_pdf;
import static swati4star.createpdf.R.id.pdf_to_images;
import static swati4star.createpdf.R.id.pdf_to_images_fav;
import static swati4star.createpdf.R.id.qr_barcode_to_pdf;
import static swati4star.createpdf.R.id.qr_barcode_to_pdf_fav;
import static swati4star.createpdf.R.id.rearrange_pages;
import static swati4star.createpdf.R.id.rearrange_pages_fav;
import static swati4star.createpdf.R.id.remove_duplicates_pages_pdf;
import static swati4star.createpdf.R.id.remove_duplicates_pages_pdf_fav;
import static swati4star.createpdf.R.id.remove_pages;
import static swati4star.createpdf.R.id.remove_pages_fav;
import static swati4star.createpdf.R.id.remove_password;
import static swati4star.createpdf.R.id.remove_password_fav;
import static swati4star.createpdf.R.id.rotate_pages;
import static swati4star.createpdf.R.id.rotate_pages_fav;
import static swati4star.createpdf.R.id.split_pdf;
import static swati4star.createpdf.R.id.split_pdf_fav;
import static swati4star.createpdf.R.id.text_to_pdf;
import static swati4star.createpdf.R.id.text_to_pdf_fav;
import static swati4star.createpdf.R.id.view_files;
import static swati4star.createpdf.R.id.view_files_fav;
import static swati4star.createpdf.R.id.view_history;
import static swati4star.createpdf.R.id.view_history_fav;
import static swati4star.createpdf.R.id.zip_to_pdf_fav;
import static swati4star.createpdf.R.string.qr_barcode_pdf;


public class CommonCodeUtils {

    Map<Integer, HomePageItem> mFragmentPositionMap;

    /**
     * updates the output recycler view if paths.size > 0
     * else give the main view
     */
    public void populateUtil(Activity mActivity, ArrayList<String> paths,
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
     * sets the appropriate text to success Text View & display images in adapter
     */
    public void updateView(Activity mActivity, int imageCount, ArrayList<String> outputFilePaths,
                           TextView successTextView, LinearLayout options, RecyclerView mCreatedImages,
                           ExtractImagesAdapter.OnFileItemClickedListener listener) {

        if (imageCount == 0) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.extract_images_failed);
            return;
        }

        String text = String.format(mActivity.getString(R.string.extract_images_success), imageCount);
        StringUtils.getInstance().showSnackbar(mActivity, text);
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

    /**
     * Closes the bottom sheet if it is expanded
     */

    public void closeBottomSheetUtil(BottomSheetBehavior sheetBehavior) {
        if (checkSheetBehaviourUtil(sheetBehavior))
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    /**
     * Checks whether the bottom sheet is expanded or collapsed
     */
    public boolean checkSheetBehaviourUtil(BottomSheetBehavior sheetBehavior) {
        return sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

    private void addFragmentPosition(boolean homePageItems, int iconA, int iconB,
                                     int iconId, int drawableId, int titleString) {
        mFragmentPositionMap.put(homePageItems ? iconA : iconB, new HomePageItem(iconId, drawableId, titleString));
    }

    public Map<Integer, HomePageItem> fillNavigationItemsMap(boolean homePageItems) {
        mFragmentPositionMap = new HashMap<>();
        addFragmentPosition(homePageItems, R.id.images_to_pdf,
                images_to_pdf_fav, nav_camera, ic_menu_camera, R.string.images_to_pdf);
        addFragmentPosition(homePageItems, qr_barcode_to_pdf,
                qr_barcode_to_pdf_fav, nav_qrcode, ic_qrcode_24dp, qr_barcode_pdf);
        addFragmentPosition(homePageItems, R.id.excel_to_pdf,
                excel_to_pdf_fav, nav_excel_to_pdf, ic_excel, excel_to_pdf);
        addFragmentPosition(homePageItems, view_files, view_files_fav,
                nav_gallery, ic_menu_gallery, R.string.viewFiles);
        addFragmentPosition(homePageItems, rotate_pages, rotate_pages_fav,
                nav_gallery, baseline_crop_rotate_24, R.string.rotate_pages);
        addFragmentPosition(homePageItems, extract_text, extract_text_fav,
                nav_text_extract, ic_broken_image_black_24dp, R.string.extract_text);
        addFragmentPosition(homePageItems, add_watermark, add_watermark_fav,
                nav_add_watermark, ic_branding_watermark_black_24dp, R.string.add_watermark);
        addFragmentPosition(homePageItems, merge_pdf, merge_pdf_fav,
                nav_merge, ic_merge_type_black_24dp, R.string.merge_pdf);
        addFragmentPosition(homePageItems, split_pdf, split_pdf_fav,
                nav_split, ic_call_split_black_24dp, R.string.split_pdf);
        addFragmentPosition(homePageItems, text_to_pdf, text_to_pdf_fav,
                nav_text_to_pdf, ic_text_format_black_24dp, R.string.text_to_pdf);
        addFragmentPosition(homePageItems, compress_pdf, compress_pdf_fav,
                nav_compress_pdf, ic_compress_image, R.string.compress_pdf);
        addFragmentPosition(homePageItems, remove_pages, remove_pages_fav,
                nav_remove_pages, ic_remove_circle_black_24dp, R.string.remove_pages);
        addFragmentPosition(homePageItems, rearrange_pages, rearrange_pages_fav,
                nav_rearrange_pages, ic_rearrange, R.string.reorder_pages);
        addFragmentPosition(homePageItems, extract_images, extract_images_fav,
                nav_extract_images, ic_broken_image_black_24dp, R.string.extract_images);
        addFragmentPosition(homePageItems, view_history, view_history_fav,
                nav_history, ic_history_black_24dp, R.string.history);
        addFragmentPosition(homePageItems, pdf_to_images, pdf_to_images_fav,
                nav_pdf_to_images, ic_image_black_24dp, R.string.pdf_to_images);
        addFragmentPosition(homePageItems, add_password, add_password_fav,
                nav_add_password, ic_lock_black_24dp, R.string.add_password);
        addFragmentPosition(homePageItems, remove_password, remove_password_fav,
                nav_remove_password, ic_lock_open_black_24dp, R.string.remove_password);
        addFragmentPosition(homePageItems, add_images, add_images_fav,
                nav_add_images, ic_add_black_24dp, R.string.add_images);
        addFragmentPosition(homePageItems, remove_duplicates_pages_pdf,
                remove_duplicates_pages_pdf_fav, nav_remove_duplicate_pages,
                R.drawable.ic_remove_duplicate_square_black, R.string.remove_duplicate_pages);
        addFragmentPosition(homePageItems, R.id.invert_pdf, invert_pdf_fav,
                nav_invert_pdf, ic_invert_color_24dp, R.string.invert_pdf);
        addFragmentPosition(homePageItems, R.id.zip_to_pdf, zip_to_pdf_fav,
                nav_zip_to_pdf, ic_zip_to_pdf, R.string.zip_to_pdf);
        addFragmentPosition(homePageItems, R.id.add_text, add_text_fav,
                nav_add_text, ic_text_format_black_24dp, R.string.add_text);
        return mFragmentPositionMap;
    }

    private static class SingletonHolder {
        static final CommonCodeUtils INSTANCE = new CommonCodeUtils();
    }

    public static CommonCodeUtils getInstance() {
        return CommonCodeUtils.SingletonHolder.INSTANCE;
    }
}

package swati4star.createpdf.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.FavouritesActivity;
import swati4star.createpdf.customviews.MyCardView;

import static swati4star.createpdf.util.Constants.ADD_IMAGES_KEY;
import static swati4star.createpdf.util.Constants.ADD_PASSWORD_KEY;
import static swati4star.createpdf.util.Constants.ADD_WATERMARK_KEY;
import static swati4star.createpdf.util.Constants.COMPRESS_PDF_KEY;
import static swati4star.createpdf.util.Constants.EXTRACT_IMAGES_KEY;
import static swati4star.createpdf.util.Constants.HISTORY_KEY;
import static swati4star.createpdf.util.Constants.IMAGE_TO_PDF_KEY;
import static swati4star.createpdf.util.Constants.INVERT_PDF_KEY;
import static swati4star.createpdf.util.Constants.MERGE_PDF_KEY;
import static swati4star.createpdf.util.Constants.PDF_TO_IMAGES_KEY;
import static swati4star.createpdf.util.Constants.QR_BARCODE_KEY;
import static swati4star.createpdf.util.Constants.REMOVE_DUPLICATE_PAGES_KEY;
import static swati4star.createpdf.util.Constants.REMOVE_PAGES_KEY;
import static swati4star.createpdf.util.Constants.REMOVE_PASSWORD_KEY;
import static swati4star.createpdf.util.Constants.REORDER_PAGES_KEY;
import static swati4star.createpdf.util.Constants.ROTATE_PAGES_KEY;
import static swati4star.createpdf.util.Constants.TEXT_TO_PDF_KEY;
import static swati4star.createpdf.util.Constants.VIEW_FILES_KEY;

public class FavouritesFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences mSharedpreferences;

    @BindView(R.id.fav_add_fab)
    FloatingActionButton mFab;
    @BindView(R.id.images_to_pdf_fav)
    MyCardView pref_img_to_pdf;
    @BindView(R.id.text_to_pdf_fav)
    MyCardView pref_text_to_pdf;
    @BindView(R.id.qr_barcode_to_pdf_fav)
    MyCardView pref_qr_barcode;
    @BindView(R.id.view_files_fav)
    MyCardView pref_view_files;
    @BindView(R.id.view_history_fav)
    MyCardView pref_history;
    @BindView(R.id.add_password_fav)
    MyCardView pref_add_password;
    @BindView(R.id.remove_password_fav)
    MyCardView pref_rem_pass;
    @BindView(R.id.rotate_pages_fav)
    MyCardView pref_rot_pages;
    @BindView(R.id.add_watermark_fav)
    MyCardView pref_add_watermark;
    @BindView(R.id.add_images_fav)
    MyCardView pref_add_images;
    @BindView(R.id.merge_pdf_fav)
    MyCardView pref_merge_pdf;
    @BindView(R.id.split_pdf_fav)
    MyCardView pref_split_pdf;
    @BindView(R.id.invert_pdf_fav)
    MyCardView pref_invert_pdf;
    @BindView(R.id.compress_pdf_fav)
    MyCardView pref_compress;
    @BindView(R.id.remove_duplicates_pages_pdf_fav)
    MyCardView pref_rem_dup_pages;
    @BindView(R.id.remove_pages_fav)
    MyCardView pref_remove_pages;
    @BindView(R.id.rearrange_pages_fav)
    MyCardView pref_reorder_pages;
    @BindView(R.id.extract_images_fav)
    MyCardView pref_extract_img;
    @BindView(R.id.pdf_to_images_fav)
    MyCardView pref_pdf_to_img;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.favourites_fragment, container, false);
        ButterKnife.bind(this, rootview);
        mSharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        mSharedpreferences.registerOnSharedPreferenceChangeListener(this);
        mFab.setOnClickListener(v ->
                startActivity(new Intent(this.getContext(), FavouritesActivity.class))
        );
        setHasOptionsMenu(true);
        return rootview;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_favourites_item).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    /**
     * This method checks for the favourites from preferences list
     * and passes them to another method for dealing with the required view.
     * @param sharedPreferences
     */
    private void checkFavs(SharedPreferences sharedPreferences) {
        // assigned due to onSharedPreferenceChanged
        mSharedpreferences = sharedPreferences;
        viewVisibility(pref_img_to_pdf, IMAGE_TO_PDF_KEY);
        viewVisibility(pref_text_to_pdf, TEXT_TO_PDF_KEY);
        viewVisibility(pref_qr_barcode, QR_BARCODE_KEY);
        viewVisibility(pref_view_files, VIEW_FILES_KEY);
        viewVisibility(pref_history, HISTORY_KEY);
        viewVisibility(pref_add_password, ADD_PASSWORD_KEY);
        viewVisibility(pref_rem_pass, REMOVE_PASSWORD_KEY);
        viewVisibility(pref_rot_pages, ROTATE_PAGES_KEY);
        viewVisibility(pref_add_watermark, ADD_WATERMARK_KEY);
        viewVisibility(pref_add_images, ADD_IMAGES_KEY);
        viewVisibility(pref_merge_pdf, MERGE_PDF_KEY);
        viewVisibility(pref_invert_pdf, INVERT_PDF_KEY);
        viewVisibility(pref_compress, COMPRESS_PDF_KEY);
        viewVisibility(pref_rem_dup_pages, REMOVE_DUPLICATE_PAGES_KEY);
        viewVisibility(pref_remove_pages, REMOVE_PAGES_KEY);
        viewVisibility(pref_reorder_pages, REORDER_PAGES_KEY);
        viewVisibility(pref_extract_img, EXTRACT_IMAGES_KEY);
        viewVisibility(pref_pdf_to_img, PDF_TO_IMAGES_KEY);
    }

    /**
     * This method toggles the visibility of the passed view.
     * @param view
     * @param id
     */
    private void viewVisibility(View view, String id) {
        if (mSharedpreferences.getBoolean(id, false)) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        checkFavs(sharedPreferences);
    }
}

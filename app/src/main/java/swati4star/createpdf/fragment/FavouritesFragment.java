package swati4star.createpdf.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.FavouritesActivity;
import swati4star.createpdf.customviews.MyCardView;

public class FavouritesFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

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
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.hide();
        mFab.setOnClickListener(v ->
                startActivity(new Intent(this.getContext(), FavouritesActivity.class))
        );
        return rootview;
    }

    /**
     * This method checks for the favourites from preferences list
     * and passes them to another method for dealing with the required view.
     * @param sharedPreferences
     */
    private void checkFavs(SharedPreferences sharedPreferences) {
        viewVisibility(sharedPreferences, pref_img_to_pdf,
                getResources().getString(R.string.img_to_pdf));
        viewVisibility(sharedPreferences, pref_text_to_pdf,
                getResources().getString(R.string.text_to_pdf));
        viewVisibility(sharedPreferences, pref_qr_barcode,
                getResources().getString(R.string.qr_barcode_pdf));
        viewVisibility(sharedPreferences, pref_view_files,
                getResources().getString(R.string.viewFiles));
        viewVisibility(sharedPreferences, pref_history,
                getResources().getString(R.string.history));
        viewVisibility(sharedPreferences, pref_add_password,
                getResources().getString(R.string.add_password));
        viewVisibility(sharedPreferences, pref_rem_pass,
                getResources().getString(R.string.remove_password));
        viewVisibility(sharedPreferences, pref_rot_pages,
                getResources().getString(R.string.rotate_pages));
        viewVisibility(sharedPreferences, pref_add_watermark,
                getResources().getString(R.string.add_watermark));
        viewVisibility(sharedPreferences, pref_add_images,
                getResources().getString(R.string.add_images));
        viewVisibility(sharedPreferences, pref_merge_pdf,
                getResources().getString(R.string.merge_pdf));
        viewVisibility(sharedPreferences, pref_invert_pdf,
                getResources().getString(R.string.invert_pdf));
        viewVisibility(sharedPreferences, pref_compress,
                getResources().getString(R.string.compress_pdf));
        viewVisibility(sharedPreferences, pref_rem_dup_pages,
                getResources().getString(R.string.remove_duplicate_pages));
        viewVisibility(sharedPreferences, pref_remove_pages,
                getResources().getString(R.string.remove_pages));
        viewVisibility(sharedPreferences, pref_reorder_pages,
                getResources().getString(R.string.reorder_pages));
        viewVisibility(sharedPreferences, pref_extract_img,
                getResources().getString(R.string.extract_images));
        viewVisibility(sharedPreferences, pref_pdf_to_img,
                getResources().getString(R.string.pdf_to_images));
    }

    /**
     * This method toggles the visibility of the passed view.
     * @param sharedPreferences
     * @param view
     * @param id
     */
    private void viewVisibility(SharedPreferences sharedPreferences, View view, String id) {
        if (sharedPreferences.getBoolean(id, false)) {
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

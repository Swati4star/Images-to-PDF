package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.FavouritesActivity;
import swati4star.createpdf.activity.MainActivity;
import swati4star.createpdf.customviews.MyCardView;
import swati4star.createpdf.model.FavouriteItem;

import static swati4star.createpdf.util.Constants.ADD_IMAGES;
import static swati4star.createpdf.util.Constants.ADD_IMAGES_KEY;
import static swati4star.createpdf.util.Constants.ADD_PASSWORD_KEY;
import static swati4star.createpdf.util.Constants.ADD_PWD;
import static swati4star.createpdf.util.Constants.ADD_TEXT_KEY;
import static swati4star.createpdf.util.Constants.ADD_WATERMARK_KEY;
import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.COMPRESS_PDF;
import static swati4star.createpdf.util.Constants.COMPRESS_PDF_KEY;
import static swati4star.createpdf.util.Constants.EXCEL_TO_PDF_KEY;
import static swati4star.createpdf.util.Constants.EXTRACT_IMAGES;
import static swati4star.createpdf.util.Constants.EXTRACT_IMAGES_KEY;
import static swati4star.createpdf.util.Constants.EXTRACT_TEXT_KEY;
import static swati4star.createpdf.util.Constants.HISTORY_KEY;
import static swati4star.createpdf.util.Constants.IMAGE_TO_PDF_KEY;
import static swati4star.createpdf.util.Constants.INVERT_PDF_KEY;
import static swati4star.createpdf.util.Constants.MERGE_PDF_KEY;
import static swati4star.createpdf.util.Constants.PDF_TO_IMAGES;
import static swati4star.createpdf.util.Constants.PDF_TO_IMAGES_KEY;
import static swati4star.createpdf.util.Constants.QR_BARCODE_KEY;
import static swati4star.createpdf.util.Constants.REMOVE_DUPLICATE_PAGES_KEY;
import static swati4star.createpdf.util.Constants.REMOVE_PAGES;
import static swati4star.createpdf.util.Constants.REMOVE_PAGES_KEY;
import static swati4star.createpdf.util.Constants.REMOVE_PASSWORD_KEY;
import static swati4star.createpdf.util.Constants.REMOVE_PWd;
import static swati4star.createpdf.util.Constants.REORDER_PAGES;
import static swati4star.createpdf.util.Constants.REORDER_PAGES_KEY;
import static swati4star.createpdf.util.Constants.ROTATE_PAGES_KEY;
import static swati4star.createpdf.util.Constants.SPLIT_PDF_KEY;
import static swati4star.createpdf.util.Constants.TEXT_TO_PDF_KEY;
import static swati4star.createpdf.util.Constants.VIEW_FILES_KEY;
import static swati4star.createpdf.util.Constants.ZIP_TO_PDF_KEY;
import static swati4star.createpdf.util.DialogUtils.ADD_WATERMARK;
import static swati4star.createpdf.util.DialogUtils.ROTATE_PAGES;

public class FavouritesFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        View.OnClickListener {
    private SharedPreferences mSharedpreferences;
    private boolean mDoesFavouritesExist;
    private Activity mActivity;
    private Map<Integer, FavouriteItem> mFragmentPositionMap;

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
    @BindView(R.id.extract_text_fav)
    MyCardView pref_extract_txt;
    @BindView(R.id.excel_to_pdf)
    MyCardView pref_excel_to_pdf;
    @BindView(R.id.add_text_fav)
    MyCardView pref_add_text;
    @BindView(R.id.favourites)
    LottieAnimationView favouritesAnimation;
    @BindView(R.id.favourites_text)
    TextView favouritesText;
    @BindView(R.id.zip_to_pdf)
    MyCardView pref_zip_to_pdf;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.favourites_fragment, container, false);
        ButterKnife.bind(this, rootview);

        initializeValues();


        setHasOptionsMenu(true);
        return rootview;
    }

    /**
     * Initializes listeners & default values
     */
    private void initializeValues() {

        mSharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(mActivity);
        mSharedpreferences.registerOnSharedPreferenceChangeListener(this);

        mDoesFavouritesExist = false;
        checkFavs(mSharedpreferences);
        fillMap();

        pref_img_to_pdf.setOnClickListener(this);
        pref_text_to_pdf.setOnClickListener(this);
        pref_qr_barcode.setOnClickListener(this);
        pref_view_files.setOnClickListener(this);
        pref_history.setOnClickListener(this);
        pref_extract_txt.setOnClickListener(this);
        pref_add_text.setOnClickListener(this);
        pref_split_pdf.setOnClickListener(this);
        pref_merge_pdf.setOnClickListener(this);
        pref_compress.setOnClickListener(this);
        pref_remove_pages.setOnClickListener(this);
        pref_reorder_pages.setOnClickListener(this);
        pref_extract_img.setOnClickListener(this);
        pref_pdf_to_img.setOnClickListener(this);
        pref_add_password.setOnClickListener(this);
        pref_rem_pass.setOnClickListener(this);
        pref_rot_pages.setOnClickListener(this);
        pref_add_watermark.setOnClickListener(this);
        pref_add_images.setOnClickListener(this);
        pref_rem_dup_pages.setOnClickListener(this);
        pref_invert_pdf.setOnClickListener(this);
        pref_excel_to_pdf.setOnClickListener(this);
        pref_zip_to_pdf.setOnClickListener(this);

    }

    @OnClick(R.id.fav_add_fab)
    public void onAddFavouriteButtonClicked() {
        startActivity(new Intent(this.getContext(), FavouritesActivity.class));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_favourites_item).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    /**
     * This method checks for the favourites from preferences list
     * and passes them to another method for dealing with the required view.
     *
     * @param sharedPreferences - preferenves instance
     */
    private void checkFavs(SharedPreferences sharedPreferences) {

        // assigned due to onSharedPreferenceChanged
        mSharedpreferences = sharedPreferences;
        viewVisibility(pref_img_to_pdf, IMAGE_TO_PDF_KEY);
        viewVisibility(pref_text_to_pdf, TEXT_TO_PDF_KEY);
        viewVisibility(pref_qr_barcode, QR_BARCODE_KEY);
        viewVisibility(pref_view_files, VIEW_FILES_KEY);
        viewVisibility(pref_history, HISTORY_KEY);
        viewVisibility(pref_add_text, ADD_TEXT_KEY);
        viewVisibility(pref_add_password, ADD_PASSWORD_KEY);
        viewVisibility(pref_rem_pass, REMOVE_PASSWORD_KEY);
        viewVisibility(pref_rot_pages, ROTATE_PAGES_KEY);
        viewVisibility(pref_add_watermark, ADD_WATERMARK_KEY);
        viewVisibility(pref_add_images, ADD_IMAGES_KEY);
        viewVisibility(pref_merge_pdf, MERGE_PDF_KEY);
        viewVisibility(pref_split_pdf, SPLIT_PDF_KEY);
        viewVisibility(pref_invert_pdf, INVERT_PDF_KEY);
        viewVisibility(pref_compress, COMPRESS_PDF_KEY);
        viewVisibility(pref_rem_dup_pages, REMOVE_DUPLICATE_PAGES_KEY);
        viewVisibility(pref_remove_pages, REMOVE_PAGES_KEY);
        viewVisibility(pref_reorder_pages, REORDER_PAGES_KEY);
        viewVisibility(pref_extract_txt, EXTRACT_TEXT_KEY);
        viewVisibility(pref_extract_img, EXTRACT_IMAGES_KEY);
        viewVisibility(pref_pdf_to_img, PDF_TO_IMAGES_KEY);
        viewVisibility(pref_excel_to_pdf, EXCEL_TO_PDF_KEY);
        viewVisibility(pref_zip_to_pdf, ZIP_TO_PDF_KEY);

        // if there are no favourites then show favourites animation and text
        if (!mDoesFavouritesExist) {
            favouritesAnimation.setVisibility(View.VISIBLE);
            favouritesText.setVisibility(View.VISIBLE);
        }
    }

    private void fillMap() {
        mFragmentPositionMap = new HashMap<>();
        mFragmentPositionMap.put(R.id.images_to_pdf_fav,
                new FavouriteItem(R.id.nav_camera, R.string.images_to_pdf));
        mFragmentPositionMap.put(R.id.qr_barcode_to_pdf_fav,
                new FavouriteItem(R.id.nav_qrcode, R.string.qr_barcode_pdf));
        mFragmentPositionMap.put(R.id.view_files_fav,
                new FavouriteItem(R.id.nav_gallery, R.string.viewFiles));
        mFragmentPositionMap.put(R.id.rotate_pages_fav,
                new FavouriteItem(R.id.nav_gallery, R.string.rotate_pages));
        mFragmentPositionMap.put(R.id.add_text_fav,
                new FavouriteItem(R.id.nav_add_text, R.string.add_text));
        mFragmentPositionMap.put(R.id.add_watermark_fav,
                new FavouriteItem(R.id.nav_add_watermark, R.string.add_watermark));
        mFragmentPositionMap.put(R.id.merge_pdf_fav,
                new FavouriteItem(R.id.nav_merge, R.string.merge_pdf));
        mFragmentPositionMap.put(R.id.split_pdf_fav,
                new FavouriteItem(R.id.nav_split, R.string.split_pdf));
        mFragmentPositionMap.put(R.id.text_to_pdf_fav,
                new FavouriteItem(R.id.nav_text_to_pdf, R.string.text_to_pdf));
        mFragmentPositionMap.put(R.id.compress_pdf_fav,
                new FavouriteItem(R.id.nav_compress_pdf, R.string.compress_pdf));
        mFragmentPositionMap.put(R.id.remove_pages_fav,
                new FavouriteItem(R.id.nav_remove_pages, R.string.remove_pages));
        mFragmentPositionMap.put(R.id.extract_text_fav,
                new FavouriteItem(R.id.nav_text_extract, R.string.extract_text));
        mFragmentPositionMap.put(R.id.rearrange_pages_fav,
                new FavouriteItem(R.id.nav_rearrange_pages, R.string.reorder_pages));
        mFragmentPositionMap.put(R.id.extract_images_fav,
                new FavouriteItem(R.id.nav_extract_images, R.string.extract_images));
        mFragmentPositionMap.put(R.id.view_history_fav,
                new FavouriteItem(R.id.nav_history, R.string.history));
        mFragmentPositionMap.put(R.id.pdf_to_images_fav,
                new FavouriteItem(R.id.nav_pdf_to_images, R.string.pdf_to_images));
        mFragmentPositionMap.put(R.id.add_password_fav,
                new FavouriteItem(R.id.nav_add_password, R.string.add_password));
        mFragmentPositionMap.put(R.id.remove_password_fav,
                new FavouriteItem(R.id.nav_remove_password, R.string.remove_password));
        mFragmentPositionMap.put(R.id.add_images_fav,
                new FavouriteItem(R.id.nav_add_images, R.string.add_images));
        mFragmentPositionMap.put(R.id.remove_duplicates_pages_pdf_fav,
                new FavouriteItem(R.id.nav_remove_duplicate_pages, R.string.remove_duplicate_pages));
        mFragmentPositionMap.put(R.id.invert_pdf_fav,
                new FavouriteItem(R.id.nav_invert_pdf, R.string.invert_pdf));
        mFragmentPositionMap.put(R.id.excel_to_pdf,
                new FavouriteItem(R.id.nav_excel_to_pdf, R.string.excel_to_pdf));
        mFragmentPositionMap.put(R.id.zip_to_pdf,
                new FavouriteItem(R.id.nav_zip_to_pdf, R.string.zip_to_pdf));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    /**
     * This method toggles the visibility of the passed view.
     *
     * @param view - the view, whose visibility is to be modified
     * @param id - get the preference value using id
     */
    private void viewVisibility(View view, String id) {
        if (mSharedpreferences.getBoolean(id, false)) {
            view.setVisibility(View.VISIBLE);
            // if any favourites exists set mDoesFavouritesExist to true
            mDoesFavouritesExist = true;
            // & disable favourites animation and text
            favouritesAnimation.setVisibility(View.GONE);
            favouritesText.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        checkFavs(sharedPreferences);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        FragmentManager fragmentManager = getFragmentManager();
        Bundle bundle = new Bundle();
        setTitleFragment(mFragmentPositionMap.get(v.getId()).getTitleString());

        switch (v.getId()) {
            case R.id.images_to_pdf_fav:
                fragment = new ImageToPdfFragment();
                break;
            case R.id.qr_barcode_to_pdf_fav:
                fragment = new QrBarcodeScanFragment();
                break;
            case R.id.text_to_pdf_fav:
                fragment = new TextToPdfFragment();
                break;
            case R.id.view_files_fav:
                fragment = new ViewFilesFragment();
                break;
            case R.id.view_history_fav:
                fragment = new HistoryFragment();
                break;
            case R.id.add_text_fav:
                fragment = new AddTextFragment();
                break;
            case R.id.merge_pdf_fav:
                fragment = new MergeFilesFragment();
                break;
            case R.id.split_pdf_fav:
                fragment = new SplitFilesFragment();
                break;
            case R.id.compress_pdf_fav:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, COMPRESS_PDF);
                fragment.setArguments(bundle);
                break;
            case R.id.extract_images_fav:
                fragment = new PdfToImageFragment();
                bundle.putString(BUNDLE_DATA, EXTRACT_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.pdf_to_images_fav:
                fragment = new PdfToImageFragment();
                bundle.putString(BUNDLE_DATA, PDF_TO_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.remove_pages_fav:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, REMOVE_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.rearrange_pages_fav:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, REORDER_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.add_password_fav:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, ADD_PWD);
                fragment.setArguments(bundle);
                break;
            case R.id.remove_password_fav:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, REMOVE_PWd);
                fragment.setArguments(bundle);
                break;
            case R.id.rotate_pages_fav:
                fragment = new ViewFilesFragment();
                bundle.putInt(BUNDLE_DATA, ROTATE_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.add_watermark_fav:
                fragment = new ViewFilesFragment();
                bundle.putInt(BUNDLE_DATA, ADD_WATERMARK);
                fragment.setArguments(bundle);
                break;
            case R.id.add_images_fav:
                fragment = new AddImagesFragment();
                bundle.putString(BUNDLE_DATA, ADD_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.remove_duplicates_pages_pdf_fav:
                fragment = new RemoveDuplicatePagesFragment();
                break;
            case R.id.invert_pdf_fav:
                fragment = new InvertPdfFragment();
                break;
            case R.id.extract_text_fav:
                fragment = new ExtractTextFragment();
                break;
            case R.id.excel_to_pdf:
                fragment = new ExceltoPdfFragment();
                break;
            case R.id.zip_to_pdf:
                fragment = new ZipToPdfFragment();
                break;
        }
        try {
            if (fragment != null && fragmentManager != null) {
                ((MainActivity) mActivity).setNavigationViewSelection(mFragmentPositionMap.get(v.getId()).getIconId());
                fragmentManager.beginTransaction()
                        .replace(R.id.content, fragment)
                        .addToBackStack(getString(R.string.favourites))
                        .commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * sets the title on action bar
     * @param title - the string id to be set
     */
    private void setTitleFragment(int title) {
        if (title != 0)
            mActivity.setTitle(title);
    }
}

package swati4star.createpdf.fragment;

import static swati4star.createpdf.util.Constants.ADD_IMAGES;
import static swati4star.createpdf.util.Constants.ADD_IMAGES_KEY;
import static swati4star.createpdf.util.Constants.ADD_PASSWORD_KEY;
import static swati4star.createpdf.util.Constants.ADD_PWD;
import static swati4star.createpdf.util.Constants.ADD_TEXT_KEY;
import static swati4star.createpdf.util.Constants.ADD_WATERMARK;
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
import static swati4star.createpdf.util.Constants.ROTATE_PAGES;
import static swati4star.createpdf.util.Constants.ROTATE_PAGES_KEY;
import static swati4star.createpdf.util.Constants.SPLIT_PDF_KEY;
import static swati4star.createpdf.util.Constants.TEXT_TO_PDF_KEY;
import static swati4star.createpdf.util.Constants.VIEW_FILES_KEY;
import static swati4star.createpdf.util.Constants.ZIP_TO_PDF_KEY;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import java.util.Map;

import swati4star.createpdf.R;
import swati4star.createpdf.activity.FavouritesActivity;
import swati4star.createpdf.activity.MainActivity;
import swati4star.createpdf.databinding.FragmentFavouritesBinding;
import swati4star.createpdf.fragment.texttopdf.TextToPdfFragment;
import swati4star.createpdf.model.HomePageItem;
import swati4star.createpdf.util.CommonCodeUtils;

public class FavouritesFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        View.OnClickListener {
    private SharedPreferences mSharedpreferences;
    private boolean mDoesFavouritesExist;
    private Activity mActivity;
    private Map<Integer, HomePageItem> mFragmentPositionMap;
    FragmentFavouritesBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentFavouritesBinding.inflate(inflater, container, false);
        View rootView = mBinding.getRoot();

        mSharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(mActivity);
        mSharedpreferences.registerOnSharedPreferenceChangeListener(this);

        initializeValues();

        setHasOptionsMenu(true);

        mBinding.favAddFab.setOnClickListener(v -> {
            startActivity(new Intent(this.getContext(), FavouritesActivity.class));
        });

        return rootView;
    }

    /**
     * Initializes listeners & default values
     */
    private void initializeValues() {

        mDoesFavouritesExist = false;
        checkFavourites(mSharedpreferences);
        mFragmentPositionMap = CommonCodeUtils.getInstance().fillNavigationItemsMap(false);

        mBinding.imagesToPdfFav.setOnClickListener(this);
        mBinding.textToPdfFav.setOnClickListener(this);
        mBinding.qrBarcodeToPdfFav.setOnClickListener(this);
        mBinding.viewFilesFav.setOnClickListener(this);
        mBinding.viewHistoryFav.setOnClickListener(this);
        mBinding.extractTextFav.setOnClickListener(this);
        mBinding.addTextFav.setOnClickListener(this);
        mBinding.splitPdfFav.setOnClickListener(this);
        mBinding.mergePdfFav.setOnClickListener(this);
        mBinding.compressPdfFav.setOnClickListener(this);
        mBinding.removePagesFav.setOnClickListener(this);
        mBinding.removePagesFav.setOnClickListener(this);
        mBinding.extractImagesFav.setOnClickListener(this);
        mBinding.pdfToImagesFav.setOnClickListener(this);
        mBinding.addPasswordFav.setOnClickListener(this);
        mBinding.removePasswordFav.setOnClickListener(this);
        mBinding.rotatePagesFav.setOnClickListener(this);
        mBinding.addWatermarkFav.setOnClickListener(this);
        mBinding.addImagesFav.setOnClickListener(this);
        mBinding.removeDuplicatesPagesPdfFav.setOnClickListener(this);
        mBinding.invertPdfFav.setOnClickListener(this);
        mBinding.excelToPdfFav.setOnClickListener(this);
        mBinding.zipToPdfFav.setOnClickListener(this);

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
     * @param sharedPreferences - preferences instance
     */
    private void checkFavourites(SharedPreferences sharedPreferences) {

        // assigned due to onSharedPreferenceChanged
        mSharedpreferences = sharedPreferences;
        viewVisibility(mBinding.imagesToPdfFav, IMAGE_TO_PDF_KEY);
        viewVisibility(mBinding.textToPdfFav, TEXT_TO_PDF_KEY);
        viewVisibility(mBinding.qrBarcodeToPdfFav, QR_BARCODE_KEY);
        viewVisibility(mBinding.viewFilesFav, VIEW_FILES_KEY);
        viewVisibility(mBinding.viewHistoryFav, HISTORY_KEY);
        viewVisibility(mBinding.addTextFav, ADD_TEXT_KEY);
        viewVisibility(mBinding.addPasswordFav, ADD_PASSWORD_KEY);
        viewVisibility(mBinding.removePasswordFav, REMOVE_PASSWORD_KEY);
        viewVisibility(mBinding.rotatePagesFav, ROTATE_PAGES_KEY);
        viewVisibility(mBinding.addWatermarkFav, ADD_WATERMARK_KEY);
        viewVisibility(mBinding.addImagesFav, ADD_IMAGES_KEY);
        viewVisibility(mBinding.mergePdfFav, MERGE_PDF_KEY);
        viewVisibility(mBinding.splitPdfFav, SPLIT_PDF_KEY);
        viewVisibility(mBinding.invertPdfFav, INVERT_PDF_KEY);
        viewVisibility(mBinding.compressPdfFav, COMPRESS_PDF_KEY);
        viewVisibility(mBinding.removeDuplicatesPagesPdfFav, REMOVE_DUPLICATE_PAGES_KEY);
        viewVisibility(mBinding.removePagesFav, REMOVE_PAGES_KEY);
        viewVisibility(mBinding.rearrangePagesFav, REORDER_PAGES_KEY);
        viewVisibility(mBinding.extractTextFav, EXTRACT_TEXT_KEY);
        viewVisibility(mBinding.extractImagesFav, EXTRACT_IMAGES_KEY);
        viewVisibility(mBinding.pdfToImagesFav, PDF_TO_IMAGES_KEY);
        viewVisibility(mBinding.excelToPdfFav, EXCEL_TO_PDF_KEY);
        viewVisibility(mBinding.zipToPdfFav, ZIP_TO_PDF_KEY);

        // if there are no favourites then show favourites animation and text
        if (!mDoesFavouritesExist) {
            mBinding.favourites.setVisibility(View.VISIBLE);
            mBinding.favouritesText.setVisibility(View.VISIBLE);
        }
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
     * @param id   - get the preference value using id
     */
    private void viewVisibility(View view, String id) {
        if (mSharedpreferences.getBoolean(id, false)) {
            view.setVisibility(View.VISIBLE);
            // if any favourites exists set mDoesFavouritesExist to true
            mDoesFavouritesExist = true;
            // & disable favourites animation and text
            mBinding.favourites.setVisibility(View.GONE);
            mBinding.favouritesText.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        checkFavourites(sharedPreferences);
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
            case R.id.excel_to_pdf_fav:
                fragment = new ExceltoPdfFragment();
                break;
            case R.id.zip_to_pdf_fav:
                fragment = new ZipToPdfFragment();
                break;
        }
        try {
            if (fragment != null && fragmentManager != null) {
                ((MainActivity) mActivity).setNavigationViewSelection(mFragmentPositionMap.get(
                        v.getId()).getNavigationItemId());
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
     *
     * @param title - the string id to be set
     */
    private void setTitleFragment(int title) {
        if (title != 0)
            mActivity.setTitle(title);
    }
}

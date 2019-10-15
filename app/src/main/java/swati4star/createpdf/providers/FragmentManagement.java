package swati4star.createpdf.providers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseIntArray;
import android.widget.Toast;

import java.util.Objects;

import swati4star.createpdf.R;
import swati4star.createpdf.activity.WelcomeActivity;
import swati4star.createpdf.fragment.AboutUsFragment;
import swati4star.createpdf.fragment.AddImagesFragment;
import swati4star.createpdf.fragment.AddTextFragment;
import swati4star.createpdf.fragment.ExceltoPdfFragment;
import swati4star.createpdf.fragment.ExtractTextFragment;
import swati4star.createpdf.fragment.FavouritesFragment;
import swati4star.createpdf.fragment.HistoryFragment;
import swati4star.createpdf.fragment.HomeFragment;
import swati4star.createpdf.fragment.ImageToPdfFragment;
import swati4star.createpdf.fragment.InvertPdfFragment;
import swati4star.createpdf.fragment.MergeFilesFragment;
import swati4star.createpdf.fragment.PdfToImageFragment;
import swati4star.createpdf.fragment.QrBarcodeScanFragment;
import swati4star.createpdf.fragment.RemoveDuplicatePagesFragment;
import swati4star.createpdf.fragment.RemovePagesFragment;
import swati4star.createpdf.fragment.SettingsFragment;
import swati4star.createpdf.fragment.SplitFilesFragment;
import swati4star.createpdf.fragment.TextToPdfFragment;
import swati4star.createpdf.fragment.ViewFilesFragment;
import swati4star.createpdf.fragment.ZipToPdfFragment;
import swati4star.createpdf.util.FeedbackUtils;
import swati4star.createpdf.util.WhatsNewUtils;

import static swati4star.createpdf.util.Constants.ACTION_MERGE_PDF;
import static swati4star.createpdf.util.Constants.ACTION_SELECT_IMAGES;
import static swati4star.createpdf.util.Constants.ACTION_TEXT_TO_PDF;
import static swati4star.createpdf.util.Constants.ACTION_VIEW_FILES;
import static swati4star.createpdf.util.Constants.ADD_IMAGES;
import static swati4star.createpdf.util.Constants.ADD_PWD;
import static swati4star.createpdf.util.Constants.ADD_WATERMARK_KEY;
import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.COMPRESS_PDF;
import static swati4star.createpdf.util.Constants.EXTRACT_IMAGES;
import static swati4star.createpdf.util.Constants.OPEN_SELECT_IMAGES;
import static swati4star.createpdf.util.Constants.PDF_TO_IMAGES;
import static swati4star.createpdf.util.Constants.REMOVE_PAGES;
import static swati4star.createpdf.util.Constants.REMOVE_PWd;
import static swati4star.createpdf.util.Constants.REORDER_PAGES;
import static swati4star.createpdf.util.Constants.ROTATE_PAGES_KEY;
import static swati4star.createpdf.util.Constants.SHOW_WELCOME_ACT;
import static swati4star.createpdf.util.DialogUtils.ADD_WATERMARK;
import static swati4star.createpdf.util.DialogUtils.ROTATE_PAGES;

/**
 * This is a fragment service that manages the fragments
 * mainly for the MainActivity.
 */
public class FragmentManagement implements IFragmentManagement {
    private FragmentActivity mContext;
    private NavigationView mNavigationView;
    private Fragment mCurrentFragment;
    private boolean mDoubleBackToExitPressedOnce = false;
    private FeedbackUtils mFeedbackUtils;

    public FragmentManagement(FragmentActivity context, NavigationView navigationView) {
        mContext = context;
        mNavigationView = navigationView;

        mFeedbackUtils = new FeedbackUtils(mContext);
    }

    public void favouritesFragmentOption() {
        Fragment currFragment = getSupportFragmentManager().findFragmentById(R.id.content);

        Fragment fragment = new FavouritesFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(R.id.content, fragment);
        if (!(currFragment instanceof HomeFragment)) {
            transaction.addToBackStack(getFragmentName(currFragment));
        }
        transaction.commit();
    }

    public Fragment checkForAppShortcutClicked() {
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (getIntent().getAction() != null) {
            switch (Objects.requireNonNull(getIntent().getAction())) {
                case ACTION_SELECT_IMAGES:
                    fragment = new ImageToPdfFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(OPEN_SELECT_IMAGES, true);
                    fragment.setArguments(bundle);
                    break;
                case ACTION_VIEW_FILES:
                    fragment = new ViewFilesFragment();
                    setNavigationViewSelection(R.id.nav_gallery);
                    break;
                case ACTION_TEXT_TO_PDF:
                    fragment = new TextToPdfFragment();
                    setNavigationViewSelection(R.id.nav_text_to_pdf);
                    break;
                case ACTION_MERGE_PDF:
                    fragment = new MergeFilesFragment();
                    setNavigationViewSelection(R.id.nav_merge);
                    break;
                default:
                    // Set default fragment
                    fragment = new HomeFragment();
                    break;
            }
        }
        if (areImagesReceived())
            fragment = new ImageToPdfFragment();

        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();

        return fragment;
    }

    public boolean handleBackPressed() {
        mCurrentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.content);
        if (mCurrentFragment instanceof HomeFragment) {
            return checkDoubleBackPress();
        } else if (checkFragmentBottomSheetBehavior())
            closeFragmentBottomSheet();
        else {
            handleBackStackEntry();
        }
        return false;
    }

    public SparseIntArray setTitleMap() {
        SparseIntArray titles = new SparseIntArray();
        titles.append(R.id.nav_home, R.string.app_name);
        titles.append(R.id.nav_camera, R.string.images_to_pdf);
        titles.append(R.id.nav_qrcode, R.string.qr_barcode_pdf);
        titles.append(R.id.nav_add_text, R.string.add_text);
        titles.append(R.id.nav_gallery, R.string.viewFiles);
        titles.append(R.id.nav_merge, R.string.merge_pdf);
        titles.append(R.id.nav_split, R.string.split_pdf);
        titles.append(R.id.nav_text_to_pdf, R.string.text_to_pdf);
        titles.append(R.id.nav_history, R.string.history);
        titles.append(R.id.nav_add_password, R.string.add_password);
        titles.append(R.id.nav_remove_password, R.string.remove_password);
        titles.append(R.id.nav_about, R.string.about_us);
        titles.append(R.id.nav_settings, R.string.settings);
        titles.append(R.id.nav_extract_images, R.string.extract_images);
        titles.append(R.id.nav_pdf_to_images, R.string.pdf_to_images);
        titles.append(R.id.nav_remove_pages, R.string.remove_pages);
        titles.append(R.id.nav_rearrange_pages, R.string.reorder_pages);
        titles.append(R.id.nav_compress_pdf, R.string.compress_pdf);
        titles.append(R.id.nav_add_images, R.string.add_images);
        titles.append(R.id.nav_remove_duplicate_pages, R.string.remove_duplicate_pages);
        titles.append(R.id.nav_invert_pdf, R.string.invert_pdf);
        titles.append(R.id.nav_add_watermark, R.string.add_watermark);
        titles.append(R.id.nav_zip_to_pdf, R.string.zip_to_pdf);
        titles.append(R.id.nav_rotate_pages, R.string.rotate_pages);
        titles.append(R.id.nav_excel_to_pdf, R.string.excel_to_pdf);
        return titles;
    }

    public boolean handleNavigationItemSelected(int itemId) {
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();

        switch (itemId) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_camera:
                fragment = new ImageToPdfFragment();
                break;
            case R.id.nav_qrcode:
                fragment = new QrBarcodeScanFragment();
                break;
            case R.id.nav_gallery:
                fragment = new ViewFilesFragment();
                break;
            case R.id.nav_merge:
                fragment = new MergeFilesFragment();
                break;
            case R.id.nav_split:
                fragment = new SplitFilesFragment();
                break;
            case R.id.nav_text_to_pdf:
                fragment = new TextToPdfFragment();
                break;
            case R.id.nav_history:
                fragment = new HistoryFragment();
                break;
            case R.id.nav_add_text:
                fragment = new AddTextFragment();
                break;
            case R.id.nav_add_password:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, ADD_PWD);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_remove_password:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, REMOVE_PWd);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_share:
                mFeedbackUtils.shareApplication();
                break;
            case R.id.nav_about:
                fragment = new AboutUsFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.nav_extract_images:
                fragment = new PdfToImageFragment();
                bundle.putString(BUNDLE_DATA, EXTRACT_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_pdf_to_images:
                fragment = new PdfToImageFragment();
                bundle.putString(BUNDLE_DATA, PDF_TO_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_excel_to_pdf:
                fragment = new ExceltoPdfFragment();
                break;
            case R.id.nav_remove_pages:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, REMOVE_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_rearrange_pages:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, REORDER_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_compress_pdf:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, COMPRESS_PDF);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_add_images:
                fragment = new AddImagesFragment();
                bundle.putString(BUNDLE_DATA, ADD_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_help:
                Intent intent = new Intent(mContext, WelcomeActivity.class);
                intent.putExtra(SHOW_WELCOME_ACT, true);
                mContext.startActivity(intent);
                break;
            case R.id.nav_remove_duplicate_pages:
                fragment = new RemoveDuplicatePagesFragment();
                break;
            case R.id.nav_invert_pdf:
                fragment = new InvertPdfFragment();
                break;
            case R.id.nav_add_watermark:
                fragment = new ViewFilesFragment();
                bundle.putInt(BUNDLE_DATA, ADD_WATERMARK);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_zip_to_pdf:
                fragment = new ZipToPdfFragment();
                break;
            case R.id.nav_whatsNew:
                WhatsNewUtils.displayDialog(mContext);
                break;
            case R.id.nav_rotate_pages:
                fragment = new ViewFilesFragment();
                bundle.putInt(BUNDLE_DATA, ROTATE_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_text_extract:
                fragment = new ExtractTextFragment();
                break;
        }

        try {
            if (fragment != null)
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // if help or share or what's new is clicked then return false, as we don't want
        // them to be selected
        return itemId != R.id.nav_share && itemId != R.id.nav_help
                && itemId != R.id.nav_whatsNew;
    }

    /**
     * Closes the app only when double clicked
     */
    private boolean checkDoubleBackPress() {
        if (mDoubleBackToExitPressedOnce) {
            return true;
        }
        mDoubleBackToExitPressedOnce = true;
        Toast.makeText(mContext, R.string.confirm_exit_message, Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean checkFragmentBottomSheetBehavior() {
        if (mCurrentFragment instanceof InvertPdfFragment )
            return ((InvertPdfFragment) mCurrentFragment).checkSheetBehaviour();

        if (mCurrentFragment instanceof MergeFilesFragment )
            return ((MergeFilesFragment) mCurrentFragment).checkSheetBehaviour();

        if (mCurrentFragment instanceof RemoveDuplicatePagesFragment )
            return ((RemoveDuplicatePagesFragment) mCurrentFragment).checkSheetBehaviour();

        if (mCurrentFragment instanceof RemovePagesFragment )
            return ((RemovePagesFragment) mCurrentFragment).checkSheetBehaviour();

        if (mCurrentFragment instanceof AddImagesFragment )
            return ((AddImagesFragment) mCurrentFragment).checkSheetBehaviour();

        if (mCurrentFragment instanceof PdfToImageFragment )
            return ((PdfToImageFragment) mCurrentFragment).checkSheetBehaviour();

        if (mCurrentFragment instanceof SplitFilesFragment )
            return ((SplitFilesFragment) mCurrentFragment).checkSheetBehaviour();

        return false;
    }

    private void closeFragmentBottomSheet() {
        if ( mCurrentFragment instanceof InvertPdfFragment)
            ((InvertPdfFragment) mCurrentFragment).closeBottomSheet();

        if (mCurrentFragment instanceof MergeFilesFragment)
            ((MergeFilesFragment) mCurrentFragment).closeBottomSheet();

        if (mCurrentFragment instanceof RemoveDuplicatePagesFragment )
            ((RemoveDuplicatePagesFragment) mCurrentFragment).closeBottomSheet();

        if (mCurrentFragment instanceof RemovePagesFragment)
            ((RemovePagesFragment) mCurrentFragment).closeBottomSheet();

        if (mCurrentFragment instanceof AddImagesFragment)
            ((AddImagesFragment) mCurrentFragment).closeBottomSheet();

        if (mCurrentFragment instanceof PdfToImageFragment)
            ((PdfToImageFragment) mCurrentFragment).closeBottomSheet();

        if (mCurrentFragment instanceof SplitFilesFragment)
            ((SplitFilesFragment) mCurrentFragment).closeBottomSheet();
    }

    /**
     *  Back stack count will be 1 when we open a item from favourite menu
     *  on clicking back, return back to fav menu and change title
     */
    private void handleBackStackEntry() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            String s = getSupportFragmentManager().getBackStackEntryAt(count - 1).getName();
            mContext.setTitle(s);
            getSupportFragmentManager().popBackStack();
        } else {
            Fragment fragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
            mContext.setTitle(R.string.app_name);
            setNavigationViewSelection(R.id.nav_home);
        }
    }

    private boolean areImagesReceived() {
        Intent intent = getIntent();
        String type = intent.getType();
        return type != null && type.startsWith("image/");
    }

    private String getFragmentName(Fragment fragment) {
        String name = "set name";
        if (fragment instanceof ImageToPdfFragment) {
            name = getString(R.string.images_to_pdf);
        } else if (fragment instanceof TextToPdfFragment) {
            name = getString(R.string.text_to_pdf);
        } else if (fragment instanceof QrBarcodeScanFragment) {
            name = getString(R.string.qr_barcode_pdf);
        } else if (fragment instanceof ExceltoPdfFragment) {
            name = getString(R.string.excel_to_pdf);
        } else if (fragment instanceof ViewFilesFragment) {
            name = checkViewFilesFragmentCode(fragment.getArguments());
        } else if (fragment instanceof HistoryFragment) {
            name = getString(R.string.history);
        } else if (fragment instanceof ExtractTextFragment) {
            name = getString(R.string.extract_text);
        } else if (fragment instanceof AddImagesFragment) {
            name = getString(R.string.add_images);
        } else if (fragment instanceof MergeFilesFragment) {
            name = getString(R.string.merge_pdf);
        } else if (fragment instanceof SplitFilesFragment) {
            name = getString(R.string.split_pdf);
        } else if (fragment instanceof InvertPdfFragment) {
            name = getString(R.string.invert_pdf);
        } else if (fragment instanceof RemoveDuplicatePagesFragment) {
            name = getString(R.string.remove_duplicate);
        } else if (fragment instanceof RemovePagesFragment) {
            name = fragment.getArguments() != null ?
                    fragment.getArguments().getString(BUNDLE_DATA) : null;
        } else if (fragment instanceof PdfToImageFragment) {
            name = getString(R.string.pdf_to_images);
        } else if (fragment instanceof ZipToPdfFragment) {
            name = getString(R.string.zip_to_pdf);
        }
        return name;
    }

    /**
     * Checks the arguments of the ViewFilesFragment
     * to determine the name of the fragment.
     * @param arguments A Bundle containing the args of the fragment.
     * @return The name of the fragment.
     */
    private String checkViewFilesFragmentCode(Bundle arguments) {
        if (arguments != null) {
            int code = arguments.getInt(BUNDLE_DATA);
            if (code == ROTATE_PAGES) {
                return ROTATE_PAGES_KEY;
            } else if (code == ADD_WATERMARK) {
                return ADD_WATERMARK_KEY;
            }
        }
        return getString(R.string.viewFiles);
    }

    private void setNavigationViewSelection(int id) {
        mNavigationView.setCheckedItem(id);
    }

    /**
     * Calls the getString method from the FragmentActivity.
     * @param resId The resource id.
     * @return the string for the given resource id.
     */
    private String getString(int resId) {
        return mContext.getString(resId);
    }

    /**
     * Calls the getSupportFragmentManager method from the FragmentActivity.
     * @return the FragmentManager.
     */
    private FragmentManager getSupportFragmentManager() {
        return mContext.getSupportFragmentManager();
    }

    /**
     * Calls the getIntent method from the FragmentActivity.
     * @return the Intent.
     */
    private Intent getIntent() {
        return mContext.getIntent();
    }
}

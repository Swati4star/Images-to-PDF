package swati4star.createpdf.providers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import swati4star.createpdf.R;
import swati4star.createpdf.fragment.AddImagesFragment;
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
import swati4star.createpdf.fragment.SplitFilesFragment;
import swati4star.createpdf.fragment.TextToPdfFragment;
import swati4star.createpdf.fragment.ViewFilesFragment;
import swati4star.createpdf.fragment.ZipToPdfFragment;

import static swati4star.createpdf.util.Constants.ADD_WATERMARK_KEY;
import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.ROTATE_PAGES_KEY;
import static swati4star.createpdf.util.DialogUtils.ADD_WATERMARK;
import static swati4star.createpdf.util.DialogUtils.ROTATE_PAGES;

/**
 * This is a fragment service that manages the fragments
 * mainly for the MainActivity.
 */
public class FragmentManagement implements IFragmentManagement {
    private FragmentActivity mContext;

    public FragmentManagement(FragmentActivity context) {
        this.mContext = context;
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
}

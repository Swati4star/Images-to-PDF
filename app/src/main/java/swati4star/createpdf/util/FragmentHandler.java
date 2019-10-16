package swati4star.createpdf.util;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import swati4star.createpdf.R;
import swati4star.createpdf.activity.MainActivity;
import swati4star.createpdf.fragment.AddImagesFragment;
import swati4star.createpdf.fragment.ExceltoPdfFragment;
import swati4star.createpdf.fragment.ExtractTextFragment;
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
import swati4star.createpdf.interfaces.OnBackPressedInterface;

public class FragmentHandler {
    private final MainActivity mMainActivity;
    private boolean mDoubleBackToExitPressedOnce;

    public FragmentHandler(MainActivity mMainActivity) {
        this.mMainActivity = mMainActivity;
    }

    public String getFragmentName(Fragment fragment) {
        String name = "set name";
        if (fragment instanceof ImageToPdfFragment) {
            name = mMainActivity.getString(R.string.images_to_pdf);
        } else if (fragment instanceof TextToPdfFragment) {
            name = mMainActivity.getString(R.string.text_to_pdf);
        } else if (fragment instanceof QrBarcodeScanFragment) {
            name = mMainActivity.getString(R.string.qr_barcode_pdf);
        } else if (fragment instanceof ExceltoPdfFragment) {
            name = mMainActivity.getString(R.string.excel_to_pdf);
        } else if (fragment instanceof ViewFilesFragment) {
            if (fragment.getArguments() != null) {
                int code = fragment.getArguments().getInt(Constants.BUNDLE_DATA);
                if (code == DialogUtils.ROTATE_PAGES) {
                    name = Constants.ROTATE_PAGES_KEY;
                } else if (code == DialogUtils.ADD_WATERMARK) {
                    name = Constants.ADD_WATERMARK_KEY;
                }
            } else {
                name = mMainActivity.getString(R.string.viewFiles);
            }
        } else if (fragment instanceof HistoryFragment) {
            name = mMainActivity.getString(R.string.history);
        } else if (fragment instanceof ExtractTextFragment) {
            name = mMainActivity.getString(R.string.extract_text);
        } else if (fragment instanceof AddImagesFragment) {
            name = mMainActivity.getString(R.string.add_images);
        } else if (fragment instanceof MergeFilesFragment) {
            name = mMainActivity.getString(R.string.merge_pdf);
        } else if (fragment instanceof SplitFilesFragment) {
            name = mMainActivity.getString(R.string.split_pdf);
        } else if (fragment instanceof InvertPdfFragment) {
            name = mMainActivity.getString(R.string.invert_pdf);
        } else if (fragment instanceof RemoveDuplicatePagesFragment) {
            name = mMainActivity.getString(R.string.remove_duplicate);
        } else if (fragment instanceof RemovePagesFragment) {
            name = fragment.getArguments().getString(Constants.BUNDLE_DATA);
        } else if (fragment instanceof PdfToImageFragment) {
            name = mMainActivity.getString(R.string.pdf_to_images);
        } else if (fragment instanceof ZipToPdfFragment) {
            name = mMainActivity.getString(R.string.zip_to_pdf);
        }
        return name;
    }

    public void handleBackPressForFragement(Fragment fragment, boolean mDoubleBackToExitPressedOnce) {
        this.mDoubleBackToExitPressedOnce = mDoubleBackToExitPressedOnce;
        if (fragment instanceof HomeFragment) {
            checkDoubleBackPress();
        } else if (checkFragmentBottomSheetBehavior(fragment))
            closeFragmentBottomSheet(fragment);
        else {
            // back stack count will be 1 when we open a item from favourite menu
            // on clicking back, return back to fav menu and change title
            int count = mMainActivity.getSupportFragmentManager().getBackStackEntryCount();
            setTitleOnBackPressed(count);
        }
    }

    public void setTitleOnBackPressed(int count) {
        if (count > 0) {
            String s = mMainActivity.getSupportFragmentManager().getBackStackEntryAt(count - 1).getName();
            mMainActivity.setTitle(s);
            mMainActivity.getSupportFragmentManager().popBackStack();
        } else {
            Fragment fragment = new HomeFragment();
            mMainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
            mMainActivity.setTitle(R.string.app_name);
            mMainActivity.setNavigationViewSelection(R.id.nav_home);
        }
    }

    public boolean checkFragmentBottomSheetBehavior(Fragment fragment) {
        return ((OnBackPressedInterface) fragment).checkSheetBehaviour();
    }

    public void closeFragmentBottomSheet(Fragment fragment) {
        ((OnBackPressedInterface) fragment).closeBottomSheet();
    }

    /**
     * Closes the app only when double clicked
     */
    public void checkDoubleBackPress() {
        if (this.mDoubleBackToExitPressedOnce) {
            mMainActivity.onBackPressed();
            return;
        }
        setmDoubleBackToExitPressedOnce(true);
        Toast.makeText(mMainActivity, R.string.confirm_exit_message, Toast.LENGTH_SHORT).show();
    }

    private void setmDoubleBackToExitPressedOnce(boolean b) {
        mDoubleBackToExitPressedOnce = true;
    }

    /**
     * Sets fragment title
     *
     * @param title - string resource id
     */
    public void setTitleFragment(int title) {
        if (title != 0)
            mMainActivity.setTitle(title);
    }
}
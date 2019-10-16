package swati4star.createpdf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import swati4star.createpdf.R;
import swati4star.createpdf.fragment.AboutUsFragment;
import swati4star.createpdf.fragment.AddImagesFragment;
import swati4star.createpdf.fragment.AddTextFragment;
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
import swati4star.createpdf.fragment.SettingsFragment;
import swati4star.createpdf.fragment.SplitFilesFragment;
import swati4star.createpdf.fragment.TextToPdfFragment;
import swati4star.createpdf.fragment.ViewFilesFragment;
import swati4star.createpdf.fragment.ZipToPdfFragment;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.FeedbackUtils;
import swati4star.createpdf.util.WhatsNewUtils;

public class FragmentBuilder {
    private final MainActivity mMainActivity;

    public FragmentBuilder(MainActivity mMainActivity) {
        this.mMainActivity = mMainActivity;
    }

    Fragment getFragment(@NonNull MenuItem item, Bundle bundle, FeedbackUtils feedbackUtils) {
        Fragment fragment = null;
        switch (item.getItemId()) {
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
                bundle.putString(Constants.BUNDLE_DATA, Constants.ADD_PWD);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_remove_password:
                fragment = new RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.REMOVE_PWd);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_share:
                feedbackUtils.shareApplication();
                break;
            case R.id.nav_about:
                fragment = new AboutUsFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.nav_extract_images:
                fragment = new PdfToImageFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.EXTRACT_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_pdf_to_images:
                fragment = new PdfToImageFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.PDF_TO_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_excel_to_pdf:
                fragment = new ExceltoPdfFragment();
                break;
            case R.id.nav_remove_pages:
                fragment = new RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.REMOVE_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_rearrange_pages:
                fragment = new RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.REORDER_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_compress_pdf:
                fragment = new RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.COMPRESS_PDF);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_add_images:
                fragment = new AddImagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.ADD_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_help:
                Intent intent = new Intent(mMainActivity, WelcomeActivity.class);
                intent.putExtra(Constants.SHOW_WELCOME_ACT, true);
                mMainActivity.startActivity(intent);
                break;
            case R.id.nav_remove_duplicate_pages:
                fragment = new RemoveDuplicatePagesFragment();
                break;
            case R.id.nav_invert_pdf:
                fragment = new InvertPdfFragment();
                break;
            case R.id.nav_add_watermark:
                fragment = new ViewFilesFragment();
                bundle.putInt(Constants.BUNDLE_DATA, DialogUtils.ADD_WATERMARK);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_zip_to_pdf:
                fragment = new ZipToPdfFragment();
                break;
            case R.id.nav_whatsNew:
                WhatsNewUtils.displayDialog(mMainActivity);
                break;
            case R.id.nav_rotate_pages:
                fragment = new ViewFilesFragment();
                bundle.putInt(Constants.BUNDLE_DATA, DialogUtils.ROTATE_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_text_extract:
                fragment = new ExtractTextFragment();
                break;
        }
        return fragment;
    }
}
package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.MainActivity;
import swati4star.createpdf.customviews.MyCardView;

import static swati4star.createpdf.util.Constants.ADD_IMAGES;
import static swati4star.createpdf.util.Constants.ADD_PWD;
import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.COMPRESS_PDF;
import static swati4star.createpdf.util.Constants.EXTRACT_IMAGES;
import static swati4star.createpdf.util.Constants.PDF_TO_IMAGES;
import static swati4star.createpdf.util.Constants.REMOVE_PAGES;
import static swati4star.createpdf.util.Constants.REMOVE_PWd;
import static swati4star.createpdf.util.Constants.REORDER_PAGES;
import static swati4star.createpdf.util.DialogUtils.ADD_WATERMARK;
import static swati4star.createpdf.util.DialogUtils.ROTATE_PAGES;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private Activity mActivity;
    @BindView(R.id.images_to_pdf)
    MyCardView imagesToPdf;
    @BindView(R.id.qr_barcode_to_pdf)
    MyCardView qrbarcodeToPdf;
    @BindView(R.id.text_to_pdf)
    MyCardView textToPdf;
    @BindView(R.id.view_files)
    MyCardView viewFiles;
    @BindView(R.id.view_history)
    MyCardView viewHistory;
    @BindView(R.id.split_pdf)
    MyCardView splitPdf;
    @BindView(R.id.merge_pdf)
    MyCardView mergePdf;
    @BindView(R.id.compress_pdf)
    MyCardView compressPdf;
    @BindView(R.id.remove_pages)
    MyCardView removePages;
    @BindView(R.id.rearrange_pages)
    MyCardView rearrangePages;
    @BindView(R.id.extract_images)
    MyCardView extractImages;
    @BindView(R.id.pdf_to_images)
    MyCardView mPdfToImages;
    @BindView(R.id.add_password)
    MyCardView addPassword;
    @BindView(R.id.remove_password)
    MyCardView removePassword;
    @BindView(R.id.rotate_pages)
    MyCardView rotatePdf;
    @BindView(R.id.add_watermark)
    MyCardView addWatermark;
    @BindView(R.id.add_images)
    MyCardView addImages;
    @BindView(R.id.remove_duplicates_pages_pdf)
    MyCardView removeDuplicatePages;
    @BindView(R.id.invert_pdf)
    MyCardView invertPdf;

    private SparseIntArray mFragmentPositionMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootview);
        fillMap();
        imagesToPdf.setOnClickListener(this);
        qrbarcodeToPdf.setOnClickListener(this);
        textToPdf.setOnClickListener(this);
        viewFiles.setOnClickListener(this);
        viewHistory.setOnClickListener(this);
        splitPdf.setOnClickListener(this);
        mergePdf.setOnClickListener(this);
        compressPdf.setOnClickListener(this);
        removePages.setOnClickListener(this);
        rearrangePages.setOnClickListener(this);
        extractImages.setOnClickListener(this);
        mPdfToImages.setOnClickListener(this);
        addPassword.setOnClickListener(this);
        removePassword.setOnClickListener(this);
        rotatePdf.setOnClickListener(this);
        addWatermark.setOnClickListener(this);
        addImages.setOnClickListener(this);
        removeDuplicatePages.setOnClickListener(this);
        invertPdf.setOnClickListener(this);
        return rootview;
    }

    private void fillMap() {
        mFragmentPositionMap = new SparseIntArray();
        mFragmentPositionMap.append(R.id.images_to_pdf, R.id.nav_camera);
        mFragmentPositionMap.append(R.id.qr_barcode_to_pdf, R.id.nav_qrcode);
        mFragmentPositionMap.append(R.id.view_files, R.id.nav_gallery);
        mFragmentPositionMap.append(R.id.rotate_pages, R.id.nav_gallery);
        mFragmentPositionMap.append(R.id.add_watermark, R.id.nav_add_watermark);
        mFragmentPositionMap.append(R.id.merge_pdf, R.id.nav_merge);
        mFragmentPositionMap.append(R.id.split_pdf, R.id.nav_split);
        mFragmentPositionMap.append(R.id.text_to_pdf, R.id.nav_text_to_pdf);
        mFragmentPositionMap.append(R.id.compress_pdf, R.id.nav_compress_pdf);
        mFragmentPositionMap.append(R.id.remove_pages, R.id.nav_remove_pages);
        mFragmentPositionMap.append(R.id.rearrange_pages, R.id.nav_rearrange_pages);
        mFragmentPositionMap.append(R.id.extract_images, R.id.nav_extract_images);
        mFragmentPositionMap.append(R.id.view_history, R.id.nav_history);
        mFragmentPositionMap.append(R.id.pdf_to_images, R.id.nav_pdf_to_images);
        mFragmentPositionMap.append(R.id.add_password, R.id.nav_add_password);
        mFragmentPositionMap.append(R.id.remove_password, R.id.nav_remove_password);
        mFragmentPositionMap.append(R.id.add_images, R.id.nav_add_images);
        mFragmentPositionMap.append(R.id.remove_duplicates_pages_pdf, R.id.nav_remove_duplicate_pages);
        mFragmentPositionMap.append(R.id.invert_pdf, R.id.nav_invert_pdf);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onClick(View v) {

        Fragment fragment = null;
        FragmentManager fragmentManager = getFragmentManager();
        Bundle bundle = new Bundle();
        highlightNavigationDrawerItem(mFragmentPositionMap.get(v.getId()));

        switch (v.getId()) {
            case R.id.images_to_pdf:
                fragment = new ImageToPdfFragment();
                break;
            case R.id.qr_barcode_to_pdf:
                fragment = new QrBarcodeScanFragment();
                break;
            case R.id.text_to_pdf:
                fragment = new TextToPdfFragment();
                break;
            case R.id.view_files:
                fragment = new ViewFilesFragment();
                break;
            case R.id.view_history:
                fragment = new HistoryFragment();
                break;
            case R.id.merge_pdf:
                fragment = new MergeFilesFragment();
                break;
            case R.id.split_pdf:
                fragment = new SplitFilesFragment();
                break;
            case R.id.compress_pdf:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, COMPRESS_PDF);
                fragment.setArguments(bundle);
                break;
            case R.id.extract_images:
                fragment = new PdfToImageFragment();
                bundle.putString(BUNDLE_DATA, EXTRACT_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.pdf_to_images:
                fragment = new PdfToImageFragment();
                bundle.putString(BUNDLE_DATA, PDF_TO_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.remove_pages:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, REMOVE_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.rearrange_pages:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, REORDER_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.add_password:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, ADD_PWD);
                fragment.setArguments(bundle);
                break;
            case R.id.remove_password:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, REMOVE_PWd);
                fragment.setArguments(bundle);
                break;
            case R.id.rotate_pages:
                fragment = new ViewFilesFragment();
                bundle.putInt(BUNDLE_DATA, ROTATE_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.add_watermark:
                fragment = new ViewFilesFragment();
                bundle.putInt(BUNDLE_DATA, ADD_WATERMARK);
                fragment.setArguments(bundle);
                break;
            case R.id.add_images:
                fragment = new AddImagesFragment();
                bundle.putString(BUNDLE_DATA, ADD_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.remove_duplicates_pages_pdf:
                fragment = new RemoveDuplicatePagesFragment();
                break;
            case R.id.invert_pdf:
                fragment = new InvertPdfFragment();
                break;
        }

        try {
            if (fragment != null && fragmentManager != null)
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void highlightNavigationDrawerItem(int id) {
        if (mActivity instanceof MainActivity)
            ((MainActivity) mActivity).setNavigationViewSelection(id);
    }
}

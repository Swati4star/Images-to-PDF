package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.base.R;
import swati4star.createpdf.activity.MainActivity;
import swati4star.createpdf.base.R2;
import swati4star.createpdf.customviews.MyCardView;

import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.COMPRESS_PDF;
import static swati4star.createpdf.util.Constants.REMOVE_PAGES;
import static swati4star.createpdf.util.Constants.REORDER_PAGES;
import static swati4star.createpdf.util.DialogUtils.ADD_PASSWORD;
import static swati4star.createpdf.util.DialogUtils.REMOVE_PASSWORD;
import static swati4star.createpdf.util.DialogUtils.ROTATE_PAGES;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private Activity mActivity;
    @BindView(R2.id.images_to_pdf)
    MyCardView imagesToPdf;
    @BindView(R2.id.qr_barcode_to_pdf)
    MyCardView qrbarcodeToPdf;
    @BindView(R2.id.text_to_pdf)
    MyCardView textToPdf;
    @BindView(R2.id.view_files)
    MyCardView viewFiles;
    @BindView(R2.id.view_history)
    MyCardView viewHistory;
    @BindView(R2.id.split_pdf)
    MyCardView splitPdf;
    @BindView(R2.id.merge_pdf)
    MyCardView mergePdf;
    @BindView(R2.id.compress_pdf)
    MyCardView compressPdf;
    @BindView(R2.id.remove_pages)
    MyCardView removePages;
    @BindView(R2.id.rearrange_pages)
    MyCardView rearrangePages;
    @BindView(R2.id.extract_images)
    MyCardView extractImages;
    @BindView(R2.id.add_password)
    MyCardView addPassword;
    @BindView(R2.id.remove_password)
    MyCardView removePassword;
    @BindView(R2.id.rotate_pages)
    MyCardView rotatePdf;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootview);
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
        addPassword.setOnClickListener(this);
        removePassword.setOnClickListener(this);
        rotatePdf.setOnClickListener(this);
        return rootview;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    private void setNavigationViewSelection(int index) {
        if (mActivity instanceof MainActivity)
            ((MainActivity) mActivity).setNavigationViewSelection(index);
    }

    @Override
    public void onClick(View v) {

        Fragment fragment = null;
        FragmentManager fragmentManager = getFragmentManager();
        Bundle bundle = new Bundle();

        /**
         * In library projects, we cannot use Switch statement for ids
         * https://stackoverflow.com/questions/12475166/resource-id-in-android-library-project
         */
        int i = v.getId();
        if (i == R.id.images_to_pdf) {
            fragment = new ImageToPdfFragment();
            setNavigationViewSelection(1);

        } else if (i == R.id.qr_barcode_to_pdf) {
            fragment = new QrBarcodeScanFragment();
            setNavigationViewSelection(2);

        } else if (i == R.id.text_to_pdf) {
            fragment = new TextToPdfFragment();
            setNavigationViewSelection(6);

        } else if (i == R.id.view_files) {
            fragment = new ViewFilesFragment();
            setNavigationViewSelection(3);

        } else if (i == R.id.view_history) {
            fragment = new HistoryFragment();
            setNavigationViewSelection(11);

        } else if (i == R.id.merge_pdf) {
            fragment = new MergeFilesFragment();
            setNavigationViewSelection(4);

        } else if (i == R.id.split_pdf) {
            fragment = new SplitFilesFragment();
            setNavigationViewSelection(5);

        } else if (i == R.id.compress_pdf) {
            fragment = new RemovePagesFragment();
            bundle.putString(BUNDLE_DATA, COMPRESS_PDF);
            fragment.setArguments(bundle);
            setNavigationViewSelection(7);

        } else if (i == R.id.extract_images) {
            fragment = new ExtractImagesFragment();
            setNavigationViewSelection(10);

        } else if (i == R.id.remove_pages) {
            fragment = new RemovePagesFragment();
            bundle.putString(BUNDLE_DATA, REMOVE_PAGES);
            fragment.setArguments(bundle);
            setNavigationViewSelection(8);

        } else if (i == R.id.rearrange_pages) {
            fragment = new RemovePagesFragment();
            bundle.putString(BUNDLE_DATA, REORDER_PAGES);
            fragment.setArguments(bundle);
            setNavigationViewSelection(9);

        } else if (i == R.id.add_password) {
            fragment = new ViewFilesFragment();
            bundle.putInt(BUNDLE_DATA, ADD_PASSWORD);
            fragment.setArguments(bundle);
            setNavigationViewSelection(3);

        } else if (i == R.id.remove_password) {
            fragment = new ViewFilesFragment();
            bundle.putInt(BUNDLE_DATA, REMOVE_PASSWORD);
            fragment.setArguments(bundle);
            setNavigationViewSelection(3);

        } else if (i == R.id.rotate_pages) {
            fragment = new ViewFilesFragment();
            bundle.putInt(BUNDLE_DATA, ROTATE_PAGES);
            fragment.setArguments(bundle);
            setNavigationViewSelection(3);

        }

        try {
            if (fragment != null && fragmentManager != null)
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

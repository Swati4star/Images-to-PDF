package swati4star.createpdf.fragment;

import static swati4star.createpdf.util.Constants.ADD_IMAGES;
import static swati4star.createpdf.util.Constants.ADD_PWD;
import static swati4star.createpdf.util.Constants.ADD_WATERMARK;
import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.COMPRESS_PDF;
import static swati4star.createpdf.util.Constants.EXTRACT_IMAGES;
import static swati4star.createpdf.util.Constants.PDF_TO_IMAGES;
import static swati4star.createpdf.util.Constants.REMOVE_PAGES;
import static swati4star.createpdf.util.Constants.REMOVE_PWd;
import static swati4star.createpdf.util.Constants.REORDER_PAGES;
import static swati4star.createpdf.util.Constants.ROTATE_PAGES;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import swati4star.createpdf.R;
import swati4star.createpdf.activity.MainActivity;
import swati4star.createpdf.adapter.RecentListAdapter;
import swati4star.createpdf.databinding.FragmentHomeBinding;
import swati4star.createpdf.fragment.texttopdf.TextToPdfFragment;
import swati4star.createpdf.model.HomePageItem;
import swati4star.createpdf.util.CommonCodeUtils;
import swati4star.createpdf.util.RecentUtil;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private Activity mActivity;
    private Map<Integer, HomePageItem> mFragmentPositionMap;
    private RecentListAdapter mAdapter;
    private FragmentHomeBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        View rootView = mBinding.getRoot();
        mFragmentPositionMap = CommonCodeUtils.getInstance().fillNavigationItemsMap(true);

        mBinding.imagesToPdf.setOnClickListener(this);
        mBinding.qrBarcodeToPdf.setOnClickListener(this);
        mBinding.textToPdf.setOnClickListener(this);
        mBinding.viewFiles.setOnClickListener(this);
        mBinding.viewHistory.setOnClickListener(this);
        mBinding.splitPdf.setOnClickListener(this);
        mBinding.mergePdf.setOnClickListener(this);
        mBinding.compressPdf.setOnClickListener(this);
        mBinding.removePages.setOnClickListener(this);
        mBinding.rearrangePages.setOnClickListener(this);
        mBinding.extractImages.setOnClickListener(this);
        mBinding.pdfToImages.setOnClickListener(this);
        mBinding.addPassword.setOnClickListener(this);
        mBinding.removePassword.setOnClickListener(this);
        mBinding.rotatePages.setOnClickListener(this);
        mBinding.addWatermark.setOnClickListener(this);
        mBinding.addImages.setOnClickListener(this);
        mBinding.removeDuplicatesPagesPdf.setOnClickListener(this);
        mBinding.invertPdf.setOnClickListener(this);
        mBinding.zipToPdf.setOnClickListener(this);
        mBinding.excelToPdf.setOnClickListener(this);
        mBinding.extractText.setOnClickListener(this);
        mBinding.addText.setOnClickListener(this);

        mAdapter = new RecentListAdapter(this);
        mBinding.recentList.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onViewCreated(
            @NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            LinkedHashMap<String, Map<String, String>> mRecentList = RecentUtil.getInstance()
                    .getList(PreferenceManager.getDefaultSharedPreferences(mActivity));
            if (!mRecentList.isEmpty()) {
                mBinding.recentLbl.setVisibility(View.VISIBLE);
                mBinding.recentListLay.setVisibility(View.VISIBLE);
                List<String> featureItemIds = new ArrayList<>(mRecentList.keySet());
                List<Map<String, String>> featureItemList = new ArrayList<>(mRecentList.values());
                mAdapter.updateList(featureItemIds, featureItemList);
                mAdapter.notifyDataSetChanged();
            } else {
                mBinding.recentLbl.setVisibility(View.GONE);
                mBinding.recentListLay.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

        highlightNavigationDrawerItem(mFragmentPositionMap.get(v.getId()).getNavigationItemId());
        setTitleFragment(mFragmentPositionMap.get(v.getId()).getTitleString());

        Map<String, String> feature = new HashMap<>();
        feature.put(
                String.valueOf(mFragmentPositionMap.get(v.getId()).getTitleString()),
                String.valueOf(mFragmentPositionMap.get(v.getId()).getmDrawableId()));

        try {
            RecentUtil.getInstance().addFeatureInRecentList(PreferenceManager
                    .getDefaultSharedPreferences(mActivity), v.getId(), feature);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
            case R.id.zip_to_pdf:
                fragment = new ZipToPdfFragment();
                break;
            case R.id.excel_to_pdf:
                fragment = new ExceltoPdfFragment();
                break;
            case R.id.extract_text:
                fragment = new ExtractTextFragment();
                break;
            case R.id.add_text:
                fragment = new AddTextFragment();
                break;
        }

        try {
            if (fragment != null && fragmentManager != null)
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Highligh navigation drawer item
     *
     * @param id - item id to be hjighlighted
     */
    private void highlightNavigationDrawerItem(int id) {
        if (mActivity instanceof MainActivity)
            ((MainActivity) mActivity).setNavigationViewSelection(id);
    }

    /**
     * Sets the title on action bar
     *
     * @param title - title of string to be shown
     */
    private void setTitleFragment(int title) {
        if (title != 0)
            mActivity.setTitle(title);
    }
}

package swati4star.createpdf.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Font;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.interfaces.OnItemClickListner;
import swati4star.createpdf.model.ImageToPDFOptions;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.PageSizeUtils;

import static swati4star.createpdf.util.Constants.DEFAULT_COMPRESSION;
import static swati4star.createpdf.util.SettingsOptions.ImageEnhancementOptionsUtils.getEnhancementOptions;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements OnItemClickListner {

    @BindView(R.id.settings_list)
    RecyclerView mEnhancementOptionsRecycleView;

    private ArrayList<EnhancementOptionsEntity> mEnhancementOptionsEntityArrayList = new ArrayList<>();
    private Activity mActivity;
    private Context mContext;
    private EnhancementOptionsAdapter mEnhancementOptionsAdapter;
    private ImageToPDFOptions mPdfOptions;
    private SharedPreferences mSharedPreferences;
    private String mFontTitle, mDefaultPageSize;
    private Font.FontFamily mFontFamily;
    private int mFontSize;
    public static String mPageSize = "A4";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, root);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mPdfOptions = new ImageToPDFOptions();
        mFontTitle = String.format(getString(R.string.edit_font_size),
                mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, Constants.DEFAULT_FONT_SIZE));
        mDefaultPageSize = mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT,
                Constants.DEFAULT_PAGE_SIZE);
        mFontFamily = Font.FontFamily.valueOf(mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY));
        mFontSize = mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, Constants.DEFAULT_FONT_SIZE);

        showEnhancementOptions();

        return root;
    }

    private void showEnhancementOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        mEnhancementOptionsEntityArrayList = getEnhancementOptions(mActivity, mFontTitle, mFontFamily);
        mEnhancementOptionsAdapter = new EnhancementOptionsAdapter(this, mEnhancementOptionsEntityArrayList);
        mEnhancementOptionsRecycleView.setAdapter(mEnhancementOptionsAdapter);
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                changeCompressImage();
                break;
            case 1:
                setPageSize();
                break;
            case 2:
                editFontSize();
                break;
            case 3:
                changeFontFamily();
                break;
        }
    }

    private void showCompressionDef() {
        mEnhancementOptionsEntityArrayList.get(0)
                .setName(mContext.getString(R.string.image_compression_value_default) +
                        mSharedPreferences.getInt(DEFAULT_COMPRESSION, 30) + "%)");
        mEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    private void changeCompressImage() {
        String title = getString(R.string.compress_image) + " " +
                mSharedPreferences.getInt(DEFAULT_COMPRESSION, 30) + "%)";

        new MaterialDialog.Builder(mActivity)
                .title(title)
                .customView(R.layout.compress_image_default, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive((dialog1, which) -> {
                    final EditText qualityInput = dialog1.getCustomView().findViewById(R.id.quality);

                    int check;
                    try {
                        check = Integer.parseInt(String.valueOf(qualityInput.getText()));
                        if (check > 100 || check < 0) {
                            showSnackbar(mActivity, R.string.invalid_entry);
                        } else {
                            mPdfOptions.setQualityString(String.valueOf(check));
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putInt(DEFAULT_COMPRESSION, check);
                            editor.apply();
                            showCompressionDef();
                        }
                    } catch (NumberFormatException e) {
                        showSnackbar(mActivity, R.string.invalid_entry);
                    }
                }).show();
    }

    private void setPageSize() {
        PageSizeUtils utils = new PageSizeUtils(mActivity, R.layout.set_page_size_default);
        utils.showPageSizeDialog();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.DEFAULT_PAGE_SIZE_TEXT, mPageSize);
        editor.apply();
        showPageSizeDef();
    }

    private void showPageSizeDef() {
        mEnhancementOptionsEntityArrayList.get(0)
                .setName(mContext.getString(R.string.image_compression_value_default) +
                        mSharedPreferences.getInt(DEFAULT_COMPRESSION, 30) + "%)");
        mEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    private void editFontSize() {
        new MaterialDialog.Builder(mActivity)
                .title(mFontTitle)
                .customView(R.layout.dialog_font_size_default, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    final EditText fontInput = dialog.getCustomView().findViewById(R.id.fontInput);
                    try {
                        int check = Integer.parseInt(String.valueOf(fontInput.getText()));
                        if (check > 1000 || check < 0) {
                            showSnackbar(mActivity, R.string.invalid_entry);
                        } else {
                            mFontSize = check;
                            showFontSizeDef();
                            showSnackbar(mActivity, R.string.font_size_changed);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putInt(Constants.DEFAULT_FONT_SIZE_TEXT, mFontSize);
                            editor.apply();
                            mFontTitle = String.format(getString(R.string.edit_font_size),
                                    mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT,
                                            Constants.DEFAULT_FONT_SIZE));

                        }
                    } catch (NumberFormatException e) {
                        showSnackbar(mActivity, R.string.invalid_entry);
                    }
                })
                .show();
    }

    private void showFontSizeDef() {
        mEnhancementOptionsEntityArrayList.get(2)
                .setName(String.format(getString(R.string.font_size_value_def) + String.valueOf(mFontSize)));
        mEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    private void changeFontFamily() {
        String fontFamily = mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY);
        int ordinal = Font.FontFamily.valueOf(fontFamily).ordinal();
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .title(String.format(getString(R.string.default_font_family_text), fontFamily))
                .customView(R.layout.dialog_font_family_default, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    View view = dialog.getCustomView();
                    RadioGroup radioGroup = view.findViewById(R.id.radio_group_font_family);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = view.findViewById(selectedId);
                    String fontFamily1 = radioButton.getText().toString();
                    mFontFamily = Font.FontFamily.valueOf(fontFamily1);
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(Constants.DEFAULT_FONT_FAMILY_TEXT, fontFamily1);
                    editor.apply();
                    showFontFamily();
                })
                .build();
        RadioGroup radioGroup = materialDialog.getCustomView().findViewById(R.id.radio_group_font_family);
        RadioButton rb = (RadioButton) radioGroup.getChildAt(ordinal);
        rb.setChecked(true);
        materialDialog.show();
    }

    private void showFontFamily() {
        mEnhancementOptionsEntityArrayList.get(3)
                .setName(getString(R.string.font_family_value_def) + mFontFamily.name());
        mEnhancementOptionsAdapter.notifyDataSetChanged();
    }


}

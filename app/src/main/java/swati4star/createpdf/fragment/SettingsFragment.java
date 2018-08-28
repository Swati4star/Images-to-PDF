package swati4star.createpdf.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Font;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.interfaces.OnItemClickListner;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.model.ImageToPDFOptions;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.PageSizeUtils;

import static swati4star.createpdf.util.Constants.DEFAULT_COMPRESSION;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_SIZE;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_SIZE_TEXT;
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
    private String mFontTitle, mDefaultPageSize, mPageTitle;
    private Font.FontFamily mFontFamily;
    private int mFontSize;
    public static String mPageSize = "A4";
    private HashMap<Integer, Integer> mPageSizeToString;
    private static int layout = R.layout.set_page_size_dialog;

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
        mDefaultPageSize = mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT,
                Constants.DEFAULT_PAGE_SIZE);
        mPageTitle = mActivity.getString(R.string.page_size_value_def);
        mPageSizeToString = new HashMap<>();
        mPageSizeToString.put(R.id.page_size_default, R.string.a4);
        mPageSizeToString.put(R.id.page_size_legal, R.string.legal);
        mPageSizeToString.put(R.id.page_size_executive, R.string.executive);
        mPageSizeToString.put(R.id.page_size_ledger, R.string.ledger);
        mPageSizeToString.put(R.id.page_size_tabloid, R.string.tabloid);
        mPageSizeToString.put(R.id.page_size_letter, R.string.letter);

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
        Log.d("COMPRESSION", mSharedPreferences.getInt(DEFAULT_COMPRESSION, 30) + "%");
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

    private void showPageSizeDef() {
        Log.d("PAGE SIZE", mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT,
                Constants.DEFAULT_PAGE_SIZE));
        mEnhancementOptionsEntityArrayList.get(1)
                .setName(mContext.getString(R.string.page_size_value_def) +
                         mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT,
                         Constants.DEFAULT_PAGE_SIZE));
        mEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    /**
     * @param selectionId   - id of selected radio button
     * @param spinnerAValue - Value of A0 to A10 spinner
     * @param spinnerBValue - Value of B0 to B10 spinner
     * @return - Rectangle page size
     */
    private String getPageSize(int selectionId, String spinnerAValue, String spinnerBValue) {
        String stringPageSize;
        switch (selectionId) {
            case R.id.page_size_a0_a10:
                stringPageSize = spinnerAValue;
                mPageSize = stringPageSize.substring(0, stringPageSize.indexOf(" "));
                break;
            case R.id.page_size_b0_b10:
                stringPageSize = spinnerBValue;
                mPageSize = stringPageSize.substring(0, stringPageSize.indexOf(" "));
                break;
            default:
                mPageSize = mActivity.getString(mPageSizeToString.get(selectionId));

        }
        return mPageSize;
    }

    public void setPageSize() {

        MaterialDialog materialDialog = getPageSizeDialog(mPageTitle);

        View view = materialDialog.getCustomView();
        RadioGroup radioGroup = view.findViewById(R.id.radio_group_page_size);
        Spinner spinnerA = view.findViewById(R.id.spinner_page_size_a0_a10);
        Spinner spinnerB = view.findViewById(R.id.spinner_page_size_b0_b10);
        RadioButton radioButtonDefault = view.findViewById(R.id.page_size_default);
        radioButtonDefault.setText(String.format(mActivity.getString(R.string.default_page_size), mDefaultPageSize));

        if (mPageSize.equals(mDefaultPageSize)) {
            radioGroup.check(R.id.page_size_default);
        } else if (mPageSize.startsWith("A")) {
            radioGroup.check(R.id.page_size_a0_a10);
            spinnerA.setSelection(java.lang.Integer.parseInt(mPageSize.substring(1)));
        } else if (mPageSize.startsWith("B")) {
            radioGroup.check(R.id.page_size_b0_b10);
            spinnerB.setSelection(java.lang.Integer.parseInt(mPageSize.substring(1)));
        } else {
            Integer key = getKey(mPageSizeToString, mPageSize);
            if (key != null)
                radioGroup.check(key.intValue());
        }

        materialDialog.show();
    }

    private MaterialDialog getPageSizeDialog(String title) {
        return new MaterialDialog.Builder(mActivity)
                .title(title)
                .customView(layout, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive((dialog1, which) -> {
                    View view = dialog1.getCustomView();
                    RadioGroup radioGroup = view.findViewById(R.id.radio_group_page_size);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    Spinner spinnerA = view.findViewById(R.id.spinner_page_size_a0_a10);
                    Spinner spinnerB = view.findViewById(R.id.spinner_page_size_b0_b10);
                    mPageSize = getPageSize(selectedId, spinnerA.getSelectedItem().toString(),
                            spinnerB.getSelectedItem().toString());
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(Constants.DEFAULT_PAGE_SIZE_TEXT, mPageSize);
                    editor.apply();
                    showPageSizeDef();
                }).build();
    }

    private Integer getKey(HashMap<Integer, Integer> map, String value) {
        for (HashMap.Entry<Integer, Integer> entry : map.entrySet()) {
            if (value.equals(mActivity.getString(entry.getValue()))) {
                return entry.getKey();
            }
        }
        return null;
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

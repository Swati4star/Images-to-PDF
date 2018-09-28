package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Font;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lib.folderpicker.FolderPicker;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.interfaces.OnItemClickListner;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.PageSizeUtils;

import static swati4star.createpdf.util.Constants.DEFAULT_COMPRESSION;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.DialogUtils.createCustomDialogWithoutContent;
import static swati4star.createpdf.util.SettingsOptions.ImageEnhancementOptionsUtils.getEnhancementOptions;
import static swati4star.createpdf.util.StringUtils.getDefaultStorageLocation;
import static swati4star.createpdf.util.StringUtils.showSnackbar;
import static swati4star.createpdf.util.ThemeUtils.getSelectedThemePosition;
import static swati4star.createpdf.util.ThemeUtils.saveTheme;

public class SettingsFragment extends Fragment implements OnItemClickListner {

    @BindView(R.id.settings_list)
    RecyclerView mEnhancementOptionsRecycleView;
    @BindView(R.id.storagelocation)
    TextView storageLocation;

    private Activity mActivity;
    private SharedPreferences mSharedPreferences;

    public SettingsFragment() {
    }

    static final int MODIFY_STORAGE_LOCATION_CODE = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, root);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        storageLocation.setText(mSharedPreferences.getString(STORAGE_LOCATION,
                getDefaultStorageLocation()));
        showSettingsOptions();
        return root;
    }

    @OnClick(R.id.storagelocation)
    void modifyStorageLocation() {
        Intent intent = new Intent(mActivity, FolderPicker.class);
        startActivityForResult(intent, MODIFY_STORAGE_LOCATION_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MODIFY_STORAGE_LOCATION_CODE:
                if (data.getExtras() != null) {
                    String folderLocation = data.getExtras().getString("data") + "/";
                    Log.i("folderLocation", folderLocation);
                    mSharedPreferences.edit().putString(STORAGE_LOCATION, folderLocation).apply();
                    showSnackbar(mActivity, R.string.storage_location_modified);
                    storageLocation.setText(mSharedPreferences.getString(STORAGE_LOCATION,
                            getDefaultStorageLocation()));
                } else {
                    showSnackbar(mActivity, R.string.error_occurred);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showSettingsOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        ArrayList<EnhancementOptionsEntity> mEnhancementOptionsEntityArrayList = getEnhancementOptions(mActivity);
        EnhancementOptionsAdapter adapter =
                new EnhancementOptionsAdapter(this, mEnhancementOptionsEntityArrayList);
        mEnhancementOptionsRecycleView.setAdapter(adapter);
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
            case 4:
                setTheme();
                break;
        }
    }

    /**
     * To modify default image compression value
     */
    private void changeCompressImage() {

        createCustomDialogWithoutContent(mActivity, R.string.compression_image_edit)
                .customView(R.layout.compress_image_default, true)
                .onPositive((dialog1, which) -> {
                    final EditText qualityInput = dialog1.getCustomView().findViewById(R.id.quality);
                    int check;
                    try {
                        check = Integer.parseInt(String.valueOf(qualityInput.getText()));
                        if (check > 100 || check < 0) {
                            showSnackbar(mActivity, R.string.invalid_entry);
                        } else {
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putInt(DEFAULT_COMPRESSION, check);
                            editor.apply();
                            showSettingsOptions();
                        }
                    } catch (NumberFormatException e) {
                        showSnackbar(mActivity, R.string.invalid_entry);
                    }
                }).show();
    }


    /**
     * To modify font size
     */
    private void editFontSize() {
        MaterialDialog.Builder builder = createCustomDialogWithoutContent(mActivity,
                R.string.font_size_edit);
        builder.customView(R.layout.dialog_font_size_default, true)
                .onPositive((dialog, which) -> {
                    final EditText fontInput = dialog.getCustomView().findViewById(R.id.fontInput);
                    try {
                        int check = Integer.parseInt(String.valueOf(fontInput.getText()));
                        if (check > 1000 || check < 0) {
                            showSnackbar(mActivity, R.string.invalid_entry);
                        } else {
                            showSnackbar(mActivity, R.string.font_size_changed);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putInt(Constants.DEFAULT_FONT_SIZE_TEXT, check);
                            editor.apply();
                            showSettingsOptions();

                        }
                    } catch (NumberFormatException e) {
                        showSnackbar(mActivity, R.string.invalid_entry);
                    }
                })
                .show();
    }

    /**
     * To modify font family
     */
    private void changeFontFamily() {
        String fontFamily = mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY);
        int ordinal = Font.FontFamily.valueOf(fontFamily).ordinal();
        MaterialDialog.Builder builder = createCustomDialogWithoutContent(mActivity,
                R.string.font_family_edit);
        MaterialDialog materialDialog = builder.customView(R.layout.dialog_font_family_default, true)
                .onPositive((dialog, which) -> {
                    View view = dialog.getCustomView();
                    RadioGroup radioGroup = view.findViewById(R.id.radio_group_font_family);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = view.findViewById(selectedId);
                    String fontFamily1 = radioButton.getText().toString();
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(Constants.DEFAULT_FONT_FAMILY_TEXT, fontFamily1);
                    editor.apply();
                    showSettingsOptions();
                })
                .build();
        RadioGroup radioGroup = materialDialog.getCustomView().findViewById(R.id.radio_group_font_family);
        RadioButton rb = (RadioButton) radioGroup.getChildAt(ordinal);
        rb.setChecked(true);
        materialDialog.show();
    }

    /**
     * To modify page size
     */
    public void setPageSize() {
        PageSizeUtils utils = new PageSizeUtils(mActivity);
        MaterialDialog materialDialog = utils.showPageSizeDialog(R.layout.set_page_size_dialog_default, true);
        materialDialog.setOnDismissListener(dialog -> showSettingsOptions());
    }

    /**
     * To modify theme
     */
    public void setTheme() {
        MaterialDialog.Builder builder = createCustomDialogWithoutContent(mActivity,
                R.string.theme_edit);
        MaterialDialog materialDialog = builder.customView(R.layout.dialog_theme_default, true)
                .onPositive(((dialog, which) -> {
                    View view = dialog.getCustomView();
                    RadioGroup radioGroup = view.findViewById(R.id.radio_group_themes);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = view.findViewById(selectedId);
                    String themeName = radioButton.getText().toString();
                    saveTheme(mActivity, themeName);
                    mActivity.recreate();
                }))
                .build();
        RadioGroup radioGroup = materialDialog.getCustomView().findViewById(R.id.radio_group_themes);
        RadioButton rb = (RadioButton) radioGroup.getChildAt(getSelectedThemePosition(mActivity));
        rb.setChecked(true);
        materialDialog.show();
    }
}

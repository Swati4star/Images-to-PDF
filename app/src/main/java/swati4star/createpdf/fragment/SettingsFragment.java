package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Font;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.OnClick;
//import lib.folderpicker.FolderPicker;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.interfaces.OnItemClickListener;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.ImageUtils;
import swati4star.createpdf.util.PageSizeUtils;
import swati4star.createpdf.util.SharedPreferencesUtil;
import swati4star.createpdf.util.StringUtils;
import swati4star.createpdf.util.ThemeUtils;

import static swati4star.createpdf.util.Constants.DEFAULT_COMPRESSION;
import static swati4star.createpdf.util.Constants.MASTER_PWD_STRING;
import static swati4star.createpdf.util.Constants.MODIFY_STORAGE_LOCATION_CODE;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.appName;
import static swati4star.createpdf.util.SettingsOptions.getEnhancementOptions;

public class SettingsFragment extends Fragment implements OnItemClickListener {

    @BindView(R.id.settings_list)
    RecyclerView mEnhancementOptionsRecycleView;
    @BindView(R.id.storagelocation)
    TextView storageLocation;

    private Activity mActivity;
    private SharedPreferences mSharedPreferences;

    public SettingsFragment() {
    }

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
                StringUtils.getInstance().getDefaultStorageLocation()));
        showSettingsOptions();
        return root;
    }

//    @OnClick(R.id.storagelocation)
//    void modifyStorageLocation() {
////        Intent intent = new Intent(mActivity, FolderPicker.class);
////        startActivityForResult(intent, MODIFY_STORAGE_LOCATION_CODE);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MODIFY_STORAGE_LOCATION_CODE) {
            if (data.getExtras() != null) {
                String folderLocation = data.getExtras().getString("data") + "/";
                mSharedPreferences.edit().putString(STORAGE_LOCATION, folderLocation).apply();
                StringUtils.getInstance().showSnackbar(mActivity, R.string.storage_location_modified);
                storageLocation.setText(mSharedPreferences.getString(STORAGE_LOCATION,
                        StringUtils.getInstance().getDefaultStorageLocation()));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showSettingsOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 2);
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
            case 5:
                ImageUtils.getInstance().showImageScaleTypeDialog(mActivity, true);
                break;
            case 6:
                changeMasterPassword();
                break;
            case 7:
                setShowPageNumber();
        }
    }

    /**
     * To modify master password of PDFs
     */
    private void changeMasterPassword() {
        MaterialDialog.Builder builder = DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity,
                R.string.change_master_pwd);
        MaterialDialog materialDialog =
                builder.customView(R.layout.dialog_change_master_pwd, true)
                        .onPositive((dialog1, which) -> {
                            View view = dialog1.getCustomView();
                            EditText et = view.findViewById(R.id.value);
                            String value = et.getText().toString();
                            if (!value.isEmpty())
                                mSharedPreferences.edit().putString(MASTER_PWD_STRING, value).apply();
                            else
                                StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);


                        }).build();
        View view = materialDialog.getCustomView();
        TextView tv = view.findViewById(R.id.content);
        tv.setText(String.format(mActivity.getString(R.string.current_master_pwd),
                mSharedPreferences.getString(MASTER_PWD_STRING, appName)));
        materialDialog.show();
    }

    /**
     * To modify default image compression value
     */
    private void changeCompressImage() {

        MaterialDialog dialog = DialogUtils.getInstance()
                .createCustomDialogWithoutContent(mActivity, R.string.compression_image_edit)
                .customView(R.layout.compress_image_dialog, true)
                .onPositive((dialog1, which) -> {
                    final EditText qualityInput = dialog1.getCustomView().findViewById(R.id.quality);
                    int check;
                    try {
                        check = Integer.parseInt(String.valueOf(qualityInput.getText()));
                        if (check > 100 || check < 0) {
                            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                        } else {
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putInt(DEFAULT_COMPRESSION, check);
                            editor.apply();
                            showSettingsOptions();
                        }
                    } catch (NumberFormatException e) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                    }
                }).build();
        View customView = dialog.getCustomView();
        customView.findViewById(R.id.cbSetDefault).setVisibility(View.GONE);
        dialog.show();
    }

    /**
     * To modify font size
     */
    private void editFontSize() {
        MaterialDialog.Builder builder = DialogUtils.getInstance()
                .createCustomDialogWithoutContent(mActivity, R.string.font_size_edit);
        MaterialDialog dialog = builder.customView(R.layout.dialog_font_size, true)
                .onPositive((dialog1, which) -> {
                    final EditText fontInput = dialog1.getCustomView().findViewById(R.id.fontInput);
                    try {
                        int check = Integer.parseInt(String.valueOf(fontInput.getText()));
                        if (check > 1000 || check < 0) {
                            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                        } else {
                            StringUtils.getInstance().showSnackbar(mActivity, R.string.font_size_changed);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putInt(Constants.DEFAULT_FONT_SIZE_TEXT, check);
                            editor.apply();
                            showSettingsOptions();
                        }
                    } catch (NumberFormatException e) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                    }
                })
                .build();
        View customView = dialog.getCustomView();
        customView.findViewById(R.id.cbSetFontDefault).setVisibility(View.GONE);
        dialog.show();
    }

    /**
     * To modify font family
     */
    private void changeFontFamily() {
        String fontFamily = mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY);
        int ordinal = Font.FontFamily.valueOf(fontFamily).ordinal();
        MaterialDialog.Builder builder = DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity,
                R.string.font_family_edit);
        MaterialDialog materialDialog = builder.customView(R.layout.dialog_font_family, true)
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
        View customView = materialDialog.getCustomView();
        RadioGroup radioGroup = customView.findViewById(R.id.radio_group_font_family);
        RadioButton rb = (RadioButton) radioGroup.getChildAt(ordinal);
        rb.setChecked(true);
        customView.findViewById(R.id.cbSetDefault).setVisibility(View.GONE);
        materialDialog.show();
    }

    /**
     * To modify page size
     */
    private void setPageSize() {
        PageSizeUtils utils = new PageSizeUtils(mActivity);
        MaterialDialog materialDialog = utils.showPageSizeDialog(true);
        materialDialog.setOnDismissListener(dialog -> showSettingsOptions());
    }

    /**
     * To modify theme
     */
    private void setTheme() {
        MaterialDialog.Builder builder = DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity,
                R.string.theme_edit);
        MaterialDialog materialDialog = builder.customView(R.layout.dialog_theme_default, true)
                .onPositive(((dialog, which) -> {
                    View view = dialog.getCustomView();
                    RadioGroup radioGroup = view.findViewById(R.id.radio_group_themes);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = view.findViewById(selectedId);
                    String themeName = radioButton.getText().toString();
                    ThemeUtils.getInstance().saveTheme(mActivity, themeName);
                    mActivity.recreate();
                }))
                .build();
        RadioGroup radioGroup = materialDialog.getCustomView().findViewById(R.id.radio_group_themes);
        RadioButton rb = (RadioButton) radioGroup
                .getChildAt(ThemeUtils.getInstance().getSelectedThemePosition(mActivity));
        rb.setChecked(true);
        materialDialog.show();
    }

    /**
     * To set page number
     */
    private void setShowPageNumber() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        int currChoseId = mSharedPreferences.getInt(Constants.PREF_PAGE_STYLE_ID, -1);

        RelativeLayout dialogLayout = (RelativeLayout) getLayoutInflater()
                .inflate(R.layout.add_pgnum_dialog, null);

        RadioButton rbOpt1 = dialogLayout.findViewById(R.id.page_num_opt1);
        RadioButton rbOpt2 = dialogLayout.findViewById(R.id.page_num_opt2);
        RadioButton rbOpt3 = dialogLayout.findViewById(R.id.page_num_opt3);
        RadioGroup rg = dialogLayout.findViewById(R.id.radioGroup);
        CheckBox cbDefault = dialogLayout.findViewById(R.id.set_as_default);

        if (currChoseId > 0) {
            cbDefault.setChecked(true);
            rg.clearCheck();
            rg.check(currChoseId);
        }

        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.choose_page_number_style)
                .customView(dialogLayout, false)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .neutralText(R.string.remove_dialog)
                .onPositive(((dialog, which) -> {
                    int id = rg.getCheckedRadioButtonId();
                    String style = null;
                    if (id == rbOpt1.getId()) {
                        style = Constants.PG_NUM_STYLE_PAGE_X_OF_N;
                    } else if (id == rbOpt2.getId()) {
                        style = Constants.PG_NUM_STYLE_X_OF_N;
                    } else if (id == rbOpt3.getId()) {
                        style = Constants.PG_NUM_STYLE_X;
                    }
                    if (cbDefault.isChecked()) {
                        SharedPreferencesUtil.getInstance().setDefaultPageNumStyle(editor, style, id);
                    } else {
                        SharedPreferencesUtil.getInstance().clearDefaultPageNumStyle(editor);
                    }
                }))
                .build();
        materialDialog.show();
    }
}

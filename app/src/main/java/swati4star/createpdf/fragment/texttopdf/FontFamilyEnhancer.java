package swati4star.createpdf.fragment.texttopdf;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Font;

import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.Enhancer;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.preferences.TextToPdfDefaultManager;

/**
 * An {@link Enhancer} that lets you select the font family.
 */
public class FontFamilyEnhancer implements Enhancer {

    private final Activity mActivity;
    private final TextToPdfDefaultManager mDefaultsManager;
    private Font.FontFamily mFontFamily;
    private EnhancementOptionsEntity mEnhancementOptionsEntity;
    private TextToPdfContract.View mView;

    FontFamilyEnhancer(@NonNull final Activity activity,
                       @NonNull final TextToPdfContract.View view) {
        mActivity = activity;
        mDefaultsManager = new TextToPdfDefaultManager(activity);
        mFontFamily = Font.FontFamily.valueOf(mDefaultsManager.getFontFamily());
        mEnhancementOptionsEntity = new EnhancementOptionsEntity(
                mActivity, R.drawable.ic_font_family_24dp,
                String.format(mActivity.getString(R.string.default_font_family_text), mFontFamily.name()));
        mView = view;
    }
    /**
     * Shows dialog to change font size
     */
    @Override
    public void enhance() {
        String fontFamily = mDefaultsManager.getFontFamily();
        int ordinal = Font.FontFamily.valueOf(fontFamily).ordinal();
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .title(String.format(mActivity.getString(R.string.default_font_family_text), fontFamily))
                .customView(R.layout.dialog_font_family, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    View view = dialog.getCustomView();
                    RadioGroup radioGroup = view.findViewById(R.id.radio_group_font_family);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = view.findViewById(selectedId);
                    String fontFamily1 = radioButton.getText().toString();
                    mFontFamily = Font.FontFamily.valueOf(fontFamily1);
                    final CheckBox cbSetDefault = view.findViewById(R.id.cbSetDefault);
                    if (cbSetDefault.isChecked()) {
                        mDefaultsManager.setFontFamily(fontFamily1);

                    }
                    showFontFamily();
                })
                .build();
        RadioGroup radioGroup = materialDialog.getCustomView().findViewById(R.id.radio_group_font_family);
        RadioButton rb = (RadioButton) radioGroup.getChildAt(ordinal);
        rb.setChecked(true);
        materialDialog.show();
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }

    /**
     * Displays font family in UI
     */
    private void showFontFamily() {
        mEnhancementOptionsEntity.setName(mActivity.getString(R.string.font_family_text) + mFontFamily.name());
        mView.updateView();

    }

    Font.FontFamily getFontFamily() {
        return mFontFamily;
    }
}

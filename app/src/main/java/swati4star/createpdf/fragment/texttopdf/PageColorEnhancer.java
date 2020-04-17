package swati4star.createpdf.fragment.texttopdf;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.Enhancer;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.util.ColorUtils;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.StringUtils;

import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_COLOR;

/**
 * An {@link Enhancer} that lets you select the page color.
 */
public class PageColorEnhancer implements Enhancer {

    private final Activity mActivity;
    private final SharedPreferences mSharedPreferences;
    private final EnhancementOptionsEntity mEnhancementOptionsEntity;
    private int mPageColor;

    PageColorEnhancer(@NonNull final Activity activity) {
        mActivity = activity;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mPageColor = mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_TTP,
                DEFAULT_PAGE_COLOR);
        mEnhancementOptionsEntity = new EnhancementOptionsEntity(
                mActivity, R.drawable.ic_page_color, R.string.page_color);
    }

    @Override
    public void enhance() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.page_color)
                .customView(R.layout.dialog_color_chooser, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    View view = dialog.getCustomView();
                    ColorPickerView colorPickerView = view.findViewById(R.id.color_picker);
                    CheckBox defaultCheckbox = view.findViewById(R.id.set_default);
                    mPageColor = colorPickerView.getColor();
                    final int fontColor = mSharedPreferences.getInt(Constants.DEFAULT_FONT_COLOR_TEXT,
                            Constants.DEFAULT_FONT_COLOR);
                    if (ColorUtils.getInstance().colorSimilarCheck(fontColor, mPageColor)) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_color_too_close);
                    }
                    if (defaultCheckbox.isChecked()) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putInt(Constants.DEFAULT_PAGE_COLOR_TTP, mPageColor);
                        editor.apply();
                    }
                })
                .build();
        ColorPickerView colorPickerView = materialDialog.getCustomView().findViewById(R.id.color_picker);
        colorPickerView.setColor(mPageColor);
        materialDialog.show();
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }

    int getPageColor() {
        return mPageColor;
    }
}

package swati4star.createpdf.fragment.texttopdf;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.Enhancer;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.model.TextToPDFOptions;
import swati4star.createpdf.preferences.TextToPdfPreferences;
import swati4star.createpdf.util.ColorUtils;
import swati4star.createpdf.util.StringUtils;

/**
 * An {@link Enhancer} that lets you select font colors.
 */
public class FontColorEnhancer implements Enhancer {

    private final Activity mActivity;
    private final EnhancementOptionsEntity mEnhancementOptionsEntity;
    private final TextToPdfPreferences mPreferences;
    private final TextToPDFOptions.Builder mBuilder;

    FontColorEnhancer(@NonNull final Activity activity,
                      @NonNull final TextToPDFOptions.Builder builder) {
        mActivity = activity;
        mPreferences = new TextToPdfPreferences(activity);
        mBuilder = builder;
        mEnhancementOptionsEntity =  new EnhancementOptionsEntity(
                mActivity, R.drawable.ic_color, R.string.font_color);
    }

    @Override
    public void enhance() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.font_color)
                .customView(R.layout.dialog_color_chooser, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    View view = dialog.getCustomView();
                    ColorPickerView colorPickerView = view.findViewById(R.id.color_picker);
                    CheckBox defaultCheckbox = view.findViewById(R.id.set_default);
                    final int fontcolor = colorPickerView.getColor();
                    final int pageColor = mPreferences.getPageColor();
                    if (ColorUtils.getInstance().colorSimilarCheck(fontcolor, pageColor)) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_color_too_close);
                    }
                    if (defaultCheckbox.isChecked()) {
                        mPreferences.setFontColor(fontcolor);
                    }
                    mBuilder.setFontColor(fontcolor);
                })
                .build();
        ColorPickerView colorPickerView = materialDialog.getCustomView().findViewById(R.id.color_picker);
        colorPickerView.setColor(mBuilder.getFontColor());
        materialDialog.show();
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }
}

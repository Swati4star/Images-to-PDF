package swati4star.createpdf.fragment.texttopdf;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.CheckBox;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.Enhancer;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.preferences.TextToPdfPreferences;
import swati4star.createpdf.util.StringUtils;

/**
 * An {@link Enhancer} that lets you select font size.
 */
public class FontSizeEnhancer implements Enhancer {

    private final Activity mActivity;
    private EnhancementOptionsEntity mEnhancementOptionsEntity;
    private String mFontTitle;
    private int mFontSize;
    private final TextToPdfContract.View mView;
    private final TextToPdfPreferences mPreferences;

    FontSizeEnhancer(@NonNull final Activity activity,
                     @NonNull final TextToPdfContract.View view) {
        mActivity = activity;
        mPreferences = new TextToPdfPreferences(activity);

        mFontTitle = String.format(mActivity.getString(R.string.edit_font_size), mPreferences.getFontSize());
        mFontSize = mPreferences.getFontSize();
        mEnhancementOptionsEntity = new EnhancementOptionsEntity(
                mActivity.getResources().getDrawable(R.drawable.ic_font_black_24dp),
                mFontTitle);
        mView = view;
    }

    /**
     * Function to take the font size of pdf as user input
     */
    @Override
    public void enhance() {
        new MaterialDialog.Builder(mActivity)
                .title(mFontTitle)
                .customView(R.layout.dialog_font_size, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    final EditText fontInput = dialog.getCustomView().findViewById(R.id.fontInput);
                    final CheckBox cbSetDefault = dialog.getCustomView().findViewById(R.id.cbSetFontDefault);
                    try {
                        int check = Integer.parseInt(String.valueOf(fontInput.getText()));
                        if (check > 1000 || check < 0) {
                            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                        } else {
                            mFontSize = check;
                            showFontSize();
                            StringUtils.getInstance().showSnackbar(mActivity, R.string.font_size_changed);
                            if (cbSetDefault.isChecked()) {
                                mPreferences.setFontSize(mFontSize);
                                mFontTitle = String.format(mActivity.getString(R.string.edit_font_size),
                                        mPreferences.getFontSize());
                            }
                        }
                    } catch (NumberFormatException e) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                    }
                })
                .show();
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }

    /**
     * Displays font size in UI
     */
    private void showFontSize() {
        mEnhancementOptionsEntity
                .setName(String.format(mActivity.getString(R.string.font_size), String.valueOf(mFontSize)));
        mView.updateView();
    }

    int getFontSize() {
        return mFontSize;
    }
}

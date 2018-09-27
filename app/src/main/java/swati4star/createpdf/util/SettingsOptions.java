package swati4star.createpdf.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.itextpdf.text.Font;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.model.EnhancementOptionsEntity;

import static swati4star.createpdf.util.Constants.DEFAULT_COMPRESSION;

public class SettingsOptions {

    public static class ImageEnhancementOptionsUtils {

        public static ArrayList<EnhancementOptionsEntity> getEnhancementOptions(Context context) {
            ArrayList<EnhancementOptionsEntity> options = new ArrayList<>();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            options.add(new EnhancementOptionsEntity(
                    context.getResources().getDrawable(R.drawable.ic_compress_image),
                    String.format(context.getString(R.string.image_compression_value_default),
                            sharedPreferences.getInt(DEFAULT_COMPRESSION, 30))));

            options.add(new EnhancementOptionsEntity(
                    context.getResources().getDrawable(R.drawable.ic_page_size_24dp),
                    String.format(context.getString(R.string.page_size_value_def),
                            sharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT,
                                    Constants.DEFAULT_PAGE_SIZE))));

            options.add(new EnhancementOptionsEntity(
                    context.getResources().getDrawable(R.drawable.ic_font_black_24dp),
                    String.format(context.getString(R.string.font_size_value_def),
                            sharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT,
                                    Constants.DEFAULT_FONT_SIZE))));

            Font.FontFamily fontFamily = Font.FontFamily.valueOf(
                    sharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                            Constants.DEFAULT_FONT_FAMILY));

            options.add(new EnhancementOptionsEntity(
                    context.getResources().getDrawable(R.drawable.ic_font_family_24dp),
                    String.format(context.getString(R.string.font_family_value_def),
                            fontFamily.name())));

            options.add(new EnhancementOptionsEntity(
                    context.getResources().getDrawable(R.drawable.baseline_settings_brightness_24),
                    String.format(context.getString(R.string.theme_value_def),
                            sharedPreferences.getString(Constants.DEFAULT_THEME_TEXT,
                                    Constants.DEFAULT_THEME))));

            return options;
        }
    }

}

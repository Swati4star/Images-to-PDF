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

        public static ArrayList<EnhancementOptionsEntity> getEnhancementOptions(Context context,
                                                                                String fontTitle,
                                                                                Font.FontFamily fontFamily) {
            ArrayList<EnhancementOptionsEntity> options = new ArrayList<>();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            options.add(new EnhancementOptionsEntity(
                    context.getResources().getDrawable(R.drawable.ic_compress_image),
                    context.getString(R.string.image_compression_value_default) +
                            sharedPreferences.getInt(DEFAULT_COMPRESSION, 30) + "%)"));

            options.add(new EnhancementOptionsEntity(
                    context.getResources().getDrawable(R.drawable.ic_page_size_24dp),
                    context.getString(R.string.page_size_value_def) +
                            sharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT,
                            Constants.DEFAULT_PAGE_SIZE)));


            options.add(new EnhancementOptionsEntity(
                    context.getResources().getDrawable(R.drawable.ic_font_black_24dp),
                    context.getString(R.string.font_size_value_def)
                            + fontTitle));

            options.add(new EnhancementOptionsEntity(
                    context.getResources().getDrawable(R.drawable.ic_font_family_24dp),
                    context.getString(R.string.font_family_value_def)
                            + fontFamily.name()));

            //options.add(new EnhancementOptionsEntity(
            //      context.getResources().getDrawable(R.drawable.ic_branding_watermark_black_24dp),
            //    context.getResources().getString(R.string.add_watermark)));

            return options;
        }
    }

}

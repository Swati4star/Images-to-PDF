package swati4star.createpdf.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.model.EnhancementOptionsEntity;

import static swati4star.createpdf.util.Constants.DEFAULT_COMPRESSION;

public class ImageEnhancementOptionsUtils {

    public static ArrayList<EnhancementOptionsEntity> getEnhancementOptions(Context context) {
        ArrayList<EnhancementOptionsEntity> options = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        options.add(new EnhancementOptionsEntity(
                context.getResources().getDrawable(R.drawable.baseline_enhanced_encryption_24),
                context.getResources().getString(R.string.password_protect_pdf_text)));

        options.add(new EnhancementOptionsEntity(
                context.getResources().getDrawable(R.drawable.baseline_crop_rotate_24),
                context.getResources().getString(R.string.edit_images_text)));

        options.add(new EnhancementOptionsEntity(
                context.getResources().getDrawable(R.drawable.ic_compress_image),
                context.getResources().getString(R.string.compress_image) + " " +
                        sharedPreferences.getInt(DEFAULT_COMPRESSION, 30) + "%)"));

        options.add(new EnhancementOptionsEntity(
                context.getResources().getDrawable(R.drawable.ic_photo_filter_black_24dp),
                context.getResources().getString(R.string.filter_images_Text)));

        options.add(new EnhancementOptionsEntity(
                context.getResources().getDrawable(R.drawable.ic_page_size_24dp),
                context.getResources().getString(R.string.set_page_size_text)));

        options.add(new EnhancementOptionsEntity(
                context.getResources().getDrawable(R.drawable.ic_play_circle_outline_black_24dp),
                context.getResources().getString(R.string.preview_image_to_pdf)));

        options.add(new EnhancementOptionsEntity(
                context.getResources().getDrawable(R.drawable.ic_border_image_black_24dp),
                context.getResources().getString(R.string.image_border)));

        return options;
    }
}

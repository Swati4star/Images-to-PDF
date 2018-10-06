package swati4star.createpdf.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.model.ImageToPDFOptions;

public class ImageEnhancementOptionsUtils {

    public static ArrayList<EnhancementOptionsEntity> getEnhancementOptions(Context context,
                                                                            ImageToPDFOptions pdfOptions) {
        ArrayList<EnhancementOptionsEntity> options = new ArrayList<>();
        int passwordicon = R.drawable.baseline_enhanced_encryption_24;
        if (pdfOptions.isPasswordProtected())
            passwordicon = R.drawable.baseline_done_24;

        options.add(new EnhancementOptionsEntity(
                context.getResources().getDrawable(passwordicon),
                context.getResources().getString(R.string.password_protect_pdf_text)));

        options.add(new EnhancementOptionsEntity(
                context.getResources().getDrawable(R.drawable.baseline_crop_rotate_24),
                context.getResources().getString(R.string.edit_images_text)));

        options.add(new EnhancementOptionsEntity(
                context.getResources().getDrawable(R.drawable.ic_compress_image),
                String.format(context.getResources().getString(R.string.compress_image),
                        pdfOptions.getQualityString())));

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
                String.format(context.getResources().getString(R.string.border_dialog_title),
                        pdfOptions.getBorderWidth())));

        options.add(new EnhancementOptionsEntity(
                context.getResources().getDrawable(R.drawable.ic_rearrange),
                context.getResources().getString(R.string.rearrange_images)));

        Drawable iconGrayscale = context.getResources().getDrawable(R.drawable.ic_photo_filter_black_24dp);
        iconGrayscale.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.SRC_IN);
        options.add(new EnhancementOptionsEntity(
                iconGrayscale,
                context.getResources().getString(R.string.grayscale_images)));

        //options.add(new EnhancementOptionsEntity(
        //      context.getResources().getDrawable(R.drawable.ic_branding_watermark_black_24dp),
        //    context.getResources().getString(R.string.add_watermark)));

        return options;
    }
}

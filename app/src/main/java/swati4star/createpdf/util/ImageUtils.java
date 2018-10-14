package swati4star.createpdf.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Rectangle;

import java.io.File;

import swati4star.createpdf.R;

import static swati4star.createpdf.util.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static swati4star.createpdf.util.Constants.IMAGE_SCALE_TYPE_STRETCH;
import static swati4star.createpdf.util.DialogUtils.createCustomDialogWithoutContent;

public class ImageUtils {

    public static String mImageScaleType;

    /**
     * Calculates the optimum size for an image, such that it scales to fit whilst retaining its aspect ratio
     *
     * @param originalWidth the original width of the image
     * @param originalHeight the original height of the image
     * @param documentSize a rectangle specifying the width and height that the image must fit within
     * @return a rectangle that provides the scaled width and height of the image
     */
    public static Rectangle calculateFitSize(float originalWidth, float originalHeight, Rectangle documentSize) {
        float widthChange = (originalWidth - documentSize.getWidth()) / originalWidth;
        float heightChange = (originalHeight - documentSize.getHeight()) / originalHeight;

        float changeFactor;
        if (widthChange >= heightChange) {
            changeFactor = widthChange;
        } else {
            changeFactor = heightChange;
        }
        float newWidth = originalWidth - (originalWidth * changeFactor);
        float newHeight = originalHeight - (originalHeight * changeFactor);

        return new Rectangle(Math.abs((int) newWidth), Math.abs((int) newHeight));
    }

    /**
     * Creates a rounded bitmap from any bitmap
     * @param bmp - input bitmap
     * @return - output bitmap
     */
    public static Bitmap getRoundBitmap(Bitmap bmp) {
        int width = bmp.getWidth(), height = bmp.getHeight();
        int radius = width > height ? height : width; // set the smallest edge as radius.
        Bitmap sbmp;

        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            float factor = smallest / radius;
            sbmp = Bitmap.createScaledBitmap(bmp,
                    (int) (bmp.getWidth() / factor),
                    (int) (bmp.getHeight() / factor), false);
        } else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(radius / 2 + 0.7f, radius / 2 + 0.7f,
                radius / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

    /**
     * Get round bitmap from file path
     * @param path - file path
     * @return - output round bitmap
     */
    public static Bitmap getRoundBitmapFromPath(String path) {
        File file = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        if (bitmap == null) return null;
        return ImageUtils.getRoundBitmap(bitmap);
    }

    public static void showImageScaleTypeDialog(Context context, Boolean saveValue) {

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        MaterialDialog.Builder builder = createCustomDialogWithoutContent((Activity) context,
                R.string.image_scale_type);
        MaterialDialog materialDialog =
                builder.customView(R.layout.image_scale_type_dialog, true)
                .onPositive((dialog1, which) -> {
                    View view = dialog1.getCustomView();
                    RadioGroup radioGroup = view.findViewById(R.id.scale_type);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    if (selectedId == R.id.aspect_ratio)
                        mImageScaleType = IMAGE_SCALE_TYPE_ASPECT_RATIO;
                    else
                        mImageScaleType = IMAGE_SCALE_TYPE_STRETCH;

                    CheckBox mSetAsDefault = view.findViewById(R.id.cbSetDefault);
                    if (saveValue || mSetAsDefault.isChecked()) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(Constants.DEFAULT_IMAGE_SCALETYPE_TEXT, mImageScaleType);
                        editor.apply();
                    }
                }).build();
        if (saveValue) {
            View customView = materialDialog.getCustomView();
            customView.findViewById(R.id.cbSetDefault).setVisibility(View.GONE);
        }
        materialDialog.show();
    }
}

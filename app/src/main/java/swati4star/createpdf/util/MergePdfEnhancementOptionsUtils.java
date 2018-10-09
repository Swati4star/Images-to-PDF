package swati4star.createpdf.util;

import android.content.Context;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.model.EnhancementOptionsEntity;

public class MergePdfEnhancementOptionsUtils {
    public static ArrayList<EnhancementOptionsEntity> getEnhancementOptions(Context context) {
        ArrayList<EnhancementOptionsEntity> options = new ArrayList<>();
        options.add(new EnhancementOptionsEntity(
                context.getResources().getDrawable(R.drawable.baseline_enhanced_encryption_24),
                context.getString(R.string.set_password)));
        return options;
    }
}
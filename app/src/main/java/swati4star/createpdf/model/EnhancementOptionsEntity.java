package swati4star.createpdf.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class EnhancementOptionsEntity {
    private Drawable mImage;
    private String mName;

    public EnhancementOptionsEntity(@NonNull Drawable image, @NonNull String name) {
        this.mImage = image;
        this.mName = name;
    }

    public EnhancementOptionsEntity(@NonNull Context context, int imageId, @NonNull String name) {
        this.mImage = context.getResources().getDrawable(imageId);
        this.mName = name;
    }

    public EnhancementOptionsEntity(@NonNull Context context, int resourceId, int stringId) {
        this.mImage = context.getResources().getDrawable(resourceId);
        this.mName = context.getString(stringId);
    }

    @NonNull
    public Drawable getImage() {
        return mImage;
    }

    public void setImage(@NonNull Drawable image) {
        this.mImage = image;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public void setName(@NonNull String name) {
        this.mName = name;
    }
}

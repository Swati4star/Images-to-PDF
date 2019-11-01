package swati4star.createpdf.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class EnhancementOptionsEntity {
    private Drawable mImage;
    private String mName;

    public EnhancementOptionsEntity(Drawable image, String name) {
        this.mImage = image;
        this.mName = name;
    }

    public EnhancementOptionsEntity(Context context, int imageId, String name) {
        this.mImage = context.getResources().getDrawable(imageId);
        this.mName = name;
    }

    public EnhancementOptionsEntity(Context context, int resourceId, int stringId) {
        this.mImage = context.getResources().getDrawable(resourceId);
        this.mName = context.getString(stringId);
    }

    public Drawable getImage() {
        return mImage;
    }

    public void setImage(Drawable image) {
        this.mImage = image;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }
}

package swati4star.createpdf.model;

import android.graphics.drawable.Drawable;

/**
 * Created by anandparmar on 08/06/18.
 */

public class EnhancementOptionsEntity {
    private Drawable mImage;
    private String mName;

    public EnhancementOptionsEntity(Drawable image, String name) {
        this.mImage = image;
        this.mName = name;
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

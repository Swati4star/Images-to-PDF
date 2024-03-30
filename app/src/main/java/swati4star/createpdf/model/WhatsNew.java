package swati4star.createpdf.model;

import androidx.annotation.NonNull;

public class WhatsNew {

    private String mTitle;
    private String mDescription;
    private String mIcon;

    public WhatsNew(@NonNull String title, @NonNull String description, @NonNull String icon) {
        this.mTitle = title;
        this.mDescription = description;
        this.mIcon = icon;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(@NonNull String title) {
        this.mTitle = title;
    }

    @NonNull
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(@NonNull String description) {
        this.mDescription = description;
    }

    @NonNull
    public String getIcon() {
        return mIcon;
    }

    public void setIcon(@NonNull String icon) {
        this.mIcon = icon;
    }
}

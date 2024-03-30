package swati4star.createpdf.model;

import androidx.annotation.NonNull;

public class PreviewImageOptionItem {
    private int mOptionImageId;
    private String mOptionName;

    public PreviewImageOptionItem(int mOptionImageId, @NonNull String mOptionName) {
        this.mOptionImageId = mOptionImageId;
        this.mOptionName = mOptionName;
    }

    public int getOptionImageId() {
        return mOptionImageId;
    }

    public void setOptionImageId(int mOptionImageId) {
        this.mOptionImageId = mOptionImageId;
    }

    @NonNull
    public String getOptionName() {
        return mOptionName;
    }

    public void setOptionName(@NonNull String mOptionName) {
        this.mOptionName = mOptionName;
    }
}

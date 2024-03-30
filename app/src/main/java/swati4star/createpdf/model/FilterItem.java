package swati4star.createpdf.model;

import androidx.annotation.NonNull;

import ja.burhanrashid52.photoeditor.PhotoFilter;

public class FilterItem {

    private int mImageId;
    private String mName;
    private PhotoFilter mFilter;

    /**
     * Filter item model
     *
     * @param imageId - id of image to be set
     * @param name    - filter mName
     */
    public FilterItem(int imageId, @NonNull String name, @NonNull PhotoFilter filter) {
        this.mImageId = imageId;
        this.mName = name;
        this.mFilter = filter;
    }

    public int getImageId() {
        return mImageId;
    }

    public void setImageId(int imageId) {
        this.mImageId = imageId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public void setName(@NonNull String name) {
        this.mName = name;
    }

    @NonNull
    public PhotoFilter getFilter() {
        return mFilter;
    }

    public void setFilter(@NonNull PhotoFilter filter) {
        this.mFilter = filter;
    }
}

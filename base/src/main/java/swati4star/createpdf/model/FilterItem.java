package swati4star.createpdf.model;

import ja.burhanrashid52.photoeditor.PhotoFilter;

public class FilterItem {

    private int mImageId;
    private String mName;
    private PhotoFilter mFilter;

    /**
     * Filter item model
     *
     * @param imageId - id of image to be set
     * @param name - filter mName
     */
    public FilterItem(int imageId, String name, PhotoFilter filter) {
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

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public PhotoFilter getFilter() {
        return mFilter;
    }

    public void setFilter(PhotoFilter filter) {
        this.mFilter = filter;
    }
}

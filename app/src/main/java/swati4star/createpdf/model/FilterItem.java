package swati4star.createpdf.model;

public class FilterItem {

    private int mImageId;
    private String mName;

    /**
     * Filter item model
     *
     * @param imageId - id of image to be set
     * @param name - filter mName
     */
    public FilterItem(int imageId, String name) {
        this.mImageId = imageId;
        this.mName = name;
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

}

package swati4star.createpdf.model;

public class FilterItem {

    int imageId;
    String name;

    /**
     * Filter item model
     *
     * @param imageId - id of image to be set
     * @param name - filter name
     */
    public FilterItem(int imageId, String name) {
        this.imageId = imageId;
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

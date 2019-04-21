package swati4star.createpdf.model;

public class FavouriteItem {

    private int mIconId;
    int mtitleString;

    /**
     * Constructor
     * @param iconId
     * @param titleString
     */
    public FavouriteItem(int iconId, int titleString) {
        mIconId = iconId;
        mtitleString = titleString;
    }

    public int getIconId() {
        return mIconId;
    }

    public int getTitleString() {
        return mtitleString;
    }
}

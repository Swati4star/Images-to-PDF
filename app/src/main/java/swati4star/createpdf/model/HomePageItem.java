package swati4star.createpdf.model;

public class HomePageItem {

    private int mIconId;
    int mtitleString;

    /**
     * Constructor
     * @param iconId
     * @param titleString
     */
    public HomePageItem(int iconId, int titleString) {
        mIconId = iconId;
        mtitleString = titleString;
    }

    public int getNavigationItemId() {
        return mIconId;
    }

    public int getTitleString() {
        return mtitleString;
    }
}

package swati4star.createpdf.model;

public class HomePageItem {

    private final int mIconId;
    private final int mTitleString;

    /**
     * Constructor for home page item
     * @param iconId - icon drawable id
     * @param titleString - title string resource id of home page item
     */
    public HomePageItem(int iconId, int titleString) {
        mIconId = iconId;
        mTitleString = titleString;
    }

    public int getNavigationItemId() {
        return mIconId;
    }

    public int getTitleString() {
        return mTitleString;
    }
}

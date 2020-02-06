package swati4star.createpdf.model;

public class HomePageItem {

    private final int mIconId;
    private final int mDrawableId;
    private final int mTitleString;

    /**
     * Constructor for home page item
     * @param iconId - icon drawable id
     * @param drawableId - drawable id of the Home Page Item
     * @param titleString - title string resource id of home page item
     */
    public HomePageItem(int iconId, int drawableId, int titleString) {
        mIconId = iconId;
        mDrawableId = drawableId;
        mTitleString = titleString;
    }

    public int getNavigationItemId() {
        return mIconId;
    }

    public int getTitleString() {
        return mTitleString;
    }

    public int getmDrawableId() {
        return mDrawableId;
    }
}

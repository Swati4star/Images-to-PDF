package swati4star.createpdf.model;

public class WhatsNew {

    private String mTitle;
    private String mDescription;
    private String mIcon;

    public WhatsNew(String title, String description, String icon) {
        this.mTitle = title;
        this.mDescription = description;
        this.mIcon = icon;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        this.mIcon = icon;
    }
}

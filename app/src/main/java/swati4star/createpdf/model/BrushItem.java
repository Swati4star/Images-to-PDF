package swati4star.createpdf.model;

import java.util.Objects;

public class BrushItem {
    private final int mColor;

    public BrushItem (int color) {
        this.mColor = color;
    }

    public int getColor() {
        return mColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrushItem brushItem = (BrushItem) o;
        return mColor == brushItem.mColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mColor);
    }
}

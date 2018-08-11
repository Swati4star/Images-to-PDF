package swati4star.createpdf.util;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.model.BrushItem;

public class BrushUtils {

    public static ArrayList<BrushItem> getBrushItems() {

        ArrayList<BrushItem> brushItems = new ArrayList<>();
        brushItems.add(new BrushItem(R.color.mb_white));
        brushItems.add(new BrushItem(R.color.red));
        brushItems.add(new BrushItem(R.color.black));
        brushItems.add(new BrushItem(R.color.mb_blue));
        brushItems.add(new BrushItem(R.color.mb_green));
        return brushItems;
    }
}
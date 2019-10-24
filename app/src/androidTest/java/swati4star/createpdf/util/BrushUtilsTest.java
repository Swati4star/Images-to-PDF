package swati4star.createpdf.util;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import swati4star.createpdf.R;
import swati4star.createpdf.model.BrushItem;
import static org.junit.Assert.assertEquals;

public class BrushUtilsTest {

    ArrayList<BrushItem> defaultBrushItems = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        defaultBrushItems.add(new BrushItem(R.color.mb_white));
        defaultBrushItems.add(new BrushItem(R.color.red));
        defaultBrushItems.add(new BrushItem(R.color.mb_blue));
        defaultBrushItems.add(new BrushItem(R.color.mb_green));
        defaultBrushItems.add(new BrushItem(R.color.colorPrimary));
        defaultBrushItems.add(new BrushItem(R.color.colorAccent));
        defaultBrushItems.add(new BrushItem(R.color.light_gray));
        defaultBrushItems.add(new BrushItem(R.color.black));
        defaultBrushItems.add(new BrushItem(R.drawable.color_palette));
    }

    @Test
    public void getBrushItems() {
        assertEquals(defaultBrushItems, BrushUtils.getBrushItems());
    }
}
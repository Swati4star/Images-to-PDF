package swati4star.createpdf.util;

import android.content.Context;

import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.PhotoFilter;
import swati4star.createpdf.R;
import swati4star.createpdf.model.FilterItem;

public class ImageFilterUtils {

    public static ArrayList<FilterItem> getFiltersList(Context context) {

        ArrayList<FilterItem> items = new ArrayList<>();

        items.add(new FilterItem(R.drawable.none,
                context.getString(R.string.filter_none), PhotoFilter.NONE));
        items.add(new FilterItem(R.drawable.brush,
                context.getString(R.string.filter_brush), PhotoFilter.NONE));
        items.add(new FilterItem(R.drawable.auto_fix,
                context.getString(R.string.filter_autofix), PhotoFilter.AUTO_FIX));
        items.add(new FilterItem(R.drawable.black,
                context.getString(R.string.filter_grayscale), PhotoFilter.GRAY_SCALE));
        items.add(new FilterItem(R.drawable.brightness,
                context.getString(R.string.filter_brightness), PhotoFilter.BRIGHTNESS));
        items.add(new FilterItem(R.drawable.contrast,
                context.getString(R.string.filter_contrast), PhotoFilter.CONTRAST));
        items.add(new FilterItem(R.drawable.cross_process,
                context.getString(R.string.filter_cross), PhotoFilter.CROSS_PROCESS));
        items.add(new FilterItem(R.drawable.documentary,
                context.getString(R.string.filter_documentary), PhotoFilter.DOCUMENTARY));
        items.add(new FilterItem(R.drawable.due_tone,
                context.getString(R.string.filter_duetone), PhotoFilter.DUE_TONE));
        items.add(new FilterItem(R.drawable.fill_light,
                context.getString(R.string.filter_filllight), PhotoFilter.FILL_LIGHT));
        items.add(new FilterItem(R.drawable.flip_vertical,
                context.getString(R.string.filter_filpver), PhotoFilter.FLIP_VERTICAL));
        items.add(new FilterItem(R.drawable.flip_horizontal,
                context.getString(R.string.filter_fliphor), PhotoFilter.FLIP_HORIZONTAL));
        items.add(new FilterItem(R.drawable.grain,
                context.getString(R.string.filter_grain), PhotoFilter.GRAIN));
        items.add(new FilterItem(R.drawable.lomish,
                context.getString(R.string.filter_lomish), PhotoFilter.LOMISH));
        items.add(new FilterItem(R.drawable.negative,
                context.getString(R.string.filter_negative), PhotoFilter.NEGATIVE));
        items.add(new FilterItem(R.drawable.poster,
                context.getString(R.string.filter_poster), PhotoFilter.POSTERIZE));
        items.add(new FilterItem(R.drawable.rotate,
                context.getString(R.string.filter_rotate), PhotoFilter.ROTATE));
        items.add(new FilterItem(R.drawable.saturate,
                context.getString(R.string.filter_saturate), PhotoFilter.SATURATE));
        items.add(new FilterItem(R.drawable.sepia,
                context.getString(R.string.filter_sepia), PhotoFilter.SEPIA));
        items.add(new FilterItem(R.drawable.sharpen,
                context.getString(R.string.filter_sharpen), PhotoFilter.SHARPEN));
        items.add(new FilterItem(R.drawable.temp,
                context.getString(R.string.filter_temp), PhotoFilter.TEMPERATURE));
        items.add(new FilterItem(R.drawable.tint,
                context.getString(R.string.filter_tint), PhotoFilter.TINT));
        items.add(new FilterItem(R.drawable.vignette,
                context.getString(R.string.filter_vig), PhotoFilter.VIGNETTE));

        return items;
    }
}

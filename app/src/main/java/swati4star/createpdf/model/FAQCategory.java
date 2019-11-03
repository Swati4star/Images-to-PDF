package swati4star.createpdf.model;

import android.annotation.SuppressLint;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

@SuppressLint("ParcelCreator")
public class FAQCategory extends ExpandableGroup<FAQItem> {

    public FAQCategory(String title, List<FAQItem> items) {
        super(title, items);
    }
}

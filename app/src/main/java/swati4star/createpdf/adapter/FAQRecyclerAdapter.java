package swati4star.createpdf.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import swati4star.createpdf.R;
import swati4star.createpdf.model.FAQCategory;
import swati4star.createpdf.model.FAQItem;
import swati4star.createpdf.viewholder.FAQCategoryViewHolder;
import swati4star.createpdf.viewholder.FAQItemViewHolder;

public class FAQRecyclerAdapter extends ExpandableRecyclerViewAdapter<FAQCategoryViewHolder, FAQItemViewHolder> {

    private Activity mActivity;

    public FAQRecyclerAdapter(List<? extends ExpandableGroup> groups, Activity mactivity) {
        super(groups);
        this.mActivity = mactivity;
    }

    @Override
    public FAQCategoryViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mlayoutinflater = (LayoutInflater) mActivity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = mlayoutinflater.inflate(R.layout.faq_category_title, parent, false);

        return new FAQCategoryViewHolder(view);
    }

    @Override
    public FAQItemViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mlayoutinflater = (LayoutInflater) mActivity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = mlayoutinflater.inflate(R.layout.item_faq, parent, false);

        return new FAQItemViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(FAQItemViewHolder holder, int flatPosition, ExpandableGroup group,
                                      int childIndex) {
        final FAQItem mfaqcategory = ((FAQCategory) group).getItems().get(childIndex);
        holder.onBind(mfaqcategory, group);
    }

    @Override
    public void onBindGroupViewHolder(FAQCategoryViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setGroupName(group);
    }
}

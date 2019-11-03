package swati4star.createpdf.viewholder;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import swati4star.createpdf.R;

public class FAQCategoryViewHolder extends GroupViewHolder {
    private TextView mCategoryTitle;

    public FAQCategoryViewHolder(View itemView) {
        super(itemView);
        mCategoryTitle = (TextView) itemView.findViewById(R.id.faqcategorytitle);
    }

    @Override
    public void expand() {
        mCategoryTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_black_24dp, 0);
    }

    @Override
    public void collapse() {
        mCategoryTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_black_24dp, 0);
    }

    public void setGroupName(ExpandableGroup group) {
        mCategoryTitle.setText(group.getTitle());
    }
}

package swati4star.createpdf.adapter;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.OnItemClickListener;
import swati4star.createpdf.model.FAQItem;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

    private final List<FAQItem> mFaqs;
    private final OnItemClickListener mOnItemClickListener;

    public FAQAdapter(List<FAQItem> faqs, OnItemClickListener mOnItemClickListener) {
        this.mFaqs = faqs;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    /**
     * Inflates the layout view and returns it
     *
     * @param viewGroup - holds view
     * @param position - view position
     * @return FAQViewHolder View
     */

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_faq, viewGroup, false);
        return new FAQAdapter.FAQViewHolder(view);
    }

    /**
     * Binds the FAQItem with the proper data that it fetches from List
     * @param viewHolder - holds view
     * @param position - view position
     */
    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder viewHolder, int position) {
        FAQItem faqItem = mFaqs.get(position);
        viewHolder.question.setText(faqItem.getQuestion());
        viewHolder.answer.setText(faqItem.getAnswer());
        boolean isExpanded = faqItem.isExpanded();
        viewHolder.expandableView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mFaqs.size();
    }

    public class FAQViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.question)
        TextView question;
        @BindView(R.id.answer)
        TextView answer;
        @BindView(R.id.expandable_view)
        ConstraintLayout expandableView;

        /**
         * Initializes and binds the view and sets the onClickListener
         * @param itemView - holds view
         */
        FAQViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            question.setOnClickListener(this);
        }

        /**
         * Defines the onItemClickListener handler
         * @param view - view
         */
        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}

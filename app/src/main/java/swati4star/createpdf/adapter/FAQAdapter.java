package swati4star.createpdf.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.OnItemClickListner;
import swati4star.createpdf.model.FAQItem;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

    List<FAQItem> faqs;
    private final OnItemClickListner mOnItemClickListener;

    public FAQAdapter(List<FAQItem> faqs, OnItemClickListner mOnItemClickListener) {
        this.faqs = faqs;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_faq, viewGroup, false);
        return new FAQAdapter.FAQViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder viewHolder, int i) {
        FAQItem faqItem = faqs.get(i);
        viewHolder.question.setText(faqItem.getQuestion());
        viewHolder.answer.setText(faqItem.getAnswer());
        boolean isExpanded = faqItem.isExpanded();
        viewHolder.expandableView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return faqs.size();
    }

    public class FAQViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.question)
        TextView question;
        @BindView(R.id.answer)
        TextView answer;
        @BindView(R.id.expandable_view)
        ConstraintLayout expandableView;

        FAQViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            question.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}

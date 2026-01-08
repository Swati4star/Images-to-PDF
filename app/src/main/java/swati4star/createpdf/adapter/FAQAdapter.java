package swati4star.createpdf.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import swati4star.createpdf.databinding.ItemFaqBinding;
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
     */
    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        ItemFaqBinding binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FAQAdapter.FAQViewHolder(binding);
    }

    /**
     * Binds the FAQItem with the proper data that it fetches from List
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

        TextView question;
        TextView answer;
        ConstraintLayout expandableView;

        /**
         * Initializes and binds the view and sets the onClickListener
         */
        FAQViewHolder(@NonNull ItemFaqBinding binding) {
            super(binding.getRoot());

            binding.question.setOnClickListener(this);
            answer = binding.answer;
            question = binding.question;
            expandableView = binding.expandableView;
        }

        /**
         * Defines the onItemClickListener handler
         *
         * @param view - view
         */
        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}

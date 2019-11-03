package swati4star.createpdf.viewholder;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;


import swati4star.createpdf.R;
import swati4star.createpdf.model.FAQItem;

public class FAQItemViewHolder extends ChildViewHolder {

    private TextView mQuestion;
    private TextView mAnswer;

    public FAQItemViewHolder(View itemView) {
        super(itemView);

        mQuestion = (TextView) itemView.findViewById(R.id.question);
        mAnswer = (TextView) itemView.findViewById(R.id.answer);
    }

    public void onBind(FAQItem faqItem, ExpandableGroup group) {
        mQuestion.setText(faqItem.getmQuestion());
        mAnswer.setText(faqItem.getmAnswer());
    }

    public void setmQuestion(FAQItem faqItem) {
        mQuestion.setText(faqItem.getmQuestion());
    }

    public void setmAnswer(FAQItem faqItem) {
        mAnswer.setText(faqItem.getmAnswer());
    }
}

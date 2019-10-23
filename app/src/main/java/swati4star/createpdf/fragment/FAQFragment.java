package swati4star.createpdf.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.FAQAdapter;
import swati4star.createpdf.interfaces.OnItemClickListner;
import swati4star.createpdf.model.FAQItem;


public class FAQFragment extends Fragment implements OnItemClickListner {

    FAQAdapter faqAdapter;
    List<FAQItem> mFaqs;
    Context mContext;

    @BindView(R.id.recycler_view_faq)
    RecyclerView mFAQRecyclerView;

    public FAQFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        ButterKnife.bind(this, view);
        mContext = view.getContext();

        initFAQs();

        initFAQRecyclerView();

        return view;
    }

    /**
     * Initializes the FAQs questions and answers by populating data from the
     * strings.xml file and adding it to the custom FAQItem Model.
     */
    private void initFAQs() {
        mFaqs = new ArrayList<>();
        String[] questions = mContext.getResources().getStringArray(R.array.faq_questions);
        String[] answers = mContext.getResources().getStringArray(R.array.faq_answers);
        FAQItem faqItem;
        for (int i = 0; i < questions.length; ++i) {
            faqItem = new FAQItem(questions[i], answers[i]);
            mFaqs.add(faqItem);
        }
    }


    /**
     * Initializes the RecyclerView using the FAQAdapter to populate the views
     * for FAQs
     */
    private void initFAQRecyclerView() {
        faqAdapter = new FAQAdapter(mFaqs, this);
        mFAQRecyclerView.setAdapter(faqAdapter);
    }

    /**
     * This method defines the behavior of the FAQItem when clicked.
     * It makes the FAQItem view expanded or contracted.
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        FAQItem faqItem = mFaqs.get(position);
        faqItem.setExpanded(!faqItem.isExpanded());
        faqAdapter.notifyItemChanged(position);
    }
}

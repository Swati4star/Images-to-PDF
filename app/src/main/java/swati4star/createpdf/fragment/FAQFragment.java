package swati4star.createpdf.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import swati4star.createpdf.R;
import swati4star.createpdf.adapter.FAQAdapter;
import swati4star.createpdf.databinding.FragmentFaqBinding;
import swati4star.createpdf.interfaces.OnItemClickListener;
import swati4star.createpdf.model.FAQItem;

public class FAQFragment extends Fragment implements OnItemClickListener {
    private FAQAdapter mFaqAdapter;
    private List<FAQItem> mFaqs;
    private List<FAQItem> mFaqsCopy;
    private Context mContext;
    private SearchView mSearchView;
    private FragmentFaqBinding mBinding;

    public FAQFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentFaqBinding.inflate(inflater, container, false);
        View rootView = mBinding.getRoot();

        mSearchView = rootView.findViewById(R.id.searchView);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterFaq(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                filterFaq(newQuery);
                return true;
            }
        });

        mContext = rootView.getContext();

        initFAQs();
        initFAQRecyclerView();

        return rootView;
    }

    /**
     * @param text - This is the search text entered in the search box.
     *             Simply filtering out the questions that contains the given search query.
     */
    public void filterFaq(String text) {
        mFaqs.clear();
        if (text.isEmpty())
            mFaqs.addAll(mFaqsCopy);
        else {
            text = text.toLowerCase();

            for (FAQItem faq : mFaqsCopy) {
                if (faq.getQuestion().toLowerCase().contains(text)) {
                    mFaqs.add(faq);
                }
            }
        }
        mFaqAdapter.notifyDataSetChanged();
    }

    /**
     * Initializes the FAQs questions and answers by populating data from the
     * strings.xml file and adding it to the custom FAQItem Model.
     */
    private void initFAQs() {
        mFaqs = new ArrayList<>();
        mFaqsCopy = new ArrayList<>();
        String[] questionAnswers = mContext.getResources().getStringArray(R.array.faq_question_answers);
        FAQItem faqItem;
        for (String questionAnswer : questionAnswers) {
            String[] questionAnswerSplit = questionAnswer.split("#####");
            faqItem = new FAQItem(questionAnswerSplit[0], questionAnswerSplit[1]);
            mFaqs.add(faqItem);
        }
        mFaqsCopy.addAll(mFaqs);
    }


    /**
     * Initializes the RecyclerView using the FAQAdapter to populate the views
     * for FAQs
     */
    private void initFAQRecyclerView() {
        mFaqAdapter = new FAQAdapter(mFaqs, this);
        mBinding.recyclerViewFaq.setAdapter(mFaqAdapter);
    }

    /**
     * This method defines the behavior of the FAQItem when clicked.
     * It makes the FAQItem view expanded or contracted.
     *
     * @param position - view position
     */
    @Override
    public void onItemClick(int position) {
        FAQItem faqItem = mFaqs.get(position);
        faqItem.setExpanded(!faqItem.isExpanded());
        mFaqAdapter.notifyItemChanged(position);
    }
}

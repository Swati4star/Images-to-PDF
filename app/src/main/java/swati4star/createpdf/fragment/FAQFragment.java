package swati4star.createpdf.fragment;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.FAQAdapter;
import swati4star.createpdf.interfaces.OnItemClickListener;
import swati4star.createpdf.model.FAQItem;


public class FAQFragment extends Fragment implements OnItemClickListener {

    private FAQAdapter mFaqAdapter;
    private List<FAQItem> mFaqs;
    private List<FAQItem> mFaqsCopy;
    private Context mContext;
    private SearchView mSearchView;
    private String mCategory;

    @BindView(R.id.recycler_view_faq)
    RecyclerView mFAQRecyclerView;

    public FAQFragment(String category) {
        // Required empty public constructor
        this.mCategory = category;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        mContext = view.getContext();

        TextView categoryView = view.findViewById(R.id.faq_category_header);
        categoryView.setText(mCategory);

        String[] categories = mContext.getResources().getStringArray(R.array.faq_categories);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.faq_category_tab_layout);

        // For FAQ items without a category
        tabLayout.addTab(tabLayout.newTab().setText("All"));

        for (String category : categories) tabLayout.addTab(tabLayout.newTab().setText(category));


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mCategory = tab.getText().toString();
                categoryView.setText(mCategory);
                initFAQs();
                initFAQRecyclerView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        mSearchView =  view.findViewById(R.id.searchView);
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

        ButterKnife.bind(this, view);

        initFAQs();
        initFAQRecyclerView();

        return view;
    }

    /**
     * @param text - This is the search text entered in the search box.
     * Simply filtering out the questions that contains the given search query.
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

            // Only add the FAQ item if we have All selected or
            // if the current category matches the selected category.
            if (mCategory.equals("All") || (questionAnswerSplit.length >= 3 && questionAnswerSplit[2].equals(mCategory))) {
                faqItem = new FAQItem(questionAnswerSplit[0], questionAnswerSplit[1]);
                mFaqs.add(faqItem);
            }

        }
        mFaqsCopy.addAll(mFaqs);
    }


    /**
     * Initializes the RecyclerView using the FAQAdapter to populate the views
     * for FAQs
     */
    private void initFAQRecyclerView() {
        mFaqAdapter = new FAQAdapter(mFaqs, this);
        mFAQRecyclerView.setAdapter(mFaqAdapter);
    }

    /**
     * This method defines the behavior of the FAQItem when clicked.
     * It makes the FAQItem view expanded or contracted.
     * @param position - view position
     */
    @Override
    public void onItemClick(int position) {
        FAQItem faqItem = mFaqs.get(position);
        faqItem.setExpanded(!faqItem.isExpanded());
        mFaqAdapter.notifyItemChanged(position);
    }
}

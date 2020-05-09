package swati4star.createpdf.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
    private Context mContext;
    private SearchView mSearchView;

    @BindView(R.id.recycler_view_faq)
    RecyclerView mFAQRecyclerView;

    public FAQFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // menu to inflate the view where search icon is there.
        inflater.inflate(R.menu.activity_faq_actions, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) item.getActionView();
        mSearchView.setQueryHint(getString(R.string.search));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                setDataForQueryChange(s);
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                setDataForQueryChange(s);
                return true;
            }
        });
        mSearchView.setOnCloseListener(() -> {
            populateFAQList(null);
            return false;
        });
        mSearchView.setIconifiedByDefault(true);
    }

    private void setDataForQueryChange(String s) {
        populateFAQList(s);
    }

    /**
     * populate faq questions with search query
     *
     * @param query to filter faq questions, {@code null} to get all
     */
    private void populateFAQList(@Nullable String query) {
        if ( query != null ) {
            if ( !query.isEmpty() ) {
                List<FAQItem> filteredMFaqs = new ArrayList<>();
                for ( int i = 0; i < mFaqs.size(); i++ ) {
                    if ( mFaqs.get(i).getQuestion().toLowerCase().contains( query.toLowerCase() ) ) {
                        filteredMFaqs.add(mFaqs.get(i));
                    }
                }
                mFaqAdapter = new FAQAdapter(filteredMFaqs, this);
            } else {
                mFaqAdapter = new FAQAdapter(mFaqs, this);
            }
        } else {
            mFaqAdapter = new FAQAdapter(mFaqs, this);
        }

        mFAQRecyclerView.setAdapter(mFaqAdapter);
    }

    /**
     * Initializes the FAQs questions and answers by populating data from the
     * strings.xml file and adding it to the custom FAQItem Model.
     */
    private void initFAQs() {
        mFaqs = new ArrayList<>();
        String[] questionAnswers = mContext.getResources().getStringArray(R.array.faq_question_answers);
        FAQItem faqItem;
        for (String questionAnswer : questionAnswers) {
            String[] questionAnswerSplit = questionAnswer.split("#####");
            faqItem = new FAQItem(questionAnswerSplit[0], questionAnswerSplit[1]);
            mFaqs.add(faqItem);
        }
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
        FAQItem faqItem = mFaqAdapter.getmFaqs().get(position);
        faqItem.setExpanded(!faqItem.isExpanded());
        mFaqAdapter.notifyItemChanged(position);
    }
}

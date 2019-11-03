package swati4star.createpdf.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import swati4star.createpdf.adapter.FAQRecyclerAdapter;
import swati4star.createpdf.interfaces.OnItemClickListner;
import swati4star.createpdf.model.FAQCategory;
import swati4star.createpdf.model.FAQItem;


public class FAQFragment extends Fragment {


    private ArrayList<FAQCategory> mFAQCategoryList = new ArrayList<>();
    private FAQRecyclerAdapter mFAQRecyclerAdapter;
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
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        mFAQRecyclerView.setLayoutManager(manager);
        mFAQRecyclerAdapter = new FAQRecyclerAdapter(mFAQCategoryList, getActivity());
        mFAQRecyclerView.setAdapter(mFAQRecyclerAdapter);


        return view;
    }

    /**
     * Initializes the FAQs questions and answers by populating data from the
     * strings.xml file and adding it to the custom FAQItem Model.
     */
    private void initFAQs() {
        FAQItem faqItem;
        ArrayList<FAQItem> marray1 = new ArrayList<>();
        String[] questionAnswers = mContext.getResources().getStringArray(R.array.getting_started);
        for (String questionAnswer : questionAnswers) {
            String[] questionAnswerSplit = questionAnswer.split("#####");
            faqItem = new FAQItem(questionAnswerSplit[0], questionAnswerSplit[1]);
            marray1.add(faqItem);
        }

        ArrayList<FAQItem> marray2 = new ArrayList<>();
        questionAnswers = mContext.getResources().getStringArray(R.array.file_formats_supported);
        for (String questionAnswer : questionAnswers) {
            String[] questionAnswerSplit = questionAnswer.split("#####");
            faqItem = new FAQItem(questionAnswerSplit[0], questionAnswerSplit[1]);
            marray2.add(faqItem);
        }

        ArrayList<FAQItem> marray3 = new ArrayList<>();
        questionAnswers = mContext.getResources().getStringArray(R.array.modifying_pdfs);
        for (String questionAnswer : questionAnswers) {
            String[] questionAnswerSplit = questionAnswer.split("#####");
            faqItem = new FAQItem(questionAnswerSplit[0], questionAnswerSplit[1]);
            marray3.add(faqItem);
        }

        ArrayList<FAQItem> marray4 = new ArrayList<>();
        questionAnswers = mContext.getResources().getStringArray(R.array.protecting_pdfs);
        for (String questionAnswer : questionAnswers) {
            String[] questionAnswerSplit = questionAnswer.split("#####");
            faqItem = new FAQItem(questionAnswerSplit[0], questionAnswerSplit[1]);
            marray4.add(faqItem);
        }

        ArrayList<FAQItem> marray5 = new ArrayList<>();
        questionAnswers = mContext.getResources().getStringArray(R.array.sharing);
        for (String questionAnswer : questionAnswers) {
            String[] questionAnswerSplit = questionAnswer.split("#####");
            faqItem = new FAQItem(questionAnswerSplit[0], questionAnswerSplit[1]);
            marray5.add(faqItem);
        }

        ArrayList<FAQItem> marray6 = new ArrayList<>();
        questionAnswers = mContext.getResources().getStringArray(R.array.storage);
        for (String questionAnswer : questionAnswers) {
            String[] questionAnswerSplit = questionAnswer.split("#####");
            faqItem = new FAQItem(questionAnswerSplit[0], questionAnswerSplit[1]);
            marray6.add(faqItem);
        }
        ArrayList<FAQItem> marray7 = new ArrayList<>();
        questionAnswers = mContext.getResources().getStringArray(R.array.privacy);
        for (String questionAnswer : questionAnswers) {
            String[] questionAnswerSplit = questionAnswer.split("#####");
            faqItem = new FAQItem(questionAnswerSplit[0], questionAnswerSplit[1]);
            marray7.add(faqItem);
        }

        ArrayList<FAQItem> marray8 = new ArrayList<>();
        questionAnswers = mContext.getResources().getStringArray(R.array.contributions_and_downloads);
        for (String questionAnswer : questionAnswers) {
            String[] questionAnswerSplit = questionAnswer.split("#####");
            faqItem = new FAQItem(questionAnswerSplit[0], questionAnswerSplit[1]);
            marray8.add(faqItem);
        }
        ArrayList<FAQItem> marray9 = new ArrayList<>();
        questionAnswers = mContext.getResources().getStringArray(R.array.language_support);
        for (String questionAnswer : questionAnswers) {
            String[] questionAnswerSplit = questionAnswer.split("#####");
            faqItem = new FAQItem(questionAnswerSplit[0], questionAnswerSplit[1]);
            marray9.add(faqItem);
        }

        ArrayList<FAQItem> marray10 = new ArrayList<>();
        questionAnswers = mContext.getResources().getStringArray(R.array.contact);
        for (String questionAnswer : questionAnswers) {
            String[] questionAnswerSplit = questionAnswer.split("#####");
            faqItem = new FAQItem(questionAnswerSplit[0], questionAnswerSplit[1]);
            marray10.add(faqItem);
        }
        mFAQCategoryList.add(new FAQCategory("Getting Started", marray1));
        mFAQCategoryList.add(new FAQCategory("File formats supported", marray2));
        mFAQCategoryList.add(new FAQCategory("Modifying PDFs", marray3));
        mFAQCategoryList.add(new FAQCategory("Protecting PDFs", marray4));
        mFAQCategoryList.add(new FAQCategory("Sharing", marray5));
        mFAQCategoryList.add(new FAQCategory("Storage", marray6));
        mFAQCategoryList.add(new FAQCategory("Privacy", marray7));
        mFAQCategoryList.add(new FAQCategory("Contributions and Downloads", marray8));
        mFAQCategoryList.add(new FAQCategory("Language Support", marray9));
        mFAQCategoryList.add(new FAQCategory("Contact", marray10));


    }

}

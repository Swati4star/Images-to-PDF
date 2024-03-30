package swati4star.createpdf.model;

import androidx.annotation.NonNull;

public class FAQItem {

    private String mQuestion;
    private String mAnswer;
    private boolean mIsExpanded;

    /**
     * FAQ Item constructor
     *
     * @param question - question text
     * @param answer   - answer text
     */
    public FAQItem(@NonNull String question, @NonNull String answer) {
        this.mQuestion = question;
        this.mAnswer = answer;
        mIsExpanded = false;
    }

    public boolean isExpanded() {
        return mIsExpanded;
    }

    public void setExpanded(boolean expanded) {
        mIsExpanded = expanded;
    }

    @NonNull
    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(@NonNull String question) {
        this.mQuestion = question;
    }

    @NonNull
    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(@NonNull String answer) {
        this.mAnswer = answer;
    }
}

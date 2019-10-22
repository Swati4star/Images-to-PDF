package swati4star.createpdf.model;

public class FAQItem {

    private String mQuestion;
    private String mAnswer;
    boolean isExpanded;

    public FAQItem(String question, String answer) {
        this.mQuestion = question;
        this.mAnswer = answer;
        isExpanded = false;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(String question) {
        this.mQuestion = question;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(String answer) {
        this.mAnswer = answer;
    }
}

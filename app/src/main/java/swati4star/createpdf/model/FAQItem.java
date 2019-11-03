package swati4star.createpdf.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FAQItem implements Parcelable {

    private String mQuestion;
    private String mAnswer;

    public FAQItem(Parcel in) {
        mQuestion = in.readString();
    }

    public String getmAnswer() {
        return mAnswer;
    }

    public void setmAnswer(String mAnswer) {
        this.mAnswer = mAnswer;
    }

    public FAQItem(String mQuestion, String mAnswer) {
        this.mQuestion = mQuestion;
        this.mAnswer = mAnswer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mQuestion);
    }

    public String getmQuestion() {
        return mQuestion;
    }

    public void setmQuestion(String mQuestion) {
        this.mQuestion = mQuestion;
    }

    public static final Creator<FAQItem> CREATOR = new Creator<FAQItem>() {
        @Override
        public FAQItem createFromParcel(Parcel source) {
            return new FAQItem(source);
        }

        @Override
        public FAQItem[] newArray(int size) {
            return new FAQItem[size];
        }
    };
}

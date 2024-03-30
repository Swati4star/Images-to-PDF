package swati4star.createpdf.model;

import androidx.annotation.NonNull;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;

public class Watermark {
    private String mWatermarkText;
    private int mRotationAngle;
    private BaseColor mTextColor;
    private int mTextSize;
    private Font.FontFamily mFontFamily;
    private int mFontStyle;

    @NonNull
    public String getWatermarkText() {
        return mWatermarkText;
    }

    public void setWatermarkText(@NonNull String watermarkText) {
        this.mWatermarkText = watermarkText;
    }

    public int getRotationAngle() {
        return mRotationAngle;
    }

    public void setRotationAngle(int rotationAngle) {
        this.mRotationAngle = rotationAngle;
    }

    @NonNull
    public BaseColor getTextColor() {
        return mTextColor;
    }

    public void setTextColor(@NonNull BaseColor textColor) {
        this.mTextColor = textColor;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    @NonNull
    public Font.FontFamily getFontFamily() {
        return mFontFamily;
    }

    public void setFontFamily(@NonNull Font.FontFamily fontFamily) {
        this.mFontFamily = fontFamily;
    }

    public int getFontStyle() {
        return mFontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.mFontStyle = fontStyle;
    }
}

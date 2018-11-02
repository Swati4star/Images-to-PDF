package swati4star.createpdf.model;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;

public class Watermark {
    private String mWatermarkText;
    private int mRotationAngle;
    private BaseColor mTextColor;
    private int mTextSize;
    private Font.FontFamily mFontFamily;
    private int mFontStyle;

    public String getWatermarkText() {
        return mWatermarkText;
    }

    public void setWatermarkText(String watermarkText) {
        this.mWatermarkText = watermarkText;
    }

    public int getRotationAngle() {
        return mRotationAngle;
    }

    public void setRotationAngle(int rotationAngle) {
        this.mRotationAngle = rotationAngle;
    }

    public BaseColor getTextColor() {
        return mTextColor;
    }

    public void setTextColor(BaseColor textColor) {
        this.mTextColor = textColor;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public Font.FontFamily getFontFamily() {
        return mFontFamily;
    }

    public void setFontFamily(Font.FontFamily fontFamily) {
        this.mFontFamily = fontFamily;
    }

    public int getFontStyle() {
        return mFontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.mFontStyle = fontStyle;
    }
}

package swati4star.createpdf.util;

import android.app.Activity;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.interfaces.DataSetChanged;
import swati4star.createpdf.model.Watermark;

import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class WatermarkUtils {

    private final Activity mContext;
    private Watermark mWatermark;
    private FileUtils mFileUtils;

    public WatermarkUtils(Activity context) {
        mContext = context;
        mFileUtils = new FileUtils(context);
    }

    public void setWatermark(String path, final DataSetChanged dataSetChanged, final ArrayList<File> mFileList) {

        final MaterialDialog mDialog = new MaterialDialog.Builder(mContext)
                .title(R.string.add_watermark)
                .customView(R.layout.add_watermark_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .build();

        final View mPositiveAction = mDialog.getActionButton(DialogAction.POSITIVE);

        this.mWatermark = new Watermark();

        final EditText watermarkTextInput = mDialog.getCustomView().findViewById(R.id.watermarkText);
        final EditText angleInput = mDialog.getCustomView().findViewById(R.id.watermarkAngle);
        final ColorPickerView colorPickerInput = mDialog.getCustomView().findViewById(R.id.watermarkColor);
        final EditText fontSizeInput = mDialog.getCustomView().findViewById(R.id.watermarkFontSize);
        final Spinner fontFamilyInput = mDialog.getCustomView().findViewById(R.id.watermarkFontFamily);
        final Spinner styleInput = mDialog.getCustomView().findViewById(R.id.watermarkStyle);

        fontFamilyInput.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,
                Font.FontFamily.values()));
        styleInput.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,
                mContext.getResources().getStringArray(R.array.fontStyles)));

        angleInput.setText("0");
        fontSizeInput.setText("50");

        watermarkTextInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        mPositiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable input) {
                        if (StringUtils.isEmpty(input))
                            showSnackbar(mContext, R.string.snackbar_watermark_cannot_be_blank);
                        else {
                            mWatermark.setWatermarkText(input.toString());
                        }
                    }
                });

        mPositiveAction.setEnabled(false);
        mPositiveAction.setOnClickListener(v -> {
            try {
                mWatermark.setWatermarkText(watermarkTextInput.getText().toString());
                mWatermark.setFontFamily(((Font.FontFamily) fontFamilyInput.getSelectedItem()));
                mWatermark.setFontStyle(getStyleValueFromName(((String) styleInput.getSelectedItem())));
                if (StringUtils.isEmpty(angleInput.getText())) {
                    mWatermark.setRotationAngle(0);
                } else {
                    mWatermark.setRotationAngle(Integer.valueOf(angleInput.getText().toString()));
                }

                if (StringUtils.isEmpty(fontSizeInput.getText())) {
                    mWatermark.setTextSize(50);
                } else {
                    mWatermark.setTextSize(Integer.valueOf(fontSizeInput.getText().toString()));
                }
                mWatermark.setTextColor((new BaseColor(
                        Color.red(colorPickerInput.getColor()),
                        Color.green(colorPickerInput.getColor()),
                        Color.blue(colorPickerInput.getColor()),
                        Color.alpha(colorPickerInput.getColor())
                )));
                createWatermark(path, mFileList);
                dataSetChanged.updateDataset();
                showSnackbar(mContext, R.string.watermark_added);
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
                showSnackbar(mContext, R.string.cannot_add_watermark);
            }
            mDialog.dismiss();
        });
        mDialog.show();
    }

    private String createWatermark(String path, final ArrayList<File> mFileList) throws IOException, DocumentException {
        String finalOutputFile = mFileUtils.getUniqueFileName(path.replace(mContext.getString(R.string.pdf_ext),
                mContext.getString(R.string.watermarked_file)), mFileList);

        PdfReader reader = new PdfReader(path);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
        Font font = new Font(this.mWatermark.getFontFamily(), this.mWatermark.getTextSize(),
                this.mWatermark.getFontStyle(), this.mWatermark.getTextColor());
        Phrase p = new Phrase(this.mWatermark.getWatermarkText(), font);

        PdfContentByte over;
        Rectangle pagesize;
        float x, y;
        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {

            // get page size and position
            pagesize = reader.getPageSizeWithRotation(i);
            x = (pagesize.getLeft() + pagesize.getRight()) / 2;
            y = (pagesize.getTop() + pagesize.getBottom()) / 2;
            over = stamper.getUnderContent(i);

            ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, this.mWatermark.getRotationAngle());
        }

        stamper.close();
        reader.close();
        new DatabaseHelper(mContext).insertRecord(finalOutputFile, mContext.getString(R.string.watermarked));
        return finalOutputFile;
    }


    public static int getStyleValueFromName(String name) {
        switch (name) {
            case "NORMAL":
                return Font.NORMAL;
            case "BOLD":
                return Font.BOLD;
            case "ITALIC":
                return Font.ITALIC;
            case "UNDERLINE":
                return Font.UNDERLINE;
            case "STRIKETHRU":
                return Font.STRIKETHRU;
            case "BOLDITALIC":
                return Font.BOLDITALIC;
            default:
                return Font.NORMAL;
        }
    }

    public static String getStyleNameFromFont(int font) {
        switch (font) {
            case Font.NORMAL:
                return "NORMAL";
            case Font.BOLD:
                return "BOLD";
            case Font.ITALIC:
                return "ITALIC";
            case Font.UNDERLINE:
                return "UNDERLINE";
            case Font.STRIKETHRU:
                return "STRIKETHRU";
            case Font.BOLDITALIC:
                return "BOLDITALIC";
            default:
                return "NORMAL";
        }
    }

}

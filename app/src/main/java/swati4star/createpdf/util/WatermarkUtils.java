package swati4star.createpdf.util;

import android.app.Activity;
import android.text.Editable;
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

import java.io.FileOutputStream;
import java.io.IOException;

import swati4star.createpdf.R;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.interfaces.DataSetChanged;
import swati4star.createpdf.model.Watermark;

public class WatermarkUtils {

    private final Activity mContext;
    private Watermark mWatermark;
    private final FileUtils mFileUtils;

    public WatermarkUtils(Activity context) {
        mContext = context;
        mFileUtils = new FileUtils(context);
    }

    public void setWatermark(String path, final DataSetChanged dataSetChanged) {

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
                new DefaultTextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        mPositiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable input) {
                        if (StringUtils.getInstance().isEmpty(input))
                            StringUtils.getInstance().
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

                mWatermark.setRotationAngle(StringUtils.getInstance().parseIntOrDefault(angleInput.getText(), 0));
                mWatermark.setTextSize(StringUtils.getInstance().parseIntOrDefault(fontSizeInput.getText(), 50));

                //colorPickerInput.getColor() returns ans ARGB Color and BaseColor can use that ARGB as parameter
                mWatermark.setTextColor((new BaseColor(colorPickerInput.getColor())));

                String filePath = createWatermark(path);
                dataSetChanged.updateDataset();
                StringUtils.getInstance().getSnackbarwithAction(mContext, R.string.watermark_added).setAction(
                        R.string.snackbar_viewAction, v1 ->
                                mFileUtils.openFile(filePath, FileUtils.FileType.e_PDF)).show();
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
                StringUtils.getInstance().showSnackbar(mContext, R.string.cannot_add_watermark);
            }
            mDialog.dismiss();
        });
        mDialog.show();
    }

    private String createWatermark(String path) throws IOException, DocumentException {
        String finalOutputFile = mFileUtils.getUniqueFileName(path.replace(mContext.getString(R.string.pdf_ext),
                mContext.getString(R.string.watermarked_file)));

        PdfReader reader = new PdfReader(path);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
        Font font = new Font(this.mWatermark.getFontFamily(), this.mWatermark.getTextSize(),
                this.mWatermark.getFontStyle(), this.mWatermark.getTextColor());
        Phrase p = new Phrase(this.mWatermark.getWatermarkText(), font);

        PdfContentByte over;
        Rectangle pageSize;
        float x, y;
        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {

            // get page size and position
            pageSize = reader.getPageSizeWithRotation(i);
            x = (pageSize.getLeft() + pageSize.getRight()) / 2;
            y = (pageSize.getTop() + pageSize.getBottom()) / 2;
            over = stamper.getOverContent(i);

            ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, this.mWatermark.getRotationAngle());
        }

        stamper.close();
        reader.close();
        new DatabaseHelper(mContext).insertRecord(finalOutputFile, mContext.getString(R.string.watermarked));
        return finalOutputFile;
    }

    public static int getStyleValueFromName(String name) {
        switch (name) {
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

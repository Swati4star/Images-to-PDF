package swati4star.createpdf.util;

import android.content.Context;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;

import swati4star.createpdf.R;

public class PageSizeUtils {

    private final Context mActivity;
    public static String mPageSize = "A4";

    public PageSizeUtils(Context mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * @param selectionId   - id of selected radio button
     * @param spinnerAValue - Value of A0 to A10 spinner
     * @param spinnerBValue - Value of B0 to B10 spinner
     * @return - Rectangle page size
     */
    private String getPageSize(int selectionId, String spinnerAValue, String spinnerBValue) {
        String mPageSize = mActivity.getString(R.string.a4);
        String stringPageSize;
        switch (selectionId) {
            case R.id.page_size_default:
                mPageSize = mActivity.getString(R.string.a4);
                break;
            case R.id.page_size_legal:
                mPageSize = mActivity.getString(R.string.legal);
                break;
            case R.id.page_size_executive:
                mPageSize = mActivity.getString(R.string.executive);
                break;
            case R.id.page_size_ledger:
                mPageSize = mActivity.getString(R.string.ledger);
                break;
            case R.id.page_size_tabloid:
                mPageSize = mActivity.getString(R.string.tabloid);
                break;
            case R.id.page_size_letter:
                mPageSize = mActivity.getString(R.string.letter);
                break;
            case R.id.page_size_a0_a10:
                stringPageSize = spinnerAValue;
                mPageSize = stringPageSize.substring(0, stringPageSize.indexOf(" "));
                break;
            case R.id.page_size_b0_b10:
                stringPageSize = spinnerBValue;
                mPageSize = stringPageSize.substring(0, stringPageSize.indexOf(" "));
                break;
        }
        return mPageSize;
    }

    public void showPageSizeDialog() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.set_page_size_text)
                .customView(R.layout.set_page_size_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive((dialog1, which) -> {
                    View view = dialog1.getCustomView();
                    RadioGroup radioGroup = view.findViewById(R.id.radio_group_page_size);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    Spinner spinnerA = view.findViewById(R.id.spinner_page_size_a0_a10);
                    Spinner spinnerB = view.findViewById(R.id.spinner_page_size_b0_b10);
                    mPageSize = getPageSize(selectedId, spinnerA.getSelectedItem().toString(),
                            spinnerB.getSelectedItem().toString());
                }).build();

        View view = materialDialog.getCustomView();
        RadioGroup radioGroup = view.findViewById(R.id.radio_group_page_size);
        Spinner spinnerA = view.findViewById(R.id.spinner_page_size_a0_a10);
        Spinner spinnerB = view.findViewById(R.id.spinner_page_size_b0_b10);
        if (mPageSize.equals(mActivity.getString(R.string.a4))) {
            radioGroup.check(R.id.page_size_default);

        } else if (mPageSize.equals(mActivity.getString(R.string.letter))) {
            radioGroup.check(R.id.page_size_letter);

        } else if (mPageSize.equals(mActivity.getString(R.string.legal))) {
            radioGroup.check(R.id.page_size_legal);

        } else if (mPageSize.equals(mActivity.getString(R.string.ledger))) {
            radioGroup.check(R.id.page_size_ledger);

        } else if (mPageSize.equals(mActivity.getString(R.string.executive))) {
            radioGroup.check(R.id.page_size_executive);

        } else if (mPageSize.equals(mActivity.getString(R.string.tabloid))) {
            radioGroup.check(R.id.page_size_tabloid);

        } else if (mPageSize.equals(mActivity.getString(R.string.letter))) {
            radioGroup.check(R.id.page_size_letter);

        } else if (mPageSize.startsWith("A")) {
            radioGroup.check(R.id.page_size_a0_a10);
            spinnerA.setSelection(Integer.parseInt(mPageSize.substring(1)));

        } else if (mPageSize.startsWith("B")) {
            radioGroup.check(R.id.page_size_b0_b10);
            spinnerB.setSelection(Integer.parseInt(mPageSize.substring(1)));
        }

        materialDialog.show();
    }
}

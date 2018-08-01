package swati4star.createpdf.util;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

import swati4star.createpdf.R;

public class PageSizeUtils {

    /**
     * @param selectionId   - id of selected radio button
     * @param spinnerAValue - Value of A0 to A10 spinner
     * @param spinnerBValue - Value of B0 to B10 spinner
     * @return - Rectangle page size
     */
    public Rectangle getPageSize(int selectionId, String spinnerAValue, String spinnerBValue) {
        Rectangle mPageSize = PageSize.A4;
        String stringPageSize;
        switch (selectionId) {
            case R.id.page_size_default:
                mPageSize = PageSize.A4;
                break;
            case R.id.page_size_legal:
                mPageSize = PageSize.LEGAL;
                break;
            case R.id.page_size_executive:
                mPageSize = PageSize.EXECUTIVE;
                break;
            case R.id.page_size_ledger:
                mPageSize = PageSize.LEDGER;
                break;
            case R.id.page_size_tabloid:
                mPageSize = PageSize.TABLOID;
                break;
            case R.id.page_size_letter:
                mPageSize = PageSize.LETTER;
                break;
            case R.id.page_size_a0_a10:
                stringPageSize = spinnerAValue;
                stringPageSize = stringPageSize.substring(0, stringPageSize.indexOf(" "));
                mPageSize = PageSize.getRectangle(stringPageSize);
                break;
            case R.id.page_size_b0_b10:
                stringPageSize = spinnerBValue;
                stringPageSize = stringPageSize.substring(0, stringPageSize.indexOf(" "));
                mPageSize = PageSize.getRectangle(stringPageSize);
                break;
        }
        return mPageSize;
    }
}

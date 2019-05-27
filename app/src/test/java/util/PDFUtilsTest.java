package util;

import org.junit.Test;

import swati4star.createpdf.util.SplitPDFUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PDFUtilsTest {

    private static final int NUM_OF_PAGES = 23;
    private static final int NO_ERROR = 0;
    private static final int ERROR_PAGE_NUMBER = 1;
    private static final int ERROR_RANGE = 2;
    private static final int ERROR_INVALID_INPUT = 3;

    @Test
    public void checkRangeCorrectRangesReturnsNoError() {
        assertThat(SplitPDFUtils.checkRangeValidity(NUM_OF_PAGES, new String[]{"1", "2", "3-8"}), is(NO_ERROR));
        assertThat(SplitPDFUtils.checkRangeValidity(NUM_OF_PAGES, new String[]{"1", "2-6", "4-8"}), is(NO_ERROR));
        assertThat(SplitPDFUtils.checkRangeValidity(NUM_OF_PAGES, new String[]{"1-5", "21-23"}), is(NO_ERROR));
    }

    @Test
    public void checkRangeIncorrectPageNumberReturnsErrorPageNumber() {
        // 24>SplitPDFUtils
        assertThat(SplitPDFUtils.checkRangeValidity(NUM_OF_PAGES, new String[]{"1", "2", "24"}), is(ERROR_PAGE_NUMBER));
        assertThat(SplitPDFUtils.checkRangeValidity(NUM_OF_PAGES, new String[]{"1-8", "24"}), is(ERROR_PAGE_NUMBER));
        // 0 is invalid page number
        assertThat(SplitPDFUtils.checkRangeValidity(
                NUM_OF_PAGES,
                new String[]{"0", "2", "3-9"}),
                is(ERROR_PAGE_NUMBER)
        );
        //0-2 range is invalid as page 0 in invalid
        assertThat(SplitPDFUtils.checkRangeValidity(
                NUM_OF_PAGES,
                new String[]{"1-3", "0-2", "10-12"}),
                is(ERROR_PAGE_NUMBER)
        );
    }

    @Test
    public void checkRangeIncorrectRangeReturnsErrorRange() {
        //invalid range 3-1
        assertThat(SplitPDFUtils.checkRangeValidity(NUM_OF_PAGES, new String[]{"1", "2", "3-1"}), is(ERROR_RANGE));
    }

    @Test
    public void checkRangeInvalidInputReturnsErrorInvalidInput() {
        // ""(empty) in invalid
        assertThat(SplitPDFUtils.checkRangeValidity(
                NUM_OF_PAGES,
                new String[]{"1", "", "2", "3-9"}),
                is(ERROR_INVALID_INPUT)
        );
        // negative numbers are invalid
        assertThat(SplitPDFUtils.checkRangeValidity(
                NUM_OF_PAGES,
                new String[]{"-1", "2", "3-9"}),
                is(ERROR_INVALID_INPUT)
        );
        //2- is invalid
        assertThat(SplitPDFUtils.checkRangeValidity(
                NUM_OF_PAGES,
                new String[]{"1", "2-", "3-9"}),
                is(ERROR_INVALID_INPUT)
        );
        // single - is invalid
        assertThat(SplitPDFUtils.checkRangeValidity(
                NUM_OF_PAGES,
                new String[]{"1", "-", "3-9"}),
                is(ERROR_INVALID_INPUT)
        );
        // 3---9 is invalid
        assertThat(SplitPDFUtils.checkRangeValidity(
                NUM_OF_PAGES,
                new String[]{"1", "2", "3---9"}),
                is(ERROR_INVALID_INPUT)
        );
    }
}

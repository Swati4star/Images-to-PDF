package util;

import org.junit.Test;

import swati4star.createpdf.util.PDFUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CheckRangeTest {
    private static final int NUM_OF_PAGES = 23;

    @Test
    public void checkRange_CorrectRanges_ReturnsZero() {
        String[] ranges = {"1", "2", "3-8"};
        assertThat(PDFUtils.checkRangeValidity(NUM_OF_PAGES, ranges), is(0));
    }

    @Test
    public void checkRange_IncorrectPageNumber_ReturnsOne() {
        String[] ranges = {"1", "2", "24"}; //24 > max number of pages
        assertThat(PDFUtils.checkRangeValidity(NUM_OF_PAGES, ranges), is(1));
    }

    @Test
    public void checkRange_IncorrectRange_ReturnsTwo() {
        String[] ranges = {"1", "2", "3-1"};  //invalid range 3-1
        assertThat(PDFUtils.checkRangeValidity(NUM_OF_PAGES, ranges), is(2));
    }
}

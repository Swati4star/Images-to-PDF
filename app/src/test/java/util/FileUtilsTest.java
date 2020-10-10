package util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.TimeZone;

import swati4star.createpdf.util.FileInfoUtils;
import swati4star.createpdf.util.FileUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static swati4star.createpdf.util.FileUtils.getFileName;

@RunWith(MockitoJUnitRunner.class)
public class FileUtilsTest {

    private static final String FILE_PATH = "/a/b/";
    private static final String FILE_NAME = "c.pdf";

    @Mock
    File file;

    @Before
    public void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void when_CallingGetFormattedDate_Expect_CorrectDateReturned() {
        when(file.lastModified()).thenReturn(0L);
        assertThat(FileInfoUtils.getFormattedDate(file), is("Thu, Jan 01 at 00:00"));
    }

    @Test
    public void when_CallingGetFormattedSize_Expect_CorrectDateReturned() {
        when(file.length()).thenReturn(5242880L);
        assertThat(FileInfoUtils.getFormattedSize(file), is("5.00 MB"));
    }

    @Test
    public void when_CallingGetFileName_Expect_CorrectValueReturned() {
        assertThat(getFileName(FILE_PATH + FILE_NAME), is(FILE_NAME));
        assertThat(getFileName(""), is(""));
    }

    @Test
    public void when_CallingStaticGetFileName_Expect_CorrectValueReturned() {
        assertThat(getFileName(FILE_PATH + FILE_NAME), is(FILE_NAME));
        assertThat(getFileName(""), is(""));
    }

    @Test
    public void when_CallingGetFileNameWithoutExtension_Expect_CorrectValueReturned() {
        assertThat(FileUtils.getFileNameWithoutExtension(FILE_PATH + FILE_NAME), is("c"));
        assertThat(FileUtils.getFileNameWithoutExtension(""), is(""));
    }

    @Test
    public void when_CallingGetFileDirectoryPath_Expect_CorrectValueReturned() {
        assertThat(FileUtils.getFileDirectoryPath(FILE_PATH + FILE_NAME), is(FILE_PATH));
        assertThat(FileUtils.getFileDirectoryPath(""), is(""));
    }
}
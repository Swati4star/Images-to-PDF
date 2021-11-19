package swati4star.createpdf.util;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

@RunWith(AndroidJUnit4.class)
public class DirectoryUtilsTest {
    private File[] mFiles;
    private DirectoryUtils mDirUtils;

    @Before
    public void setup() throws IOException {
        mDirUtils = new DirectoryUtils(InstrumentationRegistry.getTargetContext());

        List<String> paths = getPaths();
        mFiles = new File[paths.size()];
        for (int i = 0; i < paths.size(); i++) {
            File f = new File(paths.get(i));
            f.createNewFile();
            mFiles[i] = f;
        }
    }

    @After
    public void cleanup() {
        for (File f : mFiles)
            f.delete();
    }

    @Test
    public void queryOneMatching() {
        List<File> result = mDirUtils.searchPDF("abcdefg");
        Assert.assertEquals(Collections.singletonList(mFiles[2]), result);
    }

    @Test
    public void queryMoreMatching() {
        // when
        List<File> result = mDirUtils.searchPDF("ffffff");

        // then
        Assert.assertEquals(asList(mFiles[2], mFiles[3], mFiles[4]), result);
    }

    @Test
    public void queryNoMatching() {
        // when
        List<File> result = mDirUtils.searchPDF("qwer");

        // then
        Assert.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void shouldFilterOutAllNonPdfsFromGivenArray() {
        // when
        List<File> result = mDirUtils.getPdfsFromPdfFolder(mFiles);

        // then
        Assert.assertEquals(asList(mFiles[2], mFiles[3], mFiles[4]), result);
    }

    @Test
    public void shouldCreatePdfDirectory() {
        // given
        cleanup();
        File dir = new File(StringUtils.getInstance().getDefaultStorageLocation());
        dir.delete();

        Assert.assertFalse(dir.exists());

        // when
        dir = mDirUtils.getOrCreatePdfDirectory();

        // then
        Assert.assertTrue(dir.exists());
    }

    private List<String> getPaths() {
        String basepath = mDirUtils.getOrCreatePdfDirectory().getPath();
        return asList(
                basepath + "/abcdefg.txt",
                basepath + "/abcdefg.jpg",
                basepath + "/abcdefgffffff.pdf",
                basepath + "/aabcdefffffff.pdf",
                basepath + "/dfghfffffff.pdf"
        );
    }


}

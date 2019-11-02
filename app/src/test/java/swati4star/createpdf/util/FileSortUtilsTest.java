package swati4star.createpdf.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileSortUtilsTest {
    private ArrayList<File> mFiles;
    private final FileSortUtils mInstance = FileSortUtils.getInstance();

    @Before
    public void setup() throws IOException {
        List<String> paths = getPaths();
        mFiles = new ArrayList<>(paths.size());
        for (String ignored : paths) {
            File f = mock(File.class);
            f.createNewFile();
            mFiles.add(f);
        }
    }

    @After
    public void cleanup() {
        for (File item : mFiles) {
            item.delete();
        }
    }

    @Test
    public void shouldReturnArraySortedNewestToOldest() {
        // given
        long base = mFiles.get(0).lastModified();
        long[] edittimes = {base - 10000, base - 10, base + 10000, base + 10, base + 100, base + 5};
        for (int i = 0; i < edittimes.length; i++)
            when(mFiles.get(i).lastModified()).thenReturn(edittimes[i]);

        File[] expected = new File[]{mFiles.get(2), mFiles.get(4), mFiles.get(3),
                mFiles.get(5), mFiles.get(1), mFiles.get(0)};

        // when
        mInstance.performSortOperation(mInstance.DATE_INDEX, mFiles);

        // then
        Assert.assertEquals(asList(expected), mFiles);
    }

    @Test
    public void shouldReturnArraySortedAlphabetically() throws IOException {
        // given
        // (for some reason sorting mocks doesn't work)
        List<String> paths = getPaths();
        mFiles = new ArrayList<>(paths.size());
        for (String item : paths) {
            File f = new File(item);
            f.createNewFile();
            mFiles.add(f);
        }

        File[] expected = new File[]{mFiles.get(5), mFiles.get(3), mFiles.get(4),
                mFiles.get(0), mFiles.get(1), mFiles.get(2)};

        // when
        mInstance.performSortOperation(mInstance.NAME_INDEX, mFiles);

        // then
        Assert.assertEquals(asList(expected), mFiles);
    }

    @Test
    public void shouldReturnArraySortedByAscendingSize() {
        // given
        long[] sizes = {10000, 1000, 100, 50, 2000, 2500};
        for (int i = 0; i < sizes.length; i++)
            when(mFiles.get(i).length()).thenReturn(sizes[i]);

        File[] expected = new File[]{mFiles.get(3), mFiles.get(2), mFiles.get(1),
                mFiles.get(4), mFiles.get(5), mFiles.get(0)};

        // when
        mInstance.performSortOperation(mInstance.SIZE_INCREASING_ORDER_INDEX, mFiles);

        // then
        Assert.assertEquals(asList(expected), mFiles);
    }

    @Test
    public void shouldReturnArraySortedByDescendingSize() {
        // given
        long[] sizes = {10000, 1000, 100, 50, 2000, 2500};
        for (int i = 0; i < sizes.length; i++)
            when(mFiles.get(i).length()).thenReturn(sizes[i]);

        File[] expected = new File[]{mFiles.get(0), mFiles.get(5), mFiles.get(4),
                mFiles.get(1), mFiles.get(2), mFiles.get(3)};

        // when
        mInstance.performSortOperation(mInstance.SIZE_DECREASING_ORDER_INDEX, mFiles);

        // then
        Assert.assertEquals(asList(expected), mFiles);
    }

    @Test
    public void shouldThrowOnInvalidSortOption() {
        // given
        List<Integer> invalidOptions = asList(-1, 14, 65535, 8);

        for (Integer item : invalidOptions) {
            try {
                // when
                mInstance.performSortOperation(item, mFiles);
            } catch (IllegalArgumentException ex) {
                // then
                Assert.assertTrue(ex.getMessage().startsWith("Invalid sort option"));
            }
        }
    }

    private List<String> getPaths() {
        return asList(
                "src/firstfile",
                "src/secondfile",
                "src/thirdfile",
                "src/afile",
                "src/bfile",
                "src/aafile"
        );
    }

}

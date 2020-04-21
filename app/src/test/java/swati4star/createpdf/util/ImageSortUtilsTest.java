package swati4star.createpdf.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.File;
import java.util.Collections;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
@SuppressWarnings("unchecked")
public class ImageSortUtilsTest {

    @Before
    @After
    public void cleanup() {
        deleteAllFiles();
    }

    @Test
    public void shouldSortPathsByFileNamesAscending() {
        // given
        int ascendingSortOption = 0;
        List<String> paths = getFilePaths();

        // when
        ImageSortUtils.getInstance().performSortOperation(ascendingSortOption, paths);

        // then
        Assert.assertEquals(
                asList(
                        "src/A-oldest",
                        "src/B-middle",
                        "src/C-latest"
                ),
                paths
        );
    }

    @Test
    public void shouldSortPathsByFileNamesDescending() {
        // given
        int descendingSortOption = 1;
        List<String> paths = getFilePaths();

        // when
        ImageSortUtils.getInstance().performSortOperation(descendingSortOption, paths);

        // then
        Assert.assertEquals(
                asList(
                        "src/C-latest",
                        "src/B-middle",
                        "src/A-oldest"
                ),
                paths
        );
    }

    @Test
    public void shouldSortPathsByLastModifiedAsc() throws Exception {
        // given
        int dateAscendingOption = 2;
        List<String> paths = getFilePaths();
        createNewFiles();

        // when
        ImageSortUtils.getInstance().performSortOperation(dateAscendingOption, paths);

        // then
        Assert.assertEquals(
                asList(
                        "src/C-latest",
                        "src/B-middle",
                        "src/A-oldest"
                ),
                paths
        );
    }

    @Test
    public void shouldSortPathsByLastModifiedDescending() throws Exception {
        // given
        int dateDescendingOption = 3;
        List<String> paths = getFilePaths();
        createNewFiles();

        // when
        ImageSortUtils.getInstance().performSortOperation(dateDescendingOption, paths);

        // then
        Assert.assertEquals(
                asList(
                        "src/A-oldest",
                        "src/B-middle",
                        "src/C-latest"
                ),
                paths
        );
    }

    @Test
    public void shouldThrowExceptionForInvalidSortOption() {
        // given
        List<Integer> invalidSortOption = Arrays.asList(new Object[] {-10, -1, 4, 10});

        // when
        invalidSortOption.forEach(sortOption -> {
                    try {
                        ImageSortUtils.getInstance().performSortOperation(sortOption, Collections.emptyList());
                    } catch (Exception ex) {
                        assertTrue(ex instanceof IllegalArgumentException);
                        assertTrue(ex.getMessage().startsWith("Invalid sort option"));
                    }
                }
        );
    }

    private List<String> getFilePaths() {
        return asList(
                "src/A-oldest",
                "src/B-middle",
                "src/C-latest"
        );
    }

    private void createNewFiles() throws Exception {
        List<String> filePaths = getFilePaths();
        for (String filePath : filePaths) {
            new File(filePath).createNewFile();
            Thread.sleep(1000);
        }
    }

    private void deleteAllFiles() {
        List<String> filePaths = getFilePaths();
        for (String filePath : filePaths) {
            File file = new File(filePath);
            file.delete();
        }
    }


}
package swati4star.createpdf.util;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

@RunWith(BlockJUnit4ClassRunner.class)
@SuppressWarnings("unchecked")
public class ImageSortUtilsTest {


    @Test
    public void shouldSortPathsByFileNamesAscending() {
        // given
        int ascendingSortOption = 0;
        List<String> paths = getFilePaths();

        // when
        ImageSortUtils.performSortOperation(ascendingSortOption, paths);

        // then
        Assert.assertEquals(
                asList(
                        "src/test/resources/sort-test/A-oldest",
                        "src/test/resources/sort-test/B-middle",
                        "src/test/resources/sort-test/C-latest"
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
        ImageSortUtils.performSortOperation(descendingSortOption, paths);

        // then
        Assert.assertEquals(
                asList(
                        "src/test/resources/sort-test/C-latest",
                        "src/test/resources/sort-test/B-middle",
                        "src/test/resources/sort-test/A-oldest"
                ),
                paths
        );
    }

    @Test
    public void shouldSortPathsByLastModifiedAsc() {
        // given
        int dateAscendingOption = 2;
        List<String> paths = getFilePaths();

        // when
        ImageSortUtils.performSortOperation(dateAscendingOption, paths);

        // then
        Assert.assertEquals(
                asList(
                        "src/test/resources/sort-test/C-latest",
                        "src/test/resources/sort-test/B-middle",
                        "src/test/resources/sort-test/A-oldest"
                ),
                paths
        );
    }

    @Test
    public void shouldSortPathsByLastModifiedDescending() {
        // given
        int dateDescendingOption = 3;
        List<String> paths = getFilePaths();

        // when
        ImageSortUtils.performSortOperation(dateDescendingOption, paths);

        // then
        Assert.assertEquals(
                asList(
                        "src/test/resources/sort-test/A-oldest",
                        "src/test/resources/sort-test/B-middle",
                        "src/test/resources/sort-test/C-latest"
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
                        ImageSortUtils.performSortOperation(sortOption, Collections.emptyList());
                    } catch (Exception ex) {
                        assertTrue(ex instanceof IllegalArgumentException);
                        assertTrue(ex.getMessage().startsWith("Invalid sort option"));
                    }
                }
        );
    }

    private List<String> getFilePaths() {
        return asList(
                "src/test/resources/sort-test/B-middle",
                "src/test/resources/sort-test/C-latest",
                "src/test/resources/sort-test/A-oldest"
        );
    }
}
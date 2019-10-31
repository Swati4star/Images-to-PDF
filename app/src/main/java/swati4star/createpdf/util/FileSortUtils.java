package swati4star.createpdf.util;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class FileSortUtils {

    // Sorting order constants
    public final int NAME_INDEX = 0;
    public final int DATE_INDEX = 1;
    public final int SIZE_INCREASING_ORDER_INDEX = 2;
    public final int SIZE_DECREASING_ORDER_INDEX = 3;

    private FileSortUtils(){}

    public void performSortOperation(int option, List<File> pdf) {
        switch (option) {
            case DATE_INDEX:
                sortFilesByDateNewestToOldest(pdf);
                break;
            case NAME_INDEX:
                sortByNameAlphabetical(pdf);
                break;
            case SIZE_INCREASING_ORDER_INDEX:
                sortFilesBySizeIncreasingOrder(pdf);
                break;
            case SIZE_DECREASING_ORDER_INDEX:
                sortFilesBySizeDecreasingOrder(pdf);
                break;
        }
    }

    // SORTING FUNCTIONS

    /**
     * Sorts the given file list in increasing alphabetical  order
     *
     * @param filesList list of files to be sorted
     */
    private void sortByNameAlphabetical(List<File> filesList) {
        Collections.sort(filesList);
    }

    /**
     * Sorts the given file list by date from newest to oldest
     *
     * @param filesList list of files to be sorted
     */
    private void sortFilesByDateNewestToOldest(List<File> filesList) {
        Collections.sort(filesList, (file, file2) -> Long.compare(file2.lastModified(), file.lastModified()));
    }

    /**
     * Sorts the given file list in increasing order of file size
     *
     * @param filesList list of files to be sorted
     */
    private void sortFilesBySizeIncreasingOrder(List<File> filesList) {
        Collections.sort(filesList, (file1, file2) -> Long.compare(file1.length(), file2.length()));
    }

    /**
     * Sorts the given file list in decreasing order of file size
     *
     * @param filesList list of files to be sorted
     */
    private void sortFilesBySizeDecreasingOrder(List<File> filesList) {
        Collections.sort(filesList, (file1, file2) -> Long.compare(file2.length(), file1.length()));
    }

    private static class SingletonHolder {
        static final FileSortUtils INSTANCE = new FileSortUtils();
    }

    public static FileSortUtils getInstance() {
        return FileSortUtils.SingletonHolder.INSTANCE;
    }
}

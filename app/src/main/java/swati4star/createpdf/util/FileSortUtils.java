package swati4star.createpdf.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FileSortUtils {

    // Sorting order constants
    public static final int NAME_INDEX = 0;
    public static final int DATE_INDEX = 1;
    public static final int SIZE_INCREASING_ORDER_INDEX = 2;
    public static final int SIZE_DECREASING_ORDER_INDEX = 3;

    public static void performSortOperation(int option, ArrayList<File> pdf) {
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
    private static void sortByNameAlphabetical(ArrayList<File> filesList) {
        Collections.sort(filesList);
    }

    /**
     * Sorts the given file list by date from newest to oldest
     *
     * @param filesList list of files to be sorted
     */
    private static void sortFilesByDateNewestToOldest(ArrayList<File> filesList) {
        Collections.sort(filesList, (file, file2) -> Long.compare(file2.lastModified(), file.lastModified()));
    }

    /**
     * Sorts the given file list in increasing order of file size
     *
     * @param filesList list of files to be sorted
     */
    private static void sortFilesBySizeIncreasingOrder(ArrayList<File> filesList) {
        Collections.sort(filesList, (file1, file2) -> Long.compare(file1.length(), file2.length()));
    }

    /**
     * Sorts the given file list in decreasing order of file size
     *
     * @param filesList list of files to be sorted
     */
    private static void sortFilesBySizeDecreasingOrder(ArrayList<File> filesList) {
        Collections.sort(filesList, (file1, file2) -> Long.compare(file2.length(), file1.length()));
    }


}

package swati4star.createpdf.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class ImageSortUtils {
    private static final int NAME_ASC = 0;
    private static final int NAME_DESC = 1;
    private static final int DATE_ASC = 2;
    private static final int DATE_DESC = 3;


    /**
     * performs sorting operation.
     *
     * @param option sorting operation
     * @param images list of image paths
     */
    public static void performSortOperation(int option, ArrayList<String> images) {
        switch (option) {
            case NAME_ASC:
                sortByNameAsc(images);
                break;
            case NAME_DESC:
                sortByNameDesc(images);
                break;
            case DATE_ASC:
                sortByDateAsc(images);
                break;
            case DATE_DESC:
                sortByDateDesc(images);
                break;
        }
    }

    /**
     * Sorts the given list in ascending alphabetical  order
     *
     * @param imagePaths list of image paths to be sorted
     */
    private static void sortByNameAsc(ArrayList<String> imagePaths) {
        Collections.sort(imagePaths, (path1, path2) -> path1.substring(path1.lastIndexOf('/'))
                .compareTo(path2.substring(path2.lastIndexOf('/'))));
    }

    /**
     * Sorts the given list in descending alphabetical  order
     *
     * @param imagePaths list of image paths to be sorted
     */
    private static void sortByNameDesc(ArrayList<String> imagePaths) {
        Collections.sort(imagePaths, (path1, path2) -> path2.substring(path2.lastIndexOf('/'))
                .compareTo(path1.substring(path1.lastIndexOf('/'))));

    }


    /**
     * Sorts the given list in ascending  date  order
     *
     * @param imagePaths list of image paths to be sorted
     */
    private static void sortByDateAsc(ArrayList<String> imagePaths) {
        Collections.sort(imagePaths, (path1, path2) -> Long.compare(new File(path2).lastModified(),
                new File(path1).lastModified()));
    }

    /**
     * Sorts the given list in descending date  order
     *
     * @param imagePaths list of image paths to be sorted
     */
    private static void sortByDateDesc(ArrayList<String> imagePaths) {
        Collections.sort(imagePaths, (path1, path2) -> Long.compare(new File(path1).lastModified(),
                new File(path2).lastModified()));
    }
}

package swati4star.createpdf.util;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.Date;

public class FileInfoUtils {

    // GET PDF DETAILS

    /**
     * Gives a formatted last modified date for pdf ListView
     *
     * @param file file object whose last modified date is to be returned
     * @return String date modified in formatted form
     **/
    @NonNull
    public static String getFormattedDate(@NonNull File file) {
        Date lastModDate = new Date(file.lastModified());
        String[] formatDate = lastModDate.toString().split(" ");
        String time = formatDate[3];
        String[] formatTime = time.split(":");
        String date = formatTime[0] + ":" + formatTime[1];

        return formatDate[0] + ", " + formatDate[1] + " " + formatDate[2] + " at " + date;
    }

    /**
     * Gives a formatted size in MB for every pdf in pdf ListView
     *
     * @param file file object whose size is to be returned
     * @return String Size of pdf in formatted form
     */
    @NonNull
    public static String getFormattedSize(@NonNull File file) {
        return String.format("%.2f MB", (double) file.length() / (1024 * 1024));
    }
}

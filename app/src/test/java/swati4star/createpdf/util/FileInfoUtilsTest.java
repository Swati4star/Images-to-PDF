package swati4star.createpdf.util;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileInfoUtilsTest {

    @Test
    public void testGetFormattedSizeEmptyFile() {

        File file = new File("mockFileEmpty", ".pdf");
        try {
            boolean fileCreated = file.createNewFile();
            if (!fileCreated) {
                throw new IOException("Unable to create file at specified path. It already exists");
            }
            file.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String formattedSize = FileInfoUtils.getFormattedSize(file);
        assertEquals("0,00 MB", formattedSize);
    }

    @Test
    public void testGetFormattedSizeSmallFile() {

        try {
            File file = new File("mockFileSmall", ".pdf");
            try {
                boolean fileCreated = file.createNewFile();
                if (!fileCreated) {
                    throw new IOException("Unable to create file at specified path. It already exists");
                }
                file.deleteOnExit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] data = new byte[500 * 1024]; // =~ 500 KB
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            }

            String formattedSize = FileInfoUtils.getFormattedSize(file);
            assertEquals("0.49 MB", formattedSize);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFormattedSizeEdgeCase() {

        try {
            File file = new File("mockFileEdge", ".pdf");
            try {
                boolean fileCreated = file.createNewFile();
                if (!fileCreated) {
                    throw new IOException("Unable to create file at specified path. It already exists");
                }
                file.deleteOnExit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] data = new byte[1024 * 1024]; // = 1 MB
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            }

            String formattedSize = FileInfoUtils.getFormattedSize(file);
            assertEquals("1.00 MB", formattedSize);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFormattedSizeRoundedEdgeCase() {

        try {
            File file = new File("mockFileEdgeTwo", ".pdf");
            try {
                boolean fileCreated = file.createNewFile();
                if (!fileCreated) {
                    throw new IOException("Unable to create file at specified path. It already exists");
                }
                file.deleteOnExit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] data = new byte[1023 * 1024]; // = 1023 KB
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            }

            String formattedSize = FileInfoUtils.getFormattedSize(file);
            assertEquals("1.00 MB", formattedSize);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFormattedSizeLargeFile() {

        try {
            File file = new File("mockFileEdge", ".pdf");
            try {
                boolean fileCreated = file.createNewFile();
                if (!fileCreated) {
                    throw new IOException("Unable to create file at specified path. It already exists");
                }
                file.deleteOnExit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] data = new byte[100 * 1024 * 1024]; // = 5 MB
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            }

            String formattedSize = FileInfoUtils.getFormattedSize(file);
            assertEquals("100.00 MB", formattedSize);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFormattedDateSpecific() {

        File file = createTempFileWithSpecificLastModifiedDate();
        String formattedDate = FileInfoUtils.getFormattedDate(file);

        long specificLastModifiedTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 1 day ago
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd 'at' HH:mm", Locale.US);
        String expectedFormattedDate = dateFormat.format(new Date(specificLastModifiedTime));

        assertEquals(expectedFormattedDate, formattedDate);
    }

    private File createTempFileWithSpecificLastModifiedDate() {

        File file = new File("/Users/iroat/StudioProjects/Images-to-PDF/mockFileSpecDate.pdf");
        try {
            boolean fileCreated = file.createNewFile();
            if (!fileCreated) {
                throw new IOException("Unable to create file at specified path. It already exists");
            }
            file.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        long specificLastModifiedTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 1 day ago
        assertTrue(file.setLastModified(specificLastModifiedTime));

        return file;
    }

    @Test
    public void testGetFormattedDateCurrentDate() {

        File file = createTempFileWithCurrentDate();
        String formattedDate = FileInfoUtils.getFormattedDate(file);

        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd 'at' HH:mm", Locale.US);
        String expectedFormattedDate = dateFormat.format(new Date(file.lastModified()));

        assertEquals(expectedFormattedDate, formattedDate);
    }

    private File createTempFileWithCurrentDate() {

        File file = new File("/Users/iroat/StudioProjects/Images-to-PDF/mockFileCurrentDate.pdf");
        try {
            boolean fileCreated = file.createNewFile();
            if (!fileCreated) {
                throw new IOException("Unable to create file at specified path. It already exists");
            }
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}

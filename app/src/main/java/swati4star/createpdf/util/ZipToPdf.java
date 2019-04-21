package swati4star.createpdf.util;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import swati4star.createpdf.R;
import swati4star.createpdf.activity.MainActivity;

import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class ZipToPdf {

    /**
     * Converts zip file to PDF
     * @param path - path of zip file
     * @param context - current context
     */
    public static void convertZipToPDF(String path, Activity context) {

        final int BUFFER_SIZE = 4096;

        BufferedOutputStream bufferedOutputStream;
        FileInputStream fileInputStream;
        ArrayList<Uri> imageUris = new ArrayList<>();
        FileUtils.makeAndClearTemp();
        String dest = Environment.getExternalStorageDirectory().toString() +
                Constants.pdfDirectory + Constants.tempDirectory;

        try {
            fileInputStream = new FileInputStream(path);
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
            ZipEntry zipEntry;
            int folderPrefix = 0;

            /* In case there are folders in the zip file and in those folders we have images
             *  then we have possibility that file names in different folders are same.
             *  In this case we will use folderPrefix so that all images are copied to temp
             *  for every folder encountered we will increment folderPrefix by one and append
             *  it to the the image name*/
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String zipEntryName = zipEntry.getName().toLowerCase();

                if (zipEntry.isDirectory()) {
                    folderPrefix++;
                } else if (zipEntryName.endsWith(".jpg") || zipEntryName.endsWith(".png")) {
                    String newFileName = "/" + zipEntryName;
                    int index = zipEntryName.lastIndexOf("/");
                    /*index will be -1 when image is in just inside the zip
                     * and not inside some folder*/
                    if (index != -1)
                        newFileName = zipEntryName.substring(index);
                    if (folderPrefix != 0)
                        newFileName = newFileName.replace("/", "/" + folderPrefix + "- ");
                    File newFile = new File(dest + newFileName);
                    imageUris.add(Uri.fromFile(newFile));

                    byte[] buffer = new byte[BUFFER_SIZE];
                    FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                    bufferedOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER_SIZE);
                    int count;

                    while ((count = zipInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        bufferedOutputStream.write(buffer, 0, count);
                    }

                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                }
            }
            zipInputStream.close();

            if (imageUris.size() == 0) {
                showSnackbar(context, R.string.error_no_image_in_zip);
                return;
            }
            /*once we have extracted images out of zip, now we will pass
             * image uri's and to main activity which will then be used to
             * to start images to pdf fragment*/
            ((MainActivity) context).convertImagesToPdf(imageUris);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showSnackbar(context, R.string.error_occurred);
        } catch (IOException e) {
            e.printStackTrace();
            showSnackbar(context, R.string.error_occurred);
        }

    }
}

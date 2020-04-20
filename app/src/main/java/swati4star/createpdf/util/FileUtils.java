package swati4star.createpdf.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import swati4star.createpdf.R;
import swati4star.createpdf.database.DatabaseHelper;

import static swati4star.createpdf.util.Constants.AUTHORITY_APP;
import static swati4star.createpdf.util.Constants.PATH_SEPERATOR;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.pdfDirectory;
import static swati4star.createpdf.util.Constants.pdfExtension;

public class FileUtils {

    private final Activity mContext;
    private final SharedPreferences mSharedPreferences;

    public FileUtils(Activity context) {
        this.mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // GET PDF DETAILS

    /**
     * Gives a formatted last modified date for pdf ListView
     *
     * @param file file object whose last modified date is to be returned
     * @return String date modified in formatted form
     **/
    public static String getFormattedDate(File file) {
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
    public static String getFormattedSize(File file) {
        return String.format("%.2f MB", (double) file.length() / (1024 * 1024));
    }

    /**
     * Prints a file
     *
     * @param file the file to be printed
     */
    public void printFile(final File file) {
        final PrintDocumentAdapter mPrintDocumentAdapter = new PrintDocumentAdapterHelper(file);

        PrintManager printManager = (PrintManager) mContext
                .getSystemService(Context.PRINT_SERVICE);
        String jobName = mContext.getString(R.string.app_name) + " Document";
        if (printManager != null) {
            printManager.print(jobName, mPrintDocumentAdapter, null);
            new DatabaseHelper(mContext).insertRecord(file.getAbsolutePath(), mContext.getString(R.string.printed));
        }
    }

    /**
     * Emails the desired PDF using application of choice by user
     *
     * @param file - the file to be shared
     */
    public void shareFile(File file) {
        Uri uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file);
        ArrayList<Uri> uris = new ArrayList<>();
        uris.add(uri);
        shareFile(uris);
    }

    /**
     * Share the desired PDFs using application of choice by user
     *
     * @param files - the list of files to be shared
     */
    public void shareMultipleFiles(List<File> files) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (File file : files) {
            Uri uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file);
            uris.add(uri);
        }
        shareFile(uris);
    }

    /**
     * Emails the desired PDF using application of choice by user
     *
     * @param uris - list of uris to be shared
     */
    private void shareFile(ArrayList<Uri> uris) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.i_have_attached_pdfs_to_this_message));
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("application/pdf");
        mContext.startActivity(Intent.createChooser(intent,
                mContext.getResources().getString(R.string.share_chooser)));
    }

    /**
     * opens a file in appropriate application
     *
     * @param path - path of the file to be opened
     */
    public void openFile(String path) {
        if (path == null) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.error_path_not_found);
            return;
        }
        openTextFile(path);
    }

    /**
     * This function is used to open the created text file with Text editing/viewing
     * applications on the device.
     *
     * @param path - file path
     */
    public void openTextFile(String path) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            Uri uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file);
            target.setDataAndType(uri, mContext.getString(R.string.txt_type));
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            openIntent(Intent.createChooser(target, mContext.getString(R.string.open_file)));
        } catch (Exception e) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.error_open_file);
        }
    }

    /**
     * Checks if the new file already exists.
     *
     * @param finalOutputFile Path of pdf file to check
     * @param mFile           File List of all PDFs
     * @return Number to be added finally in the name to avoid overwrite
     */
    private int checkRepeat(String finalOutputFile, final List<File> mFile) {
        boolean flag = true;
        int append = 0;
        while (flag) {
            append++;
            String name = finalOutputFile.replace(mContext.getString(R.string.pdf_ext),
                    append + mContext.getString(R.string.pdf_ext));
            flag = mFile.contains(new File(name));
        }

        return append;
    }

    /**
     * Get real image path from uri.
     *
     * @param uri - uri of the image
     * @return - real path of the image file on device
     */
    public String getUriRealPath(Uri uri) {
        if (uri == null || FileUriUtils.getInstance().isWhatsappImage(uri.getAuthority()))
            return null;

        return FileUriUtils.getInstance().getUriRealPathAboveKitkat(mContext, uri);
    }

    /***
     * Check if file already exists in pdf_dir
     * @param mFileName - Name of the file
     * @return true if file exists else false
     */

    public boolean isFileExist(String mFileName) {
        String path = mSharedPreferences.getString(STORAGE_LOCATION,
                StringUtils.getInstance().getDefaultStorageLocation()) + mFileName;
        File file = new File(path);

        return file.exists();
    }

    /**
     * Extracts file name from the URI
     *
     * @param uri - file uri
     * @return - extracted filename
     */
    public String getFileName(Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();

        if (scheme == null)
            return null;

        if (scheme.equals("file")) {
            return uri.getLastPathSegment();
        } else if (scheme.equals("content")) {
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);

            if (cursor != null) {
                if (cursor.getCount() != 0) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    cursor.moveToFirst();
                    fileName = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        }

        return fileName;
    }

    /**
     * Extracts file name from the path
     *
     * @param path - file path
     * @return - extracted filename
     */
    public static String getFileName(String path) {
        if (path == null)
            return null;

        int index = path.lastIndexOf(PATH_SEPERATOR);
        return index < path.length() ? path.substring(index + 1) : null;
    }


    /**
     * Extracts file name from the URI
     *
     * @param path - file path
     * @return - extracted filename without extension
     */
    public static String getFileNameWithoutExtension(String path) {
        if (path == null || path.lastIndexOf(PATH_SEPERATOR) == -1)
            return path;

        String filename = path.substring(path.lastIndexOf(PATH_SEPERATOR) + 1);
        filename = filename.replace(pdfExtension, "");

        return filename;
    }

    /**
     * Extracts directory path from full file path
     *
     * @param path absolute path of the file
     * @return absolute path of file directory
     */
    public static String getFileDirectoryPath(String path) {
        return path.substring(0, path.lastIndexOf(PATH_SEPERATOR) + 1);
    }

    /**
     * Returns name of the last file with "_pdf" suffix.
     *
     * @param filesPath - ArrayList of image paths
     * @return fileName with _pdf suffix
     */
    public String getLastFileName(ArrayList<String> filesPath) {
        if (filesPath.size() == 0)
            return "";

        String lastSelectedFilePath = filesPath.get(filesPath.size() - 1);
        String nameWithoutExt = stripExtension(getFileNameWithoutExtension(lastSelectedFilePath));

        return nameWithoutExt + mContext.getString(R.string.pdf_suffix);
    }

    /**
     * Returns the filename without its extension
     *
     * @param fileNameWithExt fileName with extension. Ex: androidDev.jpg
     * @return fileName without extension. Ex: androidDev
     */
    public String stripExtension(String fileNameWithExt) {
        // Handle null case specially.
        if (fileNameWithExt == null) return null;

        // Get position of last '.'.
        int pos = fileNameWithExt.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.
        if (pos == -1) return fileNameWithExt;

        // Otherwise return the string, up to the dot.
        return fileNameWithExt.substring(0, pos);
    }

    /**
     * Saves bitmap to external storage
     *
     * @param filename    - name of the file
     * @param finalBitmap - bitmap to save
     */
    public static String saveImage(String filename, Bitmap finalBitmap) {

        if (finalBitmap == null || checkIfBitmapIsWhite(finalBitmap))
            return null;

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + pdfDirectory);
        String fileName = filename + ".png";

        File file = new File(myDir, fileName);
        if (file.exists())
            file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.v("saving", fileName);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return myDir + "/" + fileName;
    }

    /**
     * Checks of the bitmap is just all white pixels
     *
     * @param bitmap - input bitmap
     * @return - true, if bitmap is all white
     */
    private static boolean checkIfBitmapIsWhite(Bitmap bitmap) {
        if (bitmap == null)
            return true;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int pixel = bitmap.getPixel(i, j);
                if (pixel != Color.WHITE) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Opens image in a gallery application
     *
     * @param path - image path
     */
    public void openImage(String path) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Uri uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file);
        target.setDataAndType(uri, "image/*");
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        openIntent(Intent.createChooser(target, mContext.getString(R.string.open_file)));
    }

    /**
     * Opens the targeted intent (if possible), otherwise show a snackbar
     *
     * @param intent - input intent
     */
    private void openIntent(Intent intent) {
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_no_pdf_app);
        }
    }

    /**
     * Returns file chooser intent
     *
     * @return - intent
     */
    public Intent getFileChooser() {
        String folderPath = Environment.getExternalStorageDirectory() + "/";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Uri myUri = Uri.parse(folderPath);
        intent.setDataAndType(myUri, mContext.getString(R.string.pdf_type));

        return Intent.createChooser(intent, mContext.getString(R.string.merge_file_select));
    }

    String getUniqueFileName(String fileName) {
        String outputFileName = fileName;
        File file = new File(outputFileName);

        if (!isFileExist(file.getName()))
            return outputFileName;

        File parentFile = file.getParentFile();
        if (parentFile != null) {
            File[] listFiles = parentFile.listFiles();

            if (listFiles != null) {
                int append = checkRepeat(outputFileName, Arrays.asList(listFiles));
                outputFileName = outputFileName.replace(mContext.getString(R.string.pdf_ext),
                        append + mContext.getResources().getString(R.string.pdf_ext));
            }
        }

        return outputFileName;
    }

    /**
     * creates new folder for temp files
     */
    public static void makeAndClearTemp() {
        String dest = Environment.getExternalStorageDirectory().toString() +
                Constants.pdfDirectory + Constants.tempDirectory;
        File folder = new File(dest);
        boolean result = folder.mkdir();

        // clear all the files in it, if any
        if (result && folder.isDirectory()) {
            String[] children = folder.list();
            for (String child : children) {
                new File(folder, child).delete();
            }
        }
    }
}
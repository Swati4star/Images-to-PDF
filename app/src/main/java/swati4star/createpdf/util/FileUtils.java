package swati4star.createpdf.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import swati4star.createpdf.R;
import swati4star.createpdf.database.DatabaseHelper;

import static swati4star.createpdf.util.Constants.AUTHORITY_APP;
import static swati4star.createpdf.util.Constants.PATH_SEPERATOR;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.pdfDirectory;
import static swati4star.createpdf.util.Constants.pdfExtension;
import static swati4star.createpdf.util.FileUriUtils.getImageRealPath;
import static swati4star.createpdf.util.FileUriUtils.getUriRealPathAboveKitkat;
import static swati4star.createpdf.util.FileUriUtils.isAboveKitKat;
import static swati4star.createpdf.util.FileUriUtils.isWhatsappImage;
import static swati4star.createpdf.util.StringUtils.getDefaultStorageLocation;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class FileUtils {

    private final Activity mContext;
    private final ContentResolver mContentResolver;
    private final SharedPreferences mSharedPreferences;

    public FileUtils(Activity context) {
        this.mContext = context;
        mContentResolver = mContext.getContentResolver();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // GET PDF DETAILS
    /**
     * Gives a formatted last modified date for pdf ListView
     * @param file file object whose last modified date is to be returned
     *
     * @return String date modified in formatted form
     **/
    public static String getFormattedDate(File file) {
        Date lastModDate = new Date(file.lastModified());
        String[] formatdate = lastModDate.toString().split(" ");
        String time = formatdate[3];
        String[] formattime =  time.split(":");
        String date = formattime[0] + ":" + formattime[1];
        return formatdate[0] + ", " + formatdate[1] + " " + formatdate[2] + " at " + date;
    }

    /**
     * Gives a formatted size in MB for every pdf in pdf ListView
     * @param file file object whose size is to be returned
     *
     * @return String Size of pdf in formatted form
     */
    public static String getFormattedSize(File file) {
        return String.format("%.2f MB", (double) file.length() / (1024 * 1024));
    }

    /**
     * Prints a file
     * @param file the file to be printed
     */
    public void printFile(final File file) {

        final PrintDocumentAdapter mPrintDocumentAdapter = new PrintDocumentAdapter() {

            @Override
            public void onWrite(PageRange[] pages,
                                ParcelFileDescriptor destination,
                                CancellationSignal cancellationSignal,
                                WriteResultCallback callback) {
                try {
                    InputStream input = new FileInputStream(file.getName());
                    OutputStream output = new FileOutputStream(destination.getFileDescriptor());

                    byte[] buf = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = input.read(buf)) > 0)
                        output.write(buf, 0, bytesRead);

                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                    input.close();
                    output.close();

                } catch (Exception e) {
                    //Catch exception
                }
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes,
                                 PrintAttributes newAttributes,
                                 CancellationSignal cancellationSignal,
                                 LayoutResultCallback callback,
                                 Bundle extras) {

                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }
                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("myFile")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .build();

                callback.onLayoutFinished(pdi, true);
            }
        };

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
     * @param  file - the file to be shared
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
     * @param  files - the list of files to be shared
     */
    public void shareMultipleFiles(List<File> files) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (File file: files) {
            Uri uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file);
            uris.add(uri);
        }
        shareFile(uris);
    }

    /**
     * Emails the desired PDF using application of choice by user
     * @param uris - list of uris to be shared
     */
    private void shareFile(ArrayList<Uri> uris) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.i_have_attached_pdfs_to_this_message));
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("application/pdf");
        mContext.startActivity(Intent.createChooser(intent, "Sharing"));
    }

    /**
     * opens a file in appropriate application
     * @param path - path of the file to be opened
     */
    public void openFile(String path) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            Uri uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file);
            target.setDataAndType(uri, mContext.getString(R.string.pdf_type));
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            openIntent(Intent.createChooser(target, mContext.getString(R.string.open_file)));
        } catch (Exception e) {
            showSnackbar(mContext, R.string.error_occurred);
        }
    }

    /**
     * Checks if the new file already exists.
     *
     * @param finalOutputFile Path of pdf file to check
     * @param mFile File List of all PDFs
     * @return Number to be added finally in the name to avoid overwrite
     */
    public int checkRepeat(String finalOutputFile, final ArrayList<File> mFile) {
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
     * @param uri - uri of the image
     * @return  - real path of the image file on device
     */
    public String getUriRealPath(Uri uri) {
        String ret;
        if (isWhatsappImage(uri.getAuthority())) {
            ret = null;
        } else {
            if (isAboveKitKat()) {
                // Android OS above sdk version 19.
                ret = getUriRealPathAboveKitkat(mContext, uri);
            } else {
                // Android OS below sdk version 19
                ret = getImageRealPath(mContentResolver, uri, null);
            }
        }
        return ret;
    }

    /***
     * Check if file already exists in pdf_dir
     * @param mFileName - Name of the file
     * @return true if file exists else false
     */

    public boolean isFileExist(String mFileName) {
        String path = mSharedPreferences.getString(STORAGE_LOCATION,
                getDefaultStorageLocation()) + mFileName;
        File file = new File(path);
        return file.exists();
    }

    /**
     * Extracts file name from the URI
     * @param uri - file uri
     * @return - extracted filename
     */
    public String getFileName(Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();
        if (scheme.equals("file"))
            fileName = uri.getLastPathSegment();
        else if (scheme.equals("content")) {
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                fileName = cursor.getString(columnIndex);
            }
            if (cursor != null)
                cursor.close();
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
        return path.substring(path.lastIndexOf(PATH_SEPERATOR) + 1);
    }


    /**
     * Extracts file name from the URI
     *
     * @param path - file path
     * @return - extracted filename without extension
     */
    public static String getFileNameWithoutExtension(String path) {
        String p = path.substring(path.lastIndexOf(PATH_SEPERATOR) + 1);
        p = p.replace(pdfExtension, "");
        return p;
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
     * Saves bitmap to external storage
     * @param filename - name of the file
     * @param finalBitmap - bitmap to save
     */
    static String saveImage(String filename, Bitmap finalBitmap) {

        if (checkIfBitmapIsWhite(finalBitmap))
            return null;

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + pdfDirectory);
        String fname = filename + ".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Log.v("saving", fname);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myDir + "/" + fname;
    }

    /**
     * Checks of the bitmap is just all white pixels
     * @param bitmap - input bitmap
     * @return - true, if bitmap is all white
     */
    private static boolean checkIfBitmapIsWhite(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        for (int i =  0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int pixel =  bitmap.getPixel(i, j);
                if (pixel != Color.WHITE) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Opens image in a gallery application
     * @param path - image path
     */
    public void openImage(String path) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Uri uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file);
        target.setDataAndType(uri,  "image/*");
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        openIntent(Intent.createChooser(target, mContext.getString(R.string.open_file)));
    }

    /**
     * Opens the targeted intent (if possible), otherwise show a snackbar
     * @param intent - input intent
     */
    private void openIntent(Intent intent) {
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            showSnackbar(mContext, R.string.snackbar_no_pdf_app);
        }
    }

    /**
     * Returns file chooser intent
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
}
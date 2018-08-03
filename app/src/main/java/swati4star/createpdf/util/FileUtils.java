package swati4star.createpdf.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import swati4star.createpdf.R;
import swati4star.createpdf.database.DatabaseHelper;


public class FileUtils {

    private final Activity mContext;
    private final ContentResolver mContentResolver;

    public FileUtils(Activity context) {
        this.mContext = context;
        mContentResolver = mContext.getContentResolver();
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
        Uri uri = FileProvider.getUriForFile(mContext, "com.swati4star.shareFile", file);
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
            Uri uri = FileProvider.getUriForFile(mContext, "com.swati4star.shareFile", file);
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

        Uri uri = FileProvider.getUriForFile(mContext, "com.swati4star.shareFile", file);

        target.setDataAndType(uri,  mContext.getString(R.string.pdf_type));
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target, mContext.getString(R.string.open_file));
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(Objects.requireNonNull(mContext).findViewById(android.R.id.content),
                    R.string.snackbar_no_pdf_app, Snackbar.LENGTH_LONG).show();
        }
    }
    /**
     * Checks if the new file already exists.
     *
     * @param finalOutputFile Path of pdf file to check
     * @param mFile File List of all PDFs
     * @return Number to be added finally in the name
     */
    public static int checkRepeat(String finalOutputFile, final ArrayList<File> mFile) {
        int flag = 1;
        int append = 1;
        while (flag == 1) {
            for (int i = 0; i < mFile.size(); i++) {
                flag = 0;
                if (finalOutputFile.equals(mFile.get(i).getPath())) {
                    flag = 1;
                    append++;
                    break;
                }
            }
            finalOutputFile = finalOutputFile.replace(".pdf", append + ".pdf");
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
                ret = getUriRealPathAboveKitkat(uri);

            } else {
                // Android OS below sdk version 19
                ret = getImageRealPath(mContentResolver, uri, null);
            }
        }
        return ret;
    }

    /**
     * Get real path for Android Kitkat and above
     * @param uri - uri of the image
     * @return  - real path of the image file on device
     */
    private String getUriRealPathAboveKitkat(Uri uri) {
        String ret = "";

        if (mContext != null && uri != null) {

            if (isContentUri(uri)) {
                if (isGooglePhotoDoc(uri.getAuthority())) {
                    ret = uri.getLastPathSegment();
                } else {
                    ret = getImageRealPath(mContext.getContentResolver(), uri, null);
                }
            } else if (isFileUri(uri)) {
                ret = uri.getPath();
            } else if (isDocumentUri(uri)) {

                // Get uri related document id.
                String documentId = DocumentsContract.getDocumentId(uri);

                // Get uri authority.
                String uriAuthority = uri.getAuthority();

                if (isMediaDoc(uriAuthority)) {
                    String[] idArr = documentId.split(":");
                    if (idArr.length == 2) {
                        // First item is document type.
                        String docType = idArr[0];

                        // Second item is document real id.
                        String realDocId = idArr[1];

                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        switch (docType) {
                            case "image":
                                mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                break;
                            case "video":
                                mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                                break;
                            case "audio":
                                mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                                break;
                        }

                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getImageRealPath(mContentResolver, mediaContentUri, whereClause);
                    }

                } else if (isDownloadDoc(uriAuthority)) {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));

                    ret = getImageRealPath(mContentResolver, downloadUriAppendId, null);

                } else if (isExternalStoreDoc(uriAuthority)) {
                    String[] idArr = documentId.split(":");
                    if (idArr.length == 2) {
                        String type = idArr[0];
                        String realDocId = idArr[1];

                        if ("primary".equalsIgnoreCase(type)) {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }
        return ret;
    }

    /** Check whether current android os version is bigger than kitkat or not.
     * @return  - true if os version bigger than kitkat , else false
     */
    private boolean isAboveKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /** Check whether this uri represent a document or not.
     * @return  - true if document , else false
     */
    private boolean isDocumentUri(Uri uri) {
        boolean ret = false;
        if (mContext != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(mContext, uri);
        }
        return ret;
    }

    /** Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  @return - true if content uri, else false
     *  */
    private boolean isContentUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("content".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }

    /** Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     *  @return - true if file uri, else false
     * */
    private boolean isFileUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("file".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }

    /** Check whether this document is provided by ExternalStorageProvider.
     * @return true if document is provided by ExternalStorageProvider, else false
     */
    private boolean isExternalStoreDoc(String uriAuthority) {
        return "com.android.externalstorage.documents".equals(uriAuthority);
    }

    /** Check whether this document is provided by DownloadsProvider.
     * @return true if document is provided by DownloadsProvider, else false
     */
    private boolean isDownloadDoc(String uriAuthority) {
        return "com.android.providers.downloads.documents".equals(uriAuthority);
    }

    /** Check whether this document is provided by MediaProvider.
     * @return true if media document, else false
     */
    private boolean isMediaDoc(String uriAuthority) {
        return "com.android.providers.media.documents".equals(uriAuthority);
    }

    /** Check whether this document is provided by google photos.
     * @return true if google photo, else false
     */
    private boolean isGooglePhotoDoc(String uriAuthority) {
        return "com.google.android.apps.photos.content".equals(uriAuthority);
    }

    /** Check whether the image is whatsapp image
     * @return true if whatsapp image, else false
     */
    private boolean isWhatsappImage(String uriAuthority) {
        return "com.whatsapp.provider.media".equals(uriAuthority);
    }

    /** Get real path of image from uri
     * @param contentResolver - to access meta data from MediaStore
     * @param uri - uri of image
     * @param whereClause - add constraint on content resolver
     * @return true if google photo, else false
     */
    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if (cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            if (moveToFirst) {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if ( uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI ) {
                    columnName = MediaStore.Images.Media.DATA;
                } else if ( uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ) {
                    columnName = MediaStore.Audio.Media.DATA;
                } else if ( uri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI ) {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                if (imageColumnIndex == -1)
                    return ret;

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
                cursor.close();
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
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                mContext.getString(R.string.pdf_dir) + mFileName;
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
     * Extracts file name from the URI
     *
     * @param path - file path
     * @return - extracted filename
     */
    public String getFileName(String path) {
        return path.substring(path.lastIndexOf(mContext.getString(R.string.path_seperator)) + 1);
    }
}
package swati4star.createpdf.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

public class RealPathUtil {

    private static class SingletonHolder {
        static final RealPathUtil INSTANCE = new RealPathUtil();
    }

    public static RealPathUtil getInstance() {
        return RealPathUtil.SingletonHolder.INSTANCE;
    }

    /**
     * Returns actual path from uri
     *
     * @param context - current context
     * @param fileUri - uri of file
     * @return - actual path
     */
    public String getRealPath(Context context, Uri fileUri) {
        return getRealPathFromURI_API19(context, fileUri);
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     */
    private String getRealPathFromURI_API19(final Context context, final Uri uri) {
        String path = null;
        // DocumentProvider
        if (isDriveFile(uri)) {
            return null;
        }
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    if (split.length > 1) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        path = Environment.getExternalStorageDirectory() + "/";
                    }
                } else {
                    path = "storage" + "/" + docId.replace(":", "/");
                }

            } else if (isRawDownloadsDocument(uri)) {
                path = getDownloadsDocumentPath(context, uri, true);
            } else if (isDownloadsDocument(uri)) {
                path = getDownloadsDocumentPath(context, uri, false);
            }
        }
        return path;
    }

    /**
     * Get a file path from an Uri that points to the Downloads folder.
     *
     * @param context       The context
     * @param uri           The uri to query
     * @param hasSubFolders The flag that indicates if the file is in the root or in a subfolder
     * @return The absolute file path
     */
    private String getDownloadsDocumentPath(Context context, Uri uri, boolean hasSubFolders) {
        String fileName = getFilePath(context, uri);
        String subFolderName = hasSubFolders ? getSubFolders(uri) : "";

        if (fileName != null) {
            if (subFolderName != null)
                return Environment.getExternalStorageDirectory().toString() +
                        "/Download/" + subFolderName + fileName;
            else
                return Environment.getExternalStorageDirectory().toString() +
                        "/Download/" + fileName;
        }
        final String id = DocumentsContract.getDocumentId(uri);

        String path = null;
        if (!TextUtils.isEmpty(id)) {
            if (id.startsWith("raw:")) {
                path = id.replaceFirst("raw:", "");
            }
            try {
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                path = getDataColumn(context, contentUri, null, null);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return path;

    }

    /**
     * Get all the subfolders from an Uri.
     *
     * @param uri The uri
     * @return A string containing all the subfolders that point to the final file path
     */
    private String getSubFolders(Uri uri) {
        String replaceChars = String.valueOf(uri).replace("%2F", "/")
                .replace("%20", " ").replace("%3A", ":");
        // searches for "Download" to get the directory path
        // for example, if the file is inside a folder "test" in the Download folder, this method
        // returns "test/"
        String[] components = replaceChars.split("/");
        String sub5 = components[components.length - 2];
        String sub4 = components[components.length - 3];
        String sub3 = components[components.length - 4];
        String sub2 = components[components.length - 5];
        String sub1 = components[components.length - 6];
        if (sub1.equals("Download")) {
            return sub2 + "/" + sub3 + "/" + sub4 + "/" + sub5 + "/";
        } else if (sub2.equals("Download")) {
            return sub3 + "/" + sub4 + "/" + sub5 + "/";
        } else if (sub3.equals("Download")) {
            return sub4 + "/" + sub5 + "/";
        } else if (sub4.equals("Download")) {
            return sub5 + "/";
        } else {
            return null;
        }
    }

    /**
     * Get the file path (without subfolders if any)
     *
     * @param context The context
     * @param uri     The uri to query
     * @return The file path
     */
    private String getFilePath(Context context, Uri uri) {
        final String[] projection = {MediaStore.Files.FileColumns.DISPLAY_NAME};
        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null,
                null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
                return cursor.getString(index);
            }
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        final String column = "_data";
        final String[] projection = {
                column
        };
        String path = null;
        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                path = cursor.getString(index);
            }
        } catch (Exception e) {
            Log.e("Error", " " + e.getMessage());
        }
        return path;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * This function is used to check for a drive file URI.
     *
     * @param uri - input uri
     * @return true, if is google drive uri, otherwise false
     */
    private boolean isDriveFile(Uri uri) {
        if ("com.google.android.apps.docs.storage".equals(uri.getAuthority()))
            return true;
        return "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check
     * @return True if is a raw downloads document, otherwise false
     */
    private boolean isRawDownloadsDocument(Uri uri) {
        String uriToString = String.valueOf(uri);
        return uriToString.contains("com.android.providers.downloads.documents/document/raw");
    }
}
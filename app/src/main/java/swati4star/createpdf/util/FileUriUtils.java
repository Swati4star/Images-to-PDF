package swati4star.createpdf.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

class FileUriUtils {

    /** Check whether current android os version is bigger than kitkat or not.
     * @return  - true if os version bigger than kitkat , else false
     */
    static boolean isAboveKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /** Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  @return - true if content uri, else false
     *  */
    static boolean isContentUri(Uri uri) {
        return uri != null && uri.getScheme().equalsIgnoreCase("content");
    }

    /** Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     *  @return - true if file uri, else false
     * */
    static boolean isFileUri(Uri uri) {
        return uri != null && uri.getScheme().equalsIgnoreCase("file");
    }

    /** Check whether this document is provided by ExternalStorageProvider.
     * @return true if document is provided by ExternalStorageProvider, else false
     */
    static boolean isExternalStoreDoc(String uriAuthority) {
        return "com.android.externalstorage.documents".equals(uriAuthority);
    }

    /** Check whether this document is provided by DownloadsProvider.
     * @return true if document is provided by DownloadsProvider, else false
     */
    static boolean isDownloadDoc(String uriAuthority) {
        return "com.android.providers.downloads.documents".equals(uriAuthority);
    }

    /** Check whether this document is provided by MediaProvider.
     * @return true if media document, else false
     */
    static boolean isMediaDoc(String uriAuthority) {
        return "com.android.providers.media.documents".equals(uriAuthority);
    }

    /** Check whether this document is provided by google photos.
     * @return true if google photo, else false
     */
    static boolean isGooglePhotoDoc(String uriAuthority) {
        return "com.google.android.apps.photos.content".equals(uriAuthority);
    }

    /** Check whether the image is whatsapp image
     * @return true if whatsapp image, else false
     */
    static boolean isWhatsappImage(String uriAuthority) {
        return "com.whatsapp.provider.media".equals(uriAuthority);
    }


    /** Check whether this uri represent a document or not.
     * @return  - true if document , else false
     */
    private static boolean isDocumentUri(Context mContext, Uri uri) {
        boolean ret = false;
        if (mContext != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(mContext, uri);
        }
        return ret;
    }

    /**
     * Get real path for Android Kitkat and above
     * @param mContext - context
     * @param uri - uri of the image
     * @return  - real path of the image file on device
     */
    static String getUriRealPathAboveKitkat(Context mContext, Uri uri) {
        String ret = "";
        ContentResolver mContentResolver = mContext.getContentResolver();

        if (uri != null) {

            if (isContentUri(uri)) {
                if (isGooglePhotoDoc(uri.getAuthority())) {
                    ret = uri.getLastPathSegment();
                } else {
                    ret = getImageRealPath(mContentResolver, uri, null);
                }
            } else if (isFileUri(uri)) {
                ret = uri.getPath();
            } else if (isDocumentUri(mContext, uri)) {

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

    /** Get real path of image from uri
     * @param contentResolver - to access meta data from MediaStore
     * @param uri - uri of image
     * @param whereClause - add constraint on content resolver
     * @return true if google photo, else false
     */
    static String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {
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

}

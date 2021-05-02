package swati4star.createpdf.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import swati4star.createpdf.R;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.util.lambda.Consumer;

import static swati4star.createpdf.util.Constants.AUTHORITY_APP;
import static swati4star.createpdf.util.Constants.PATH_SEPERATOR;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.pdfExtension;

public class FileUtils {

    private final Activity mContext;
    private final SharedPreferences mSharedPreferences;

    public FileUtils(Activity context) {
        this.mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public enum FileType {
        e_PDF,
        e_TXT
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
        intent.setType(mContext.getString(R.string.pdf_type));
        mContext.startActivity(Intent.createChooser(intent,
                mContext.getResources().getString(R.string.share_chooser)));
    }

    /**
     * opens a file in appropriate application
     *
     * @param path - path of the file to be opened
     *
     */
    public void openFile(String path, FileType fileType) {
        if (path == null) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.error_path_not_found);
            return;
        }
        openFileInternal(path, fileType == FileType.e_PDF ?
                mContext.getString(R.string.pdf_type) : mContext.getString(R.string.txt_type));
    }

    /**
     * This function is used to open the created file
     * applications on the device.
     *
     * @param path - file path
     */
    private void openFileInternal(String path, String dataType) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            Uri uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file);

            target.setDataAndType(uri, dataType);
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
     * Opens a Dialog to select a filename.
     * If the file under that name already exists, an overwrite dialog gets opened.
     * If the overwrite is cancelled, this first dialog gets opened again.
     * @param preFillName a prefill Name for the file
     * @param ext the file extension
     * @param saveMethod the method that should be called when a filename is chosen
     */
    public void openSaveDialog(String preFillName, String ext, Consumer<String> saveMethod) {

        MaterialDialog.Builder builder = DialogUtils.getInstance().createCustomDialog(mContext,
                R.string.creating_pdf, R.string.enter_file_name);
        builder.input(mContext.getString(R.string.example), preFillName, (dialog, input) -> {
            if (StringUtils.getInstance().isEmpty(input)) {
                StringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_name_not_blank);
            } else {
                final String filename = input.toString();
                if (!isFileExist(filename + ext)) {
                    saveMethod.accept(filename);
                } else {
                    MaterialDialog.Builder builder2 = DialogUtils.getInstance().createOverwriteDialog(mContext);
                    builder2.onPositive((dialog2, which) -> saveMethod.accept(filename))
                            .onNegative((dialog1, which) ->
                                    openSaveDialog(preFillName, ext, saveMethod)).show();
                }
            }
        }).show();
    }
}
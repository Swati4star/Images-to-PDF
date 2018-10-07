package swati4star.createpdf.util;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import swati4star.createpdf.R;

import static swati4star.createpdf.util.StringUtils.getDefaultStorageLocation;

public class MoveFilesToDirectory extends AsyncTask<String, String, String> {

    private MaterialDialog mDialog;
    private final ArrayList<String> mFilePath;
    private final String mDirectoryName;
    private final Context mContext;
    private final int mOperationID;

    public static final int MOVE_FILES = 1;
    public static final int DELETE_DIRECTORY = 2;
    public static final int HOME_DIRECTORY = 3;

    /**
     * Inititlize async task to perform directory related operation
     * @param context - context object
     * @param filePath - list of file paths to be moved
     * @param directoryName - directory name on which operation is to be performed
     * @param mOperationID - operation id
     */
    public MoveFilesToDirectory(Context context, ArrayList<String> filePath, String directoryName, int mOperationID ) {
        this.mContext = context;
        this.mFilePath = filePath;
        this.mDirectoryName = directoryName;
        this.mOperationID = mOperationID;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                .title(R.string.please_wait)
                .cancelable(false)
                .progress(true, 0);
        if (mOperationID == MOVE_FILES || mOperationID == HOME_DIRECTORY) {
            builder.content(R.string.moving_files);
        } else if (mOperationID == DELETE_DIRECTORY) {
            builder.content(R.string.move_files_delete_dir);
        }
        mDialog = builder.build();
        mDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        switch (mOperationID) {
            case MOVE_FILES:
                String destination;
                for (String path : mFilePath) {
                    String[] fileName = path.split("/");
                    destination = getDefaultStorageLocation()
                            + mDirectoryName + "/" + fileName[fileName.length - 1];
                    if (!path.equalsIgnoreCase(destination))
                        moveFile(mDirectoryName + "/", path, destination);
                }
                break;

            case DELETE_DIRECTORY:
                for (String path : mFilePath)
                    new File(path).delete();

                new File(getDefaultStorageLocation() + mDirectoryName).delete();
                break;

            case HOME_DIRECTORY:
                for (String path : mFilePath) {
                    String[] fileName = path.split("/");
                    destination = getDefaultStorageLocation()
                            + fileName[fileName.length - 1];
                    moveFile(null, path, destination);
                }
                break;
        }
        return null;
    }

    /**
     * Moves files to a given directory
     * @param directoryName - new directory
     * @param source - source path
     * @param destination - destination path
     */
    private void moveFile( String directoryName, String source, String destination) {
        InputStream in;
        OutputStream out;
        int read;
        try {
            if (directoryName != null) {
                File folder = new File(getDefaultStorageLocation() + directoryName);
                if (!folder.exists())
                    folder.mkdir();
            }

            in = new FileInputStream(source);
            out = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];
            while ((read = in.read(buffer)) != -1)
                out.write(buffer, 0, read);

            in.close();
            out.flush();
            out.close();

            // delete the original file
            new File(source).delete();

        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(true)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss());

        if (mOperationID == MOVE_FILES || mOperationID == HOME_DIRECTORY) {
            builder.setTitle(R.string.moved_files)
                    .setMessage(R.string.success_move);
        } else if (mOperationID == DELETE_DIRECTORY) {
            builder.setTitle(R.string.directory_deleted)
                    .setMessage(R.string.success_delete_directory);
        }
        builder.create().show();
    }
}

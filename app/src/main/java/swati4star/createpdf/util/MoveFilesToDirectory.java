package swati4star.createpdf.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import swati4star.createpdf.R;

public class MoveFilesToDirectory extends AsyncTask<String, String, String> {

    private MaterialDialog mDialog;
    private ArrayList<String> mFilePath;
    private String mDirectoryName;
    private Context mContext;
    private int mOperationID;

    private static final int MOVE_FILES = 1;
    private static final int DELETE_DIRECTORY = 2;
    private static final int HOME_DIRECTORY = 3;

    public MoveFilesToDirectory(Context context , ArrayList<String> filePath , String directoryName , int mOperationID ) {
        this.mContext = context;
        this.mFilePath = filePath;
        this.mDirectoryName = directoryName;
        this.mOperationID = mOperationID;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MaterialDialog.Builder builder = null;
        if (mOperationID == MOVE_FILES || mOperationID == HOME_DIRECTORY) {
            builder = new MaterialDialog.Builder(mContext)
                    .title(R.string.please_wait)
                    .content(R.string.moving_files)
                    .cancelable(false)
                    .progress(true, 0);
        } else if (mOperationID == DELETE_DIRECTORY) {
            builder = new MaterialDialog.Builder(mContext)
                    .title(R.string.please_wait)
                    .content(R.string.move_files_delete_dir)
                    .cancelable(false)
                    .progress(true, 0);
        }
        mDialog = builder.build();
        mDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {

        if (mOperationID == MOVE_FILES) {
            String destination;
            for (String path : mFilePath) {
                String[] fileName = path.split("/");
                destination = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + mContext.getResources().getString(R.string.pdf_dir)
                        + mDirectoryName
                        +"/"
                        + fileName[fileName.length - 1];
                if (!path.equalsIgnoreCase(destination)) {
                    moveFile(mDirectoryName + "/", path, destination);
                }
            }
        } else if (mOperationID == DELETE_DIRECTORY) {
            for (String path : mFilePath) {
                new File(path).delete();
            }
            new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + mContext.getResources().getString(R.string.pdf_dir)
                    + mDirectoryName).delete();

        } else if (mOperationID == HOME_DIRECTORY) {
            String destination;
            for (String path : mFilePath) {
                String[] fileName = path.split("/");
                destination = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + mContext.getResources().getString(R.string.pdf_dir)
                        + fileName[fileName.length - 1];
                moveFile(null , path , destination);
            }
        }

        return null;
    }

    private void moveFile( String directoryName , String source , String destination ) {

        InputStream in = null;
        OutputStream out = null;
        int read;
        try {
            if (directoryName != null) {
                File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + mContext.getResources().getString(R.string.pdf_dir)
                        + directoryName);
                if (!folder.exists()) {
                    folder.mkdir();
                }
            }
            in = new FileInputStream(source);
            out = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(source).delete();

        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mDialog.dismiss();
        AlertDialog.Builder builder = null;
        if (mOperationID == MOVE_FILES || mOperationID == HOME_DIRECTORY) {
            builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.moved_files)
                    .setMessage(R.string.success_move)
                    .setCancelable(true)
                    .setPositiveButton("okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        } else if (mOperationID == DELETE_DIRECTORY) {
            builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.directory_deleted)
                    .setMessage(R.string.success_delete_directory)
                    .setCancelable(true)
                    .setPositiveButton("okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

        }
        builder.create().show();
    }
}

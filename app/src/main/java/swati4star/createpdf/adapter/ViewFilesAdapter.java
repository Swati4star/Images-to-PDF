package swati4star.createpdf.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.support.annotation.StringRes;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialripple.MaterialRippleLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.fragment.ViewFilesFragment;


/**
 * Created by swati on 9/10/15.
 * <p>
 * An adapter to view the existing PDF files
 */


public class ViewFilesAdapter extends RecyclerView.Adapter<ViewFilesAdapter.ViewFilesHolder> {

    private static LayoutInflater inflater;
    private Context mContext;
    private ArrayList<File> mFileList;
    private String mFileName;
    private PrintDocumentAdapter mPrintDocumentAdapter = new PrintDocumentAdapter() {

        @Override
        public void onWrite(PageRange[] pages,
                            ParcelFileDescriptor destination,
                            CancellationSignal cancellationSignal,
                            WriteResultCallback callback) {
            InputStream input = null;
            OutputStream output = null;
            try {
                input = new FileInputStream(mFileName);
                output = new FileOutputStream(destination.getFileDescriptor());

                byte[] buf = new byte[1024];
                int bytesRead;

                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }

                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

            } catch (Exception e) {
                //Catch exception
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    /**
     * Returns adapter instance
     *
     * @param context   the context calling this adapter
     * @param feedItems array list containing path of files
     */
    public ViewFilesAdapter(Context context, ArrayList<File> feedItems) {
        this.mContext = context;
        this.mFileList = feedItems;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewFilesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new ViewFilesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewFilesHolder holder, int position) {
        Log.d("logs", "getItemCount: " + mFileList.size());
        // Extract file name from path
        final String fileName = mFileList.get(position).getPath();
        final int filePosition = position;
        String[] name = fileName.split("/");

        holder.mFilename.setText(name[name.length - 1]);

        holder.mRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(mContext)
                        .title(R.string.title)
                        .items(R.array.items)
                        .itemsIds(R.array.itemIds)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                switch (which) {
                                    case 0: //Open
                                        openFile(fileName);
                                        break;

                                    case 1: //delete
                                        deleteFile(fileName, filePosition);
                                        break;

                                    case 2: //delete all files
                                        deleteAllFiles();
                                        break;

                                    case 3: //rename
                                        renameFile(filePosition);
                                        break;

                                    case 4: //Print
                                        doPrint(fileName);
                                        break;

                                    case 5: //Email
                                        shareFile(fileName);
                                        break;
                                }
                            }
                        })
                        .show();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFileList == null ? 0 : mFileList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Sets pdf files
     *
     * @param pdfFiles array list containing path of files
     */
    public void setData(ArrayList<File> pdfFiles) {
        mFileList = pdfFiles;
        notifyDataSetChanged();
    }

    public void openFile(String name) {
        File file = new File(name);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file), mContext.getString(R.string.pdf_type));
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, mContext.getString(R.string.open_file));
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, mContext.getString(R.string.toast_no_pdf_app), Toast.LENGTH_LONG).show();
        }
    }

    private void deleteFile(String name, int position) {
        File fdelete = new File(name);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Toast.makeText(mContext, R.string.toast_file_deleted, Toast.LENGTH_LONG).show();
                mFileList.remove(position);
                notifyDataSetChanged();
                if (mFileList.size() == 0) {
                    ViewFilesFragment.emptyStatusTextView.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(mContext, R.string.toast_file_not_deleted, Toast.LENGTH_LONG).show();
            }
        }

    }

    // iterate through filelist and remove all elements
    private void deleteAllFiles() {
        int deletedCount = 0;
        List<File> toRemove = new ArrayList<>();
        for (File fDelete : mFileList) {
            if (fDelete.exists()) {
                if (fDelete.delete()) {
                    toRemove.add(fDelete);
                    deletedCount++;
                }
            }
        }
        for (File fToRemove : toRemove) {
            mFileList.remove(fToRemove);
        }
        notifyDataSetChanged();
        if (mFileList.size() == 0) {
            ViewFilesFragment.emptyStatusTextView.setVisibility(View.VISIBLE);
        }
        Toast.makeText(mContext
                , String.format(string(R.string.toast_multipleFiles_deleted), deletedCount)
                , Toast.LENGTH_SHORT).show();
    }

    private void renameFile(final int position) {
        new MaterialDialog.Builder(mContext)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(mContext.getString(R.string.example), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input == null) {
                            Toast.makeText(mContext, R.string.toast_name_not_blank, Toast.LENGTH_LONG).show();
                        } else {
                            String newname = input.toString();
                            File oldfile = mFileList.get(position);
                            String[] x = mFileList.get(position).getPath().split("/");
                            String newfilename = "";
                            for (int i = 0; i < x.length - 1; i++)
                                newfilename = newfilename + "/" + x[i];

                            File newfile = new File(newfilename + "/" + newname + mContext.getString(R.string.pdf_ext));

                            Log.e("Old file name", oldfile + " ");
                            Log.e("New file name", newfile + " ");

                            if (oldfile.renameTo(newfile)) {
                                Toast.makeText(mContext, R.string.toast_file_renamed, Toast.LENGTH_LONG).show();
                                mFileList.set(position, newfile);
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(mContext, R.string.toast_file_not_renamed, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .show();
    }

    /**
     * Prints a file
     *
     * @param fileName Path of file to be printed
     */
    private void doPrint(String fileName) {
        PrintManager printManager = (PrintManager) mContext
                .getSystemService(Context.PRINT_SERVICE);

        mFileName = fileName;
        String jobName = mContext.getString(R.string.app_name) + " Document";
        printManager.print(jobName, mPrintDocumentAdapter, null);
    }

    /**
     * Emails the desired PDF using application of choice by user
     *
     * @author RakiRoad
     */
    private void shareFile(String name) {
        Uri uri = FileProvider.getUriForFile(mContext, "com.swati4star.shareFile", new File(name));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "I have attached a PDF to this message");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("application/pdf");
        mContext.startActivity(Intent.createChooser(intent, "Sharing"));
    }

    public String string(@StringRes int resId) {
        return mContext.getString(resId);
    }

    public class ViewFilesHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fileRipple)
        MaterialRippleLayout mRipple;
        @BindView(R.id.fileName)
        TextView mFilename;


        public ViewFilesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
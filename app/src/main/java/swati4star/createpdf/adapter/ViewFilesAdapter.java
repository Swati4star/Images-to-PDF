package swati4star.createpdf.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialripple.MaterialRippleLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.interfaces.DataSetChanged;
import swati4star.createpdf.interfaces.EmptyStateChangeListener;
import swati4star.createpdf.util.DirectoryUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.PDFEncryptionUtility;
import swati4star.createpdf.util.PDFUtils;

import static swati4star.createpdf.util.FileUtils.getFormattedDate;


/**
 * Created by swati on 9/10/15.
 * <p>
 * An adapter to view the existing PDF files
 */

public class ViewFilesAdapter extends RecyclerView.Adapter<ViewFilesAdapter.ViewFilesHolder>
        implements DataSetChanged {

    private final Activity mActivity;
    private final EmptyStateChangeListener mEmptyStateChangeListener;

    private ArrayList<File> mFileList;
    int isFileDeleteUndoClicked = 0;
    private final ArrayList<Integer> mSelectedFiles;

    private final FileUtils mFileUtils;
    private DirectoryUtils mDirectoryUtils;
    private final PDFUtils mPDFUtils;
    private final PDFEncryptionUtility mPDFEncryptionUtils;
    private final DatabaseHelper mDatabaseHelper;

    /**
     * Returns adapter instance
     *
     * @param activity                 the activity calling this adapter
     * @param feedItems                array list containing path of files
     * @param emptyStateChangeListener interface for empty state change
     */
    public ViewFilesAdapter(Activity activity,
                            ArrayList<File> feedItems,
                            EmptyStateChangeListener emptyStateChangeListener) {
        this.mActivity = activity;
        this.mEmptyStateChangeListener = emptyStateChangeListener;
        this.mFileList = feedItems;
        mSelectedFiles = new ArrayList<>();
        mFileUtils = new FileUtils(activity);
        mPDFUtils = new PDFUtils(activity);
        mDirectoryUtils = new DirectoryUtils(activity);
        mPDFEncryptionUtils = new PDFEncryptionUtility(activity);
        mDatabaseHelper = new DatabaseHelper(mActivity);
    }

    @NonNull
    @Override
    public ViewFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new ViewFilesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewFilesHolder holder, final int pos) {
        Log.d("logs", "getItemCount: " + mFileList.size());

        final int position          = holder.getAdapterPosition();
        final File file             = mFileList.get(position);

        holder.mFilename.setText(file.getName());
        holder.mFilesize.setText(FileUtils.getFormattedSize(file));
        holder.mFiledate.setText(getFormattedDate(file));
        holder.checkBox.setChecked(mSelectedFiles.contains(position));

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!mSelectedFiles.contains(position)) {
                        mSelectedFiles.add(position);
                    }
                } else
                    mSelectedFiles.remove(Integer.valueOf(position));
            }
        });

        holder.mRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.title)
                        .items(R.array.items)
                        .itemsIds(R.array.itemIds)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                performOperation(which, position, file);
                            }
                        })
                        .show();
                notifyDataSetChanged();
            }
        });
    }

    /**
     * Performs the required option on file
     * as per user selction
     *
     * @param index - index of operation performed
     * @param position - position of item clicked
     * @param file - file object clicked
     */
    private void performOperation(int index, int position, File file) {
        switch (index) {
            case 0: //Open
                mFileUtils.openFile(file.getPath());
                break;

            case 1: //delete
                deleteFile(file.getPath(), position);
                break;

            case 2: //rename
                renameFile(position);
                break;

            case 3: //Print
                mFileUtils.printFile(file);
                break;

            case 4: //Email
                mFileUtils.shareFile(file);
                break;

            case 5: //Details
                mPDFUtils.showDetails(file);
                break;

            case 6://Password Set
                mPDFEncryptionUtils.setPassword(file.getPath(), ViewFilesAdapter.this, mFileList);
                break;

            case 7://Password  Remove
                mPDFEncryptionUtils.removePassword(file.getPath(), ViewFilesAdapter.this, mFileList);
                break;
        }
    }

    /**
     * Checks all the PDFs list
     */
    public void checkAll() {
        mSelectedFiles.clear();
        for (int i = 0; i < mFileList.size(); i++)
            mSelectedFiles.add(i);
        notifyDataSetChanged();
    }

    /**
     * Unchecks every item in the item
     */
    public void unCheckAll() {
        mSelectedFiles.clear();
        notifyDataSetChanged();
    }

    /**
     * Returns path of selected files
     * @return paths of files
     */
    public ArrayList<String> getSelectedFilePath() {
        ArrayList<String> filePathList = new ArrayList<>();
        for (int position : mSelectedFiles) {
            filePathList.add(mFileList.get(position).getPath());
        }
        return filePathList;
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
     * @param pdfFiles array list containing path of files
     */
    public void setData(ArrayList<File> pdfFiles) {
        mFileList = pdfFiles;
        notifyDataSetChanged();
    }

    /**
     * Checks if any item is selected
     * @return tru, if atleast one item is checked
     */
    public boolean areItemsSelected() {
        return mSelectedFiles.size() > 0;
    }

    /**
     * Delete the file
     * @param name - name of the file
     * @param position - position of file in arraylist
     */
    private void deleteFile(String name, int position) {
        isFileDeleteUndoClicked = 0;
        final File fdelete = new File(name);
        final File fcreate = new File(name);
        if (fdelete.exists()) {
            mFileList.remove(position);
            notifyDataSetChanged();
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content)
                    , R.string.snackbar_file_deleted
                    , Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_undoAction, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFileList.add(fcreate);
                            notifyDataSetChanged();
                            isFileDeleteUndoClicked = 1;
                        }
                    }).addCallback(new Snackbar.Callback() {

                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if (isFileDeleteUndoClicked == 0) {
                                fdelete.delete();
                                mDatabaseHelper.insertRecord(fdelete.getAbsolutePath(),
                                        mActivity.getString(R.string.deleted));
                            }
                        }
                    }).show();
        }
        if (mFileList.size() == 0) {
            mEmptyStateChangeListener.setEmptyStateVisible();
        }
    }

    /**
     * iterate through filelist and remove all elements
     */
    public void deleteFiles() {

        for (int position : mSelectedFiles) {
            String fileName = mFileList.get(position).getPath();
            File fdelete = new File(fileName);
            mDatabaseHelper.insertRecord(fdelete.getAbsolutePath(), mActivity.getString(R.string.deleted));
            if (fdelete.exists() && !fdelete.delete())
                showSnackbar(R.string.snackbar_file_not_deleted);
        }

        ArrayList<File> newList = new ArrayList<>();
        for (int position = 0; position < mFileList.size(); position++)
            if (!mSelectedFiles.contains(position))
                newList.add(mFileList.get(position));

        mSelectedFiles.clear();
        if (newList.size() == 0)
            mEmptyStateChangeListener.setEmptyStateVisible();

        setData(newList);
    }

    /**
     * Opens file sharer for selected files
     */
    public void shareFiles() {
        ArrayList<File> files = new ArrayList<>();
        for (int position : mSelectedFiles) {
            files.add(mFileList.get(position));
        }
        mFileUtils.shareMultipleFiles(files);
    }

    /**
     * Renames the selected file
     * @param position - position of file to be renamed
     */
    private void renameFile(final int position) {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(mActivity.getString(R.string.example), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (input == null || input.toString().trim().isEmpty())
                            showSnackbar(R.string.snackbar_name_not_blank);
                        else {
                            File oldfile = mFileList.get(position);
                            String oldPath = oldfile.getPath();
                            String newfilename = oldPath.substring(0, oldPath.lastIndexOf('/'))
                                    + "/" + input.toString() + mActivity.getString(R.string.pdf_ext);
                            File newfile = new File(newfilename);
                            if (oldfile.renameTo(newfile)) {
                                showSnackbar(R.string.snackbar_file_renamed);
                                mFileList.set(position, newfile);
                                notifyDataSetChanged();
                                mDatabaseHelper.insertRecord(oldPath, mActivity.getString(R.string.renamed));
                            } else
                                showSnackbar(R.string.snackbar_file_not_renamed);
                        }
                    }
                }).show();
    }

    @Override
    public void updateDataset() {
        File folder = mDirectoryUtils.getOrCreatePdfDirectory();
        ArrayList<File> pdfsFromFolder = mDirectoryUtils.getPdfsFromPdfFolder(folder.listFiles());
        setData(pdfsFromFolder);
    }

    private void showSnackbar(int resID) {
        Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }

    public class ViewFilesHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fileRipple)
        MaterialRippleLayout mRipple;
        @BindView(R.id.fileName)
        TextView mFilename;
        @BindView(R.id.checkbox)
        CheckBox checkBox;
        @BindView(R.id.fileDate)
        TextView mFiledate;
        @BindView(R.id.fileSize)
        TextView mFilesize;

        ViewFilesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
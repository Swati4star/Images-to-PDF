package swati4star.createpdf.adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialripple.MaterialRippleLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.interfaces.DataSetChanged;
import swati4star.createpdf.interfaces.EmptyStateChangeListener;
import swati4star.createpdf.interfaces.ItemSelectedListener;
import swati4star.createpdf.model.PDFFile;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.DirectoryUtils;
import swati4star.createpdf.util.FileInfoUtils;
import swati4star.createpdf.util.FileSortUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.PDFEncryptionUtility;
import swati4star.createpdf.util.PDFRotationUtils;
import swati4star.createpdf.util.PDFUtils;
import swati4star.createpdf.util.PopulateList;
import swati4star.createpdf.util.StringUtils;
import swati4star.createpdf.util.WatermarkUtils;

import static swati4star.createpdf.util.Constants.SORTING_INDEX;
import static swati4star.createpdf.util.FileInfoUtils.getFormattedDate;

/**
 * Created by swati on 9/10/15.
 * <p>
 * An adapter to view the existing PDF files
 */

public class ViewFilesAdapter extends RecyclerView.Adapter<ViewFilesAdapter.ViewFilesHolder>
        implements DataSetChanged, EmptyStateChangeListener {

    private final Activity mActivity;
    private final EmptyStateChangeListener mEmptyStateChangeListener;
    private final ItemSelectedListener mItemSelectedListener;
    private final ArrayList<Integer> mSelectedFiles;
    private final FileUtils mFileUtils;
    private final PDFUtils mPDFUtils;
    private final PDFRotationUtils mPDFRotationUtils;
    private final WatermarkUtils mWatermarkUtils;
    private final PDFEncryptionUtility mPDFEncryptionUtils;
    private final DatabaseHelper mDatabaseHelper;
    private final SharedPreferences mSharedPreferences;

    private List<PDFFile> mFileList;

    /**
     * Returns adapter instance
     *
     * @param activity                 the activity calling this adapter
     * @param feedItems                list containing {@link PDFFile}
     * @param emptyStateChangeListener interface for empty state change
     * @param itemSelectedListener     interface for monitoring the status of file selection
     */
    public ViewFilesAdapter(Activity activity,
                            List<PDFFile> feedItems,
                            EmptyStateChangeListener emptyStateChangeListener,
                            ItemSelectedListener itemSelectedListener) {
        this.mActivity = activity;
        this.mEmptyStateChangeListener = emptyStateChangeListener;
        this.mItemSelectedListener = itemSelectedListener;
        this.mFileList = feedItems;
        mSelectedFiles = new ArrayList<>();
        mFileUtils = new FileUtils(activity);
        mPDFUtils = new PDFUtils(activity);
        mPDFRotationUtils = new PDFRotationUtils(activity);
        mPDFEncryptionUtils = new PDFEncryptionUtility(activity);
        mWatermarkUtils = new WatermarkUtils(activity);
        mDatabaseHelper = new DatabaseHelper(mActivity);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
    }

    @NonNull
    @Override
    public ViewFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new ViewFilesHolder(itemView, mItemSelectedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewFilesHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final PDFFile pdfFile = mFileList.get(position);

        holder.fileName.setText(pdfFile.getPdfFile().getName());
        holder.fileSize.setText(FileInfoUtils.getFormattedSize(pdfFile.getPdfFile()));
        holder.fileDate.setText(getFormattedDate(pdfFile.getPdfFile()));
        holder.checkBox.setChecked(mSelectedFiles.contains(position));
        holder.encryptionImage.setVisibility(pdfFile.isEncrypted() ? View.VISIBLE : View.GONE);
        holder.ripple.setOnClickListener(view -> {
            new MaterialDialog.Builder(mActivity)
                    .title(R.string.title)
                    .items(R.array.items)
                    .itemsIds(R.array.itemIds)
                    .itemsCallback((dialog, view1, which, text)
                            -> performOperation(which, position, pdfFile.getPdfFile()))
                    .show();
            notifyDataSetChanged();
        });
    }

    /**
     * Performs the required option on file
     * as per user selection
     *
     * @param index    - index of operation performed
     * @param position - position of item clicked
     * @param file     - file object clicked
     */
    private void performOperation(int index, int position, File file) {
        switch (index) {
            case 0: //Open
                mFileUtils.openFile(file.getPath(), FileUtils.FileType.e_PDF);
                break;

            case 1: //delete
                deleteFile(position);
                break;

            case 2: //rename
                onRenameFileClick(position);
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
                mPDFEncryptionUtils.setPassword(file.getPath(), ViewFilesAdapter.this);
                break;

            case 7://Password Remove
                mPDFEncryptionUtils.removePassword(file.getPath(), ViewFilesAdapter.this);
                break;

            case 8://Rotate Pages
                mPDFRotationUtils.rotatePages(file.getPath(), ViewFilesAdapter.this);
                break;

            case 9: // Add Watermark
                mWatermarkUtils.setWatermark(file.getPath(), ViewFilesAdapter.this);
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
     * unchecks every item in the item
     */
    public void unCheckAll() {
        mSelectedFiles.clear();
        notifyDataSetChanged();
        updateActionBarTitle();
    }

    /**
     * Sets the action bar title to app name when all files have been unchecked
     */
    private void updateActionBarTitle() {
        mActivity.setTitle(R.string.app_name);
    }

    /**
     * Returns path of selected files
     *
     * @return paths of files
     */
    public ArrayList<String> getSelectedFilePath() {
        ArrayList<String> filePathList = new ArrayList<>();
        for (int position : mSelectedFiles) {
            if (mFileList.size() > position)
                filePathList.add(mFileList.get(position).getPdfFile().getPath());
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
     *
     * @param pdfFiles list containing {@link PDFFile}
     */
    public void setData(List<PDFFile> pdfFiles) {
        mFileList = pdfFiles;
        notifyDataSetChanged();
    }

    /**
     * get {@link PDFUtils} bound with this adapter
     *
     * @return the Pdf Utils
     */
    public PDFUtils getPDFUtils() {
        return mPDFUtils;
    }

    /**
     * Checks if any item is selected
     *
     * @return tru, if at least one item is checked
     */
    public boolean areItemsSelected() {
        return mSelectedFiles.size() > 0;
    }

    /**
     * Deletes all selected files
     */
    public void deleteFile() {
        deleteFiles(mSelectedFiles);
    }

    /**
     * Delete single file
     */
    private void deleteFile(int position) {
        if (position < 0 || position >= mFileList.size())
            return;

        ArrayList<Integer> files = new ArrayList<>();
        files.add(position);
        deleteFiles(files);
    }

    /**
     * Shows an alert to delete files and
     * iterate through filelist and deletes
     * all files on positive response
     */
    private void deleteFiles(ArrayList<Integer> files ) {

        int messageAlert , messageSnackbar;
        if (files.size() > 1) {
            messageAlert = R.string.delete_alert_selected;
            messageSnackbar = R.string.snackbar_files_deleted;
        } else {
            messageAlert = R.string.delete_alert_singular;
            messageSnackbar = R.string.snackbar_file_deleted;
        }

        AlertDialog.Builder dialogAlert = new AlertDialog.Builder(mActivity)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .setTitle(messageAlert)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    ArrayList<String> filePath = new ArrayList<>();

                    for (int position : files) {
                        if (position >= mFileList.size())
                            continue;
                        filePath.add(mFileList.get(position).getPdfFile().getPath());
                        mFileList.remove(position);
                    }

                    mSelectedFiles.clear();
                    files.clear();
                    updateActionBarTitle();
                    notifyDataSetChanged();

                    if (mFileList.size() == 0)
                        mEmptyStateChangeListener.setEmptyStateVisible();

                    AtomicInteger undoClicked = new AtomicInteger();
                    StringUtils.getInstance().getSnackbarwithAction(mActivity, messageSnackbar)
                            .setAction(R.string.snackbar_undoAction, v -> {
                                if (mFileList.size() == 0) {
                                    mEmptyStateChangeListener.setEmptyStateInvisible();
                                }
                                updateDataset();
                                undoClicked.set(1);
                            }).addCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        if (undoClicked.get() == 0) {
                                            for (String path : filePath) {
                                                File fdelete = new File(path);
                                                mDatabaseHelper.insertRecord(fdelete.getAbsolutePath(),
                                                        mActivity.getString(R.string.deleted));
                                                if (fdelete.exists() && !fdelete.delete())
                                                    StringUtils.getInstance().showSnackbar(mActivity,
                                                            R.string.snackbar_file_not_deleted);
                                            }
                                        }
                                    }
                            }).show();
                });
        dialogAlert.create().show();
    }

    /**
     * Opens file sharer for selected files
     */
    public void shareFiles() {
        ArrayList<File> files = new ArrayList<>();
        for (int position : mSelectedFiles) {
            if (mFileList.size() > position)
                files.add(mFileList.get(position).getPdfFile());
        }
        mFileUtils.shareMultipleFiles(files);
    }

    /**
     * Renames the selected file
     *
     * @param position - position of file to be renamed
     */
    private void onRenameFileClick(final int position) {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(mActivity.getString(R.string.example), null, (dialog, input) -> {
                    if (input == null || input.toString().trim().isEmpty())
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                    else {
                        if (!mFileUtils.isFileExist(input + mActivity.getString(R.string.pdf_ext))) {
                            renameFile(position, input.toString());
                        } else {
                            MaterialDialog.Builder builder = DialogUtils.getInstance().createOverwriteDialog(mActivity);
                            builder.onPositive((dialog2, which) -> renameFile(position, input.toString()))
                                    .onNegative((dialog1, which) -> onRenameFileClick(position))
                                    .show();
                        }
                    }
                }).show();
    }

    private void renameFile(int position, String newName) {
        PDFFile pdfFile = mFileList.get(position);
        File oldfile = pdfFile.getPdfFile();
        String oldPath = oldfile.getPath();
        String newfilename = oldPath.substring(0, oldPath.lastIndexOf('/'))
                + "/" + newName + mActivity.getString(R.string.pdf_ext);
        File newfile = new File(newfilename);
        if (oldfile.renameTo(newfile)) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_file_renamed);
            pdfFile.setPdfFile(newfile);
            notifyDataSetChanged();
            mDatabaseHelper.insertRecord(newfilename, mActivity.getString(R.string.renamed));
        } else
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_file_not_renamed);
    }

    @Override
    public void updateDataset() {
        int index = mSharedPreferences.getInt(SORTING_INDEX, FileSortUtils.getInstance().NAME_INDEX);
        new PopulateList(this, this,
                new DirectoryUtils(mActivity), index, null).execute();
    }

    @Override
    public void setEmptyStateVisible() {

    }

    @Override
    public void setEmptyStateInvisible() {

    }

    @Override
    public void showNoPermissionsView() {

    }

    @Override
    public void hideNoPermissionsView() {

    }

    @Override
    public void filesPopulated() {

    }

    class ViewFilesHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fileRipple)
        MaterialRippleLayout ripple;
        @BindView(R.id.fileName)
        TextView fileName;
        @BindView(R.id.checkbox)
        CheckBox checkBox;
        @BindView(R.id.fileDate)
        TextView fileDate;
        @BindView(R.id.fileSize)
        TextView fileSize;
        @BindView(R.id.encryptionImage)
        ImageView encryptionImage;

        ViewFilesHolder(View itemView, ItemSelectedListener itemSelectedListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!mSelectedFiles.contains(getAdapterPosition())) {
                        mSelectedFiles.add(getAdapterPosition());
                        itemSelectedListener.isSelected(true, mSelectedFiles.size());
                    }
                } else
                    mSelectedFiles.remove(Integer.valueOf(getAdapterPosition()));
                itemSelectedListener.isSelected(false, mSelectedFiles.size());
            });
        }
    }
}
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
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.DataSetChanged;
import swati4star.createpdf.interfaces.EmptyStateChangeListener;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.PDFUtils;


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
    private final ArrayList<Integer> mDeleteNames;

    private final FileUtils mFileUtils;
    private final PDFUtils mPDFUtils;

    /**
     * Returns adapter instance
     *
     * @param activity the activity calling this adapter
     * @param feedItems array list containing path of files
     * @param emptyStateChangeListener interface for empty state change
     */
    public ViewFilesAdapter(Activity activity,
                            ArrayList<File> feedItems,
                            EmptyStateChangeListener emptyStateChangeListener) {
        this.mActivity = activity;
        this.mEmptyStateChangeListener = emptyStateChangeListener;
        this.mFileList = feedItems;
        mDeleteNames = new ArrayList<>();
        mFileUtils = new FileUtils(activity);
        mPDFUtils = new PDFUtils(activity);
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
        // Extract file name from path
        final int position = holder.getAdapterPosition();
        final String filePath = mFileList.get(position).getPath();
        final String[] fileName = filePath.split("/");
        File file = mFileList.get(position);
        final String lastModDate = FileUtils.getFormattedDate(file);
        final String fileSize = FileUtils.getFormattedSize(file);

        holder.mFilename.setText(fileName[fileName.length - 1]);
        holder.mFilesize.setText(fileSize);
        holder.mFiledate.setText(lastModDate);

        if (mDeleteNames.contains(position))
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mDeleteNames.add(position);
                } else {
                    mDeleteNames.remove(Integer.valueOf(position));
                }
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

                                switch (which) {
                                    case 0: //Open
                                        mFileUtils.openFile(filePath);
                                        break;

                                    case 1: //delete
                                        deleteFile(filePath, position);
                                        break;

                                    case 2: //rename
                                        renameFile(position);
                                        break;

                                    case 3: //Print
                                        mFileUtils.printFile(mFileList.get(position));
                                        break;

                                    case 4: //Email
                                        mFileUtils.shareFile(mFileList.get(position));
                                        break;

                                    case 5: //Details
                                        mPDFUtils.showDetails(fileName[fileName.length - 1],
                                                filePath, fileSize, lastModDate);
                                        break;
                                    case 6://Password Set
                                        mPDFUtils.setPassword(filePath, ViewFilesAdapter.this);
                                        break;
                                    case 7://Password  Remove
                                        mPDFUtils.removePassword(filePath, ViewFilesAdapter.this, mFileList);
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

    public boolean areItemsSelected() {
        return mDeleteNames.size() > 0;
    }

    private void deleteFile(String name, int position) {
        File fdelete = new File(name);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                        R.string.snackbar_file_deleted,
                        Snackbar.LENGTH_LONG).show();
                mFileList.remove(position);
                notifyDataSetChanged();
            } else {
                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                        R.string.snackbar_file_not_deleted,
                        Snackbar.LENGTH_LONG).show();
            }
        }
        if (mFileList.size() == 0) {
            mEmptyStateChangeListener.setEmptyStateVisible();
        }
    }

    // iterate through filelist and remove all elements
    public void deleteFiles() {
        for (int position : mDeleteNames) {
            String fileName = mFileList.get(position).getPath();
            File fdelete = new File(fileName);
            if (fdelete.exists() && !fdelete.delete()) {
                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                        R.string.snackbar_file_not_deleted,
                        Snackbar.LENGTH_LONG).show();
            }
        }

        ArrayList<File> newList = new ArrayList<>();
        for (int position = 0; position < mFileList.size(); position++)
            if (!mDeleteNames.contains(position))
                newList.add(mFileList.get(position));

        mDeleteNames.clear();
        if (newList.size() == 0) {
            mEmptyStateChangeListener.setEmptyStateVisible();
        }
        setData(newList);
    }

    public void shareFiles() {
        ArrayList<File> files = new ArrayList<>();
        for (int position: mDeleteNames) {
            files.add(mFileList.get(position));
        }
        mFileUtils.shareMultipleFiles(files);
    }

    private void renameFile(final int position) {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(mActivity.getString(R.string.example), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (input == null || input.toString().trim().isEmpty()) {
                            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                    R.string.snackbar_name_not_blank,
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            File oldfile = mFileList.get(position);
                            String oldPath =   mFileList.get(position).getPath();
                            int index = oldPath.lastIndexOf('/');
                            String newfilename = oldPath.substring(0, index) + "/" + input.toString() +
                                    mActivity.getString(R.string.pdf_ext);

                            File newfile = new File(newfilename);
                            if (oldfile.renameTo(newfile)) {
                                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                        R.string.snackbar_file_renamed,
                                        Snackbar.LENGTH_LONG).show();
                                mFileList.set(position, newfile);
                                notifyDataSetChanged();
                            } else {
                                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                        R.string.snackbar_file_not_renamed,
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                }).show();
    }

    @Override
    public void updateDataset() {
        File folder = mFileUtils.getOrCreatePdfDirectory();
        ArrayList<File> pdfsFromFolder = mFileUtils.getPdfsFromPdfFolder(folder.listFiles());
        setData(pdfsFromFolder);
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
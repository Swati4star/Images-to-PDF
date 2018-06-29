package swati4star.createpdf.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
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
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.util.FileUtils;


/**
 * Created by swati on 9/10/15.
 * <p>
 * An adapter to view the existing PDF files
 */


public class ViewFilesAdapter extends RecyclerView.Adapter<ViewFilesAdapter.ViewFilesHolder> {

    private final Context mContext;
    private final Activity mActivity;
    private final EmptyStateChangeListener mEmptyStateChangeListener;
    private ArrayList<File> mFileList;
    private FileUtils mFileUtils;
    private final ArrayList<Integer> mDeleteNames;

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
        this.mContext = activity;
        this.mEmptyStateChangeListener = emptyStateChangeListener;
        this.mFileList = feedItems;
        mDeleteNames = new ArrayList<>();
        mFileUtils = new FileUtils(activity);
    }

    @NonNull
    @Override
    public ViewFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new ViewFilesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewFilesHolder holder, final int position) {
        Log.d("logs", "getItemCount: " + mFileList.size());
        // Extract file name from path
        final String fileName = mFileList.get(position).getPath();
        final String[] name = fileName.split("/");
        File file = mFileList.get(position);
        final String lastModDate = mFileUtils.getFormattedDate(file);
        final String file_size = mFileUtils.getFormattedSize(file);

        holder.mFilename.setText(name[name.length - 1]);

        holder.mFilesize.setText(FileUtils.getFormattedSize(mFileList.get(position)));
        holder.mFiledate.setText(FileUtils.getFormattedDate(mFileList.get(position)));

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
                                        deleteFile(fileName, position);
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
                                        showDetails(name[name.length - 1], fileName, file_size, lastModDate);
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
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Uri uri = FileProvider.getUriForFile(mContext, "com.swati4star.shareFile", file);

        target.setDataAndType(uri,  mContext.getString(R.string.pdf_type));
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target, mContext.getString(R.string.open_file));
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                    R.string.snackbar_no_pdf_app,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    public boolean areItemsForDeleteSelected() {
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

    private void renameFile(final int position) {
        new MaterialDialog.Builder(mContext)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(mContext.getString(R.string.example), null, new MaterialDialog.InputCallback() {
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
                                    mContext.getString(R.string.pdf_ext);

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

    @SuppressLint("ResourceAsColor")
    private void showDetails(String name, String path, String size, String lastModDate) {
        TextView message = new TextView(mContext);
        TextView title = new TextView(mContext);
        message.setText("\n  File Name : " + name
                + "\n\n  Path : " + path
                + "\n\n  Size : " + size
                + " \n\n  Date Modified : " + lastModDate);
        message.setTextIsSelectable(true);
        title.setText(R.string.details);
        title.setPadding(20, 10, 10, 10);
        title.setTextSize(30);
        title.setTextColor(R.color.black_54);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.create();
        builder.setView(message);
        builder.setCustomTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    public String string(@StringRes int resId) {
        return mContext.getString(resId);
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

    public interface EmptyStateChangeListener {
        void setEmptyStateVisible();
        void setEmptyStateGone();
    }
}
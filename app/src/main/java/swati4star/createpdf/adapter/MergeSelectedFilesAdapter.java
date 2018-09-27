package swati4star.createpdf.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.util.FileUtils;

public class MergeSelectedFilesAdapter extends
        RecyclerView.Adapter<MergeSelectedFilesAdapter.MergeSelectedFilesHolder> {

    private final ArrayList<String> mFilePaths;
    private final Activity mContext;
    private final FileUtils mFileUtils;
    private final OnFileItemClickListener mOnClickListener;

    public MergeSelectedFilesAdapter(Activity mContext, ArrayList<String> mFilePaths,
                                     OnFileItemClickListener mOnClickListener) {
        this.mContext = mContext;
        this.mFilePaths = mFilePaths;
        mFileUtils = new FileUtils(mContext);
        this.mOnClickListener = mOnClickListener;
    }

    @NonNull
    @Override
    public MergeSelectedFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_merge_selected_files, parent, false);
        return new MergeSelectedFilesAdapter.MergeSelectedFilesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MergeSelectedFilesHolder holder, int position) {
        holder.mFileName.setText(mFileUtils.getFileName(mFilePaths.get(position)));
    }

    @Override
    public int getItemCount() {
        return mFilePaths == null ? 0 : mFilePaths.size();
    }

    public class MergeSelectedFilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.fileName)
        TextView mFileName;
        @BindView(R.id.view_file)
        ImageView mViewFile;
        @BindView(R.id.remove)
        ImageView mRemove;
        @BindView(R.id.up_file)
        ImageView mUp;
        @BindView(R.id.down_file)
        ImageView mDown;

        MergeSelectedFilesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mViewFile.setOnClickListener(this);
            mRemove.setOnClickListener(this);
            mUp.setOnClickListener(this);
            mDown.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.view_file) {
                mOnClickListener.viewFile(mFilePaths.get(getAdapterPosition()));
            } else if (view.getId() == R.id.up_file) {
                if (getAdapterPosition() != 0) {
                    mOnClickListener.moveUp(getAdapterPosition());
                }
            } else if (view.getId() == R.id.down_file) {
                if (mFilePaths.size() != getAdapterPosition() + 1) {
                    mOnClickListener.moveDown(getAdapterPosition());
                }
            } else {
                mOnClickListener.removeFile(mFilePaths.get(getAdapterPosition()));
            }
        }
    }

    public interface OnFileItemClickListener {
        void viewFile(String path);
        void removeFile(String path);
        void moveUp(int position);
        void moveDown(int position);
    }
}

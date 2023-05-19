package swati4star.createpdf.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.util.FileUtils;

public class FilesListAdapter extends RecyclerView.Adapter<FilesListAdapter.ViewMergeFilesHolder> {

    private final ArrayList<String> mFilePaths;
    private final Activity mContext;
    private final OnFileItemClickedListener mOnClickListener;

    public FilesListAdapter(Activity mContext, ArrayList<String> mFilePaths,
                            OnFileItemClickedListener mOnClickListener) {
        this.mContext = mContext;
        this.mFilePaths = mFilePaths;
        this.mOnClickListener = mOnClickListener;
    }

    @NonNull
    @Override
    public ViewMergeFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_merge_files, parent, false);
        return new FilesListAdapter.ViewMergeFilesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewMergeFilesHolder holder, int position) {
        holder.mFileName.setText(FileUtils.getFileName(mFilePaths.get(position)));
        holder.mEncryptionImage.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mFilePaths == null ? 0 : mFilePaths.size();
    }

    public interface OnFileItemClickedListener {
        void onFileItemClick(String path);
    }

    public class ViewMergeFilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.fileName)
        TextView mFileName;
        @BindView(R.id.encryptionImage)
        ImageView mEncryptionImage;

        ViewMergeFilesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mFileName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onFileItemClick(mFilePaths.get(getAdapterPosition()));
        }
    }
}

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

import swati4star.createpdf.databinding.ItemMergeFilesBinding;
import swati4star.createpdf.util.FileUtils;

public class FilesListAdapter extends RecyclerView.Adapter<FilesListAdapter.ViewMergeFilesHolder> {

    private final ArrayList<String> mFilePaths;
    private final OnFileItemClickedListener mOnClickListener;

    public FilesListAdapter(Activity mContext, ArrayList<String> mFilePaths,
                            OnFileItemClickedListener mOnClickListener) {
        this.mFilePaths = mFilePaths;
        this.mOnClickListener = mOnClickListener;
    }

    @NonNull
    @Override
    public ViewMergeFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMergeFilesBinding binding = ItemMergeFilesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FilesListAdapter.ViewMergeFilesHolder(binding);
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
        TextView mFileName;
        ImageView mEncryptionImage;

        ViewMergeFilesHolder(ItemMergeFilesBinding binding) {
            super(binding.getRoot());
            binding.fileName.setOnClickListener(this);
            mFileName = binding.fileName;
            mEncryptionImage = binding.encryptionImage;
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onFileItemClick(mFilePaths.get(getAdapterPosition()));
        }
    }
}

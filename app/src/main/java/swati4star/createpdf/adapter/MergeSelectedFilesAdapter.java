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

import swati4star.createpdf.R;
import swati4star.createpdf.databinding.ItemMergeSelectedFilesBinding;
import swati4star.createpdf.util.FileUtils;

public class MergeSelectedFilesAdapter extends
        RecyclerView.Adapter<MergeSelectedFilesAdapter.MergeSelectedFilesHolder> {

    private final ArrayList<String> mFilePaths;
    private final OnFileItemClickListener mOnClickListener;

    public MergeSelectedFilesAdapter(Activity mContext, ArrayList<String> mFilePaths,
                                     OnFileItemClickListener mOnClickListener) {
        this.mFilePaths = mFilePaths;
        this.mOnClickListener = mOnClickListener;
    }

    @NonNull
    @Override
    public MergeSelectedFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMergeSelectedFilesBinding binding = ItemMergeSelectedFilesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MergeSelectedFilesAdapter.MergeSelectedFilesHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MergeSelectedFilesHolder holder, int position) {
        holder.mFileName.setText(FileUtils.getFileName(mFilePaths.get(position)));
    }

    @Override
    public int getItemCount() {
        return mFilePaths == null ? 0 : mFilePaths.size();
    }

    public interface OnFileItemClickListener {
        void viewFile(String path);

        void removeFile(String path);

        void moveUp(int position);

        void moveDown(int position);
    }

    public class MergeSelectedFilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mFileName;
        ImageView mViewFile;
        ImageView mRemove;
        ImageView mUp;
        ImageView mDown;

        MergeSelectedFilesHolder(ItemMergeSelectedFilesBinding binding) {
            super(binding.getRoot());
            binding.viewFile.setOnClickListener(this);
            binding.remove.setOnClickListener(this);
            binding.upFile.setOnClickListener(this);
            binding.downFile.setOnClickListener(this);

            mFileName = binding.fileName;
            mViewFile = binding.viewFile;
            mUp = binding.upFile;
            mDown = binding.downFile;
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
}

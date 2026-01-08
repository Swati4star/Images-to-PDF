package swati4star.createpdf.adapter;

import static swati4star.createpdf.util.FileUtils.getFileName;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import swati4star.createpdf.databinding.ItemImageExtractedBinding;
import swati4star.createpdf.util.ImageUtils;

public class ExtractImagesAdapter extends RecyclerView.Adapter<ExtractImagesAdapter.ViewMergeFilesHolder> {

    private final ArrayList<String> mFilePaths;
    private final OnFileItemClickedListener mOnClickListener;

    public ExtractImagesAdapter(Activity mContext, ArrayList<String> mFilePaths,
                                OnFileItemClickedListener mOnClickListener) {
        this.mFilePaths = mFilePaths;
        this.mOnClickListener = mOnClickListener;
    }

    @NonNull
    @Override
    public ViewMergeFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageExtractedBinding binding = ItemImageExtractedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ExtractImagesAdapter.ViewMergeFilesHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewMergeFilesHolder holder, int position) {
        holder.mFileName.setText(getFileName(mFilePaths.get(position)));
        Bitmap bitmap = ImageUtils.getInstance().getRoundBitmapFromPath(mFilePaths.get(position));
        if (bitmap != null)
            holder.mImagePreview.setImageBitmap(bitmap);
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
        ImageView mImagePreview;

        ViewMergeFilesHolder(ItemImageExtractedBinding binding) {
            super(binding.getRoot());
            binding.fileName.setOnClickListener(this);
            mFileName = binding.fileName;
            mImagePreview = binding.imagePreview;
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() < mFilePaths.size())
                mOnClickListener.onFileItemClick(mFilePaths.get(getAdapterPosition()));
        }
    }
}

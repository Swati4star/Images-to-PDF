package swati4star.createpdf.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import swati4star.createpdf.databinding.ItemMergeFilesBinding;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.PDFUtils;

public class MergeFilesAdapter extends RecyclerView.Adapter<MergeFilesAdapter.ViewMergeFilesHolder> {

    private final ArrayList<String> mFilePaths;
    private final OnClickListener mOnClickListener;
    private final PDFUtils mPDFUtils;
    private final boolean mIsMergeFragment;

    public MergeFilesAdapter(Activity mContext, ArrayList<String> mFilePaths,
                             boolean mIsMergeFragment, OnClickListener mOnClickListener) {
        this.mFilePaths = mFilePaths;
        this.mOnClickListener = mOnClickListener;
        mPDFUtils = new PDFUtils(mContext);
        this.mIsMergeFragment = mIsMergeFragment;
    }

    @NonNull
    @Override
    public ViewMergeFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMergeFilesBinding binding = ItemMergeFilesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MergeFilesAdapter.ViewMergeFilesHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewMergeFilesHolder holder, int position) {
        boolean isEncrypted = mPDFUtils.isPDFEncrypted(mFilePaths.get(position));
        holder.mFileName.setText(FileUtils.getFileName(mFilePaths.get(position)));
        holder.mEncryptionImage.setVisibility(isEncrypted ? View.VISIBLE : View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return mFilePaths == null ? 0 : mFilePaths.size();
    }

    public interface OnClickListener {
        void onItemClick(String path);
    }

    public class ViewMergeFilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mFileName;
        ImageView mEncryptionImage;
        AppCompatCheckBox mCheckbox;

        ViewMergeFilesHolder(ItemMergeFilesBinding binding) {
            super(binding.getRoot());
            binding.fileName.setOnClickListener(this);
            if (mIsMergeFragment) {
                binding.itemMergeCheckbox.setVisibility(View.VISIBLE);
            } else {
                binding.itemMergeCheckbox.setVisibility(View.GONE);
            }
            mFileName = binding.fileName;
            mEncryptionImage = binding.encryptionImage;
            mCheckbox = binding.itemMergeCheckbox;
            binding.itemMergeCheckbox.setOnClickListener(v -> {
                mOnClickListener.onItemClick(mFilePaths.get(getAdapterPosition()));
            });
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() >= mFilePaths.size())
                return;

            if (mIsMergeFragment) mCheckbox.toggle();
            mOnClickListener.onItemClick(mFilePaths.get(getAdapterPosition()));
        }
    }
}

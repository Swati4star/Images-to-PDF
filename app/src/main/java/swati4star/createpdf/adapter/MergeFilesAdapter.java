package swati4star.createpdf.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.PDFUtils;

public class MergeFilesAdapter extends RecyclerView.Adapter<MergeFilesAdapter.ViewMergeFilesHolder> {

    private final ArrayList<String> mFilePaths;
    private final Activity mContext;
    private final FileUtils mFileUtils;
    private final OnClickListener mOnClickListener;
    private final PDFUtils mPDFUtils;
    private final boolean mIsMergeFragment;

    public MergeFilesAdapter(Activity mContext, ArrayList<String> mFilePaths,
                             boolean mIsMergeFragment, OnClickListener mOnClickListener) {
        this.mContext = mContext;
        this.mFilePaths = mFilePaths;
        mFileUtils = new FileUtils(mContext);
        this.mOnClickListener = mOnClickListener;
        mPDFUtils = new PDFUtils(mContext);
        this.mIsMergeFragment = mIsMergeFragment;
    }

    @NonNull
    @Override
    public ViewMergeFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_merge_files, parent, false);
        return new MergeFilesAdapter.ViewMergeFilesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewMergeFilesHolder holder, int position) {
        boolean isEncrypted = mPDFUtils.isPDFEncrypted(mFilePaths.get(position));
        holder.mFileName.setText(mFileUtils.getFileName(mFilePaths.get(position)));
        holder.mEncryptionImage.setVisibility(isEncrypted ? View.VISIBLE : View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return mFilePaths == null ? 0 : mFilePaths.size();
    }

    public class ViewMergeFilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.fileName)
        TextView mFileName;
        @BindView(R.id.encryptionImage)
        ImageView mEncryptionImage;
        @BindView(R.id.itemMerge_checkbox)
        AppCompatCheckBox mCheckbox;

        ViewMergeFilesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mFileName.setOnClickListener(this);
            if (mIsMergeFragment) mCheckbox.setVisibility(View.VISIBLE);
            else mCheckbox.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            if (mIsMergeFragment) mCheckbox.toggle();
            mOnClickListener.onItemClick(mFilePaths.get(getAdapterPosition()));
        }

        @OnClick(R.id.itemMerge_checkbox)
        public void onCheckboxClick() {
            mOnClickListener.onItemClick(mFilePaths.get(getAdapterPosition()));
        }

    }

    public interface OnClickListener {
        void onItemClick(String path);
    }
}

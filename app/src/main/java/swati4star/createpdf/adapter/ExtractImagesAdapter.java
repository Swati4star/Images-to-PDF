package swati4star.createpdf.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
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

import static swati4star.createpdf.util.FileUtils.getFileName;
import static swati4star.createpdf.util.ImageUtils.getRoundBitmapFromPath;

public class ExtractImagesAdapter extends RecyclerView.Adapter<ExtractImagesAdapter.ViewMergeFilesHolder> {

    private final ArrayList<String> mFilePaths;
    private final Activity mContext;
    private final OnFileItemClickedListener mOnClickListener;

    public ExtractImagesAdapter(Activity mContext, ArrayList<String> mFilePaths,
                                OnFileItemClickedListener mOnClickListener) {
        this.mContext = mContext;
        this.mFilePaths = mFilePaths;
        this.mOnClickListener = mOnClickListener;
    }

    @NonNull
    @Override
    public ViewMergeFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_extracted, parent, false);
        return new ExtractImagesAdapter.ViewMergeFilesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewMergeFilesHolder holder, int position) {
        holder.mFileName.setText(getFileName(mFilePaths.get(position)));
        Bitmap bitmap = getRoundBitmapFromPath(mFilePaths.get(position));
        if (bitmap != null)
            holder.mImagePreview.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return mFilePaths == null ? 0 : mFilePaths.size();
    }

    public class ViewMergeFilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.fileName)
        TextView mFileName;
        @BindView(R.id.imagePreview)
        ImageView mImagePreview;

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

    public interface OnFileItemClickedListener {
        void onFileItemClick(String path);
    }
}

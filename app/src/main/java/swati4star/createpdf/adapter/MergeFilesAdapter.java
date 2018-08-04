package swati4star.createpdf.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.util.FileUtils;

public class MergeFilesAdapter extends RecyclerView.Adapter<MergeFilesAdapter.ViewMergeFilesHolder> {

    private ArrayList<String> mFilePaths;
    private Activity mContext;
    private FileUtils mFileUtils;
    private OnClickListener mOnClickListener;

    public MergeFilesAdapter(Activity mContext, ArrayList<String> mFilePaths, OnClickListener mOnClickListener) {
        this.mContext = mContext;
        this.mFilePaths = mFilePaths;
        mFileUtils = new FileUtils(mContext);
        this.mOnClickListener = mOnClickListener;
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
        Log.v("adding ", String.valueOf(position));
        holder.mFileName.setText(mFileUtils.getFileName(mFilePaths.get(position)));
    }

    @Override
    public int getItemCount() {
        return mFilePaths == null ? 0 : mFilePaths.size();
    }

    public class ViewMergeFilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mFileName;

        ViewMergeFilesHolder(View itemView) {
            super(itemView);
            mFileName = itemView.findViewById(R.id.fileName);
            mFileName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClick(mFilePaths.get(getAdapterPosition()));
        }
    }

    public interface OnClickListener {
        void onItemClick(String path);
    }
}

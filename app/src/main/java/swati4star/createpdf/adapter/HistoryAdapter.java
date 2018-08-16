package swati4star.createpdf.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.database.History;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHistoryHolder> {

    private final List<History> mHistoryList;
    private final Activity mActivity;
    private final OnClickListener mOnClickListener;

    public HistoryAdapter(Activity mActivity, List<History> mHistoryList, OnClickListener mOnClickListener) {
        this.mHistoryList = mHistoryList;
        this.mActivity = mActivity;
        this.mOnClickListener = mOnClickListener;
    }

    @NonNull
    @Override
    public ViewHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_history, parent, false);
        return new HistoryAdapter.ViewHistoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHistoryHolder holder, int position) {

        final String filePath = mHistoryList.get(position).getFilePath();
        File file = new File(filePath);
        final String operationDate = mHistoryList.get(position).getDate();
        String[] formatdate = operationDate.split(" ");
        String date;
        if (formatdate.length >= 3) {
            String time = formatdate[3];
            String[] formattime =  time.split(":");
            date = formattime[0] + ":" + formattime[1];
            date = formatdate[0] + ", " + formatdate[1] + " " + formatdate[2] + " at " + date;
        } else {
            date = operationDate;
        }

        final String operationType = mHistoryList.get(position).getOperationType();
        final String fileName = file.getName();

        holder.mFilename.setText(fileName);
        holder.mOperationDate.setText(date);
        holder.mOperationType.setText(operationType);
        holder.mOperationImage.setImageResource(getIconFromString(operationType));
    }

    public void deleteHistory() {
        mHistoryList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mHistoryList == null ? 0 : mHistoryList.size();
    }

    public class ViewHistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.fileName)
        TextView mFilename;
        @BindView(R.id.operationDate)
        TextView mOperationDate;
        @BindView(R.id.operationType)
        TextView mOperationType;
        @BindView(R.id.operationImage)
        ImageView mOperationImage;

        ViewHistoryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClick(mHistoryList.get(getAdapterPosition()).getFilePath());
        }
    }

    public interface OnClickListener {
        void onItemClick(String path);
    }

    private int getIconFromString(String operation) {
        if (operation.equalsIgnoreCase(mActivity.getString(R.string.printed)))
            return R.drawable.ic_print_black_24dp;
        if (operation.equalsIgnoreCase(mActivity.getString(R.string.created)))
            return R.drawable.ic_insert_drive_file_black_24dp;
        if (operation.equalsIgnoreCase(mActivity.getString(R.string.deleted)))
            return R.drawable.baseline_delete_24;
        if (operation.equalsIgnoreCase(mActivity.getString(R.string.renamed)))
            return R.drawable.ic_create_black_24dp;
        return R.drawable.ic_insert_drive_file_black_24dp;
    }
}
package swati4star.createpdf.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.database.History;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHistoryHolder> {

    private final List<History> mHistoryList;
    private final Activity mActivity;

    public HistoryAdapter(Activity mActivity, List<History> mHistoryList) {
        this.mHistoryList = mHistoryList;
        this.mActivity = mActivity;
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
        final String operationDate = mHistoryList.get(position).getDate();
        final String operationType = mHistoryList.get(position).getOperationType();
        final String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);

        holder.mFilename.setText(fileName);
        holder.mOperationDate.setText(operationDate);
        holder.mOperationType.setText(operationType);
    }

    public void deleteHistory() {
        mHistoryList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mHistoryList == null ? 0 : mHistoryList.size();
    }

    public class ViewHistoryHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fileName)
        TextView mFilename;
        @BindView(R.id.operationDate)
        TextView mOperationDate;
        @BindView(R.id.operationType)
        TextView mOperationType;

        ViewHistoryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
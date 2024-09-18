package swati4star.createpdf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.OnItemClickListener;
import swati4star.createpdf.model.BrushItem;

public class BrushItemAdapter extends RecyclerView.Adapter<BrushItemAdapter.BrushItemViewHolder> {

    private final Context mContext;
    private final OnItemClickListener mOnItemClickListener;
    private final List<BrushItem> mBrushItems;

    public BrushItemAdapter(Context context,
                            OnItemClickListener onItemClickListener,
                            List<BrushItem> brushItems) {
        mBrushItems = brushItems;
        mOnItemClickListener = onItemClickListener;
        mContext = context;
    }

    @NonNull
    @Override
    public BrushItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.brush_color_item, parent, false);
        return new BrushItemAdapter.BrushItemViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull BrushItemViewHolder holder, int position) {
        int color = mBrushItems.get(position).getColor();
        if (position == mBrushItems.size() - 1)
            holder.doodleButton.setBackground(mContext.getResources().getDrawable(color));
        else
            holder.doodleButton.setBackgroundColor(mContext.getResources().getColor(color));
    }

    @Override
    public int getItemCount() {
        return mBrushItems.size();
    }

    public class BrushItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.doodle_color)
        Button doodleButton;

        BrushItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

            // 假设存在一个函数检查是否有PDF文件创建
            boolean isPdfCreated = checkIfPdfCreated();

            Button deleteButton = findViewById(R.id.btn_delete);

            if (isPdfCreated) {
                deleteButton.setEnabled(true); // 启用删除按钮
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 删除PDF的代码逻辑
                    }
                });
            } else {
                deleteButton.setEnabled(false); // 禁用删除按钮
            }

        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}

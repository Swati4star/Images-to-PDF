package swati4star.createpdf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import swati4star.createpdf.databinding.BrushColorItemBinding;
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
        BrushColorItemBinding binding = BrushColorItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BrushItemAdapter.BrushItemViewHolder(binding);
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

        Button doodleButton;

        BrushItemViewHolder(BrushColorItemBinding binding) {
            super(binding.getRoot());
            binding.getRoot().setOnClickListener(this);
            doodleButton = binding.doodleColor;
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}

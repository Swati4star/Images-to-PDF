package swati4star.createpdf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.model.PreviewImageOptionItem;

public class PreviewImageOptionsAdapter extends RecyclerView.Adapter<PreviewImageOptionsAdapter.ViewHolder> {
    private final ArrayList<PreviewImageOptionItem> mOptions;
    private final Context mContext;
    private final OnItemClickListener mOnItemClickListener;

    public PreviewImageOptionsAdapter(OnItemClickListener onItemClickListener,
                                      ArrayList<PreviewImageOptionItem> optionItems, Context context) {
        mOnItemClickListener = onItemClickListener;
        mOptions = optionItems;
        mContext = context;
    }

    @NonNull
    @Override
    public PreviewImageOptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview_image_options,
                parent, false);
        return new PreviewImageOptionsAdapter.ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull PreviewImageOptionsAdapter.ViewHolder holder, int position) {
        int imageId = mOptions.get(position).getOptionImageId();
        holder.imageView.setImageDrawable(mContext.getDrawable(imageId));
        holder.textView.setText(mOptions.get(position).getOptionName());
    }

    @Override
    public int getItemCount() {
        return mOptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageView;
        final TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.option_image);
            textView = itemView.findViewById(R.id.option_name);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}

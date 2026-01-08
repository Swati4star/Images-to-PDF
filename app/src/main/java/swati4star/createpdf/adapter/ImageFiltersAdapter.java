package swati4star.createpdf.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import swati4star.createpdf.databinding.ListItemFilterBinding;
import swati4star.createpdf.interfaces.OnFilterItemClickedListener;
import swati4star.createpdf.model.FilterItem;
import swati4star.createpdf.util.ImageUtils;

public class ImageFiltersAdapter extends RecyclerView.Adapter<ImageFiltersAdapter.ViewHolder> {

    private final ArrayList<FilterItem> mFilterItem;
    private final OnFilterItemClickedListener mOnFilterItemClickedListener;
    private final Context mContext;

    public ImageFiltersAdapter(ArrayList<FilterItem> filterItems, Context context,
                               OnFilterItemClickedListener listener) {
        mFilterItem = filterItems;
        mContext = context;
        mOnFilterItemClickedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemFilterBinding binding = ListItemFilterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int imageId = mFilterItem.get(position).getImageId();
        Bitmap roundBitmap = BitmapFactory.decodeResource(mContext.getResources(), imageId);
        if (roundBitmap != null) {
            holder.img.setImageBitmap(ImageUtils.getInstance().getRoundBitmap(roundBitmap));
        } else
            holder.img.setImageResource(imageId);
        holder.name.setText(mFilterItem.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mFilterItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;
        TextView name;

        ViewHolder(ListItemFilterBinding binding) {
            super(binding.getRoot());
            binding.getRoot().setOnClickListener(this);
            img = binding.filterPreview;
            name = binding.filterName;
        }

        @Override
        public void onClick(View view) {
            mOnFilterItemClickedListener.onItemClick(view, getAdapterPosition());
        }
    }
}

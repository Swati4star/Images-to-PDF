package swati4star.createpdf.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.OnFilterItemClickedListener;
import swati4star.createpdf.model.FilterItem;
import swati4star.createpdf.util.ImageUtils;

public class ImageFiltersAdapter extends RecyclerView.Adapter<ImageFiltersAdapter.ViewHolder> {

    private final ArrayList<FilterItem> mFilterItem;
    private final OnFilterItemClickedListener mOnFilterItemClickedListener;
    private final Context  mContext;

    public ImageFiltersAdapter(ArrayList<FilterItem> filterItems, Context context,
                               OnFilterItemClickedListener listener) {
        mFilterItem = filterItems;
        mContext = context;
        mOnFilterItemClickedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int imageid = mFilterItem.get(position).getImageId();

        Bitmap roundBitmap = BitmapFactory.decodeResource(mContext.getResources(), imageid);
        if (roundBitmap != null) {
            int width = roundBitmap.getWidth(), height = roundBitmap.getHeight();
            int radius = width > height ? height : width; // set the smallest edge as radius.
            roundBitmap = ImageUtils.getRoundBitmap(roundBitmap, radius);
            holder.img.setImageBitmap(roundBitmap);
        } else
            holder.img.setImageResource(imageid);
        holder.Filter_name.setText(mFilterItem.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mFilterItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView img;
        final TextView Filter_name;

        ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.filter_preview);
            Filter_name = itemView.findViewById(R.id.filter_Name);
            ButterKnife.bind(this, itemView);
            img.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int mAdapterp = getAdapterPosition();
            mOnFilterItemClickedListener.onItemClick(view, mAdapterp);
        }
    }
}

package swati4star.createpdf.adapter;

import android.content.Context;
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

public class ImageFiltersAdapter extends RecyclerView.Adapter<ImageFiltersAdapter.ViewHolder> {

    private ArrayList<FilterItem> mFilterItem;
    private Context mContext;
    private final OnFilterItemClickedListener mOnFilterItemClickedListener;
    private int mAdapterp;

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
        holder.img.setImageResource(imageid);
        holder.Filter_name.setText(mFilterItem.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mFilterItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;
        TextView Filter_name;

        ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.filter_preview);
            Filter_name = itemView.findViewById(R.id.filter_Name);
            ButterKnife.bind(this, itemView);
            img.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mAdapterp = getAdapterPosition();
            mOnFilterItemClickedListener.onItemClick(view, mAdapterp);
        }
    }
}

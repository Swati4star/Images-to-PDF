package swati4star.createpdf.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.OnFilterItemClickedListener;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mNames = new ArrayList<>();
    private Context mContext;
    private final OnFilterItemClickedListener mOnFilterItemClickedListener;
    private int[] mImages;
    private int mAdapterp;

    public RecyclerViewAdapter(ArrayList<String> names, Context context, int[] images,
                               OnFilterItemClickedListener listener) {
        mNames = names;
        mContext = context;
        this.mImages = images;
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
        int imageid = mImages[position];
        holder.img.setImageResource(imageid);
        holder.Filter_name.setText(mNames.get(position));
    }

    @Override
    public int getItemCount() {
        return mImages.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;
        RelativeLayout parentLayout;
        TextView Filter_name;

        public ViewHolder(View itemView) {
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

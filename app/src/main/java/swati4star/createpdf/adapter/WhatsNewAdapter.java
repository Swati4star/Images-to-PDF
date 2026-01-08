package swati4star.createpdf.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import swati4star.createpdf.databinding.ItemWhatsNewBinding;
import swati4star.createpdf.model.WhatsNew;

public class WhatsNewAdapter extends RecyclerView.Adapter<WhatsNewAdapter.WhatsNewViewHolder> {

    private final Context mContext;
    private final List<WhatsNew> mWhatsNewsList;

    public WhatsNewAdapter(Context context, ArrayList<WhatsNew> mWhatsNewsList) {
        this.mContext = context;
        this.mWhatsNewsList = mWhatsNewsList;
    }

    @NonNull
    @Override
    public WhatsNewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWhatsNewBinding binding = ItemWhatsNewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WhatsNewAdapter.WhatsNewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WhatsNewViewHolder holder, int position) {
        holder.tvDescription.setText(mWhatsNewsList.get(position).getDescription());
        holder.tvHeading.setText(mWhatsNewsList.get(position).getTitle());
        if (!mWhatsNewsList.get(position).getIcon().equals("")) {
            Resources resources = mContext.getResources();
            final int resourceId = resources.getIdentifier(mWhatsNewsList.get(position).getIcon(),
                    "drawable", mContext.getPackageName());
            holder.icon.setBackgroundResource(resourceId);
        }
    }

    @Override
    public int getItemCount() {
        return mWhatsNewsList.size();
    }

    class WhatsNewViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeading;
        TextView tvDescription;
        ImageView icon;

        WhatsNewViewHolder(ItemWhatsNewBinding binding) {
            super(binding.getRoot());
            tvHeading = binding.title;
            tvDescription = binding.description;
            icon = binding.icon;
        }
    }
}

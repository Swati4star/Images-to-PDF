package swati4star.createpdf.adapter;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
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
    public WhatsNewViewHolder onCreateViewHolder(@NonNull ViewGroup mParent, int viewType) {
        View mView = LayoutInflater.from(mParent.getContext())
                .inflate(R.layout.item_whats_new, mParent, false);
        return new WhatsNewAdapter.WhatsNewViewHolder(mView);
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

        @BindView(R.id.title)
        TextView tvHeading;
        @BindView(R.id.description)
        TextView tvDescription;
        @BindView(R.id.icon)
        ImageView icon;

        WhatsNewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}

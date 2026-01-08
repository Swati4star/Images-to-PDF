package swati4star.createpdf.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Map;

import swati4star.createpdf.R;
import swati4star.createpdf.databinding.ItemViewEnhancementOptionBinding;

public class RecentListAdapter extends RecyclerView.Adapter<RecentListAdapter.RecentItemViewHolder> {

    private final View.OnClickListener mOnClickListener;
    private List<String> mKeys;
    private List<Map<String, String>> mValues;

    public RecentListAdapter(View.OnClickListener listener) {
        this.mOnClickListener = listener;
    }


    /**
     * Updates the recent list
     *
     * @param keys       - list of all the feature viewId
     * @param recentList - list of the features
     */
    public void updateList(List<String> keys, List<Map<String, String>> recentList) {
        this.mKeys = keys;
        this.mValues = recentList;
    }

    @NonNull
    @Override
    public RecentItemViewHolder onCreateViewHolder(
            @NonNull final ViewGroup parent, final int i) {
        ItemViewEnhancementOptionBinding binding = ItemViewEnhancementOptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);;
        return new RecentListAdapter.RecentItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull final RecentItemViewHolder recentItemViewHolder, final int i) {

        recentItemViewHolder.name.setText(recentItemViewHolder.itemView.getContext().getString(
                Integer.parseInt(mValues.get(i).keySet().iterator().next())
        ));
        recentItemViewHolder.icon.setImageDrawable(ContextCompat.getDrawable(recentItemViewHolder.itemView.getContext(),
                Integer.parseInt(mValues.get(i).values().iterator().next())));

        recentItemViewHolder.itemView.setId(Integer.parseInt(mKeys.get(i)));
        recentItemViewHolder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class RecentItemViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;

        MaterialCardView cardView;

        private RecentItemViewHolder(@NonNull final ItemViewEnhancementOptionBinding binding) {
            super(binding.getRoot());
            cardView = itemView.findViewById(R.id.container_card_view);
            icon = binding.optionImage;
            name = binding.optionName;
        }
    }
}

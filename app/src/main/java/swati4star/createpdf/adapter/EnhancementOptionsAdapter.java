package swati4star.createpdf.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import swati4star.createpdf.R;
import swati4star.createpdf.databinding.ItemViewEnhancementOptionBinding;
import swati4star.createpdf.interfaces.OnItemClickListener;
import swati4star.createpdf.model.EnhancementOptionsEntity;

public class EnhancementOptionsAdapter
        extends RecyclerView.Adapter<EnhancementOptionsAdapter.EnhancementOptionsViewHolder> {

    private final OnItemClickListener mOnItemClickListener;
    private final List<EnhancementOptionsEntity> mEnhancementOptionsEntityList;

    public EnhancementOptionsAdapter(OnItemClickListener mOnItemClickListener,
                                     List<EnhancementOptionsEntity> mEnhancementOptionsEntityList) {
        this.mOnItemClickListener = mOnItemClickListener;
        this.mEnhancementOptionsEntityList = mEnhancementOptionsEntityList;
    }

    @NonNull
    @Override
    public EnhancementOptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewEnhancementOptionBinding binding = ItemViewEnhancementOptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EnhancementOptionsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EnhancementOptionsViewHolder holder, int position) {
        holder.optionImage.setImageDrawable(mEnhancementOptionsEntityList.get(position).getImage());
        holder.optionName.setText(mEnhancementOptionsEntityList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mEnhancementOptionsEntityList.size();
    }

    public class EnhancementOptionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView optionImage;
        TextView optionName;
        MaterialCardView cardView;
        EnhancementOptionsViewHolder(ItemViewEnhancementOptionBinding binding) {
            super(binding.getRoot());
            binding.getRoot().setOnClickListener(this);
            cardView = binding.getRoot().findViewById(R.id.container_card_view);
            optionImage = binding.optionImage;
            optionName = binding.optionName;
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}

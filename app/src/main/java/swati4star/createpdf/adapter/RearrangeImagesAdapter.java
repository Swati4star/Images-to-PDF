package swati4star.createpdf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.databinding.ItemRearrangeImagesBinding;

public class RearrangeImagesAdapter extends RecyclerView.Adapter<RearrangeImagesAdapter.ViewHolder> {
    private final Context mContext;
    private final OnClickListener mOnClickListener;
    private ArrayList<String> mImagesUri;

    public RearrangeImagesAdapter(OnClickListener onClickListener,
                                  ArrayList<String> uris, Context context) {
        mOnClickListener = onClickListener;
        mImagesUri = uris;
        mContext = context;
    }

    @NonNull
    @Override
    public RearrangeImagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRearrangeImagesBinding binding = ItemRearrangeImagesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RearrangeImagesAdapter.ViewHolder(binding);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull RearrangeImagesAdapter.ViewHolder holder, int position) {
        if (position == 0) {
            holder.buttonUp.setVisibility(View.GONE);
        } else {
            holder.buttonUp.setVisibility(View.VISIBLE);
        }
        if (position == getItemCount() - 1) {
            holder.buttonDown.setVisibility(View.GONE);
        } else {
            holder.buttonDown.setVisibility(View.VISIBLE);
        }
        Picasso.with(mContext).load(Uri.parse(mImagesUri.get(position))).into(holder.imageView);
        holder.pageNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return mImagesUri.size();
    }

    public void positionChanged(ArrayList<String> images) {
        mImagesUri = images;
        notifyDataSetChanged();
    }

    public interface OnClickListener {
        void onUpClick(int position);

        void onDownClick(int position);

        void onRemoveClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        ImageButton buttonUp;
        ImageButton buttonDown;
        TextView pageNumber;
        ImageButton removeImage;

        ViewHolder(ItemRearrangeImagesBinding binding) {
            super(binding.getRoot());
            imageView = binding.image;
            buttonUp = binding.buttonUp;
            buttonDown = binding.buttonDown;
            pageNumber = binding.pageNumber;
            removeImage = binding.removeImage;
            buttonDown.setOnClickListener(this);
            buttonUp.setOnClickListener(this);
            removeImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.buttonUp:
                    mOnClickListener.onUpClick(getAdapterPosition());
                    break;
                case R.id.buttonDown:
                    mOnClickListener.onDownClick(getAdapterPosition());
                    break;
                case R.id.removeImage:
                    mOnClickListener.onRemoveClick(getAdapterPosition());
            }
        }
    }
}
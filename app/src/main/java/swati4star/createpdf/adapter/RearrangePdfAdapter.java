package swati4star.createpdf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.databinding.ItemRearrangeImagesBinding;

public class RearrangePdfAdapter extends RecyclerView.Adapter<RearrangePdfAdapter.ViewHolder> {
    private final Context mContext;
    private final OnClickListener mOnClickListener;
    private ArrayList<Bitmap> mBitmaps;

    public RearrangePdfAdapter(OnClickListener onClickListener,
                               ArrayList<Bitmap> uris, Context context) {
        mOnClickListener = onClickListener;
        mBitmaps = uris;
        mContext = context;
    }

    @NonNull
    @Override
    public RearrangePdfAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRearrangeImagesBinding binding = ItemRearrangeImagesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RearrangePdfAdapter.ViewHolder(binding);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull RearrangePdfAdapter.ViewHolder holder, int position) {
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
        holder.imageView.setImageBitmap(mBitmaps.get(position));
        holder.pageNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return mBitmaps.size();
    }

    public void positionChanged(ArrayList<Bitmap> images) {
        mBitmaps = images;
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
            buttonDown = binding.buttonDown;
            buttonUp = binding.buttonUp;
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
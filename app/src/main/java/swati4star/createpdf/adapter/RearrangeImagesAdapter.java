package swati4star.createpdf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;

public class RearrangeImagesAdapter extends RecyclerView.Adapter<RearrangeImagesAdapter.ViewHolder> {
    private ArrayList<String> mImagesUri;
    private final Context mContext;
    private final OnClickListener mOnClickListener;

    public RearrangeImagesAdapter(OnClickListener onClickListener,
                                  ArrayList<String> uris, Context context) {
        mOnClickListener = onClickListener;
        mImagesUri = uris;
        mContext = context;
    }

    @NonNull
    @Override
    public RearrangeImagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rearrange_images, parent, false);
        return new RearrangeImagesAdapter.ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull RearrangeImagesAdapter.ViewHolder holder, int position) {
        File imageFile = new File(mImagesUri.get(position));
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
        Picasso.with(mContext).load(imageFile).into(holder.imageView);
        holder.pageNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return mImagesUri.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image)
        ImageView imageView;
        @BindView(R.id.buttonUp)
        ImageButton buttonUp;
        @BindView(R.id.buttonDown)
        ImageButton buttonDown;
        @BindView(R.id.pageNumber)
        TextView pageNumber;
        @BindView(R.id.removeImage)
        ImageButton mRemoveImage;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            buttonDown.setOnClickListener(this);
            buttonUp.setOnClickListener(this);
            mRemoveImage.setOnClickListener(this);
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

    public void positionChanged(ArrayList<String> images) {
        mImagesUri = images;
        notifyDataSetChanged();
    }

    public interface OnClickListener {
        void onUpClick(int position);
        void onDownClick(int position);
        void onRemoveClick(int position);
    }
}
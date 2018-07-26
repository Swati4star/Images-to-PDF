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

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import swati4star.createpdf.R;


public class ReArrangeImagesAdapter extends RecyclerView.Adapter<ReArrangeImagesAdapter.ViewHolder> {
    private ArrayList<String> mImagesUri;
    private final Context mContext;
    private final OnClickListener mOnClickListener;


    public ReArrangeImagesAdapter(OnClickListener onClickListener,
                                  ArrayList<String> uris, Context context) {
        mOnClickListener = onClickListener;
        mImagesUri = uris;
        mContext = context;
    }

    @NonNull
    @Override
    public ReArrangeImagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rearrange_images, parent, false);
        return new ReArrangeImagesAdapter.ViewHolder(view);

    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ReArrangeImagesAdapter.ViewHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return mImagesUri.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageView;
        final ImageButton buttonUp;
        final ImageButton buttonDown;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            buttonUp = itemView.findViewById(R.id.buttonUp);
            buttonDown = itemView.findViewById(R.id.buttonDown);

            buttonDown.setOnClickListener(this);
            buttonUp.setOnClickListener(this);
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
    }
}
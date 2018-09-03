package swati4star.createpdf.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import swati4star.createpdf.R;

public class PreviewAdapter extends PagerAdapter {

    private final Context mContext;
    private final ArrayList<String> mPreviewItems;
    private final LayoutInflater mInflater;

    public PreviewAdapter(Context context, ArrayList<String> previewItems) {
        mContext = context;
        mPreviewItems = previewItems;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        View layout = mInflater.inflate(R.layout.pdf_preview_item, view, false);
        final ImageView imageView = layout.findViewById(R.id.image);
        String path = mPreviewItems.get(position);
        File fileLocation = new File(path);
        Picasso.with(mContext).load(fileLocation).into(imageView);
        TextView fileName = layout.findViewById(R.id.tvFileName);
        String fileNameString = fileLocation.getName();
        fileName.setText(fileNameString);
        view.addView(layout, 0);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return mPreviewItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.format(mContext.getResources().getString(R.string.showing_image),
                position + 1, mPreviewItems.size());
    }

    public void setData(ArrayList<String> images) {
        mPreviewItems.clear();
        mPreviewItems.addAll(images);
    }
}

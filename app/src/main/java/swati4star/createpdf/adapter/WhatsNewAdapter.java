package swati4star.createpdf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import swati4star.createpdf.R;

public class WhatsNewAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<String> mTitleList;
    private final List<String> mContentList;
    private final LayoutInflater mInflater;

    public WhatsNewAdapter(Context context, ArrayList<String> titleList, ArrayList<String> contentList) {
        this.mContext = context;
        this.mTitleList = titleList;
        this.mContentList = contentList;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mTitleList.size();
    }

    @Override
    public Object getItem(int i) {
        return mTitleList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = mInflater.inflate(R.layout.item_whats_new, null);
        TextView tvHeading = view.findViewById(R.id.title);
        tvHeading.setText(mTitleList.get(i));
        final TextView tvDescription = view.findViewById(R.id.description);
        tvDescription.setText(mContentList.get(i));
        return view;
    }
}

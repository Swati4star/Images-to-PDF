package swati4star.createpdf;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;


/**
 * Created by swati on 9/10/15.
 */


public class Files_adapter extends BaseAdapter {

    Context context;
    private static LayoutInflater inflater = null;
    ArrayList<String> FeedItems;
    TextView t;
    LinearLayout l;
    ImageView iv;

    public Files_adapter(Context context, ArrayList<String> FeedItems) {
        this.context = context;
        this.FeedItems = FeedItems;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return FeedItems.size();
    }

    @Override
    public Object getItem(int position) {
        return FeedItems.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.file_litsitem, null);


        t = (TextView) vi.findViewById(R.id.name);
        l = (LinearLayout) vi.findViewById(R.id.parent);
        iv = (ImageView) vi.findViewById(R.id.iv);

        String[] x = FeedItems.get(position).split("/");

        t.setText(x[x.length-1]);



        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                File file = new File(FeedItems.get(position));
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No app to read PDF File", Toast.LENGTH_LONG).show();
                }


            }
        });

        return vi;
    }



}
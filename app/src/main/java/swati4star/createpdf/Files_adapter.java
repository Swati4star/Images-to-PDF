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


import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;

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
    RippleView ripple;

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
        ripple = (RippleView) vi.findViewById(R.id.ripp);

        String[] x = FeedItems.get(position).split("/");

        t.setText(x[x.length-1]);


        ripple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                new MaterialDialog.Builder(context)
                        .title(R.string.title)
                        .items(R.array.items)
                        .itemsIds(R.array.itemIds)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                Toast.makeText(context, which + ": " + text + ", ID = " + view.getId(), Toast.LENGTH_SHORT).show();


                                switch(which){
                                    case 0 :  File file = new File(FeedItems.get(position));
                                        Intent target = new Intent(Intent.ACTION_VIEW);
                                        target.setDataAndType(Uri.fromFile(file), "application/pdf");
                                        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                        Intent intent = Intent.createChooser(target, "Open File");
                                        try {
                                            context.startActivity(intent);
                                        } catch (ActivityNotFoundException e) {
                                            Toast.makeText(context, "No app to read PDF File", Toast.LENGTH_LONG).show();
                                        }
                                        break;


                                    case 1 : //delete
                                        File fdelete = new File(FeedItems.get(position));
                                        if (fdelete.exists()) {
                                            if (fdelete.delete()) {
                                                Toast.makeText(context, "File deleted.", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(context, "File can't be deleted.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        break;



                                    case 2 : //rename
                                        new MaterialDialog.Builder(context)
                                                .title("Creating PDF")
                                                .content("Enter file name")
                                                .input("Example : abc",null, new MaterialDialog.InputCallback() {
                                                    @Override
                                                    public void onInput(MaterialDialog dialog, CharSequence input) {
                                                        // Do something
                                                        if(input ==null){
                                                            Toast.makeText(context, "Name cannot be blank", Toast.LENGTH_LONG).show();

                                                        }else {
                                                            String newname = input.toString();
                                                            File oldfile = new File(FeedItems.get(position));
                                                            String x[]=FeedItems.get(position).split("/");
                                                            String newfilename="";
                                                            for(int i=0;i<x.length-1;i++)
                                                                newfilename = newfilename+"/"+x[i];

                                                            File newfile = new File(newfilename+"/"+newname+".pdf");

                                                            Log.e("Old file name",oldfile+" ");
                                                            Log.e("New file name",newfile+" ");

                                                            if (oldfile.renameTo(newfile)) {
                                                                Toast.makeText(context, "File renamed.", Toast.LENGTH_LONG).show();
                                                            } else {
                                                                Toast.makeText(context, "File can't be renamed.", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    }
                                                })
                                                .show();
                                        break;


                                }

                            }
                        })
                        .show();
                notifyDataSetChanged();

            }
        });





        return vi;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();


    }
}
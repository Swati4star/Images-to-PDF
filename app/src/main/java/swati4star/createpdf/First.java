package swati4star.createpdf;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingButton;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class First extends Fragment {


    private int PICK_IMAGE_REQUEST = 1;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private int mMorphCounter1 = 1;

    List<String> imagesuri;
    TextView t;
    Button b, badd;
    String path;
    Activity ac;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ac = (Activity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_first,container);

        imagesuri = new ArrayList<>();


        t = (TextView) root.findViewById(R.id.text);
        b = (Button) root.findViewById(R.id.b);
        badd = (Button) root.findViewById(R.id.badd);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(path);
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ac, "No app to read PDF File", Toast.LENGTH_LONG).show();
                }
            }
        });


        badd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);


                Intent intent  = new Intent(ac, ImagePickerActivity.class);
                startActivityForResult(intent,INTENT_REQUEST_GET_IMAGES);


            }
        });




        final MorphingButton btnMorph1 = (MorphingButton) root.findViewById(R.id.pdfcreate);
        btnMorph1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imagesuri.size()==0){
                    Toast.makeText(ac, "No Images selected", Toast.LENGTH_LONG).show();

                }else {
                    imcreate();
                    onMorphButton1Clicked(btnMorph1);
                }
            }
        });



        return super.onCreateView(inflater, container, savedInstanceState);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            String pat = uri.getPath();
            imagesuri.add(pat);

            Toast.makeText(ac, "Image added", Toast.LENGTH_LONG).show();
            t.append(pat + "\n");


        }else
        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK ) {

            ArrayList<Uri>  image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

            //do something

            for(int i=0;i<image_uris.size();i++)
                imagesuri.add(image_uris.get(i).getPath());

        }
    }

    private void onMorphButton1Clicked(final MorphingButton btnMorph) {
        if (mMorphCounter1 == 0) {
            mMorphCounter1++;
            morphToSquare(btnMorph, integer(R.integer.mb_animation));
        } else if (mMorphCounter1 == 1) {
            mMorphCounter1 = 0;
            morphToSuccess(btnMorph);
        }
    }

    private void morphToSquare(final MorphingButton btnMorph, int duration) {
        MorphingButton.Params square = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(dimen(R.dimen.mb_corner_radius_2))
                .width(dimen(R.dimen.mb_width_200))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_blue))
                .colorPressed(color(R.color.mb_blue_dark))
                .text(getString(R.string.mb_button));
        btnMorph.morph(square);
    }

    private void morphToSuccess(final MorphingButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(integer(R.integer.mb_animation))
                .cornerRadius(dimen(R.dimen.mb_height_56))
                .width(dimen(R.dimen.mb_height_56))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_green))
                .colorPressed(color(R.color.mb_green_dark))
                .icon(R.drawable.ic_done);
        btnMorph.morph(circle);
    }
    public void imcreate() {
        new doindstuuf().execute();
    }
   public class doindstuuf extends AsyncTask<String,String,String>{

        Dialog d;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            d = new Dialog(ac);
            d.setTitle("Creating PDF. This may take a while!");
            d.setCancelable(false);
            d.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            b.setVisibility(View.VISIBLE);
            t.append("done");

            d.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {







            File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDFfiles/");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }



            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDFfiles/";

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDFfiles/");

            String t = Long.toString(System.currentTimeMillis());
            path = path + "MyPDFFILE" +t+
                    ".pdf";
            File f = new File(file, "MyPDFFILE" +t+
                    ".pdf");

            Log.v("stage 1", "store the pdf in sd card");
            //t.append("store the pdf in sd card\n");

            Document document = new Document(PageSize.A4, 38, 38, 50, 38);

            Log.v("stage 2", "Document Created");
            //t.append("Document Created\n");
            Rectangle documentRect = document.getPageSize();


            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));


                Log.v("Stage 3", "Pdf writer");
              //  t.append("Pdf writer\n");

                document.open();

                Log.v("Stage 4", "Document opened");
               // t.append("Document opened\n");


                for (int i = 0; i < imagesuri.size(); i++) {



                    Bitmap bmp = BitmapFactory.decodeFile(imagesuri.get(i));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 70, stream);



                    Image image = Image.getInstance(stream.toByteArray());




                    if(bmp.getWidth()>documentRect.getWidth() || bmp.getHeight()>documentRect.getHeight())
                    {
                        //bitmap is larger than page,so set bitmap's size similar to the whole page
                        image.scaleAbsolute(documentRect.getWidth(), documentRect.getHeight());
                    }
                    else
                    {
                        //bitmap is smaller than page, so add bitmap simply.[note: if you want to fill page by stretching image, you may set size similar to page as above]
                        image.scaleAbsolute(bmp.getWidth(), bmp.getHeight());
                    }


                    Log.v("Stage 6", "Image path adding");

                    image.setAbsolutePosition((documentRect.getWidth()-image.getScaledWidth())/2, (documentRect.getHeight()-image.getScaledHeight())/2);
                    Log.v("Stage 7", "Image Alignments");

                    image.setBorder(Image.BOX);

                    image.setBorderWidth(15);

                    document.add(image);
                    document.newPage();
                }

                Log.v("Stage 8", "Image adding");
               // t.append("Image adding\n");

                document.close();

                Log.v("Stage 7", "Document Closed"+path);
             //   t.append("Document Closed\n");
            } catch (Exception e) {
                e.printStackTrace();
            }

            document.close();
            imagesuri.clear();

            return null;
        }
    }

    public int integer(@IntegerRes int resId) {
        return getResources().getInteger(resId);
    }
    public int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

}

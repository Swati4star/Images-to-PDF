package swati4star.createpdf;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;


/**
 * Home fragment to start with creating PDF
 */
public class Home extends Fragment {

    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private int mMorphCounter1 = 1;
    Activity activity;
    List<String> imagesUri;
    String path, filename;
    Image image;
    MorphingButton createPdf;
    MorphingButton openPdf;
    MorphingButton addImages;
    TextView textView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);

        //initialising variables
        imagesUri = new ArrayList<>();
        addImages = (MorphingButton) root.findViewById(R.id.addImages);
        createPdf = (MorphingButton) root.findViewById(R.id.pdfcreate);
        openPdf = (MorphingButton) root.findViewById(R.id.pdfOpen);
        textView = (TextView) root.findViewById(R.id.text);


        morphToSquare(createPdf, integer(R.integer.mb_animation));
        openPdf.setVisibility(View.GONE);

        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddingImages();
            }
        });

        createPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPdf();
            }
        });

        openPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPdf();
            }
        });

        // Get runtime permissions if build version >= Android M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
            }
        }

        return root;
    }

    // Adding Images to PDF
    void startAddingImages() {
        // Check if permissions are granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
            } else {
                selectImages();
            }
        } else {
            selectImages();
        }
    }


    // Create Pdf of selected images
    void createPdf() {
        if (imagesUri.size() == 0) {
            Toast.makeText(activity, R.string.toast_no_images, Toast.LENGTH_LONG).show();
        } else {
            new MaterialDialog.Builder(activity)
                    .title(R.string.creating_pdf)
                    .content(R.string.enter_file_name)
                    .input(getString(R.string.example), null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if (input == null || input.toString().trim().equals("")) {
                                Toast.makeText(activity, R.string.toast_name_not_blank, Toast.LENGTH_LONG).show();
                            } else {
                                filename = input.toString();

                                new creatingPDF().execute();

                                if (mMorphCounter1 == 0) {
                                    mMorphCounter1++;
                                }
                            }
                        }
                    })
                    .show();
        }
    }


    void openPdf() {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file), getString(R.string.pdf_type));
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, getString(R.string.open_file));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.toast_no_pdf_app, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Opens ImagePickerActivity to select Images
     */
    public void selectImages() {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }

    /**
     * Called after user is asked to grant permissions
     *
     * @param requestCode  REQUEST Code for opening permissions
     * @param permissions  permissions asked to user
     * @param grantResults bool array indicating if permission is granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImages();
                    Toast.makeText(activity, R.string.toast_permissions_given, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, R.string.toast_insufficient_permissions, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Called after ImagePickerActivity is called
     *
     * @param requestCode REQUEST Code for opening ImagePickerActivity
     * @param resultCode  result code of the process
     * @param data        Data of the image selected
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK) {

            imagesUri.clear();

            ArrayList<Uri> image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            for (int i = 0; i < image_uris.size(); i++) {
                imagesUri.add(image_uris.get(i).getPath());
            }
            Toast.makeText(activity, R.string.toast_images_added, Toast.LENGTH_LONG).show();
            morphToSquare(createPdf, integer(R.integer.mb_animation));
        }
    }

    /**
     * Converts morph button ot square shape
     *
     * @param btnMorph the button to be converted
     * @param duration time period of transition
     */
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

    /**
     * Converts morph button into success shape
     *
     * @param btnMorph the button to be converted
     */
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

    /**
     * An async task that converts selected images to Pdf
     */
    public class creatingPDF extends AsyncTask<String, String, String> {

        // Progress dialog
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .title(R.string.please_wait)
                .content(R.string.populating_list)
                .cancelable(false)
                .progress(true, 0);
        MaterialDialog dialog = builder.build();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {


            File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.pdf_dir));
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }


            path = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.pdf_dir);

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.pdf_dir));

            path = path + filename + getString(R.string.pdf_ext);

            Log.v("stage 1", "store the pdf in sd card");

            Document document = new Document(PageSize.A4, 38, 38, 50, 38);

            Log.v("stage 2", "Document Created");

            Rectangle documentRect = document.getPageSize();


            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));

                Log.v("Stage 3", "Pdf writer");

                document.open();

                Log.v("Stage 4", "Document opened");

                for (int i = 0; i < imagesUri.size(); i++) {


                    Bitmap bmp = BitmapFactory.decodeFile(imagesUri.get(i));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 70, stream);


                    image = Image.getInstance(imagesUri.get(i));


                    if (bmp.getWidth() > documentRect.getWidth() || bmp.getHeight() > documentRect.getHeight()) {
                        //bitmap is larger than page,so set bitmap's size similar to the whole page
                        image.scaleAbsolute(documentRect.getWidth(), documentRect.getHeight());
                    } else {
                        //bitmap is smaller than page, so add bitmap simply.[note: if you want to fill page by stretching image, you may set size similar to page as above]
                        image.scaleAbsolute(bmp.getWidth(), bmp.getHeight());
                    }


                    Log.v("Stage 6", "Image path adding");

                    image.setAbsolutePosition((documentRect.getWidth() - image.getScaledWidth()) / 2, (documentRect.getHeight() - image.getScaledHeight()) / 2);
                    Log.v("Stage 7", "Image Alignments");

                    image.setBorder(Image.BOX);

                    image.setBorderWidth(15);

                    document.add(image);

                    document.newPage();
                }

                Log.v("Stage 8", "Image adding");

                document.close();

                Log.v("Stage 7", "Document Closed" + path);
            } catch (Exception e) {
                e.printStackTrace();
            }

            document.close();
            imagesUri.clear();

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            openPdf.setVisibility(View.VISIBLE);
            Snackbar.make(getActivity().findViewById(android.R.id.content), "PDF created!", Snackbar.LENGTH_LONG)
                    .setAction("View", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<String> list = new ArrayList<String>(Arrays.asList(path));
                            FilesAdapter filesAdapter = new FilesAdapter(getContext(), list);
                            filesAdapter.openFile(path);
                        }
                    }).show();
            dialog.dismiss();
            morphToSuccess(createPdf);
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

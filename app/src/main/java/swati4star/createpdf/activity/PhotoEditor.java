package swati4star.createpdf.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.RecyclerViewAdapter;
import swati4star.createpdf.interfaces.OnFilterItemClickedListener;
import swati4star.createpdf.model.Data;

public class PhotoEditor extends AppCompatActivity implements OnFilterItemClickedListener {
    private ArrayList<String> mFilterUris = new ArrayList<>();
    private ArrayList<String> mImagepaths = new ArrayList<>();
    private ArrayList<String> mPreviewUris = new ArrayList<>();
    private ArrayList<String> mFilterNameUris = new ArrayList<>();
    int size, dispsize, i = 1, j = 1;
    @BindView(R.id.nextimageButton)
    ImageButton mNextButton;
    Button mSepiaFilterButton;
    @BindView(R.id.imagecount)
    TextView mImgcount;
    @BindView(R.id.savecurrent)
    Button savecurr;
    PhotoEditorView mPhotoEditorView;
    Bitmap bitmap;
    private int[] mImages = {R.drawable.none, R.drawable.black, R.drawable.d};
    boolean isClicked = false, isClickedFilter = false, isLast = false;
    File outFile;
    FileOutputStream outStream = null;
    ja.burhanrashid52.photoeditor.PhotoEditor mPhotoEditor;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish:
                done();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);
        ButterKnife.bind(this);
        ActionBar actionBar = getActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String data = getIntent().getExtras().getString("Images");
        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mFilterUris = getIntent().getExtras().getStringArrayList("first");
        dispsize = mFilterUris.size();
        size = mFilterUris.size() - 1;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeFile(mFilterUris.get(0));
        mPhotoEditorView.getSource().setImageBitmap(bitmap);
        String sTextbeg = "Showing " + String.valueOf(1) + " of " + dispsize;
        mImgcount.setText(sTextbeg);
        getImages();
    }

    @OnClick(R.id.nextimageButton)
    void nextImg() {
        try {
            //Proceed if Save Current has been clicked
            if (isClicked) {
                next();
                incimgCount();
            } else {
                Toast.makeText(getApplicationContext(), R.string.save_first, Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @OnClick(R.id.savecurrent)
    void saveC() {
        isClicked = true;
        if (isClicked && isClickedFilter) {
            saveimgcurent();
        } else {
            nonefilter();
            saveimgcurent();

        }
    }
    //Increment image count to display in textView
    void incimgCount() {
        if (i < dispsize) {
            String sText = "Showing " + String.valueOf(i) + " of " + dispsize;
            mImgcount.setText(sText);
        } else if (i == dispsize) {
            String sText = "Showing " + String.valueOf(i) + " of " + dispsize;
            mImgcount.setText(sText);
            mNextButton.setVisibility(View.INVISIBLE);
            isLast = true;
        } else {
            mNextButton.setEnabled(false);
        }

    }
    //Saves Current Image
    private void saveimgcurent() {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/PDFfilter");
            dir.mkdirs();
            String fileName = String.format("%d.jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);
            String imagePath = outFile.getAbsolutePath();

            mPhotoEditor.saveAsFile(imagePath, new ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        mImagepaths.add(imagePath);
                        Log.e("imgFilter", "Saved Successfully");
                        Toast.makeText(getApplicationContext(), R.string.saving_dialog, Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("imgFilter", "Failed to save");
                    }
                });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    // Apply GrayScale Filter to Image
    public void grayscaleFilter() {
        try {
            isClickedFilter = true;
            mPhotoEditor = new ja.burhanrashid52.photoeditor.PhotoEditor.Builder(this, mPhotoEditorView)
                    .setPinchTextScalable(true)
                    .build();
            mPhotoEditor.setFilterEffect(PhotoFilter.GRAY_SCALE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Apply Sepia Filter to Image
    public void sepiaFilter() {
        try {
            isClickedFilter = true;
            mPhotoEditor = new ja.burhanrashid52.photoeditor.PhotoEditor.Builder(this, mPhotoEditorView)
                    .setPinchTextScalable(true)
                    .build();

            mPhotoEditor.setFilterEffect(PhotoFilter.SEPIA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Apply No Filter to Image
    public void nonefilter() {
        try {
            isClickedFilter = true;
            mPhotoEditor = new ja.burhanrashid52.photoeditor.PhotoEditor.Builder(this, mPhotoEditorView)
                    .setPinchTextScalable(true)
                    .build();
            mPhotoEditor.setFilterEffect(PhotoFilter.NONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Store imagepaths for creation of PDF when Done
    public void done() {
        if (!isClicked) {
            passUris(mFilterUris);
        } else if (i <= dispsize) {
            for (j = i + 1; j <= mFilterUris.size(); j++) {
                savelastfew();
            }
            if (!isClicked || isLast) {
                i++;
            }
            passUris(mImagepaths);

        } else {
            passUris(mImagepaths);
        }

    }

    public void savelastfew() {
        mImagepaths.add(mFilterUris.get(j - 1));
    }
    //Intent to Pass Back URIs
    public void passUris(ArrayList<String> mImagepaths) {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra("result", mImagepaths);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
    //Display next image on nextImage button click
    private void next() {
        try {
            if (i <= size) {
                bitmap = BitmapFactory.decodeFile(mFilterUris.get(i));
                mPhotoEditorView.getSource().setImageBitmap(bitmap);
            }
            i++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Add Items in Recycler View
    private void getImages() {
        mPreviewUris.add("R.drawable.none");
        mFilterNameUris.add("None");

        mPreviewUris.add("R.drawable.blacknwhite");
        mFilterNameUris.add("GrayScale");

        mPreviewUris.add("R.drawable.sepiafill");
        mFilterNameUris.add("Sepia");
        initRecyclerView();

    }
    //Initialize Recycler View
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mFilterNameUris, this, mImages, this);
        recyclerView.setAdapter(adapter);
    }


    //Get Item Position and call Filter Function
    @Override
    public void onItemClick(View v, int position) {
        if (position == 0) {
            nonefilter();
        } else if (position == 1) {
            grayscaleFilter();
        } else if (position == 2) {
            sepiaFilter();
        }
    }
}





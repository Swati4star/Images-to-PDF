package swati4star.createpdf.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class PhotoEditor extends AppCompatActivity {
    private ArrayList<String> mFilterUris = new ArrayList<>();
    private ArrayList<String> mImagepaths = new ArrayList<>();
    int size, i = 1;
    @BindView(R.id.nextimageButton)
    ImageButton mNextButton;
    @BindView(R.id.grayscalefilter)
    Button mgrayscaleButton;
    @BindView(R.id.sepiafilter)
    Button mSepiaFilterButton;
    @BindView(R.id.savecurrentfilter)
    Button msavecurrent;
    PhotoEditorView mPhotoEditorView;
    Bitmap bitmap;
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);
        ButterKnife.bind(this);
        String data = getIntent().getExtras().getString("Images");
        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mFilterUris = getIntent().getExtras().getStringArrayList("first");
        size = mFilterUris.size() - 1;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeFile(mFilterUris.get(0));
        mPhotoEditorView.getSource().setImageBitmap(bitmap);
    }

    @OnClick(R.id.grayscalefilter)
    void grayf() {
        grayscaleFilter();
    }
    @OnClick(R.id.sepiafilter)
    void sepiaf() {
        sepiaFilter();
    }

    @OnClick(R.id.nextimageButton)
    void nextImg() {
        next();
    }

    @OnClick(R.id.savecurrentfilter)
    void savcurr() {
        saveimgcurent();
    }

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
    private void grayscaleFilter() {
        try {
            mPhotoEditor = new ja.burhanrashid52.photoeditor.PhotoEditor.Builder(this, mPhotoEditorView)
                    .setPinchTextScalable(true)
                    .build();

            mPhotoEditor.setFilterEffect(PhotoFilter.GRAY_SCALE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sepiaFilter() {
        try {
            mPhotoEditor = new ja.burhanrashid52.photoeditor.PhotoEditor.Builder(this, mPhotoEditorView)
                    .setPinchTextScalable(true)
                    .build();

            mPhotoEditor.setFilterEffect(PhotoFilter.SEPIA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void done() {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra("result", mImagepaths);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
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
}





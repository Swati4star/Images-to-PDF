package swati4star.createpdf.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.ImageFiltersAdapter;
import swati4star.createpdf.interfaces.OnFilterItemClickedListener;
import swati4star.createpdf.model.FilterItem;

public class ImageEditor extends AppCompatActivity implements OnFilterItemClickedListener {

    private ArrayList<String> mFilterUris = new ArrayList<>();
    private ArrayList<String> mImagepaths = new ArrayList<>();
    private ArrayList<FilterItem> mFilterItems = new ArrayList<>();

    int imagesCount, dispsize, currentImage = 0, j = 1;

    @BindView(R.id.nextimageButton)
    ImageButton mNextButton;
    @BindView(R.id.imagecount)
    TextView mImgcount;
    @BindView(R.id.savecurrent)
    Button saveCurrent;
    @BindView(R.id.previousImageButton)
    ImageButton mPreviousButton;

    Bitmap bitmap;
    PhotoEditorView mPhotoEditorView;
    boolean isClicked = false, isClickedFilter = false, isLast = false;

    PhotoEditor mPhotoEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);
        ButterKnife.bind(this);

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mFilterUris = getIntent().getExtras().getStringArrayList("first");
        dispsize = mFilterUris.size();
        imagesCount = mFilterUris.size() - 1;
        bitmap = BitmapFactory.decodeFile(mFilterUris.get(0));
        mPhotoEditorView.getSource().setImageBitmap(bitmap);
        String showingText = "Showing " + String.valueOf(1) + " of " + dispsize;
        mImgcount.setText(showingText);
        mPreviousButton.setVisibility(View.INVISIBLE);
        showFilters();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.nextimageButton)
    void nextImg() {
        try {
            //Proceed if Save Current has been clicked
            if (isClicked) {
                next();
                incrementImageCount();
            } else {
                Toast.makeText(getApplicationContext(), R.string.save_first, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.previousImageButton)
    void previousImg() {
        try {
            //Proceed if Save Current has been clicked
            if (isClicked) {
                previous();
                decrementImageCount();
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
        if (isClickedFilter) {
            saveimgcurent();
        } else {
            nonefilter();
            saveimgcurent();
        }
    }

    //Increment image count to display in textView
    void incrementImageCount() {
        if (currentImage < imagesCount) {
            String sText = "Showing " + String.valueOf(currentImage + 1) + " of " + dispsize;
            mImgcount.setText(sText);
            if (mPreviousButton.getVisibility() == View.INVISIBLE)
                mPreviousButton.setVisibility(View.VISIBLE);
        } else if (currentImage == imagesCount) {
            String sText = "Showing " + String.valueOf(currentImage + 1) + " of " + dispsize;
            mImgcount.setText(sText);
            mNextButton.setVisibility(View.INVISIBLE);
            mPreviousButton.setVisibility(View.VISIBLE);
            isLast = true;
        } else {
            mNextButton.setEnabled(false);
        }
    }

    //Decrement image count to display in textView
    void decrementImageCount() {
        if (currentImage > 0) {
            String sText = "Showing " + String.valueOf(currentImage + 1) + " of " + dispsize;
            mImgcount.setText(sText);
            if (mNextButton.getVisibility() == View.INVISIBLE)
                mNextButton.setVisibility(View.VISIBLE);
        } else if (currentImage == 0) {
            String sText = "Showing " + String.valueOf(currentImage + 1) + " of " + dispsize;
            mImgcount.setText(sText);
            mPreviousButton.setVisibility(View.INVISIBLE);
            mNextButton.setVisibility(View.VISIBLE);
        } else {
            mPreviousButton.setEnabled(false);
        }
    }

    //Saves Current Image
    private void saveimgcurent() {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/PDFfilter");
            dir.mkdirs();
            String fileName = String.format("%sepia.jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);
            String imagePath = outFile.getAbsolutePath();

            mPhotoEditor.saveAsFile(imagePath, new PhotoEditor.OnSaveListener() {
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

    /**
     * Store imagepaths for creation of PDF when Done
     */
    public void done() {
        if (!isClicked) {
            passUris(mFilterUris);
        } else if (currentImage <= dispsize) {
            for (j = currentImage + 1; j <= mFilterUris.size(); j++) {
                // Append the images which are not edited
                mImagepaths.add(mFilterUris.get(j - 1));
            }
            if (!isClicked || isLast) {
                currentImage++;
            }
            passUris(mImagepaths);

        } else {
            passUris(mImagepaths);
        }
    }

    /**
     * Intent to Send Back final edited URIs
     *
     * @param mImagepaths - the images array to be send pack
     */
    public void passUris(ArrayList<String> mImagepaths) {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra("result", mImagepaths);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Display next image on nextImage button click
     */
    private void next() {
        try {
            if (currentImage + 1 <= imagesCount) {
                bitmap = BitmapFactory.decodeFile(mFilterUris.get(currentImage + 1));
                mPhotoEditorView.getSource().setImageBitmap(bitmap);
                currentImage++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Display Previous image on previousImage button click
     */
    private void previous() {
        try {
            if (currentImage - 1 >= 0) {
                bitmap = BitmapFactory.decodeFile(mFilterUris.get((currentImage - 1)));
                mPhotoEditorView.getSource().setImageBitmap(bitmap);
                currentImage--;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add Items in Recycler View & intialize adapter
     */
    private void showFilters() {
        mFilterItems.add(new FilterItem(R.drawable.none, "None"));
        mFilterItems.add(new FilterItem(R.drawable.black, "GrayScale"));
        mFilterItems.add(new FilterItem(R.drawable.sepia, "Sepia"));
        initRecyclerView();
    }

    /**
     * Initialize Recycler View
     */
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        ImageFiltersAdapter adapter = new ImageFiltersAdapter(mFilterItems, this, this);
        recyclerView.setAdapter(adapter);
    }

    //Get Item Position and call Filter Function
    @Override
    public void onItemClick(View view, int position) {
        if (position == 0) {
            nonefilter();
        } else if (position == 1) {
            grayscaleFilter();
        } else if (position == 2) {
            sepiaFilter();
        }
    }

    /**
     * Apply GrayScale Filter to Image
     */
    public void grayscaleFilter() {
        try {
            isClickedFilter = true;
            mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                    .setPinchTextScalable(true)
                    .build();
            mPhotoEditor.setFilterEffect(PhotoFilter.GRAY_SCALE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Apply Sepia Filter to Image
     */
    public void sepiaFilter() {
        try {
            isClickedFilter = true;
            mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                    .setPinchTextScalable(true)
                    .build();

            mPhotoEditor.setFilterEffect(PhotoFilter.SEPIA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Apply No Filter to Image
     */
    public void nonefilter() {
        try {
            isClickedFilter = true;
            mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                    .setPinchTextScalable(true)
                    .build();
            mPhotoEditor.setFilterEffect(PhotoFilter.NONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
}





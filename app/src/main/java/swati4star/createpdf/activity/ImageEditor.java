package swati4star.createpdf.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import java.util.Objects;

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

import static swati4star.createpdf.util.Constants.IMAGE_EDITOR_KEY;
import static swati4star.createpdf.util.ImageFilterUtils.getFiltersList;

public class ImageEditor extends AppCompatActivity implements OnFilterItemClickedListener {

    private ArrayList<String> mFilterUris = new ArrayList<>();
    private final ArrayList<String> mImagepaths = new ArrayList<>();
    private ArrayList<FilterItem> mFilterItems;

    private int mImagesCount;
    private int mDisplaySize;
    private int mCurrentImage = 0;

    @BindView(R.id.nextimageButton)
    ImageButton mNextButton;
    @BindView(R.id.imagecount)
    TextView mImgcount;
    @BindView(R.id.savecurrent)
    Button saveCurrent;
    @BindView(R.id.previousImageButton)
    ImageButton mPreviousButton;

    private Bitmap mBitmap;
    private PhotoEditorView mPhotoEditorView;
    private boolean mClicked = false;
    private boolean mClickedFilter = false;
    private boolean mIsLast = false;

    private PhotoEditor mPhotoEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);
        ButterKnife.bind(this);

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mFilterUris = getIntent().getExtras().getStringArrayList(IMAGE_EDITOR_KEY);

        if (mFilterUris == null || mFilterUris.size() < 1) {
            Snackbar.make(Objects.requireNonNull(this).findViewById(android.R.id.content),
                    R.string.snackbar_no_images,
                    Snackbar.LENGTH_LONG).show();
            finish();
        }

        mDisplaySize = mFilterUris.size();
        mImagesCount = mFilterUris.size() - 1;
        mBitmap = BitmapFactory.decodeFile(mFilterUris.get(0));
        mPhotoEditorView.getSource().setImageBitmap(mBitmap);
        String showingText = "Showing " + String.valueOf(1) + " of " + mDisplaySize;
        mImgcount.setText(showingText);
        mPreviousButton.setVisibility(View.INVISIBLE);
        showFilters();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.nextimageButton)
    void nextImg() {
        try {
            //Proceed if Save Current has been clicked
            if (mClicked) {
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
            if (mClicked) {
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
        mClicked = true;
        if (mClickedFilter) {
            saveimgcurent();
        } else {
            applyFilter(PhotoFilter.NONE);
            saveimgcurent();
        }
    }

    /**
     * Increment image count to display in textView
     */
    private void incrementImageCount() {
        if (mCurrentImage < mImagesCount) {
            String sText = "Showing " + String.valueOf(mCurrentImage + 1) + " of " + mDisplaySize;
            mImgcount.setText(sText);
            if (mPreviousButton.getVisibility() == View.INVISIBLE)
                mPreviousButton.setVisibility(View.VISIBLE);
        } else if (mCurrentImage == mImagesCount) {
            String sText = "Showing " + String.valueOf(mCurrentImage + 1) + " of " + mDisplaySize;
            mImgcount.setText(sText);
            mNextButton.setVisibility(View.INVISIBLE);
            mPreviousButton.setVisibility(View.VISIBLE);
            mIsLast = true;
        } else {
            mNextButton.setEnabled(false);
        }
    }

    /**
     * Decrement image count to display in textView
     */
    private void decrementImageCount() {
        if (mCurrentImage > 0) {
            String sText = "Showing " + String.valueOf(mCurrentImage + 1) + " of " + mDisplaySize;
            mImgcount.setText(sText);
            if (mNextButton.getVisibility() == View.INVISIBLE)
                mNextButton.setVisibility(View.VISIBLE);
        } else if (mCurrentImage == 0) {
            String sText = "Showing " + String.valueOf(mCurrentImage + 1) + " of " + mDisplaySize;
            mImgcount.setText(sText);
            mPreviousButton.setVisibility(View.INVISIBLE);
            mNextButton.setVisibility(View.VISIBLE);
        } else {
            mPreviousButton.setEnabled(false);
        }
    }

    /**
     * Saves Current Image with applied filter
     */
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
    private void done() {
        if (!mClicked) {
            passUris(mFilterUris);
        } else if (mCurrentImage <= mDisplaySize) {
            int mCounter = 1;
            for (mCounter = mCurrentImage + 1; mCounter <= mFilterUris.size(); mCounter++) {
                // Append the images which are not edited
                mImagepaths.add(mFilterUris.get(mCounter - 1));
            }
            if (!mClicked || mIsLast) {
                mCurrentImage++;
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
    private void passUris(ArrayList<String> mImagepaths) {
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
            if (mCurrentImage + 1 <= mImagesCount) {
                mBitmap = BitmapFactory.decodeFile(mFilterUris.get(mCurrentImage + 1));
                mPhotoEditorView.getSource().setImageBitmap(mBitmap);
                mCurrentImage++;
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
            if (mCurrentImage - 1 >= 0) {
                mBitmap = BitmapFactory.decodeFile(mFilterUris.get((mCurrentImage - 1)));
                mPhotoEditorView.getSource().setImageBitmap(mBitmap);
                mCurrentImage--;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add Items in Recycler View & intialize adapter
     */
    private void showFilters() {
        mFilterItems = getFiltersList(this);
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

    /**
     * Get Item Position and call Filter Function
     *
     * @param view     - view which is clicked
     * @param position - position of item clicked
     */
    @Override
    public void onItemClick(View view, int position) {
        PhotoFilter filter = mFilterItems.get(position).getFilter();
        applyFilter(filter);
    }

    /**
     * Apply GrayScale Filter to Image
     */
    private void applyFilter(PhotoFilter filterType) {
        try {
            mClickedFilter = true;
            mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                    .setPinchTextScalable(true)
                    .build();
            mPhotoEditor.setFilterEffect(filterType);
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





package swati4star.createpdf.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

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
import swati4star.createpdf.adapter.BrushItemAdapter;
import swati4star.createpdf.adapter.ImageFiltersAdapter;
import swati4star.createpdf.interfaces.OnFilterItemClickedListener;
import swati4star.createpdf.interfaces.OnItemClickListner;
import swati4star.createpdf.model.BrushItem;
import swati4star.createpdf.model.FilterItem;

import static swati4star.createpdf.util.BrushUtils.getBrushItems;
import static swati4star.createpdf.util.Constants.IMAGE_EDITOR_KEY;
import static swati4star.createpdf.util.Constants.RESULT;
import static swati4star.createpdf.util.ImageFilterUtils.getFiltersList;

public class ImageEditor extends AppCompatActivity implements OnFilterItemClickedListener, OnItemClickListner {

    private ArrayList<String> mFilterUris = new ArrayList<>();
    private final ArrayList<String> mImagepaths = new ArrayList<>();
    private ArrayList<FilterItem> mFilterItems;
    private ArrayList<BrushItem> mBrushItems;

    private int mImagesCount;
    private int mDisplaySize;
    private int mCurrentImage = 0;
    private String mFilterName;

    @BindView(R.id.nextimageButton)
    ImageButton mNextButton;
    @BindView(R.id.imagecount)
    TextView mImgcount;
    @BindView(R.id.savecurrent)
    Button saveCurrent;
    @BindView(R.id.previousImageButton)
    ImageButton mPreviousButton;
    @BindView(R.id.resetCurrent)
    Button resetCurrent;
    @BindView(R.id.doodleSeekBar)
    SeekBar doodleSeekBar;
    @BindView(R.id.photoEditorView)
    PhotoEditorView mPhotoEditorView;
    @BindView(R.id.scrollViewImage)
    ScrollView mImageScrollView;
    @BindView(R.id.doodle_colors)
    RecyclerView brushColorsView;

    private boolean mClicked = false;
    private boolean mClickedFilter = false;
    private boolean mDoodleSelected = false;

    private PhotoEditor mPhotoEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);
        ButterKnife.bind(this);

        // Extract images
        mFilterUris = getIntent().getExtras().getStringArrayList(IMAGE_EDITOR_KEY);
        mDisplaySize = mFilterUris.size();
        mImagesCount = mFilterUris.size() - 1;
        mPhotoEditorView.getSource()
                .setImageBitmap(BitmapFactory.decodeFile(mFilterUris.get(0)));
        setImageCount();
        if (mDisplaySize == 1) {
            mNextButton.setVisibility(View.INVISIBLE);
        }
        mFilterItems = getFiltersList(this);
        mBrushItems = getBrushItems();
        mImagepaths.addAll(mFilterUris);
        initRecyclerView();

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build();
        mPhotoEditor.setBrushSize(30);
        doodleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPhotoEditor.setBrushSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mPhotoEditor.setBrushDrawingMode(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.nextimageButton)
    void nextImg() {
        //Proceed to next if Save Current has been clicked
        if (mClicked == mClickedFilter) {
            next();
            incrementImageCount();
            mClicked = false;
            mClickedFilter = false;
        } else
            Toast.makeText(getApplicationContext(), R.string.save_first, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.previousImageButton)
    void previousImg() {
        //move to previous if Save Current has been clicked
        if (mClicked == mClickedFilter) {
            previous();
            decrementImageCount();
            mClicked = false;
            mClickedFilter = false;
        } else
            Toast.makeText(getApplicationContext(), R.string.save_first, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.savecurrent)
    void saveC() {
        mClicked = true;
        if (mClickedFilter || mDoodleSelected) {
            saveCurrentImage();
        } else {
            applyFilter(PhotoFilter.NONE);
            saveCurrentImage();
        }
    }

    @OnClick(R.id.resetCurrent)
    void resetCurrent() {
        String originalPath = mFilterUris.get(mCurrentImage);
        mImagepaths.set(mCurrentImage, originalPath);
        mPhotoEditorView.getSource()
                .setImageBitmap(BitmapFactory.decodeFile(originalPath));
        mPhotoEditor.clearAllViews();
        mPhotoEditor.undo();
    }

    /**
     * Increment image count to display in textView
     */
    private void incrementImageCount() {
        if (mCurrentImage < mImagesCount) {
            setImageCount();
            mPreviousButton.setVisibility(View.VISIBLE);
        } else if (mCurrentImage == mImagesCount) {
            setImageCount();
            mNextButton.setVisibility(View.INVISIBLE);
            mPreviousButton.setVisibility(View.VISIBLE);
        } else {
            mNextButton.setEnabled(false);
        }
    }

    /**
     * Decrement image count to display in textView
     */
    private void decrementImageCount() {
        if (mCurrentImage > 0) {
            setImageCount();
            mNextButton.setVisibility(View.VISIBLE);
        } else if (mCurrentImage == 0) {
            setImageCount();
            mPreviousButton.setVisibility(View.INVISIBLE);
            mNextButton.setVisibility(View.VISIBLE);
        } else {
            mPreviousButton.setEnabled(false);
        }
    }

    private void setImageCount() {
        String sText = "Showing " + String.valueOf(mCurrentImage + 1) + " of " + mDisplaySize;
        mImgcount.setText(sText);
    }

    /**
     * Saves Current Image with applied filter
     */
    private void saveCurrentImage() {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/PDFfilter");
            dir.mkdirs();
            String fileName = String.format(getString(R.string.filter_file_name),
                    String.valueOf(System.currentTimeMillis()), mFilterName);
            File outFile = new File(dir, fileName);
            String imagePath = outFile.getAbsolutePath();

            mPhotoEditor.saveAsFile(imagePath, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String imagePath) {
                    mImagepaths.remove(mCurrentImage);
                    mImagepaths.add(mCurrentImage, imagePath);
                    mPhotoEditorView.getSource()
                            .setImageBitmap(BitmapFactory.decodeFile(mImagepaths.get(mCurrentImage)));
                    Toast.makeText(getApplicationContext(), R.string.filter_saved, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), R.string.filter_not_saved, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    /**
     * Intent to Send Back final edited URIs
     *
     * @param mImagepaths - the images array to be send pack
     */
    private void passUris(ArrayList<String> mImagepaths) {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra(RESULT, mImagepaths);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Display next image on nextImage button click
     */
    private void next() {
        try {
            if (mCurrentImage + 1 <= mImagesCount) {
                mPhotoEditorView.getSource()
                        .setImageBitmap(BitmapFactory.decodeFile(mImagepaths.get(mCurrentImage + 1)));
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
                mPhotoEditorView.getSource()
                        .setImageBitmap(BitmapFactory.decodeFile(mImagepaths.get((mCurrentImage - 1))));
                mCurrentImage--;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        brushColorsView.setLayoutManager(layoutManager2);
        BrushItemAdapter brushItemAdapter = new BrushItemAdapter(this,
                this, mBrushItems);
        brushColorsView.setAdapter(brushItemAdapter);
    }

    /**
     * Get Item Position and call Filter Function
     *
     * @param view     - view which is clicked
     * @param position - position of item clicked
     */
    @Override
    public void onItemClick(View view, int position) {
        // Brush effect is in second position
        if (position == 1) {
            mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                    .setPinchTextScalable(true)
                    .build();
            if (doodleSeekBar.getVisibility() == View.GONE && brushColorsView.getVisibility() == View.GONE) {
                mPhotoEditor.setBrushDrawingMode(true);
                doodleSeekBar.setVisibility(View.VISIBLE);
                brushColorsView.setVisibility(View.VISIBLE);
                mDoodleSelected = true;
            } else if (doodleSeekBar.getVisibility() == View.VISIBLE &&
                    brushColorsView.getVisibility() == View.VISIBLE) {
                mPhotoEditor.setBrushDrawingMode(false);
                doodleSeekBar.setVisibility(View.GONE);
                brushColorsView.setVisibility(View.GONE);
            }
        } else {
            PhotoFilter filter = mFilterItems.get(position).getFilter();
            applyFilter(filter);
        }
    }

    /**
     * Apply Filter to Image
     */
    private void applyFilter(PhotoFilter filterType) {
        try {
            mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                    .setPinchTextScalable(true)
                    .build();
            mPhotoEditor.setFilterEffect(filterType);
            mFilterName = filterType.name();
            mClickedFilter = filterType != PhotoFilter.NONE;
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
                passUris(mImagepaths);
                return true;
            case android.R.id.home:
                new MaterialDialog.Builder(this)
                        .onPositive((dialog, which) -> finish())
                        .title(R.string.filter_cancel_question)
                        .content(R.string.filter_cancel_description)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        passUris(mImagepaths);
    }

    @Override
    public void onItemClick(int position) {
        int color = mBrushItems.get(position).getColor();
        doodleSeekBar.setBackgroundColor(getResources().getColor(color));
        mPhotoEditor.setBrushColor(getResources().getColor(color));
    }

}
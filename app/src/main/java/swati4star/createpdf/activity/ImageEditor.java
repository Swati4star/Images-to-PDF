package swati4star.createpdf.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
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
import swati4star.createpdf.adapter.BrushItemAdapter;
import swati4star.createpdf.adapter.ImageFiltersAdapter;
import swati4star.createpdf.interfaces.OnFilterItemClickedListener;
import swati4star.createpdf.interfaces.OnItemClickListner;
import swati4star.createpdf.model.BrushItem;
import swati4star.createpdf.model.FilterItem;
import swati4star.createpdf.util.ThemeUtils;

import static swati4star.createpdf.util.BrushUtils.getBrushItems;
import static swati4star.createpdf.util.Constants.IMAGE_EDITOR_KEY;
import static swati4star.createpdf.util.Constants.RESULT;
import static swati4star.createpdf.util.ImageFilterUtils.getFiltersList;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class ImageEditor extends AppCompatActivity implements OnFilterItemClickedListener, OnItemClickListner {

    private ArrayList<String> mFilterUris = new ArrayList<>();
    private final ArrayList<String> mImagepaths = new ArrayList<>();
    private ArrayList<FilterItem> mFilterItems;
    private ArrayList<BrushItem> mBrushItems;

    private int mDisplaySize;
    private int mCurrentImage; // 0 by default
    private String mFilterName;

    @BindView(R.id.nextimageButton)
    ImageView mNextButton;
    @BindView(R.id.imagecount)
    TextView mImgcount;
    @BindView(R.id.previousImageButton)
    ImageView mPreviousButton;
    @BindView(R.id.doodleSeekBar)
    SeekBar doodleSeekBar;
    @BindView(R.id.photoEditorView)
    PhotoEditorView mPhotoEditorView;
    @BindView(R.id.doodle_colors)
    RecyclerView brushColorsView;

    private boolean mClicked = true;
    private boolean mClickedFilter = false;
    private boolean mDoodleSelected = false;

    private PhotoEditor mPhotoEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set selected theme
        ThemeUtils.setThemeApp(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);
        ButterKnife.bind(this);

        initValues();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    void initValues() {

        // Extract images
        mFilterUris = getIntent().getStringArrayListExtra(IMAGE_EDITOR_KEY);
        mDisplaySize = mFilterUris.size();
        mFilterItems = getFiltersList(this);
        mBrushItems = getBrushItems();
        mImagepaths.addAll(mFilterUris);

        mPhotoEditorView.getSource()
                .setImageBitmap(BitmapFactory.decodeFile(mFilterUris.get(0)));
        changeAndShowImageCount(0);

        initRecyclerView();

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build();
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

        mPhotoEditor.setBrushSize(30);
        mPhotoEditor.setBrushDrawingMode(false);
    }

    @OnClick(R.id.nextimageButton)
    void nextImg() {
        //Proceed to next if Save Current has been clicked
        if (mClicked) {
            changeAndShowImageCount((mCurrentImage + 1) % mDisplaySize);
        } else
            showSnackbar(this, R.string.save_first);
    }

    @OnClick(R.id.previousImageButton)
    void previousImg() {
        //move to previous if Save Current has been clicked
        if (mClicked) {
            changeAndShowImageCount((mCurrentImage - 1 % mDisplaySize));
        } else
            showSnackbar(this, R.string.save_first);
    }

    // modify current image num & display in textview
    private void changeAndShowImageCount(int count) {

        if (count < 0 || count >= mDisplaySize)
            return;

        mCurrentImage = count % mDisplaySize;
        mPhotoEditorView.getSource()
                .setImageBitmap(BitmapFactory.decodeFile(mImagepaths.get(mCurrentImage)));
        mImgcount.setText(String.format(getString(R.string.showing_image), mCurrentImage + 1, mDisplaySize));
    }

    @OnClick(R.id.savecurrent)
    void saveC() {
        mClicked = true;
        if (mClickedFilter || mDoodleSelected) {
            saveCurrentImage();
            hideBrushEffect();
            mClickedFilter = false;
            mDoodleSelected = false;
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
        //setting mClicked true when none filter is selected otherwise false
        mClicked = position == 0;
        // Brush effect is in second position
        if (position == 1) {
            mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                    .setPinchTextScalable(true)
                    .build();
            if (doodleSeekBar.getVisibility() == View.GONE && brushColorsView.getVisibility() == View.GONE) {
                showBrushEffect();
            } else if (doodleSeekBar.getVisibility() == View.VISIBLE &&
                    brushColorsView.getVisibility() == View.VISIBLE) {
                hideBrushEffect();
            }
        } else {
            applyFilter(mFilterItems.get(position).getFilter());
        }
    }

    /**
     * Shows the brush effect
     */
    private void showBrushEffect() {
        mPhotoEditor.setBrushDrawingMode(true);
        doodleSeekBar.setVisibility(View.VISIBLE);
        brushColorsView.setVisibility(View.VISIBLE);
        mDoodleSelected = true;
    }

    /**
     * Hides brush effect
     */
    private void hideBrushEffect() {
        mPhotoEditor.setBrushDrawingMode(false);
        doodleSeekBar.setVisibility(View.GONE);
        brushColorsView.setVisibility(View.GONE);
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
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra(RESULT, mImagepaths);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onItemClick(int position) {
        int color = mBrushItems.get(position).getColor();
        doodleSeekBar.setBackgroundColor(this.getResources().getColor(color));
        mPhotoEditor.setBrushColor(this.getResources().getColor(color));
    }

    public static Intent getStartIntent(Context context, ArrayList<String> uris) {
        Intent intent = new Intent(context, ImageEditor.class);
        intent.putExtra(IMAGE_EDITOR_KEY, uris);
        return intent;
    }
}
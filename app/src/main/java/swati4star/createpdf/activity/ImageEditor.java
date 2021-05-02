package swati4star.createpdf.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

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
import swati4star.createpdf.interfaces.OnItemClickListener;
import swati4star.createpdf.model.BrushItem;
import swati4star.createpdf.model.FilterItem;
import swati4star.createpdf.util.BrushUtils;
import swati4star.createpdf.util.ImageFilterUtils;
import swati4star.createpdf.util.StringUtils;
import swati4star.createpdf.util.ThemeUtils;

import static swati4star.createpdf.util.Constants.IMAGE_EDITOR_KEY;
import static swati4star.createpdf.util.Constants.RESULT;

public class ImageEditor extends AppCompatActivity implements OnFilterItemClickedListener, OnItemClickListener {

    private ArrayList<String> mFilterUris = new ArrayList<>();
    private final ArrayList<String> mImagePaths = new ArrayList<>();
    private ArrayList<FilterItem> mFilterItems;
    private ArrayList<BrushItem> mBrushItems;

    private int mDisplaySize;
    private int mCurrentImage; // 0 by default
    private String mFilterName;

    @BindView(R.id.nextimageButton)
    ImageView nextButton;
    @BindView(R.id.imagecount)
    TextView imageCount;
    @BindView(R.id.previousImageButton)
    ImageView previousButton;
    @BindView(R.id.doodleSeekBar)
    SeekBar doodleSeekBar;
    @BindView(R.id.photoEditorView)
    PhotoEditorView photoEditorView;
    @BindView(R.id.doodle_colors)
    RecyclerView brushColorsView;

    private boolean mClicked = true;
    private boolean mClickedFilter = false;
    private boolean mDoodleSelected = false;

    private PhotoEditor mPhotoEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set selected theme
        ThemeUtils.getInstance().setThemeApp(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);
        ButterKnife.bind(this);

        initValues();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void initValues() {

        // Extract images
        mFilterUris = getIntent().getStringArrayListExtra(IMAGE_EDITOR_KEY);
        mDisplaySize = mFilterUris.size();
        mFilterItems = ImageFilterUtils.getInstance().getFiltersList(this);
        mBrushItems = BrushUtils.getInstance().getBrushItems();
        mImagePaths.addAll(mFilterUris);

        photoEditorView.getSource()
                .setImageBitmap(BitmapFactory.decodeFile(mFilterUris.get(0)));
        changeAndShowImageCount(0);

        initRecyclerView();

        mPhotoEditor = new PhotoEditor.Builder(this, photoEditorView)
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
            StringUtils.getInstance().showSnackbar(this, R.string.save_first);
    }

    @OnClick(R.id.previousImageButton)
    void previousImg() {
        //move to previous if Save Current has been clicked
        if (mClicked) {
            changeAndShowImageCount((mCurrentImage - 1 % mDisplaySize));
        } else
            StringUtils.getInstance().showSnackbar(this, R.string.save_first);
    }

    // modify current image num & display in text view
    private void changeAndShowImageCount(int count) {

        if (count < 0 || count >= mDisplaySize)
            return;

        mCurrentImage = count % mDisplaySize;
        photoEditorView.getSource()
                .setImageBitmap(BitmapFactory.decodeFile(mImagePaths.get(mCurrentImage)));
        imageCount.setText(String.format(getString(R.string.showing_image), mCurrentImage + 1, mDisplaySize));
    }

    @OnClick(R.id.savecurrent)
    void saveC() {
        mClicked = true;
        if (mClickedFilter || mDoodleSelected) {
            saveCurrentImage();
            showHideBrushEffect(false);
            mClickedFilter = false;
            mDoodleSelected = false;
        }
    }

    @OnClick(R.id.resetCurrent)
    void resetCurrent() {
        mClicked = true;
        String originalPath = mFilterUris.get(mCurrentImage);
        mImagePaths.set(mCurrentImage, originalPath);
        photoEditorView.getSource()
                .setImageBitmap(BitmapFactory.decodeFile(originalPath));
        mPhotoEditor.clearAllViews();
        mPhotoEditor.undo();
    }


    /**
     * Saves Current Image with applied filter
     */
    private void saveCurrentImage() {
        try {
            File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File dir = new File(sdCard.getAbsolutePath() + "/PDFfilter");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = String.format(getString(R.string.filter_file_name),
                    String.valueOf(System.currentTimeMillis()), mFilterName);
            File outFile = new File(dir, fileName);
            String imagePath = outFile.getAbsolutePath();

            mPhotoEditor.saveAsFile(imagePath, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String imagePath) {
                    mImagePaths.remove(mCurrentImage);
                    mImagePaths.add(mCurrentImage, imagePath);
                    photoEditorView.getSource()
                            .setImageBitmap(BitmapFactory.decodeFile(mImagePaths.get(mCurrentImage)));
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
            mPhotoEditor = new PhotoEditor.Builder(this, photoEditorView)
                    .setPinchTextScalable(true)
                    .build();
            if (doodleSeekBar.getVisibility() == View.GONE && brushColorsView.getVisibility() == View.GONE) {
                showHideBrushEffect(true);
            } else if (doodleSeekBar.getVisibility() == View.VISIBLE &&
                    brushColorsView.getVisibility() == View.VISIBLE) {
                showHideBrushEffect(false);
            }
        } else {
            applyFilter(mFilterItems.get(position).getFilter());
        }
    }

    /**
     * Shows the brush effect
     */
    private void showHideBrushEffect(boolean show) {
        mPhotoEditor.setBrushDrawingMode(show);
        doodleSeekBar.setVisibility(show ? View.VISIBLE : View.GONE);
        brushColorsView.setVisibility(show ? View.VISIBLE : View.GONE);
        mDoodleSelected = true;
    }

    /**
     * Apply Filter to Image
     */
    private void applyFilter(PhotoFilter filterType) {
        try {
            mPhotoEditor = new PhotoEditor.Builder(this, photoEditorView)
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
        returnIntent.putStringArrayListExtra(RESULT, mImagePaths);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onItemClick(int position) {
        int color = mBrushItems.get(position).getColor();
        if (position == mBrushItems.size() - 1) {
            final MaterialDialog colorPallete = new MaterialDialog.Builder(this)
                    .title(R.string.choose_color_text)
                    .customView(R.layout.color_pallete_layout, true)
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .build();
            final View mPositiveAction = colorPallete.getActionButton(DialogAction.POSITIVE);
            final ColorPickerView colorPickerInput = colorPallete.getCustomView().findViewById(R.id.color_pallete);

            mPositiveAction.setEnabled(true);
            mPositiveAction.setOnClickListener(v -> {
                try {
                    doodleSeekBar.setBackgroundColor(colorPickerInput.getColor());
                    mPhotoEditor.setBrushColor(colorPickerInput.getColor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                colorPallete.dismiss();
            });
            colorPallete.show();

        } else {
            doodleSeekBar.setBackgroundColor(this.getResources().getColor(color));
            mPhotoEditor.setBrushColor(this.getResources().getColor(color));
        }
    }

    public static Intent getStartIntent(Context context, ArrayList<String> uris) {
        Intent intent = new Intent(context, ImageEditor.class);
        intent.putExtra(IMAGE_EDITOR_KEY, uris);
        return intent;
    }
}

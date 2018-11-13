package swati4star.createpdf.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.fragment.ImageToPdfFragment;
import swati4star.createpdf.util.FileUtils;

import static swati4star.createpdf.util.Constants.pdfDirectory;

public class CropImageActivity extends AppCompatActivity {

    private int mCurrentImageIndex = 0;
    private ArrayList<String> mImages;
    private HashMap<Integer, Uri> mCroppedImageUris = new HashMap<Integer, Uri>();

    @BindView(R.id.imagecount)
    TextView mImagecount;

    @BindView(R.id.cropImageView)
    CropImageView mCropImageView;

    @BindView(R.id.cropButton)
    Button cropImageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image_activity);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);

        setUpCropImageView();

        mImages = ImageToPdfFragment.mImagesUri;

        setImage(0);
    }

    @OnClick(R.id.cropButton)
    public void cropButtonClicked() {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + pdfDirectory);
        Uri uri = mCropImageView.getImageUri();
        String path = uri.getPath();
        String fname = "cropped_im";
        if (path != null)
            fname = "cropped_" + FileUtils.getFileName(path);

        File file = new File(myDir, fname);

        mCropImageView.saveCroppedImageAsync(Uri.fromFile(file));
    }

    @OnClick(R.id.rotateButton)
    public void rotateButtonClicked() {
        mCropImageView.rotateImage(90);
    }

    @OnClick(R.id.nextimageButton)
    public void nextImageClicked() {
        if (mCurrentImageIndex == mImages.size() - 1) {
            setImage(0);
        } else {
            setImage(mCurrentImageIndex + 1);
        }
    }

    @OnClick(R.id.previousImageButton)
    public void prevImgBtnClicked() {
        if (mCurrentImageIndex == 0) {

            setImage(mImages.size() - 1);
        } else {
            setImage(mCurrentImageIndex - 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_crop_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            case R.id.action_done:
                Intent intent = new Intent();
                intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, mCroppedImageUris);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initial setup of crop image view
     */
    private void setUpCropImageView() {
        mCropImageView.setOnCropImageCompleteListener((CropImageView view, CropImageView.CropResult result) -> {
            mCroppedImageUris.put(mCurrentImageIndex, result.getUri());
            Toast.makeText(CropImageActivity.this, R.string.image_successfully_cropped, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Set image in cropimage view & increment counters
     * @param index - image index
     */
    private void setImage(int index) {
        mImagecount.setText(getString(R.string.cropImage_activityTitle) + (index + 1));
        mCurrentImageIndex = index;
        mCropImageView.setImageUriAsync(Uri.fromFile(new File(mImages.get(index))));
    }
}

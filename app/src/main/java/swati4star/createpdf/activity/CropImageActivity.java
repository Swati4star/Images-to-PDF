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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import swati4star.createpdf.R;
import swati4star.createpdf.fragment.ImageToPdfFragment;
import swati4star.createpdf.util.FileUtils;

import static swati4star.createpdf.util.Constants.pdfDirectory;

public class CropImageActivity extends AppCompatActivity {

    private int mCurrentImageIndex = 0;
    private ArrayList<String> mImages;
    private HashMap<Integer, Uri> mCroppedImageUris = new HashMap<Integer, Uri>();

    private TextView mImagecount;
    private CropImageView mCropImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);

        setUpCropImageView();

        Button cropImageButton = findViewById(R.id.cropButton);
        cropImageButton.setOnClickListener((View v) -> {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + pdfDirectory);
            String fname = "cropped_" + FileUtils.getFileName(mCropImageView.getImageUri().getPath());

            File file = new File(myDir, fname);

            mCropImageView.saveCroppedImageAsync(Uri.fromFile(file));
        });

        Button rotateButton = findViewById(R.id.rotateButton);
        rotateButton.setOnClickListener((View v) -> {
            mCropImageView.rotateImage(90);
        });

        mImagecount = findViewById(R.id.imagecount);

        ImageView nextimageButton = findViewById(R.id.nextimageButton);
        nextimageButton.setOnClickListener((View v) -> {
            if (mCurrentImageIndex == mImages.size() - 1) {
                setImage(0);
            } else {
                setImage(mCurrentImageIndex + 1);
            }
        });

        mImages = ImageToPdfFragment.mImagesUri;

        setImage(0);

        /*  .setActivityMenuIconColor(mMorphButtonUtility.color(R.color.colorPrimary))
                .setInitialCropWindowPaddingRatio(0)
                .setAllowRotation(true)
                .setActivityTitle(getString(R.string.cropImage_activityTitle) + (mImageCounter + 1))
                .start(mActivity, this);*/
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

    private void setUpCropImageView() {
        mCropImageView = findViewById(R.id.cropImageView);
        mCropImageView.setOnCropImageCompleteListener((CropImageView view, CropImageView.CropResult result) -> {
            mCroppedImageUris.put(mCurrentImageIndex, result.getUri());
            Toast.makeText(CropImageActivity.this, R.string.image_successfully_cropped, Toast.LENGTH_SHORT).show();
        });
    }

    private void setImage(int index) {
        mImagecount.setText(getString(R.string.cropImage_activityTitle) + (index + 1));

        mCurrentImageIndex = index;
        mCropImageView.setImageUriAsync(Uri.fromFile(new File(mImages.get(index))));
    }
}

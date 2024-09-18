package swati4star.createpdf.activity;

import static swati4star.createpdf.util.Constants.pdfDirectory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.fragment.ImageToPdfFragment;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.StringUtils;

public class CropImageActivity extends AppCompatActivity {

    private final HashMap<Integer, Uri> mCroppedImageUris = new HashMap<>();
    private int mCurrentImageIndex = 0;
    private ArrayList<String> mImages;
    private boolean mCurrentImageEdited = false;
    private boolean mFinishedClicked = false;
    private CropImageView mCropImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image_activity);
        ButterKnife.bind(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mCropImageView = findViewById(R.id.cropImageView);

        setUpCropImageView();

        mImages = ImageToPdfFragment.mImagesUri;
        mFinishedClicked = false;

        for (int i = 0; i < mImages.size(); i++)
            mCroppedImageUris.put(i, Uri.fromFile(new File(mImages.get(i))));

        if (mImages.size() == 0)
            finish();

        setImage(0);
        Button cropImageButton = findViewById(R.id.cropButton);
        cropImageButton.setOnClickListener(view -> cropButtonClicked());

        Button rotateButton = findViewById(R.id.rotateButton);
        rotateButton.setOnClickListener(view -> rotateButtonClicked());

        ImageView nextImageButton = findViewById(R.id.nextimageButton);
        nextImageButton.setOnClickListener(view -> nextImageClicked());
        ImageView previousImageButton = findViewById(R.id.previousImageButton);
        previousImageButton.setOnClickListener(view -> prevImgBtnClicked());
    }

    public void cropButtonClicked() {

        // 假设存在一个函数检查是否有PDF文件创建
        boolean isPdfCreated = checkIfPdfCreated();
        private boolean checkIfPdfCreated() {
            AppDatabase db = AppDatabase.getDatabase(mContext.getApplicationContext());
            // 你需要创建一个查询方法来检查是否存在PDF记录
            // 假设历史记录中包含一个字段标识操作类型
            // 这里假设operationType为"PDF_CREATED"，具体取决于你的实现
            String operationTypeToCheck = "PDF_CREATED";

            // 查询是否有任何记录的操作类型为"PDF_CREATED"
            return db.historyDao().existsOperationType(operationTypeToCheck);
        }



        Button deleteButton = findViewById(R.id.btn_delete);

        if (isPdfCreated) {
            deleteButton.setEnabled(true); // 启用删除按钮
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 删除PDF的代码逻辑
                    mCurrentImageEdited = false;
                    String root = Environment.getExternalStorageDirectory().toString();
                    File folder = new File(root + pdfDirectory);
                    Uri uri = mCropImageView.getImageUri();

                    if (uri == null) {
                        StringUtils.getInstance().showSnackbar(this, R.string.error_uri_not_found);
                        return;
                    }

                    String path = uri.getPath();
                    String filename = "cropped_im";
                    if (path != null)
                        filename = "cropped_" + FileUtils.getFileName(path);

                    File file = new File(folder, filename);

                    mCropImageView.saveCroppedImageAsync(Uri.fromFile(file));
                }
            });
        } else {
            deleteButton.setEnabled(false); // 禁用删除按钮
        }


    }

    public void rotateButtonClicked() {
        mCurrentImageEdited = true;
        mCropImageView.rotateImage(90);
    }

    public void nextImageClicked() {
        if (mImages.size() == 0)
            return;

        if (!mCurrentImageEdited) {
            mCurrentImageIndex = (mCurrentImageIndex + 1) % mImages.size();
            setImage(mCurrentImageIndex);
        } else {
            StringUtils.getInstance().showSnackbar(this, R.string.save_first);
        }
    }

    public void prevImgBtnClicked() {
        if (mImages.size() == 0)
            return;

        if (!mCurrentImageEdited) {
            if (mCurrentImageIndex == 0) {
                mCurrentImageIndex = mImages.size();
            }
            mCurrentImageIndex = (mCurrentImageIndex - 1) % mImages.size();
            setImage(mCurrentImageIndex);
        } else {
            StringUtils.getInstance().showSnackbar(this, R.string.save_first);
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
        if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        } else if (item.getItemId() == R.id.action_done) {
            mFinishedClicked = true;
            cropButtonClicked();
        } else if (item.getItemId() == R.id.action_skip) {
            mCurrentImageEdited = false;
            nextImageClicked();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initial setup of crop image view
     */
    private void setUpCropImageView() {
        mCropImageView.setOnCropImageCompleteListener((CropImageView view, CropImageView.CropResult result) -> {
            mCroppedImageUris.put(mCurrentImageIndex, result.getUri());
            mCropImageView.setImageUriAsync(mCroppedImageUris.get(mCurrentImageIndex));

            if (mFinishedClicked) {
                Intent intent = new Intent();
                intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, mCroppedImageUris);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * Set image in crop image view & increment counters
     *
     * @param index - image index
     */
    private void setImage(int index) {

        mCurrentImageEdited = false;
        if (index < 0 || index >= mImages.size())
            return;
        TextView mImageCount = findViewById(R.id.imagecount);
        mImageCount.setText(String.format("%s %d of %d", getString(R.string.cropImage_activityTitle)
                , index + 1, mImages.size()));
        mCropImageView.setImageUriAsync(mCroppedImageUris.get(index));
    }
}

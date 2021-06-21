package swati4star.createpdf.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.PreviewAdapter;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.DirectoryUtils;
import swati4star.createpdf.util.Preference;
import swati4star.createpdf.util.RealPathUtil;
import swati4star.createpdf.util.ThemeUtils;

import static swati4star.createpdf.util.Constants.PREVIEW_IMAGES;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION_URI;

public class ImagesPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_images);

        ButterKnife.bind(this);
        // Extract mImagesArrayList uri array from the intent
        Intent intent = getIntent();
        ArrayList<String> mImagesArrayList = intent.getStringArrayListExtra(PREVIEW_IMAGES);

        ViewPager mViewPager = findViewById(R.id.viewpager);
        PreviewAdapter mPreviewAdapter = new PreviewAdapter(this, mImagesArrayList);
        mViewPager.setAdapter(mPreviewAdapter);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    /**
     * get start intent for this activity
     * @param context - context to start activity from
     * @param uris - extra images uri
     * @return - start intent
     */
    public static Intent getStartIntent(Context context, ArrayList<String>  uris) {
        Intent intent = new Intent(context, ImagesPreviewActivity.class);
        intent.putExtra(PREVIEW_IMAGES, uris);
        return intent;
    }

    private void checkAndAskForStorageDir() {
        if (Preference.getStringPref(this, STORAGE_LOCATION).isEmpty() || DirectoryUtils.isStorageDirNotExist(this)) {
            askUserToSelectStorageDir();
        }
    }

    private void askUserToSelectStorageDir() {
        new MaterialAlertDialogBuilder(this).setTitle("Storage folder not found!")
                .setMessage("Storage directory not found. Please select a folder to save PDF")
                .setCancelable(false)
                .setNeutralButton("Choose Folder", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    startActivityForResult(intent, Constants.REQUEST_CODE_FOR_ACTION_OPEN_DOCUMENT_TREE);
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || resultCode != RESULT_OK)
            return;
        if (requestCode == Constants.REQUEST_CODE_FOR_ACTION_OPEN_DOCUMENT_TREE) {
            Uri uri = DocumentsContract.buildDocumentUriUsingTree(data.getData(), DocumentsContract.getTreeDocumentId(data.getData()));
            String storagePath = RealPathUtil.getInstance().getRealPath(this, uri);
            Preference.setStringPref(this, STORAGE_LOCATION, storagePath);
            Preference.setStringPref(this, STORAGE_LOCATION_URI, data.getData().toString());
            DirectoryUtils.getPersistablePermissionOfStorageDir(this, data.getData());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAndAskForStorageDir();
    }
}
package swati4star.createpdf.activity;

import static swati4star.createpdf.util.Constants.PREVIEW_IMAGES;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.eftimoff.viewpagertransformers.DepthPageTransformer;

import java.util.ArrayList;

import swati4star.createpdf.adapter.PreviewAdapter;
import swati4star.createpdf.databinding.ActivityPreviewImagesBinding;
import swati4star.createpdf.util.ThemeUtils;

public class ImagesPreviewActivity extends AppCompatActivity {

    private ActivityPreviewImagesBinding mBinding;

    /**
     * get start intent for this activity
     *
     * @param context - context to start activity from
     * @param uris    - extra images uri
     * @return - start intent
     */
    public static Intent getStartIntent(Context context, ArrayList<String> uris) {
        Intent intent = new Intent(context, ImagesPreviewActivity.class);
        intent.putExtra(PREVIEW_IMAGES, uris);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(savedInstanceState);

        mBinding = ActivityPreviewImagesBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        // Extract mImagesArrayList uri array from the intent
        Intent intent = getIntent();
        ArrayList<String> mImagesArrayList = intent.getStringArrayListExtra(PREVIEW_IMAGES);

        PreviewAdapter mPreviewAdapter = new PreviewAdapter(this, mImagesArrayList);
        mBinding.viewpager.setAdapter(mPreviewAdapter);
        mBinding.viewpager.setPageTransformer(true, new DepthPageTransformer());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}
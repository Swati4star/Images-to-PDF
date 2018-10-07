package swati4star.createpdf.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.eftimoff.viewpagertransformers.DepthPageTransformer;

import java.util.ArrayList;

import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.PreviewAdapter;
import swati4star.createpdf.util.ThemeUtils;

import static swati4star.createpdf.util.Constants.PREVIEW_IMAGES;

public class ImagesPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.setThemeApp(this);
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
     * get start intent for this axtivity
     * @param context - context to start activity from
     * @param uris - extra images uri
     * @return - start intent
     */
    public static Intent getStartIntent(Context context, ArrayList<String>  uris) {
        Intent intent = new Intent(context, ImagesPreviewActivity.class);
        intent.putExtra(PREVIEW_IMAGES, uris);
        return intent;
    }
}
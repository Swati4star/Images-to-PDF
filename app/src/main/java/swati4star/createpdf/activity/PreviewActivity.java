package swati4star.createpdf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.eftimoff.viewpagertransformers.DepthPageTransformer;

import java.util.ArrayList;
import java.util.Objects;

import swati4star.createpdf.R;
import swati4star.createpdf.adapter.PreviewAdapter;

import static swati4star.createpdf.util.Constants.PREVIEW_IMAGES;

public class PreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        // Extract images uri array from the intent
        Intent intent = getIntent();
        ArrayList<String> images = intent.getStringArrayListExtra(PREVIEW_IMAGES);

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new PreviewAdapter(this, images));
        viewPager.setPageTransformer(true, new DepthPageTransformer());

        Objects.requireNonNull(getSupportActionBar()).hide();
    }

}
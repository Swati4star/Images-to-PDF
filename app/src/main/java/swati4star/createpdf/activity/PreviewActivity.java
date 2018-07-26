package swati4star.createpdf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.eftimoff.viewpagertransformers.DepthPageTransformer;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.PreviewAdapter;
import swati4star.createpdf.adapter.PreviewImageOptionsAdapter;
import swati4star.createpdf.model.PreviewImageOptionItem;

import static swati4star.createpdf.util.Constants.PREVIEW_IMAGES;

public class PreviewActivity extends AppCompatActivity implements PreviewImageOptionsAdapter.OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private ArrayList<String> mImagesArrayList;
    private static final int INTENT_REQUEST_REARRANGE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        ButterKnife.bind(this);
        // Extract mImagesArrayList uri array from the intent
        Intent intent = getIntent();
        mImagesArrayList = intent.getStringArrayListExtra(PREVIEW_IMAGES);

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new PreviewAdapter(this, mImagesArrayList));
        viewPager.setPageTransformer(true, new DepthPageTransformer());

        Objects.requireNonNull(getSupportActionBar()).hide();
        showOptions();
    }

    private void showOptions() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        PreviewImageOptionsAdapter adapter = new PreviewImageOptionsAdapter(this, getOptions(),
                getApplicationContext());
        mRecyclerView.setAdapter(adapter);
    }

    private ArrayList<PreviewImageOptionItem> getOptions() {
        ArrayList<PreviewImageOptionItem> mOptions = new ArrayList<>();
        mOptions.add(new PreviewImageOptionItem(R.drawable.ic_rearrange, getString(R.string.rearrange_text)));
        return mOptions;
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                reArrangeImages();
                break;
        }
    }

    private void reArrangeImages() {
        Intent intent = new Intent(this, ReArrangeImages.class);
        intent.putStringArrayListExtra(PREVIEW_IMAGES, mImagesArrayList);
        startActivityForResult(intent, INTENT_REQUEST_REARRANGE_IMAGE);
    }

    private void passUris() {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra("result", mImagesArrayList);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INTENT_REQUEST_REARRANGE_IMAGE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        try {
                            mImagesArrayList = data.getStringArrayListExtra("result");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        passUris();
    }
}
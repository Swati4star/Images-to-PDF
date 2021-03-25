package swati4star.createpdf.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.RearrangeImagesAdapter;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.ImageSortUtils;
import swati4star.createpdf.util.ThemeUtils;

import static swati4star.createpdf.util.Constants.CHOICE_REMOVE_IMAGE;
import static swati4star.createpdf.util.Constants.PREVIEW_IMAGES;

public class RearrangeImages extends AppCompatActivity implements RearrangeImagesAdapter.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private ArrayList<String> mImages;
    private RearrangeImagesAdapter mRearrangeImagesAdapter;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rearrange_images);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mImages = intent.getStringArrayListExtra(PREVIEW_IMAGES);
        initRecyclerView(mImages);
    }

    private void initRecyclerView(ArrayList<String> images) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        mRearrangeImagesAdapter = new RearrangeImagesAdapter(this, images, this);
        recyclerView.setAdapter(mRearrangeImagesAdapter);
    }

    @Override
    public void onUpClick(int position) {
        mImages.add(position - 1, mImages.remove(position));
        mRearrangeImagesAdapter.positionChanged(mImages);
    }

    @Override
    public void onDownClick(int position) {
        mImages.add(position + 1, mImages.remove(position));
        mRearrangeImagesAdapter.positionChanged(mImages);

    }

    @Override
    public void onRemoveClick(int position) {
        if (mSharedPreferences.getBoolean(Constants.CHOICE_REMOVE_IMAGE, false)) {
            mImages.remove(position);
            mRearrangeImagesAdapter.positionChanged(mImages);
        } else {
            MaterialDialog.Builder builder = DialogUtils.getInstance().createWarningDialog(this,
                    R.string.remove_image_message);
            builder.checkBoxPrompt(getString(R.string.dont_show_again), false, null)
                    .onPositive((dialog, which) -> {
                        if (dialog.isPromptCheckBoxChecked()) {
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putBoolean(CHOICE_REMOVE_IMAGE, true);
                            editor.apply();
                        }
                        mImages.remove(position);
                        mRearrangeImagesAdapter.positionChanged(mImages);

                    })
                    .show();
        }
    }

    private void passUris() {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra(Constants.RESULT, mImages);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        passUris();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            passUris();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getStartIntent(Context context, ArrayList<String>  uris) {
        Intent intent = new Intent(context, RearrangeImages.class);
        intent.putExtra(PREVIEW_IMAGES, uris);
        return intent;
    }

    private void sortImages() {
        new MaterialDialog.Builder(this)
                .title(R.string.sort_by_title)
                .items(R.array.sort_options_images)
                .itemsCallback((dialog, itemView, position, text) -> {
                    ImageSortUtils.getInstance().performSortOperation(position, mImages);
                    mRearrangeImagesAdapter.positionChanged(mImages);
                })
                .negativeText(R.string.cancel)
                .show();
    }

    @OnClick(R.id.sort)
    void sortImg() {
        sortImages();
    }
}


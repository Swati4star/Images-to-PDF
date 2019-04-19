package swati4star.createpdf.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.RearrangePdfAdapter;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.ThemeUtils;

import static swati4star.createpdf.util.Constants.CHOICE_REMOVE_IMAGE;
import static swati4star.createpdf.util.Constants.RESULT;
import static swati4star.createpdf.util.Constants.SAMEFILE;
import static swati4star.createpdf.util.DialogUtils.createWarningDialog;

public class RearrangePdfPages extends AppCompatActivity implements RearrangePdfAdapter.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.sort)
    Button sortButton;

    public static ArrayList<Bitmap> mImages;
    private RearrangePdfAdapter mRearrangeImagesAdapter;
    private SharedPreferences mSharedPreferences;
    private ArrayList<Integer> mSequence, mInitialSequence;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ThemeUtils.setThemeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rearrange_images);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        mSequence = new ArrayList<>();
        mInitialSequence = new ArrayList<>();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        sortButton.setVisibility(View.GONE);
        if (mImages == null || mImages.size() < 1) {
            finish();
        } else
            initRecyclerView(mImages);
    }

    private void initRecyclerView(ArrayList<Bitmap> images) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        mRearrangeImagesAdapter = new RearrangePdfAdapter(this, images, this);
        recyclerView.setAdapter(mRearrangeImagesAdapter);
        mSequence = new ArrayList<>();
        for ( int i = 0; i < images.size(); i++) {
            mSequence.add(i + 1);
        }
        mInitialSequence.addAll(mSequence);
    }

    void swap (int pos1, int pos2) {

        if (pos1 >= mSequence.size())
            return;

        int val = mSequence.get(pos1);
        mSequence.set(pos1, mSequence.get(pos2));
        mSequence.set(pos2, val);
    }

    @Override
    public void onUpClick(int position) {
        mImages.add(position - 1, mImages.remove(position));
        mRearrangeImagesAdapter.positionChanged(mImages);
        swap(position, position - 1);
    }

    @Override
    public void onDownClick(int position) {
        mImages.add(position + 1, mImages.remove(position));
        mRearrangeImagesAdapter.positionChanged(mImages);
        swap(position, position + 1);
    }

    @Override
    public void onRemoveClick(int position) {
        if (mSharedPreferences.getBoolean(Constants.CHOICE_REMOVE_IMAGE, false)) {
            mImages.remove(position);
            mRearrangeImagesAdapter.positionChanged(mImages);
            mSequence.remove(position);
        } else {
            MaterialDialog.Builder builder = createWarningDialog(this,
                    R.string.remove_page_message);
            builder.checkBoxPrompt(getString(R.string.dont_show_again), false, null)
                    .onPositive((dialog, which) -> {
                        if (dialog.isPromptCheckBoxChecked()) {
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putBoolean(CHOICE_REMOVE_IMAGE, true);
                            editor.apply();
                        }
                        mImages.remove(position);
                        mRearrangeImagesAdapter.positionChanged(mImages);
                        mSequence.remove(position);
                    })
                    .show();
        }
    }

    private void passUris() {
        Intent returnIntent = new Intent();
        StringBuilder result = new StringBuilder();
        for ( int x : mSequence)
            result.append(x).append(",");
        returnIntent.putExtra(RESULT, result.toString());
        boolean sameFile = mInitialSequence.equals(mSequence);
        returnIntent.putExtra(SAMEFILE, sameFile);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                passUris();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, RearrangePdfPages.class);
    }
}


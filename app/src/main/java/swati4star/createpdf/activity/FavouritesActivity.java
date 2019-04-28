package swati4star.createpdf.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import swati4star.createpdf.R;

import static swati4star.createpdf.util.Constants.ADD_IMAGES_KEY;
import static swati4star.createpdf.util.Constants.ADD_PASSWORD_KEY;
import static swati4star.createpdf.util.Constants.ADD_WATERMARK_KEY;
import static swati4star.createpdf.util.Constants.COMPRESS_PDF_KEY;
import static swati4star.createpdf.util.Constants.EXCEL_TO_PDF_KEY;
import static swati4star.createpdf.util.Constants.EXTRACT_IMAGES_KEY;
import static swati4star.createpdf.util.Constants.HISTORY_KEY;
import static swati4star.createpdf.util.Constants.IMAGE_TO_PDF_KEY;
import static swati4star.createpdf.util.Constants.INVERT_PDF_KEY;
import static swati4star.createpdf.util.Constants.MERGE_PDF_KEY;
import static swati4star.createpdf.util.Constants.PDF_TO_IMAGES_KEY;
import static swati4star.createpdf.util.Constants.QR_BARCODE_KEY;
import static swati4star.createpdf.util.Constants.REMOVE_DUPLICATE_PAGES_KEY;
import static swati4star.createpdf.util.Constants.REMOVE_PAGES_KEY;
import static swati4star.createpdf.util.Constants.REMOVE_PASSWORD_KEY;
import static swati4star.createpdf.util.Constants.REORDER_PAGES_KEY;
import static swati4star.createpdf.util.Constants.ROTATE_PAGES_KEY;
import static swati4star.createpdf.util.Constants.SPLIT_PDF_KEY;
import static swati4star.createpdf.util.Constants.TEXT_TO_PDF_KEY;
import static swati4star.createpdf.util.Constants.VIEW_FILES_KEY;
import static swati4star.createpdf.util.Constants.ZIP_TO_PDF_KEY;

public class FavouritesActivity extends AppCompatActivity {

    private SharedPreferences mSharedpreferences;
    private boolean[] mKeyState = new boolean[21];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.add_to_favourite);
        mSharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        setContentView(R.layout.fav_pref_screen);

        storeInitialState();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourite_pref_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.fav_action_done:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        onBackPressedState();
        super.onBackPressed();
    }

    /**
     * store the intial state of checkbox before
     * the user make new changes
     */
    private void storeInitialState() {
        mKeyState[0] = mSharedpreferences.getBoolean(IMAGE_TO_PDF_KEY, false);
        mKeyState[1] = mSharedpreferences.getBoolean(TEXT_TO_PDF_KEY, false);
        mKeyState[2] = mSharedpreferences.getBoolean(QR_BARCODE_KEY, false);
        mKeyState[3] = mSharedpreferences.getBoolean(VIEW_FILES_KEY, false);
        mKeyState[4] = mSharedpreferences.getBoolean(HISTORY_KEY, false);
        mKeyState[5] = mSharedpreferences.getBoolean(ADD_PASSWORD_KEY, false);
        mKeyState[6] = mSharedpreferences.getBoolean(REMOVE_PASSWORD_KEY, false);
        mKeyState[7] = mSharedpreferences.getBoolean(ROTATE_PAGES_KEY, false);
        mKeyState[8] = mSharedpreferences.getBoolean(ADD_WATERMARK_KEY, false);
        mKeyState[9] = mSharedpreferences.getBoolean(ADD_IMAGES_KEY, false);
        mKeyState[10] = mSharedpreferences.getBoolean(MERGE_PDF_KEY, false);
        mKeyState[11] = mSharedpreferences.getBoolean(SPLIT_PDF_KEY, false);
        mKeyState[12] = mSharedpreferences.getBoolean(INVERT_PDF_KEY, false);
        mKeyState[13] = mSharedpreferences.getBoolean(COMPRESS_PDF_KEY, false);
        mKeyState[14] = mSharedpreferences.getBoolean(REMOVE_DUPLICATE_PAGES_KEY, false);
        mKeyState[15] = mSharedpreferences.getBoolean(REMOVE_PAGES_KEY, false);
        mKeyState[16] = mSharedpreferences.getBoolean(REORDER_PAGES_KEY, false);
        mKeyState[17] = mSharedpreferences.getBoolean(EXTRACT_IMAGES_KEY, false);
        mKeyState[18] = mSharedpreferences.getBoolean(PDF_TO_IMAGES_KEY, false);
        mKeyState[19] = mSharedpreferences.getBoolean(EXCEL_TO_PDF_KEY, false);
        mKeyState[20] = mSharedpreferences.getBoolean(ZIP_TO_PDF_KEY, false);
    }
    /**
     * Restore the intial state if user
     * press the back button
     */
    private void onBackPressedState() {
        mSharedpreferences.edit().putBoolean(IMAGE_TO_PDF_KEY, mKeyState[0] ).apply();
        mSharedpreferences.edit().putBoolean(TEXT_TO_PDF_KEY, mKeyState[1] ).apply();
        mSharedpreferences.edit().putBoolean(QR_BARCODE_KEY, mKeyState[2] ).apply();
        mSharedpreferences.edit().putBoolean(VIEW_FILES_KEY, mKeyState[3] ).apply();
        mSharedpreferences.edit().putBoolean(HISTORY_KEY, mKeyState[4] ).apply();
        mSharedpreferences.edit().putBoolean(ADD_PASSWORD_KEY, mKeyState[5] ).apply();
        mSharedpreferences.edit().putBoolean(REMOVE_PASSWORD_KEY, mKeyState[6] ).apply();
        mSharedpreferences.edit().putBoolean(ROTATE_PAGES_KEY, mKeyState[7] ).apply();
        mSharedpreferences.edit().putBoolean(ADD_WATERMARK_KEY, mKeyState[8] ).apply();
        mSharedpreferences.edit().putBoolean(ADD_IMAGES_KEY, mKeyState[9] ).apply();
        mSharedpreferences.edit().putBoolean(MERGE_PDF_KEY, mKeyState[10] ).apply();
        mSharedpreferences.edit().putBoolean(SPLIT_PDF_KEY, mKeyState[11] ).apply();
        mSharedpreferences.edit().putBoolean(INVERT_PDF_KEY, mKeyState[12] ).apply();
        mSharedpreferences.edit().putBoolean(COMPRESS_PDF_KEY, mKeyState[13] ).apply();
        mSharedpreferences.edit().putBoolean(REMOVE_DUPLICATE_PAGES_KEY, mKeyState[14] ).apply();
        mSharedpreferences.edit().putBoolean(REMOVE_PAGES_KEY, mKeyState[15] ).apply();
        mSharedpreferences.edit().putBoolean(REORDER_PAGES_KEY, mKeyState[16] ).apply();
        mSharedpreferences.edit().putBoolean(EXTRACT_IMAGES_KEY, mKeyState[17] ).apply();
        mSharedpreferences.edit().putBoolean(PDF_TO_IMAGES_KEY, mKeyState[18] ).apply();
        mSharedpreferences.edit().putBoolean(EXCEL_TO_PDF_KEY, mKeyState[19] ).apply();
        mSharedpreferences.edit().putBoolean(ZIP_TO_PDF_KEY, mKeyState[20] ).apply();
    }
}

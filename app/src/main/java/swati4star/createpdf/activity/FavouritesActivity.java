package swati4star.createpdf.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Objects;

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

    private SharedPreferences mSharedPreferences;
    private final boolean[] mKeyState = new boolean[21];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.add_to_favourite);
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        setContentView(R.layout.fav_pref_screen);

        storeInitialState();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
     * store the initial state of checkbox before
     * the user make new changes
     */
    private void storeInitialState() {
        mKeyState[0] = mSharedPreferences.getBoolean(IMAGE_TO_PDF_KEY, false);
        mKeyState[1] = mSharedPreferences.getBoolean(TEXT_TO_PDF_KEY, false);
        mKeyState[2] = mSharedPreferences.getBoolean(QR_BARCODE_KEY, false);
        mKeyState[3] = mSharedPreferences.getBoolean(VIEW_FILES_KEY, false);
        mKeyState[4] = mSharedPreferences.getBoolean(HISTORY_KEY, false);
        mKeyState[5] = mSharedPreferences.getBoolean(ADD_PASSWORD_KEY, false);
        mKeyState[6] = mSharedPreferences.getBoolean(REMOVE_PASSWORD_KEY, false);
        mKeyState[7] = mSharedPreferences.getBoolean(ROTATE_PAGES_KEY, false);
        mKeyState[8] = mSharedPreferences.getBoolean(ADD_WATERMARK_KEY, false);
        mKeyState[9] = mSharedPreferences.getBoolean(ADD_IMAGES_KEY, false);
        mKeyState[10] = mSharedPreferences.getBoolean(MERGE_PDF_KEY, false);
        mKeyState[11] = mSharedPreferences.getBoolean(SPLIT_PDF_KEY, false);
        mKeyState[12] = mSharedPreferences.getBoolean(INVERT_PDF_KEY, false);
        mKeyState[13] = mSharedPreferences.getBoolean(COMPRESS_PDF_KEY, false);
        mKeyState[14] = mSharedPreferences.getBoolean(REMOVE_DUPLICATE_PAGES_KEY, false);
        mKeyState[15] = mSharedPreferences.getBoolean(REMOVE_PAGES_KEY, false);
        mKeyState[16] = mSharedPreferences.getBoolean(REORDER_PAGES_KEY, false);
        mKeyState[17] = mSharedPreferences.getBoolean(EXTRACT_IMAGES_KEY, false);
        mKeyState[18] = mSharedPreferences.getBoolean(PDF_TO_IMAGES_KEY, false);
        mKeyState[19] = mSharedPreferences.getBoolean(EXCEL_TO_PDF_KEY, false);
        mKeyState[20] = mSharedPreferences.getBoolean(ZIP_TO_PDF_KEY, false);
    }

    /**
     * Restore the initial state if user
     * press the back button
     */
    private void onBackPressedState() {
        mSharedPreferences.edit().putBoolean(IMAGE_TO_PDF_KEY, mKeyState[0]).apply();
        mSharedPreferences.edit().putBoolean(TEXT_TO_PDF_KEY, mKeyState[1]).apply();
        mSharedPreferences.edit().putBoolean(QR_BARCODE_KEY, mKeyState[2]).apply();
        mSharedPreferences.edit().putBoolean(VIEW_FILES_KEY, mKeyState[3]).apply();
        mSharedPreferences.edit().putBoolean(HISTORY_KEY, mKeyState[4]).apply();
        mSharedPreferences.edit().putBoolean(ADD_PASSWORD_KEY, mKeyState[5]).apply();
        mSharedPreferences.edit().putBoolean(REMOVE_PASSWORD_KEY, mKeyState[6]).apply();
        mSharedPreferences.edit().putBoolean(ROTATE_PAGES_KEY, mKeyState[7]).apply();
        mSharedPreferences.edit().putBoolean(ADD_WATERMARK_KEY, mKeyState[8]).apply();
        mSharedPreferences.edit().putBoolean(ADD_IMAGES_KEY, mKeyState[9]).apply();
        mSharedPreferences.edit().putBoolean(MERGE_PDF_KEY, mKeyState[10]).apply();
        mSharedPreferences.edit().putBoolean(SPLIT_PDF_KEY, mKeyState[11]).apply();
        mSharedPreferences.edit().putBoolean(INVERT_PDF_KEY, mKeyState[12]).apply();
        mSharedPreferences.edit().putBoolean(COMPRESS_PDF_KEY, mKeyState[13]).apply();
        mSharedPreferences.edit().putBoolean(REMOVE_DUPLICATE_PAGES_KEY, mKeyState[14]).apply();
        mSharedPreferences.edit().putBoolean(REMOVE_PAGES_KEY, mKeyState[15]).apply();
        mSharedPreferences.edit().putBoolean(REORDER_PAGES_KEY, mKeyState[16]).apply();
        mSharedPreferences.edit().putBoolean(EXTRACT_IMAGES_KEY, mKeyState[17]).apply();
        mSharedPreferences.edit().putBoolean(PDF_TO_IMAGES_KEY, mKeyState[18]).apply();
        mSharedPreferences.edit().putBoolean(EXCEL_TO_PDF_KEY, mKeyState[19]).apply();
        mSharedPreferences.edit().putBoolean(ZIP_TO_PDF_KEY, mKeyState[20]).apply();
    }
}

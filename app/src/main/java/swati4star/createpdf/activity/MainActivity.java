package swati4star.createpdf.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import swati4star.createpdf.R;
import swati4star.createpdf.fragment.AboutUsFragment;
import swati4star.createpdf.fragment.ExtractImagesFragment;
import swati4star.createpdf.fragment.HistoryFragment;
import swati4star.createpdf.fragment.HomeFragment;
import swati4star.createpdf.fragment.ImageToPdfFragment;
import swati4star.createpdf.fragment.MergeFilesFragment;
import swati4star.createpdf.fragment.QrBarcodeScanFragment;
import swati4star.createpdf.fragment.RemovePagesFragment;
import swati4star.createpdf.fragment.SettingsFragment;
import swati4star.createpdf.fragment.SplitFilesFragment;
import swati4star.createpdf.fragment.TextToPdfFragment;
import swati4star.createpdf.fragment.ViewFilesFragment;
import swati4star.createpdf.util.FeedbackUtils;
import swati4star.createpdf.util.ThemeUtils;

import static swati4star.createpdf.util.Constants.ACTION_MERGE_PDF;
import static swati4star.createpdf.util.Constants.ACTION_SELECT_IMAGES;
import static swati4star.createpdf.util.Constants.ACTION_TEXT_TO_PDF;
import static swati4star.createpdf.util.Constants.ACTION_VIEW_FILES;
import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.COMPRESS_PDF;
import static swati4star.createpdf.util.Constants.LAUNCH_COUNT;
import static swati4star.createpdf.util.Constants.OPEN_SELECT_IMAGES;
import static swati4star.createpdf.util.Constants.REMOVE_PAGES;
import static swati4star.createpdf.util.Constants.REORDER_PAGES;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FeedbackUtils mFeedbackUtils;
    private NavigationView mNavigationView;
    private SharedPreferences mSharedPreferences;
    private boolean mDoubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.setThemeApp(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        // Set navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);

        //Replaced setDrawerListener with addDrawerListener because it was deprecated.
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // initialize values
        initializeValues();

        // Check for app shortcuts & select default fragment
        Fragment fragment = checkForAppShortcutClicked();

        // Check if  images are received
        handleReceivedImagesIntent(fragment);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int count = mSharedPreferences.getInt(LAUNCH_COUNT, 0);
        if (count > 0 && count % 15 == 0)
            mFeedbackUtils.rateUs();
        mSharedPreferences.edit().putInt(LAUNCH_COUNT, count + 1).apply();
    }

    /**
     * Sets a fragment based on app shortcut selected, otherwise default
     *
     * @return - instance of current fragment
     */
    private Fragment checkForAppShortcutClicked() {
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (Objects.requireNonNull(getIntent().getAction())) {
            case ACTION_SELECT_IMAGES:
                fragment = new ImageToPdfFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(OPEN_SELECT_IMAGES, true);
                fragment.setArguments(bundle);
                break;
            case ACTION_VIEW_FILES:
                fragment = new ViewFilesFragment();
                setDefaultMenuSelected(1);
                break;
            case ACTION_TEXT_TO_PDF:
                fragment = new TextToPdfFragment();
                setDefaultMenuSelected(4);
                break;
            case ACTION_MERGE_PDF:
                fragment = new MergeFilesFragment();
                setDefaultMenuSelected(2);
                break;
            default:
                // Set default fragment
                fragment = new HomeFragment();
                break;
        }

        if (areImagesRecevied())
            fragment = new ImageToPdfFragment();

        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        return fragment;
    }

    /**
     * Ininitializes default values
     */
    private void initializeValues() {
        mFeedbackUtils = new FeedbackUtils(this);
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        setDefaultMenuSelected(0);
    }

    /*
     * This will set default menu item selected at the position mentioned
     */
    public void setDefaultMenuSelected(int position) {
        if (mNavigationView != null && mNavigationView.getMenu() != null &&
                position < mNavigationView.getMenu().size()
                && mNavigationView.getMenu().getItem(position) != null) {
            mNavigationView.getMenu().getItem(position).setChecked(true);
        }
    }

    /**
     * Checks if images are received in the intent
     *
     * @param fragment - instance of current fragment
     */
    private void handleReceivedImagesIntent(Fragment fragment) {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (type == null || !type.startsWith("image/"))
            return;

        if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            handleSendMultipleImages(intent, fragment); // Handle multiple images
        } else if (Intent.ACTION_SEND.equals(action)) {
            handleSendImage(intent, fragment); // Handle single image
        }
    }


    private boolean areImagesRecevied() {
        Intent intent = getIntent();
        String type = intent.getType();
        return type != null && type.startsWith("image/");
    }

    /**
     * Get image uri from intent and send the image to homeFragment
     *
     * @param intent   - intent containing image uris
     * @param fragment - instance of homeFragment
     */
    private void handleSendImage(Intent intent, Fragment fragment) {
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        ArrayList<Uri> imageUris = new ArrayList<>();
        imageUris.add(uri);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.bundleKey), imageUris);
        fragment.setArguments(bundle);
    }

    /**
     * Get ArrayList of image uris from intent and send the image to homeFragment
     *
     * @param intent   - intent containing image uris
     * @param fragment - instance of homeFragment
     */
    private void handleSendMultipleImages(Intent intent, Fragment fragment) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(getString(R.string.bundleKey), imageUris);
            fragment.setArguments(bundle);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment currentFragment = getSupportFragmentManager()
                    .findFragmentById(R.id.content);
            if (currentFragment instanceof HomeFragment) {
                checkDoubleBackPress();
            } else {
                Fragment fragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
                setDefaultMenuSelected(0);
            }
        }
    }

    /**
     * Closes the app only when double clicked
     */
    private void checkDoubleBackPress() {
        if (mDoubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.mDoubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.confirm_exit_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();

        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_camera:
                fragment = new ImageToPdfFragment();
                break;
            case R.id.nav_qrcode:
                fragment = new QrBarcodeScanFragment();
                break;
            case R.id.nav_gallery:
                fragment = new ViewFilesFragment();
                break;
            case R.id.nav_merge:
                fragment = new MergeFilesFragment();
                break;
            case R.id.nav_split:
                fragment = new SplitFilesFragment();
                break;
            case R.id.nav_text_to_pdf:
                fragment = new TextToPdfFragment();
                break;
            case R.id.nav_history:
                fragment = new HistoryFragment();
                break;
            case R.id.nav_share:
                mFeedbackUtils.shareApplication();
                break;
            case R.id.nav_about:
                fragment = new AboutUsFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.nav_extract_images:
                fragment = new ExtractImagesFragment();
                break;
            case R.id.nav_remove_pages:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, REMOVE_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_rearrange_pages:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, REORDER_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_compress_pdf:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, COMPRESS_PDF);
                fragment.setArguments(bundle);
                break;
        }

        try {
            if (fragment != null)
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void setNavigationViewSelection(int index) {
        mNavigationView.getMenu().getItem(index).setChecked(true);
    }
}
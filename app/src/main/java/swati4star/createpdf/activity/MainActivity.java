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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import swati4star.createpdf.BuildConfig;
import swati4star.createpdf.R;
import swati4star.createpdf.fragment.AboutUsFragment;
import swati4star.createpdf.fragment.AddImagesFragment;
import swati4star.createpdf.fragment.AddTextFragment;
import swati4star.createpdf.fragment.ExceltoPdfFragment;
import swati4star.createpdf.fragment.ExtractTextFragment;
import swati4star.createpdf.fragment.HistoryFragment;
import swati4star.createpdf.fragment.HomeFragment;
import swati4star.createpdf.fragment.ImageToPdfFragment;
import swati4star.createpdf.fragment.InvertPdfFragment;
import swati4star.createpdf.fragment.MergeFilesFragment;
import swati4star.createpdf.fragment.PdfToImageFragment;
import swati4star.createpdf.fragment.QrBarcodeScanFragment;
import swati4star.createpdf.fragment.RemoveDuplicatePagesFragment;
import swati4star.createpdf.fragment.RemovePagesFragment;
import swati4star.createpdf.fragment.SettingsFragment;
import swati4star.createpdf.fragment.SplitFilesFragment;
import swati4star.createpdf.fragment.TextToPdfFragment;
import swati4star.createpdf.fragment.ViewFilesFragment;
import swati4star.createpdf.fragment.ZipToPdfFragment;
import swati4star.createpdf.providers.FragmentManagement;
import swati4star.createpdf.util.FeedbackUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.ThemeUtils;
import swati4star.createpdf.util.WhatsNewUtils;

import static swati4star.createpdf.util.Constants.ADD_IMAGES;
import static swati4star.createpdf.util.Constants.ADD_PWD;
import static swati4star.createpdf.util.Constants.BUNDLE_DATA;
import static swati4star.createpdf.util.Constants.COMPRESS_PDF;
import static swati4star.createpdf.util.Constants.EXTRACT_IMAGES;
import static swati4star.createpdf.util.Constants.IS_WELCOME_ACTIVITY_SHOWN;
import static swati4star.createpdf.util.Constants.LAUNCH_COUNT;
import static swati4star.createpdf.util.Constants.PDF_TO_IMAGES;
import static swati4star.createpdf.util.Constants.READ_WRITE_CAMERA_PERMISSIONS;
import static swati4star.createpdf.util.Constants.READ_WRITE_PERMISSIONS;
import static swati4star.createpdf.util.Constants.REMOVE_PAGES;
import static swati4star.createpdf.util.Constants.REMOVE_PWd;
import static swati4star.createpdf.util.Constants.REORDER_PAGES;
import static swati4star.createpdf.util.Constants.SHOW_WELCOME_ACT;
import static swati4star.createpdf.util.Constants.VERSION_NAME;
import static swati4star.createpdf.util.DialogUtils.ADD_WATERMARK;
import static swati4star.createpdf.util.DialogUtils.ROTATE_PAGES;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FeedbackUtils mFeedbackUtils;
    private NavigationView mNavigationView;
    private SharedPreferences mSharedPreferences;
    private SparseIntArray mFragmentSelectedMap;
    private FragmentManagement mFragmentManagement;

    private static final int PERMISSION_REQUEST_CODE = 0;

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

        setXMLParsers();
        // Check for app shortcuts & select default fragment
        Fragment fragment = mFragmentManagement.checkForAppShortcutClicked();

        // Check if  images are received
        handleReceivedImagesIntent(fragment);

        displayFeedBackAndWhatsNew();
        getRuntimePermissions();

        //check for welcome activity
        openWelcomeActivity();
    }

    /**
     * Set suitable xml parsers for reading .docx files.
     */
    private  void setXMLParsers() {
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl");
    }

    /**
     * A method for the feedback and whats new dialogs.
     */
    private void displayFeedBackAndWhatsNew() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int count = mSharedPreferences.getInt(LAUNCH_COUNT, 0);
        if (count > 0 && count % 15 == 0) {
            mFeedbackUtils.rateUs();
        }
        if (count != -1) {
            mSharedPreferences.edit().putInt(LAUNCH_COUNT, count + 1).apply();
        }

        String versionName = mSharedPreferences.getString(VERSION_NAME, "");
        if (versionName != null && !versionName.equals(BuildConfig.VERSION_NAME)) {
            WhatsNewUtils.displayDialog(this);
            mSharedPreferences.edit().putString(VERSION_NAME, BuildConfig.VERSION_NAME).apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isStoragePermissionGranted()) {
            FileUtils.makeAndClearTemp();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_favourites_item) {
            setTitle(R.string.favourites);
            mFragmentManagement.favouritesFragmentOption();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * if welcome activity isnt opened ever, it is shown
     */
    private void openWelcomeActivity() {
        if (!mSharedPreferences.getBoolean(IS_WELCOME_ACTIVITY_SHOWN, false)) {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            mSharedPreferences.edit().putBoolean(IS_WELCOME_ACTIVITY_SHOWN, true).apply();
            startActivity(intent);
        }
    }

    /**
     * Ininitializes default values
     */
    private void initializeValues() {
        mFeedbackUtils = new FeedbackUtils(this);
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_home);

        mFragmentManagement = new FragmentManagement(this, mNavigationView);
        mFragmentSelectedMap = mFragmentManagement.setTitleMap();
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
            boolean shouldExit = mFragmentManagement.handleBackPressed();
            if (shouldExit)
                super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        setTitleFragment(mFragmentSelectedMap.get(item.getItemId()));
        return mFragmentManagement.handleNavigationItemSelected(item.getItemId());
    }

    public void setNavigationViewSelection(int id) {
        mNavigationView.setCheckedItem(id);
    }

    private void getRuntimePermissions() {
        PermissionsUtils.requestRuntimePermissions(this,
                READ_WRITE_CAMERA_PERMISSIONS,
                PERMISSION_REQUEST_CODE);
    }

    private boolean isStoragePermissionGranted() {
        return PermissionsUtils.checkRuntimePermissions(this, READ_WRITE_PERMISSIONS);
    }

    /**
     * puts image uri's in a bundle and start ImageToPdf fragment with this bundle
     * as argument
     *
     * @param imageUris - ArrayList of image uri's in temp directory
     */
    public void convertImagesToPdf(ArrayList<Uri> imageUris) {
        Fragment fragment = new ImageToPdfFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.bundleKey), imageUris);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
    }

    /**
     * Sets fragment title
     * @param title - string resource id
     */
    private void setTitleFragment(int title) {
        if (title != 0)
            setTitle(title);
    }
}

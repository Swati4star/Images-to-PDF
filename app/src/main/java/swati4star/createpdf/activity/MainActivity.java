package swati4star.createpdf.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.Objects;

import swati4star.createpdf.R;
import swati4star.createpdf.fragment.HomeFragment;
import swati4star.createpdf.fragment.ViewFilesFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        // Set navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //Replaced setDrawerListener with addDrawerListener because it was deprecated.
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Set HomeFragment fragment
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (id) {
            case R.id.nav_camera:
                fragment = new HomeFragment();
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
                break;
            case R.id.nav_gallery:
                fragment = new ViewFilesFragment();
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
                break;
            case R.id.nav_feedback:
                getFeedback();
                break;
            case R.id.nav_share:
                shareApplication();
                break;
            case R.id.nav_rate_us:
                rateUs();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getFeedback() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"swari4star@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback_subject));
        intent.putExtra(Intent.EXTRA_TEXT   , getResources().getString(R.string.feedback_text));
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.feedback_chooser)));
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(Objects.requireNonNull(MainActivity.this).findViewById(android.R.id.content),
                    R.string.snackbar_no_email_clients,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void shareApplication() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT , getResources().getString(R.string.rate_us_text));
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.share_chooser)));
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(Objects.requireNonNull(MainActivity.this).findViewById(android.R.id.content),
                    R.string.snackbar_no_share_app,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void rateUs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.rate_title))
                .setMessage(getString(R.string.rate_dialog_text))
                .setNegativeButton(getString(R.string.rate_negative), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton(getString(R.string.rate_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("market://details?id=" +
                                                    getApplicationContext().getPackageName())));
                        } catch (Exception e) {
                            Snackbar.make(Objects.requireNonNull(MainActivity.this).findViewById(android.R.id.content),
                                    R.string.playstore_not_installed,
                                    Snackbar.LENGTH_LONG).show();
                        }
                        dialogInterface.dismiss();

                    }
                });
        builder.create().show();
    }
}
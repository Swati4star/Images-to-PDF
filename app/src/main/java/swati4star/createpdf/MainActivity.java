package swati4star.createpdf;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Changed the setContentView from activity_main to the newly created activity_dual in the refs.xml files
        * */
        setContentView(R.layout.activity_dual);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        // Set navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        switchLayout();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (id) {
            case R.id.nav_camera:
                fragment = new Home();
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
                break;
            case R.id.nav_gallery:
                fragment = new ViewFiles();
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    * Newly added method for switching layout according to device orientation
    *
    * Added by Abdelaziz faki aka Azooz2014 */
    public void switchLayout(){

        //Checking the orientation of the device in order to choose the right layout.
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            // Set Home fragment
            Fragment fragment = new Home();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){

            // Set ViewFiles fragment
            FragmentManager fragmentManager = getSupportFragmentManager();

            Fragment viewFragment = new ViewFiles();

            //Add the newly created viewFragment to it's fragment_view_files_holder in activity_main_layout.xml
            fragmentManager.beginTransaction().add(R.id.fragment_view_files_holder, viewFragment).commit();
        }
    }
}
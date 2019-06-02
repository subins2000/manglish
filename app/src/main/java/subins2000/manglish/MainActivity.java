package subins2000.manglish;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener, AboutFragment.OnFragmentInteractionListener {

    NavigationView navigationView;

    Fragment homeFragment = new HomeFragment();
    Fragment aboutFragment = new AboutFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);

        // Add fragments
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame, homeFragment);
        transaction.add(R.id.frame, aboutFragment);
        transaction.commit();

        showFragment(homeFragment);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            showFragment(aboutFragment);
            navigationView.getMenu().getItem(1).setChecked(true);
        }

        return super.onOptionsItemSelected(item);
    }

    public void showFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(homeFragment);
        transaction.hide(aboutFragment);
        transaction.show(frag);
        transaction.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment frag = null; // create a Fragment Object

        navigationView.setCheckedItem(id);

        if (id == R.id.nav_home) {
            frag = homeFragment;
        } else if (id == R.id.nav_about) {
            frag = aboutFragment;
        }

        if (frag != null) {
            showFragment(frag);

            // close the all open Drawer Views
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            return true;
        }
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
}

/**
 * Manglish, malayalam to manglish converter
 * Copyright (C) 2019 Subin Siby
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package subins2000.manglish;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener, AboutFragment.OnFragmentInteractionListener,
        OverlayAboutFragment.OnFragmentInteractionListener {

    private static final String NAV_ID = "NavId";

    DrawerLayout drawer;
    NavigationView navigationView;

    SharedPreferences prefs;

    int activeNavId = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset > 0) tryHideKeyboard();
            }
        });
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        if (savedInstanceState == null) {
            setFragment(new HomeFragment());
        } else {
            activeNavId = savedInstanceState.getInt(NAV_ID);
            navigationView.getMenu().findItem(activeNavId).setChecked(true);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ID, activeNavId);
    }

    @Override
    public void onBackPressed() {
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

        if (id == R.id.action_about && activeNavId != R.id.nav_about) {
            setFragment(new AboutFragment());

            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_about).setChecked(true);
            menu.performIdentifierAction(R.id.nav_about, 0);

            activeNavId = R.id.nav_about;
            return true;
        } else if (id == R.id.action_overlay_about && activeNavId != R.id.action_overlay_about) {
            setFragment(new OverlayAboutFragment());

            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_overlay_about).setChecked(true);
            menu.performIdentifierAction(R.id.nav_about, 0);

            activeNavId = R.id.action_overlay_about;
            return true;
        } else if (id == R.id.action_share) {
            shareApp();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        try {
            if (activeNavId != id) {
                activeNavId = id;
                switch (id) {
                    case R.id.nav_home:
                        setFragment(new HomeFragment());
                        return true;
                    case R.id.nav_overlay_about:
                        setFragment(new OverlayAboutFragment());
                        return true;
                    case R.id.nav_about:
                        setFragment(new AboutFragment());
                        return true;
                    case R.id.nav_share:
                        shareApp();
                }
            }
            return false;
        } finally {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .commit();
    }

    private void tryHideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View curFocus = getCurrentFocus();
        if (curFocus != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void shareApp() {
        Intent intent2 = new Intent(); intent2.setAction(Intent.ACTION_SEND);
        intent2.setType("text/plain");
        intent2.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
        startActivity(Intent.createChooser(intent2, "Share via"));
    }
}
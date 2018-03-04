package sotifc2017.pickup.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sotifc2017.pickup.fragments.ExtendedProfileFragment;
import sotifc2017.pickup.R;
import sotifc2017.pickup.fragments.MainSearchFragment;
import sotifc2017.pickup.fragments.SettingsFragment;
import sotifc2017.pickup.api.Authentication;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private List<LatLng> sampleGames;
    private int MY_PERMISSIONS_FINE_LOCATION;
    private int MY_PERMISSIONS_COARSE_LOCATION;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ImageView searchButton;
    private static long back_pressed_time;
    private static long PERIOD = 2000;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.bringToFront();

        navigationView = findViewById(R.id.navigation_view_main);
        drawerLayout = findViewById(R.id.activity_map);
        searchButton = findViewById(R.id.toolbar_search_icon);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new MainSearchFragment(), true, R.id.action_search);
            }
        });
        setDrawerLayout();

        // Obtain the MapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = new MapFragment();
        replaceFragment(mapFragment, false, R.id.action_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed(){
        int count = getFragmentManager().getBackStackEntryCount();
        if (count != 0) {
            if (count == 1) setNavItemSelectedById(R.id.action_map);
            if (count > 1)
                setNavItemSelectedById(Integer.parseInt(getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 2).getName()));
            super.onBackPressed();
        }
        else
        {
            if (back_pressed_time + PERIOD > System.currentTimeMillis()) super.onBackPressed();
            else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
            back_pressed_time = System.currentTimeMillis();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkPermissions()) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            displayGames(location);
                        }
                    });
        }
        else {
            askForPermissions();
            displayGames(null);
        }
    }
    public void askForPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_COARSE_LOCATION);
    }

    public boolean checkPermissions() {
        return !(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    // Have to make sure OnMapReady() is invoked before using this.
    //TODO: Custom Map Marker
    public void plotGameOnMap(GoogleMap mMap, LatLng gameLoc) {
        mMap.addMarker(new MarkerOptions().position(gameLoc));
    }

    //TODO: Want to use Game object, or whatever we actually pull from backend here. Need to sync-up.
    public void plotGames(GoogleMap mMap, List<LatLng> games) {
        for (LatLng game : games) {
            plotGameOnMap(mMap, game);
        }
    }

    public void zoomToUser(GoogleMap mMap, LatLng userLoc) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 10));
    }

    //From https://stackoverflow.com/questions/14828217/android-map-v2-zoom-to-show-all-the-markers
    public void zoomToViewPoints(GoogleMap mMap, List<LatLng> locations) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng loc : locations) {
            builder.include(loc);
        }
        LatLngBounds bounds = builder.build();
        int padding = 50;
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    public void displayGames(Location location) {
        if (location != null && checkPermissions()) {
            System.out.println(location.getLatitude() + " : " + location.getLongitude());
            // Generates Sample Games. Take out when connected to backend.
            sampleGames = new ArrayList<>();
            Random random = new Random();
            for (double i = 0; i < 8; i++) {
                // Convert radius from meters to degrees.
                double radiusInDegrees = 1000 / 111320f;

                // Get a random distance and a random angle.
                double u = random.nextDouble();
                double v = random.nextDouble();
                double w = radiusInDegrees * Math.sqrt(u);
                double t = 2 * Math.PI * v;
                // Get the x and y delta values.
                double x = w * Math.cos(t);
                double y = w * Math.sin(t);

                // Compensate the x value.
                double new_x = x / Math.cos(Math.toRadians(location.getLatitude()));

                double foundLatitude = location.getLatitude() + y;
                double foundLongitude = location.getLongitude() + new_x;
                sampleGames.add(new LatLng(foundLatitude, foundLongitude));
            }
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    plotGames(mMap, sampleGames);
                    zoomToViewPoints(mMap, sampleGames);
                }
            });
        }
        else {
            //TODO: Zoom to actual city from user's profile, not random points.
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    zoomToUser(mMap, new LatLng(43, -79));
                }
            });
        }
    }

    private void setDrawerLayout() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_closed) {
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                if (item.getItemId() != R.id.action_sign_out) {
                    for (int i = 0; i < navigationView.getMenu().size(); i++) {
                        navigationView.getMenu().getItem(i).setChecked(false).setEnabled(true);
                    }
                    item.setChecked(true).setEnabled(false);
                }
                switch(item.getItemId()) {
                    case R.id.action_map:
                        MapFragment mapFragment = new MapFragment();
                        replaceFragment(mapFragment, true, R.id.action_map);
                        mapFragment.getMapAsync(MainActivity.this);
                        break;
                    case R.id.action_profile:
                        replaceFragment(new ExtendedProfileFragment(), true, R.id.action_profile);
                        break;
                    case R.id.action_settings:
                        replaceFragment(new SettingsFragment(), true, R.id.action_settings);
                        break;
                    case R.id.action_search:
                        replaceFragment(new MainSearchFragment(), true, R.id.action_search);
                        break;
                    case R.id.action_sign_out:
                        AlertDialog diaBox = AskOption();
                        diaBox.show();
                        break;
                }
                return true;
            }
        });
    }

    private AlertDialog AskOption() {
        return new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle(getString(R.string.sign_out_title))
                .setMessage(getString(R.string.sign_out_message))
                .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //sign out call
                        Authentication.logout(MainActivity.this);
                        intent = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    //https://stackoverflow.com/questions/5658675/replacing-a-fragment-with-another-fragment-inside-activity-group
    private void replaceFragment(Fragment frag, boolean backStackAdd, int fragId){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment_container, frag);
        if(backStackAdd) transaction.addToBackStack(String.valueOf(fragId));

        if(fragId != R.id.action_map) searchButton.setVisibility(View.INVISIBLE);
        else searchButton.setVisibility(View.VISIBLE);

        // Commit the transaction
        transaction.commit();
    }

    private void setNavItemSelectedById(int id){
        drawerLayout.closeDrawers();
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false).setEnabled(true);
        }
        navigationView.getMenu().findItem(id).setChecked(true).setEnabled(false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (checkPermissions()) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            displayGames(location);
                        }
                    });
        }
        else {
            displayGames(null);
        }

    }
}

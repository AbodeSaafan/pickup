package sotifc2017.pickup.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sotifc2017.pickup.R;
import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.fragment_interfaces.OnFragmentReplacement;
import sotifc2017.pickup.fragment_managers.ConfigurableFragmentItemsManager;
import sotifc2017.pickup.fragments.CreateGameFragment;
import sotifc2017.pickup.fragments.ExtendedProfileFragment;
import sotifc2017.pickup.fragments.ListViewFragment;
import sotifc2017.pickup.fragments.MainSearchFragment;
import sotifc2017.pickup.fragments.RefinedMapFragment;
import sotifc2017.pickup.fragments.SettingsFragment;

import static sotifc2017.pickup.Common.Defaults.FC_TAG;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, HostingActivity, OnFragmentReplacement, RangeTimePickerDialog.ISelectedTime {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private List<LatLng> sampleGames;
    private int MY_PERMISSIONS_FINE_LOCATION;
    private int MY_PERMISSIONS_COARSE_LOCATION;
    private Toolbar toolbar;
    private FloatingActionButton fabNewGame;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ImageView searchButton;
    private static long back_pressed_time;
    private static long PERIOD = 2000;
    private int PLACE_PICKER_REQUEST = 1;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbarSearch();
        setUpFloatingCreateNewGame();

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
        MapFragment mapFragment = new RefinedMapFragment();
        replaceFragment(mapFragment, false, R.id.action_map);
        mapFragment.getMapAsync(this);
    }

    private void setUpToolbarSearch() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.bringToFront();
    }

    private void setUpFloatingCreateNewGame() {
        fabNewGame = findViewById(R.id.fab_new_game);
        fabNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new CreateGameFragment(), true, R.id.fab_new_game);
            }
        });
    }

    @Override
    public void onBackPressed(){
        int count = getFragmentManager().getBackStackEntryCount();
        if (count != 0) {
            super.onBackPressed();
        } else {
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
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        if (checkPermissions()) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            displayGames(location);
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("SotiFc", "Failed getting user's location");
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
                switch(item.getItemId()) {
                    case R.id.action_map:
                        MapFragment mapFragment = new RefinedMapFragment();
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

        // Commit the transaction
        transaction.commit();
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

    @Override
    public void onDisplayGameSearchResults(String gameListJson) {
        Bundle bundle = new Bundle();
        bundle.putString("gameListJson", gameListJson);

        ListViewFragment listViewFragment = new ListViewFragment();
        listViewFragment.setArguments(bundle);

        replaceFragment(listViewFragment, true, -1);
    }

    @Override
    public void onDisplayUserSearchResults(String userListJson) {
        Bundle bundle = new Bundle();
        bundle.putString("userListJson", userListJson);

        //TODO connect to user list view here, show the fragment

        ListViewFragment listViewFragment = new ListViewFragment();
        listViewFragment.setArguments(bundle);

        replaceFragment(listViewFragment, true, -1);

    }

    // Create game UI items
    public void onAgeRadioButtonClicked(View view) {
        CreateGameFragment.onAgeRadioButtonClicked(view);
    }

    public void onGenderRadioButtonClicked(View view) {
        CreateGameFragment.onGenderRadioButtonClicked(view);
    }

    public void onPlayerRestrictedRadioButtonClicked(View view) {
        CreateGameFragment.onPlayerRestrictedRadioButtonClicked(view);
    }

    @Override
    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd)
    {
        Toast.makeText(this, "Start: "+hourStart+":"+minuteStart+"\nEnd: "+hourEnd+":"+minuteEnd, Toast.LENGTH_SHORT).show();
    }

    // Fragments callbacks
    @Override
    public void configureMenuItemSelection(int currentFragmentId, boolean padTop) {
        RelativeLayout fragmentContainer = (findViewById(R.id.fragment_container));
        RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) fragmentContainer.getLayoutParams();

        layoutParam.setMargins(0, padTop ? findViewById(R.id.toolbar).getHeight() : 0, 0 , 0);

        fragmentContainer.setLayoutParams(layoutParam);
        drawerLayout.closeDrawers();
        ConfigurableFragmentItemsManager.configureMenuItemSelection(navigationView, currentFragmentId);
    }

    public void startPlacePickerActivity() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(FC_TAG, "May recover from: ", e);
            Toast.makeText(this, "Please try selecting your location again", Toast.LENGTH_SHORT).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(FC_TAG, "Google Services: ", e);
            Toast.makeText(this, "Check Google services. Are they installed? ", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                LatLngBounds locationChosen = PlacePicker.getLatLngBounds(data);
                String toastMsg = String.format("Place: %s, Location: %s", place.getName(), locationChosen);
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

            }
        }
    }
}

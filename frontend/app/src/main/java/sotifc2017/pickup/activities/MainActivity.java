package sotifc2017.pickup.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sotifc2017.pickup.Common.Defaults;
import sotifc2017.pickup.R;
import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.enums.ENFORCED_PARAMS;
import sotifc2017.pickup.api.models.GameModel;
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
    private CoordinatorLayout floatGameItem;
    private LayoutInflater inflater;
    private static long back_pressed_time;
    private static long PERIOD = 2000;
    private int PLACE_PICKER_REQUEST = 1;
    private LocationCallback mLocationCallback;
    private GameModel[] games;
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
        floatGameItem = findViewById(R.id.coordinatorLayout);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    zoomToUser(mMap, new LatLng(location.getLatitude(), location.getLongitude()));
                    SaveLastKnownLocation(location);
                }
            }

            ;
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        if(mFusedLocationClient == null) { return; }
        mFusedLocationClient.requestLocationUpdates(createLocationRequest(),
                mLocationCallback,
                null /* Looper */);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
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
                replaceFragment(new CreateGameFragment(), true, R.id.action_create_game);
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
            startLocationUpdates();
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            createCustomGames();
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
            createCustomGames();
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
        stopLocationUpdates();
        if (location != null && checkPermissions()) {
            // Save as last known location
            SaveLastKnownLocation(location);
            // Generates Sample Games. Take out when connected to backend.
            sampleGames = new ArrayList<>();
            /*
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
            */
            for (GameModel game: games) {
                sampleGames.add(new LatLng(game.location.get("lat"), game.location.get("lng")));
            }
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    plotGames(mMap, sampleGames);
                    zoomToViewPoints(mMap, sampleGames);
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            System.out.println(marker.getId());
                            System.out.println(marker.getPosition());
                            Snackbar snackbar = Snackbar.make(floatGameItem, "", Snackbar.LENGTH_LONG);
                            Snackbar.SnackbarLayout slt = (Snackbar.SnackbarLayout) snackbar.getView();

                            View snackView = inflater.inflate(R.layout.fragment_game_list_item, null);
                            TextView gameName = snackView.findViewById(R.id.gameName);
                            gameName.setText("Rad's game");

                            TextView gameLocation = snackView.findViewById(R.id.location);
                            gameLocation.setText("Mississauga, ON");

                            TextView dateTime = snackView.findViewById(R.id.dateTime);
                            dateTime.setText("Jun 10, 2018 4:00 to 5:00 PM");

                            TextView player_info = snackView.findViewById(R.id.players);
                            player_info.setText("1/10");

                            slt.setPadding(0,0,0,0);
                            slt.addView(snackView, 0);
                            snackbar.show();

                            return false;
                        };
                    });
                }
            });
        }
        else {
            //TODO: Zoom to actual city from user's profile, not random points.
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    Location lastKnown = GetLastKnownLocation();
                    if(lastKnown.getAccuracy() == Float.MIN_VALUE){
                        // Don't have any info
                        // TODO we should prevent this with permission flow
                        zoomToUser(mMap, new LatLng(43, -79));
                    } else {
                        zoomToUser(mMap, new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude()));
                    }
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
    public void replaceFragment(Fragment frag, boolean backStackAdd, int fragId){
        hideKeyboard();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment_container, frag, String.valueOf(fragId));
        if(backStackAdd) transaction.addToBackStack(null);

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
        CreateGameFragment createGameFrag =
                (CreateGameFragment) getFragmentManager().findFragmentByTag(String.valueOf(R.id.action_create_game));

        if (createGameFrag != null) {
            createGameFrag.onAgeRadioButtonClicked(view);
        }
    }

    public void onGenderRadioButtonClicked(View view) {
        CreateGameFragment createGameFrag =
                (CreateGameFragment) getFragmentManager().findFragmentByTag(String.valueOf(R.id.action_create_game));

        if (createGameFrag != null) {
            createGameFrag.onGenderRadioButtonClicked(view);
        }
    }

    @Override
    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd)
    {
        CreateGameFragment createGameFrag =
                (CreateGameFragment) getFragmentManager().findFragmentByTag(String.valueOf(R.id.action_create_game));

        if (createGameFrag != null) {
            createGameFrag.onSelectedTime(hourStart, minuteStart, hourEnd, minuteEnd);
        }
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

    @Override
    public void clearMenuItemSelection(){
        ConfigurableFragmentItemsManager.enableFullMenu(navigationView);
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
                LatLngBounds locationChosen = PlacePicker.getLatLngBounds(data);
                CreateGameFragment createGameFrag =
                        (CreateGameFragment) getFragmentManager().findFragmentByTag(String.valueOf(R.id.action_create_game));

                if (createGameFrag != null) {
                    createGameFrag.onSelectedLocation(locationChosen, data);
                }
            }
        }
    }

    // https://stackoverflow.com/a/17789187
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void SaveLastKnownLocation(Location location){
        Log.v("location", String.format("updating location with lat %f and lng %f", location.getLatitude(), location.getLongitude()));
        getSharedPreferences(Defaults.FC_TAG, MODE_PRIVATE).edit()
                .putBoolean("locationSaved", true).apply();
        getSharedPreferences(Defaults.FC_TAG, MODE_PRIVATE).edit().
                putLong("lastKnownLocationLat", Double.doubleToRawLongBits(location.getLatitude())).apply();
        getSharedPreferences(Defaults.FC_TAG, MODE_PRIVATE).edit().
                putLong("lastKnownLocationLng", Double.doubleToRawLongBits(location.getLongitude())).apply();
    }

    private Location GetLastKnownLocation(){
        Location result = new Location("FromPrefs");
        boolean locationSaved = getSharedPreferences(Defaults.FC_TAG, MODE_PRIVATE).getBoolean("locationSaved", false);

        if(!locationSaved){
            result.setAccuracy(Float.MIN_VALUE);
            return result;
        }

        result.setLatitude(Double.longBitsToDouble(getSharedPreferences(Defaults.FC_TAG, MODE_PRIVATE).getLong("lastKnownLocationLat", Long.MIN_VALUE)));
        result.setLongitude(Double.longBitsToDouble(getSharedPreferences(Defaults.FC_TAG, MODE_PRIVATE).getLong("lastKnownLocationLng", Long.MIN_VALUE)));

        return result;
    }

    private void createCustomGames() {
        // create the hard coded game Objects:
        HashMap<String, Double> location_1 = new HashMap<String, Double>();
        location_1.put("lat", 43.624366);
        location_1.put("lng", -79.670428);
        HashMap <String, Double> location_2 = new HashMap<String, Double>();
        location_2.put("lat", 43.618327);
        location_2.put("lng", -79.680148);
        HashMap <String, Double> location_3 = new HashMap<String, Double>();
        location_3.put("lat", 43.626796);
        location_3.put("lng", -79.684664);
        HashMap <String, Double> location_4 = new HashMap<String, Double>();
        location_4.put("lat", 43.615908);
        location_4.put("lng", -79.672354);

        int[] game_id = new int[] {1, 2, 3, 4};
        String[] name = new String[]{"radhika's game", "radhika's game pt II", "radhika's game pt III", "radhika's game pt IV"};
        String[] type = new String[]{"casual", "serious", "casual", "serious"};
        int[] skill_min = new int[] {5, 3, 2, 4};
        int[] skill_max = new int[] {7, 10, 8, 9};
        int[] total_players_required = new int[] {15, 10, 20, 12};
        int[] total_players_added = new int[] {12, 5, 11, 5};
        int[] start_time = new int[] {1504272395, 1504272380, 1504272350, 1504272358};
        int[] end_time = new int[] {1504272600, 1504272700, 1504272800, 1504272370};
        List<HashMap<String, Double>> locations = new ArrayList<HashMap<String, Double>>();
        locations.add(location_1);
        locations.add(location_2);
        locations.add(location_3);
        locations.add(location_4);
        int[] creator_id = new int[] {1, 2, 3, 4};
        String[] descriptions = new String[] {"Casual basketball game", "Serious basketball game pt II", "Casual basketball game pt III", "Serious basketball game pt III"};
        String[] location_notes = new String[] {"Come around the back and knock on the blue door", "Come around the back and knock on the red door", "Come around the back and knock on the yellow door", "Come around the back and knock on the purple door"};
        String[] gender = new String[] {"A", "F", "M", "A"};
        List<int[]> age_range = new ArrayList<int[]>();
        age_range.add(new int[]{20, 30});
        age_range.add(new int[]{18, 35});
        age_range.add(new int[]{});
        age_range.add(new int[]{30, 45});
        int[] time_created = new int[] {1504272395, 1504272395, 1504272395, 1504272395};
        List<ENFORCED_PARAMS[]> enforced_params = new ArrayList<ENFORCED_PARAMS[]>();
        enforced_params.add(new ENFORCED_PARAMS[] {ENFORCED_PARAMS.age});
        enforced_params.add(new ENFORCED_PARAMS[] {ENFORCED_PARAMS.age, ENFORCED_PARAMS.gender});
        enforced_params.add(new ENFORCED_PARAMS[] {ENFORCED_PARAMS.gender});
        enforced_params.add(new ENFORCED_PARAMS[] {ENFORCED_PARAMS.age});
        boolean[] player_restricted = new boolean[]{true, false, true, false};

        GameModel game_1 = new GameModel(game_id[0], name[0], type[0], -1, total_players_required[0],total_players_added[0], "", "",
                start_time[0], end_time[0], locations.get(0),creator_id[0], descriptions[0], location_notes[0], gender[0],age_range.get(0), enforced_params.get(0), time_created[0], player_restricted[0]);
        GameModel game_2 = new GameModel(game_id[1], name[1], type[1], -1, total_players_required[1],total_players_added[1], "", "",
                start_time[1], end_time[1], locations.get(1),creator_id[1], descriptions[1], location_notes[1], gender[1],age_range.get(1), enforced_params.get(1), time_created[1], player_restricted[1]);
        GameModel game_3 = new GameModel(game_id[2], name[2], type[2], -1, total_players_required[2],total_players_added[2], "", "",
                start_time[2], end_time[2], locations.get(2),creator_id[2], descriptions[2], location_notes[2], gender[2],age_range.get(2), enforced_params.get(2), time_created[2], player_restricted[2]);
        GameModel game_4 = new GameModel(game_id[3], name[3], type[3], -1, total_players_required[3],total_players_added[3], "", "",
                start_time[3], end_time[3], locations.get(3),creator_id[3], descriptions[3], location_notes[3], gender[3],age_range.get(3), enforced_params.get(3), time_created[3], player_restricted[3]);

        games = new GameModel[] {game_1, game_2, game_3, game_4};
    };

}
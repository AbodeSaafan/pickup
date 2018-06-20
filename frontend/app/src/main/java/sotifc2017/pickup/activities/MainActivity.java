package sotifc2017.pickup.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
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

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sotifc2017.pickup.Common.Defaults;
import sotifc2017.pickup.R;
import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.GetJwt;
import sotifc2017.pickup.api.Search;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.GetSearchRequest;
import sotifc2017.pickup.api.enums.ENFORCED_PARAMS;
import sotifc2017.pickup.api.models.GameModel;
import sotifc2017.pickup.fragment_interfaces.OnFragmentReplacement;
import sotifc2017.pickup.fragment_managers.ConfigurableFragmentItemsManager;
import sotifc2017.pickup.fragments.CreateGameFragment;
import sotifc2017.pickup.fragments.ExtendedProfileFragment;
import sotifc2017.pickup.fragments.GameViewFragment;
import sotifc2017.pickup.fragments.ListViewFragment;
import sotifc2017.pickup.fragments.MainSearchFragment;
import sotifc2017.pickup.fragments.RefinedMapFragment;
import sotifc2017.pickup.fragments.SettingsFragment;
import sotifc2017.pickup.helpers.GameListItemHelper;

import static sotifc2017.pickup.Common.Defaults.FC_TAG;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, HostingActivity, OnFragmentReplacement, RangeTimePickerDialog.ISelectedTime, GetJwt.Callback {

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
    private HashMap<String, GameModel> pin_of_game = new HashMap<String, GameModel>();
    private GameListItemHelper helper;
    private Geocoder geocoder;
    private Activity mContext;
    private View snackView;
    private Snackbar snackbar;
    private ProgressDialog loadingResponse;

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
        helper = new GameListItemHelper();
        geocoder = new Geocoder(this, Locale.getDefault());
        mContext = (Activity) this;

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
        if (mFusedLocationClient == null) {
            return;
        }
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
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count != 0) {
            super.onBackPressed();
        } else {
            if (back_pressed_time + PERIOD > System.currentTimeMillis()) super.onBackPressed();
            else
                Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
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
                            //createCustomGames();
                            new GetJwt(MainActivity.this).execute(MainActivity.this);
                            //displayGames(location);
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("SotiFc", "Failed getting user's location");
                        }
                    });
        } else {
            askForPermissions();
            //createCustomGames();

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


    //TODO: Want to use Game object, or whatever we actually pull from backend here. Need to sync-up.
    public void plotGames(GameModel[] all_games, GoogleMap mMap) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (GameModel game : all_games) {
            LatLng point = new LatLng(game.location.get("lat"), game.location.get("lng"));
            Marker marker = mMap.addMarker(new MarkerOptions().position(point));
            pin_of_game.put(marker.getId(), game);
            builder.include(point);
        }

        LatLngBounds bounds = builder.build();
        int padding = 50;
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    public void zoomToUser(GoogleMap mMap, LatLng userLoc) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 10));
    }


    public void displayGames(final GameModel[] games, Location location) {
        stopLocationUpdates();
        if (location != null && checkPermissions()) {
            // Save as last known location
            SaveLastKnownLocation(location);
            // Generates Sample Games. Take out when connected to backend.
            sampleGames = new ArrayList<>();

            for (GameModel game : games) {
                sampleGames.add(new LatLng(game.location.get("lat"), game.location.get("lng")));
            }


            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    plotGames(games, mMap);


                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            System.out.println(marker.getId());
                            System.out.println(marker.getPosition());
                            snackbar = Snackbar.make(floatGameItem, "", Snackbar.LENGTH_INDEFINITE);
                            Snackbar.SnackbarLayout slt = (Snackbar.SnackbarLayout) snackbar.getView();

                            final GameModel gameObject = pin_of_game.get(marker.getId());
                            snackView = inflater.inflate(R.layout.fragment_game_list_item, null);

                            TextView gameName = snackView.findViewById(R.id.gameName);
                            gameName.setText(gameObject.name);

                            TextView gameLocation = snackView.findViewById(R.id.location);
                            gameLocation.setText(helper.getLocation(geocoder, gameObject.location.get("lat"), gameObject.location.get("lng")));


                            TextView dateTime = snackView.findViewById(R.id.dateTime);
                            TextView finalTime = snackView.findViewById(R.id.time);
                            HashMap<String, String> date_time = new HashMap<String, String>();
                            date_time = helper.getDate(gameObject.start_time, gameObject.end_time);
                            if (date_time.get("finalTime") == "") {
                                dateTime.setText(date_time.get("dateTime"));
                            } else {
                                dateTime.setText(date_time.get("dateTime"));
                                finalTime.setText(date_time.get("finalTime"));
                            }


                            TextView player_info = snackView.findViewById(R.id.players);
                            player_info.setText(helper.getPlayerCount(gameObject.total_players_added, gameObject.total_players_required));

                            ImageView player_icon = snackView.findViewById(R.id.player_icon);
                            helper.setPlayerIcon(mContext, player_icon, gameObject.total_players_required, gameObject.total_players_added);


                            slt.setPadding(0, 0, 0, 0);
                            slt.addView(snackView, 0);
                            snackbar.show();


                            if (gameObject.player_restricted) {
                                ImageButton warning = snackView.findViewById(R.id.warning);
                                warning.setVisibility(View.VISIBLE);
                                snackView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //popup toast
                                        Toast.makeText(MainActivity.this, "Cannot join game", Toast.LENGTH_SHORT).show();
                                        dismissSnackbar();
                                    }
                                });

                            } else {
                                snackView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //popup toast
                                        Bundle bundle = new Bundle();
                                        String gameJson = Utils.gson.toJson(gameObject);
                                        bundle.putString("gameJson", gameJson);

                                        GameViewFragment gameViewFragment = new GameViewFragment();
                                        gameViewFragment.setArguments(bundle);

                                        ((HostingActivity) mContext).replaceFragment(gameViewFragment, true, -1);
                                    }
                                });
                            }


                            return false;
                        }

                        ;

                    });
                }
            });
        } else {
            //TODO: Zoom to actual city from user's profile, not random points.
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    Location lastKnown = GetLastKnownLocation();
                    if (lastKnown.getAccuracy() == Float.MIN_VALUE) {
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
                switch (item.getItemId()) {
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
    public void replaceFragment(Fragment frag, boolean backStackAdd, int fragId) {
        hideKeyboard();
        dismissSnackbar();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment_container, frag, String.valueOf(fragId));
        if (backStackAdd) transaction.addToBackStack(null);

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

                        }
                    });
        } else {

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
    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd) {
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

        layoutParam.setMargins(0, padTop ? findViewById(R.id.toolbar).getHeight() : 0, 0, 0);

        fragmentContainer.setLayoutParams(layoutParam);
        drawerLayout.closeDrawers();
        ConfigurableFragmentItemsManager.configureMenuItemSelection(navigationView, currentFragmentId);
    }

    @Override
    public void clearMenuItemSelection() {
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

    private void SaveLastKnownLocation(Location location) {
        Log.v("location", String.format("updating location with lat %f and lng %f", location.getLatitude(), location.getLongitude()));
        getSharedPreferences(Defaults.FC_TAG, MODE_PRIVATE).edit()
                .putBoolean("locationSaved", true).apply();
        getSharedPreferences(Defaults.FC_TAG, MODE_PRIVATE).edit().
                putLong("lastKnownLocationLat", Double.doubleToRawLongBits(location.getLatitude())).apply();
        getSharedPreferences(Defaults.FC_TAG, MODE_PRIVATE).edit().
                putLong("lastKnownLocationLng", Double.doubleToRawLongBits(location.getLongitude())).apply();
    }

    private Location GetLastKnownLocation() {
        Location result = new Location("FromPrefs");
        boolean locationSaved = getSharedPreferences(Defaults.FC_TAG, MODE_PRIVATE).getBoolean("locationSaved", false);

        if (!locationSaved) {
            result.setAccuracy(Float.MIN_VALUE);
            return result;
        }

        result.setLatitude(Double.longBitsToDouble(getSharedPreferences(Defaults.FC_TAG, MODE_PRIVATE).getLong("lastKnownLocationLat", Long.MIN_VALUE)));
        result.setLongitude(Double.longBitsToDouble(getSharedPreferences(Defaults.FC_TAG, MODE_PRIVATE).getLong("lastKnownLocationLng", Long.MIN_VALUE)));

        return result;
    }

    private void dismissSnackbar() {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    private void plotSearchGames(String jwt) {
        Location userLocation = GetLastKnownLocation();
        Map<String, Double> game_location = new HashMap<>();
        game_location.put("lat", userLocation.getLatitude());
        game_location.put("lng", userLocation.getLongitude());

        GetSearchRequest request = GetSearchRequest.CreateGameRequest(jwt, game_location, 20);
        Utils.getInstance(this).getRequestQueue(this).add(Search.getSearch_request(request, successful_game_search, error_game_search));
    }

    @Override
    public void jwtSuccess(String jwt) {
        plotSearchGames(jwt);

    }

    @Override
    public void jwtFailure(GetJwt.JwtOutcome outcome) {
        switch (outcome) {
            case NoRefresh:
            case BadJwtRetrieval:
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
            case ServerFault:
            default:
                GetJwt.exitAppDialog(this).show();
        }
    }

    private Response.Listener<JSONObject> successful_game_search = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                // get results
                // display results by loading correct list view
                GameModel[] games = getGamesFromSearch(response.get("games").toString());
                displayGames(games, GetLastKnownLocation());

                loadingResponse.cancel();
            } catch (Exception e) {
                Log.e("search", "error parsing results");
            }

        }
    };

    private Response.ErrorListener error_game_search = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                if (error.networkResponse.statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                    searchDoesNotHaveResults();
                } else {
                    Log.v("search", "panic?");
                }
                Log.e("search", errorJSON.toString());
            } catch (Exception e) {
                Log.e("search", "error parsing failure");
            }
        }
    };

    private GameModel[] getGamesFromSearch(String searchGamesResult) {
        return Utils.gson.fromJson(searchGamesResult, GameModel[].class);
    }

    ;

    private void searchDoesNotHaveResults() {
        loadingResponse.cancel();
        android.app.AlertDialog.Builder exitDialog = new android.app.AlertDialog.Builder(this).
                setMessage(this.getString(R.string.main_search_nothing_matches)).
                setCancelable(true).
                setPositiveButton(
                        "Okay",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        exitDialog.create().show();
    }

    /*
    private void createCustomGames() {
        // create the hard coded game Objects:
        HashMap<String, Double> location_1 = new HashMap<String, Double>();
        location_1.put("lat", 43.624366);
        location_1.put("lng", -79.670428);
        HashMap <String, Double> location_2 = new HashMap<String, Double>();
        location_2.put("lat", 43.618327);
        location_2.put("lng", -79.680148);
        //Toronto Location
        HashMap <String, Double> location_3 = new HashMap<String, Double>();
        location_3.put("lat", 43.655163);
        location_3.put("lng", -79.388488);
        //Brampton Location
        HashMap <String, Double> location_4 = new HashMap<String, Double>();
        location_4.put("lat", 43.651489);
        location_4.put("lng", -79.736289);

        int[] game_id = new int[] {1, 2, 3, 4};
        String[] name = new String[]{"radhika's game", "radhika's game pt II", "radhika's game pt III", "radhika's game pt IV"};
        String[] type = new String[]{"casual", "serious", "casual", "serious"};
        int[] skill_min = new int[] {5, 3, 2, 4};
        int[] skill_max = new int[] {7, 10, 8, 9};
        int[] total_players_required = new int[] {15, 10, 20, 12};
        int[] total_players_added = new int[] {12, 5, 11, 5};
        //Game 1 -> June 15 (12:00 PM to 1:00 PM)
        //Game 2 -> June 15 - June 16 (11:00 PM to 1:00 AM)
        //Game 3 -> June 17 - June 18 (10:30 PM to 3:00 AM)
        //Game 4 -> June 16 (9:00 AM to 1:45 PM)
        long[] start_time = new long[] {1529078400, 1529118000, 1529289000, 1529154000};
        long[] end_time = new long[] {1529082000, 1529125200, 1529305200, 1529171100};
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
        age_range.add(new int[]{15, 20});
        age_range.add(new int[]{30, 45});
        int[] time_created = new int[] {1504272395, 1504272395, 1504272395, 1504272395};
        List<ENFORCED_PARAMS[]> enforced_params = new ArrayList<ENFORCED_PARAMS[]>();
        enforced_params.add(new ENFORCED_PARAMS[] {ENFORCED_PARAMS.age});
        enforced_params.add(new ENFORCED_PARAMS[] {ENFORCED_PARAMS.age, ENFORCED_PARAMS.gender});
        enforced_params.add(new ENFORCED_PARAMS[] {ENFORCED_PARAMS.age, ENFORCED_PARAMS.gender});
        enforced_params.add(new ENFORCED_PARAMS[] {ENFORCED_PARAMS.age});
        boolean[] player_restricted = new boolean[]{true, false, true, false};

        List<GameModel> gamesLst = new ArrayList<GameModel>();
        for (int i = 0; i <= 3; i++) {
            GameModel game = new GameModel(game_id[i],
                    name[i],
                    type[i],
                    -1,
                    total_players_required[i],
                    total_players_added[i],
                    start_time[i],
                    end_time[i],
                    end_time[i] - start_time[i],
                    locations.get(i),creator_id[i],
                    descriptions[i],
                    location_notes[i],
                    gender[i],
                    age_range.get(i),
                    enforced_params.get(i),
                    time_created[i],
                    player_restricted[i],
                    "-1",
                    "-1", 0, 0);

            gamesLst.add(game);
        }

        games = new GameModel[gamesLst.size()];
        games = gamesLst.toArray(games);
    };

*/


}
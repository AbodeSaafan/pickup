package sotifc2017.pickup;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private List<LatLng> sampleGames;
    private int MY_PERMISSIONS_FINE_LOCATION;
    private int MY_PERMISSIONS_COARSE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.activity_map);
        mapFragment.getMapAsync(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askForPermissions();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askForPermissions();
        }
        else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
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

                                    double foundLatitude;
                                    double foundLongitude;

                                    foundLatitude = location.getLatitude() + y;
                                    foundLongitude = location.getLongitude() + new_x;
                                    sampleGames.add(new LatLng(foundLatitude, foundLongitude));
                                }
                                plotGames(mMap, sampleGames);
//                                zoomToUser(mMap, position);
                                zoomToViewPoints(mMap, sampleGames);
                            }
                        }
                    });

        }
    }
    public void askForPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_COARSE_LOCATION);
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15));
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

}

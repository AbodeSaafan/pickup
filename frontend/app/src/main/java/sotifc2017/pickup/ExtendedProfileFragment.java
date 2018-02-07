package sotifc2017.pickup;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.ExtendedProfile;
import sotifc2017.pickup.api.GetJwt;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.GetExtendedProfileResponse;

public class ExtendedProfileFragment extends Fragment implements GetJwt.Callback {

    TextView age;
    TextView gender;
    TextView skillevel;
    TextView location;
    RatingBar averageReview;
    TextView username;
    TextView gamesCreated;
    TextView gamesPlayed;
    private ProgressDialog progressDialog;
    String user_id;
    Geocoder geocoder;
    String[] LatLng;

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_extended_profile, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().setContentView(R.layout.fragment_extended_profile);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        user_id =  getActivity().getIntent().getStringExtra("userID");

        new GetJwt(this).execute(getActivity());

        if (user_id == null) {
            //Log.d("CREATION", "reached here");
            user_id = String.valueOf(Authentication.getUserId(getActivity()));
        } else {
            Button addFriendButton = (Button) getView().findViewById(R.id.add_friend);
            addFriendButton.setVisibility (View.VISIBLE);
        }
    }

    @Override
    public void jwtSuccess(String jwt) {
        GetExtendedProfile(jwt);
    }

    @Override
    public void jwtFailure(Exception e) {
        Log.e("jwt", e.getMessage());
        Authentication.logout(getActivity());
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);
    }


    private Response.Listener<JSONObject> successful_extendedProfile = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try{
                ExtendedProfileSuccess(Utils.gson.fromJson(response.toString(), GetExtendedProfileResponse.class));
            }
            //TODO: Implement Failure
            catch (Exception e){
                ExtendedProfileFailure(e.getMessage());
            }

        }
    };

    private Response.ErrorListener error_extendedProfile =  new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                ExtendedProfileFailure(errorJSON.getString("jwtFailure"));
            }
            //TODO: Implement Failure
            catch (Exception e){
                ExtendedProfileFailure(e.getMessage());
            }
        }
    };

    private void GetExtendedProfile(String jwt) {
        Utils.getInstance(getActivity()).getRequestQueue(getActivity()).add(ExtendedProfile.getProfile_request(jwt, user_id, successful_extendedProfile, error_extendedProfile));

    }



    private void ExtendedProfileSuccess(GetExtendedProfileResponse response) throws IOException {
        Toast.makeText(getActivity(), "ExtendedProfile successsful", Toast.LENGTH_SHORT).show();
        age = (TextView) getView().findViewById(R.id.age);
        age.setText(response.age + " years old");
        gender = (TextView)getView().findViewById(R.id.gender);
        if (response.gender == "M") {
            gender.setText("Male");
        } else if (response.gender == "F") {
            gender.setText("Female");
        }

        skillevel = (TextView)getView().findViewById(R.id.skill_level);
        String skill = SignUpActivity.skillLevels[Integer.parseInt(response.skilllevel)] + "(" + response.skilllevel + ")";

        //Log.d("CREATION", "skilllevel " + SignUpActivity.skillLevels[Integer.parseInt(response.skilllevel)]);

        skillevel.setText(skill);

        location = (TextView)getView().findViewById(R.id.location);
        LatLng = response.location.split(",");
        double latitude = Double.parseDouble(LatLng[0].substring(1));
        double longtitude = Double.parseDouble(LatLng[1].substring(0, LatLng[1].length() - 1));
        String newLocation;
        List<Address> addresses  = geocoder.getFromLocation(latitude, longtitude, 1);
        if (addresses.size() > 0) {
            newLocation = addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryCode();
        } else {
            newLocation = "N/A";
        }

        location.setText(newLocation);


        averageReview = (RatingBar)getView().findViewById(R.id.averageReviewValue);
        float aReviewValue = Float.parseFloat(response.average_review);
        averageReview.setRating(aReviewValue);

        gamesCreated = (TextView) getView().findViewById(R.id.gamesCreatedValue);
        gamesCreated.setText(response.games_created);

        gamesPlayed = (TextView) getView().findViewById(R.id.gamesCreatedValue);
        gamesPlayed.setText(response.games_joined);


        username = (TextView) getView().findViewById(R.id.user_profile_name);
        username.setText(response.username);

    }

    private void ExtendedProfileFailure(String message) {

        Toast.makeText(getActivity(), "ExtendedProfile failed: " + message, Toast.LENGTH_SHORT).show();

    }



}




package sotifc2017.pickup.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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

import sotifc2017.pickup.R;
import sotifc2017.pickup.activities.SignInActivity;
import sotifc2017.pickup.activities.SignUpActivity;
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
    String user_id;
    Geocoder geocoder;
    String[] LatLng;

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
    public void jwtFailure(GetJwt.JwtOutcome outcome) {
        //TODO improve to handle well
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
        if (response.gender.equals("M")) {
            gender.setText("Male");
        } else if (response.gender.equals("F")) {
            gender.setText("Female");
        }

        skillevel = (TextView) getView().findViewById(R.id.skill_level);
        String skill = SignUpActivity.skillLevels[Integer.parseInt(response.skilllevel)] + "(" + response.skilllevel + ")";

        //Log.d("CREATION", "skilllevel " + SignUpActivity.skillLevels[Integer.parseInt(response.skilllevel)]);

        skillevel.setText(skill);

        location = (TextView) getView().findViewById(R.id.location);
        LatLng = response.location.split(",");
        double latitude = Double.parseDouble(LatLng[0].substring(1));
        double longitude = Double.parseDouble(LatLng[1].substring(0, LatLng[1].length() - 1));
        String newLocation;
        List<Address> addresses  = geocoder.getFromLocation(latitude, longitude, 1);
        if (addresses.size() > 0) {
            newLocation = addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryCode();
        } else {
            newLocation = "N/A";
        }

        location.setText(newLocation);

        username = (TextView) getView().findViewById(R.id.user_profile_name);
        username.setText(response.username);


        averageReview = (RatingBar)getView().findViewById(R.id.averageReviewValue);
        float aReviewValue = Float.parseFloat(response.average_review);
        averageReview.setRating(aReviewValue);

        /*GamesCreated and GamesPlayed not working*/

        gamesCreated = (TextView) getView().findViewById(R.id.gamesCreatedValue);
        gamesCreated.setText(response.games_created);
        //Log.d("CREATION", String.valueOf(response.games_created));

        gamesPlayed = (TextView) getView().findViewById(R.id.gamesCreatedValue);
        gamesPlayed.setText(response.games_joined);
        //Log.d("CREATION", String.valueOf(response.games_joined));


    }

    private void ExtendedProfileFailure(String message) {

        Toast.makeText(getActivity(), "ExtendedProfile failed: " + message, Toast.LENGTH_SHORT).show();

    }



}




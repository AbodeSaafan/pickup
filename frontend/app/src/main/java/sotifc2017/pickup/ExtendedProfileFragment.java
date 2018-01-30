package sotifc2017.pickup;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.Locale;

import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.ExtendedProfile;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.GetExtendedProfileResponse;


/**
 * Created by radhika on 2018-01-14.
 */

public class ExtendedProfileFragment extends Fragment {

    TextView age;
    TextView gender;
    TextView skillevel;
    TextView location;
    TextView averageReview;
    TextView username;
    private ProgressDialog progressDialog;
    String jwt;
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
        return inflater.inflate(R.layout.activity_extended_profile, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().setContentView(R.layout.activity_extended_profile);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        user_id =  getActivity().getIntent().getStringExtra("userID");

        try {
            jwt = Authentication.getJwt(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (user_id == null) {
            //Log.d("CREATION", "reached here");
            user_id = String.valueOf(Authentication.getUserId(getActivity()));
        } else {
            Button addFriendButton = (Button) getView().findViewById(R.id.addFriend);
            addFriendButton.setVisibility (View.VISIBLE);
        }

        GetExtendedProfile();

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
                ExtendedProfileFailure(errorJSON.getString("error"));
            }
            //TODO: Implement Failure
            catch (Exception e){
                ExtendedProfileFailure(e.getMessage());
            }
        }
    };

    private void GetExtendedProfile() {

        //Utils.getInstance(ExtendedProfileFragment.this).addToRequestQueue(ExtendedProfile.getProfile_request(jwt, user_id, successful_extendedProfile, error_extendedProfile));

        Utils.getInstance(getActivity()).getRequestQueue(getActivity()).add(ExtendedProfile.getProfile_request(jwt, user_id, successful_extendedProfile, error_extendedProfile));

    }



    private void ExtendedProfileSuccess(GetExtendedProfileResponse response) {
        Toast.makeText(getActivity(), "ExtendedProfile successsful", Toast.LENGTH_SHORT).show();
        age = (TextView)getView().findViewById(R.id.ageValue);
        age.setText(response.age);
        gender = (TextView)getView().findViewById(R.id.genderValue);
        gender.setText(response.gender);
        skillevel = (TextView)getView().findViewById(R.id.skillLevelValue);

        //skillevel.setText(SignUpActivity.skillLevels[Integer.parseInt(response.skilllevel)]);
        //Log.d("CREATION", "skilllevel " + SignUpActivity.skillLevels[Integer.parseInt(response.skilllevel)]);

        skillevel.setText(response.skilllevel);

        location = (TextView)getView().findViewById(R.id.locationValue);
        LatLng = response.location.split("(,)");
        
        location.setText(response.location);
        averageReview = (TextView)getView().findViewById(R.id.averageReviewValue);
        averageReview.setText(response.average_review);
        username = (TextView) getView().findViewById(R.id.user_profile_name);
        username.setText(response.username);

    }

    private void ExtendedProfileFailure(String message) {

        Toast.makeText(getActivity(), "ExtendedProfile failed: " + message, Toast.LENGTH_SHORT).show();

    }



}




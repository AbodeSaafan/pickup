package sotifc2017.pickup.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import sotifc2017.pickup.Common.SkillLevel;
import sotifc2017.pickup.CommonComponents;
import sotifc2017.pickup.R;
import sotifc2017.pickup.activities.SignInActivity;
import sotifc2017.pickup.activities.SignUpActivity;
import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.ExtendedProfile;
import sotifc2017.pickup.api.GetJwt;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.GetExtendedProfileResponse;
import sotifc2017.pickup.fragment_interfaces.OnFragmentReplacement;

public class ExtendedProfileFragment extends Fragment implements GetJwt.Callback {
    int currentFragmentId = R.id.action_profile;
    private OnFragmentReplacement mCallback;

    private ProgressDialog loadingResponse;

    private TextView age;
    private TextView gender;
    private TextView skillevel;
    private TextView location;
    private RatingBar averageReview;
    private TextView username;
    private TextView gamesCreated;
    private TextView gamesPlayed;
    private String user_id;
    private TextView topTagValue;
    private Geocoder geocoder;
    private String[] LatLng;

    private boolean viewingSelfProfile;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFragmentReplacement) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "[ExtendedProfileFragment] must implement OnFragmentReplacement)");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        loadingResponse = CommonComponents.getLoadingProgressDialog(getActivity());
        loadingResponse.show();
        new GetJwt(this).execute(getActivity());

        if (getArguments() != null) {
            user_id = getArguments().getString("userID");
            viewingSelfProfile = false;

        } else {
            viewingSelfProfile = true;
            user_id = String.valueOf(Authentication.getUserId(getActivity()));
        }

        return inflater.inflate(R.layout.fragment_extended_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ImageButton addFriendButton = view.findViewById(R.id.add_friend);
        if(!viewingSelfProfile){
            addFriendButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

    }

    @Override
    public void onResume() {
        if(viewingSelfProfile){
            mCallback.configureMenuItemSelection(currentFragmentId, true);
        } else {
            mCallback.clearMenuItemSelection();
        }

        super.onResume();
    }

    @Override
    public void jwtSuccess(String jwt) {
        GetExtendedProfile(jwt);
    }

    @Override
    public void jwtFailure(GetJwt.JwtOutcome outcome) {
        switch(outcome){
            case NoRefresh:
            case BadJwtRetrieval:
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            case ServerFault:
            default:
                GetJwt.exitAppDialog(getActivity()).show();
        }
    }


    private Response.Listener<JSONObject> successful_extendedProfile = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try{
                ExtendedProfileSuccess(Utils.gson.fromJson(response.toString(), GetExtendedProfileResponse.class));
            }
            //TODO we should deal with this another way, how many failures can we have, lets deal with those per case
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

    private void ExtendedProfileSuccess(GetExtendedProfileResponse response) {
        SetGlobalsBasedOnView(getView());
        SetElementsBasedOnProfile(response);

        loadingResponse.cancel();
    }

    private void ExtendedProfileFailure(String message) {
        Toast.makeText(getActivity(), "ExtendedProfile failed: " + message, Toast.LENGTH_SHORT).show();
        loadingResponse.cancel();
    }

    private void SetGlobalsBasedOnView(View view){
        age = view.findViewById(R.id.age);
        gender = getView().findViewById(R.id.gender);
        skillevel = getView().findViewById(R.id.skill_level);
        location = getView().findViewById(R.id.location);
        username = getView().findViewById(R.id.user_profile_name);
        averageReview = getView().findViewById(R.id.averageReviewValue);
        gamesCreated = getView().findViewById(R.id.gamesCreatedValue);
        gamesPlayed = getView().findViewById(R.id.gamesPlayedValue);
        topTagValue = getView().findViewById(R.id.topTagAwardedValue);
    }
    private void SetElementsBasedOnProfile(GetExtendedProfileResponse response){

        //TODO separate into functions with more checking
        age.setText(String.format(getResources().getString(R.string.extended_profile_age_text), Integer.toString(response.age)));

        if (response.gender.equals("M")) {
            gender.setText(getResources().getString(R.string.prompt_male));
        } else if (response.gender.equals("F")) {
            gender.setText(getResources().getString(R.string.prompt_female));
        } else {
            gender.setText(getResources().getString(R.string.prompt_other_gender));
        }

        skillevel.setText(
                String.format(
                        getResources().getString(R.string.extended_profile_skill_text),
                        getResources().getString(SkillLevel.GetFriendlyTextResourceId(response.skilllevel)),
                        response.skilllevel));


        LatLng = response.location.split(",");
        double latitude = Double.parseDouble(LatLng[0].substring(1));
        double longitude = Double.parseDouble(LatLng[1].substring(0, LatLng[1].length() - 1));
        String newLocation;
        try {
            List<Address> addresses  = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                newLocation = addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryCode();
            } else {
                newLocation = "N/A";
            }
        } catch (IOException e){
            newLocation = "N/A";
        }

        location.setText(newLocation);

        username.setText(response.username);

        averageReview.setRating(response.average_review);

        gamesCreated.setText(Integer.toString(response.games_created));

        gamesPlayed.setText(Integer.toString(response.games_joined));

        topTagValue.setText(response.top_tag != null ? String.format(getResources().getString(R.string.extended_profile_top_tag_value), response.top_tag, response.top_tag_count) : getResources().getString(R.string.extended_profile_no_tags));
    }
}




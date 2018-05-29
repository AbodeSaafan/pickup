package sotifc2017.pickup.fragments;

import android.annotation.SuppressLint;
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
import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.ExtendedProfile;
import sotifc2017.pickup.api.GetJwt;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.GetExtendedProfileResponse;
import sotifc2017.pickup.api.enums.API_GENDER;
import sotifc2017.pickup.fragment_interfaces.OnFragmentReplacement;

public class ExtendedProfileFragment extends Fragment implements GetJwt.Callback {
    int currentFragmentId = R.id.action_profile;
    private OnFragmentReplacement mCallback;

    private ProgressDialog loadingResponse;

    private TextView ageTextView;
    private TextView genderTextView;
    private TextView skillevelTextView;
    private TextView locationTextView;
    private RatingBar averageReviewRatingBar;
    private TextView usernameTextView;
    private TextView gamesCreatedTextView;
    private TextView gamesPlayedTextView;
    private TextView topTagValueTextView;

    private String user_id;

    private Geocoder geocoder;

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

        String userIdPassedIn = getArguments() == null ? "" : getArguments().getString("userID");

        if (userIdPassedIn != null && !userIdPassedIn.isEmpty()) {
            user_id = userIdPassedIn;
        } else {
            user_id = String.valueOf(Authentication.getUserId(getActivity()));
        }

        new GetJwt(this).execute(getActivity());

        return inflater.inflate(R.layout.fragment_extended_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(!viewingSelfProfile()){
            ImageButton addFriendButton = view.findViewById(R.id.add_friend);
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
        if(viewingSelfProfile()){
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
        ageTextView = view.findViewById(R.id.age);
        genderTextView = view.findViewById(R.id.gender);
        skillevelTextView = view.findViewById(R.id.skill_level);
        locationTextView = view.findViewById(R.id.location);
        usernameTextView = view.findViewById(R.id.user_profile_name);
        averageReviewRatingBar = view.findViewById(R.id.averageReviewValue);
        gamesCreatedTextView = view.findViewById(R.id.gamesCreatedValue);
        gamesPlayedTextView = view.findViewById(R.id.gamesPlayedValue);
        topTagValueTextView = view.findViewById(R.id.topTagAwardedValue);
    }
    private void SetElementsBasedOnProfile(GetExtendedProfileResponse response){
        SetAge(response.age);
        SetGender(response.gender);
        SetSkillLevel(response.skilllevel);
        SetLocation(response.location);
        SetUsername(response.username);
        SetAverageRating(response.average_review);
        SetGamesCounters(response.games_created, response.games_joined);
        SetTopTag(response.top_tag, response.top_tag_count);
    }

    private void SetAge(int age){
        ageTextView.setText(
                String.format(getResources().getString(R.string.extended_profile_age_text),
                        Integer.toString(age)));
    }

    private void SetGender(API_GENDER gender) {
        switch (gender) {
            case M:
                genderTextView.setText(getResources().getString(R.string.prompt_male));
                break;
            case F:
                genderTextView.setText(getResources().getString(R.string.prompt_female));
                break;
            case O:
                genderTextView.setText(getResources().getString(R.string.prompt_other_gender));
                break;
            default:
                genderTextView.setText(getResources().getString(R.string.none));
        }
    }

    private void SetSkillLevel(int level){
        skillevelTextView.setText(
                String.format(
                        getResources().getString(R.string.extended_profile_skill_text),
                        getResources().getString(SkillLevel.GetFriendlyTextResourceId(level)), level));
    }

    private void SetLocation(String loc){
        String[] latLng = loc.split(",");
        double latitude = Double.parseDouble(latLng[0].substring(1));
        double longitude = Double.parseDouble(latLng[1].substring(0, latLng[1].length() - 1));
        String newLocation;
        try {
            List<Address> addresses  = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                newLocation = addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryCode();
            } else {
                newLocation = getResources().getString(R.string.none);
            }
        } catch (IOException e){
            newLocation = getResources().getString(R.string.none);
        }

        locationTextView.setText(newLocation);
    }

    private void SetUsername(String username){
        usernameTextView.setText(username);
    }

    private void SetAverageRating(float average_review){
        averageReviewRatingBar.setRating(average_review);
    }

    @SuppressLint("SetTextI18n") // We are displaying plain integers
    private void SetGamesCounters(int created, int played){
        gamesCreatedTextView.setText(Integer.toString(created));

        gamesPlayedTextView.setText(Integer.toString(played));
    }

    private void SetTopTag(String tag, int count){
        if(tag != null){
            topTagValueTextView.setText(String.format(getResources().getString(R.string.extended_profile_top_tag_value), tag, count));
        }
        else{
            topTagValueTextView.setText(getResources().getString(R.string.extended_profile_no_tags));
        }

    }

    private boolean viewingSelfProfile(){
        return user_id.equals(String.valueOf(Authentication.getUserId(getActivity())));
    }
}
package sotifc2017.pickup;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.ExtendedProfile;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.GetExtendedProfileResponse;

/**
 * Created by radhika on 2018-01-14.
 */

public class ExtendedProfileActivity extends AppCompatActivity {

    TextView age;
    TextView gender;
    TextView skillevel;
    TextView location;
    TextView averageReview;
    private ProgressDialog progressDialog;
    String jwt;
    String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extended_profile);
        /*
        age = (TextView)findViewById(R.id.ageValue);
        age.setText("21");
        gender = (TextView)findViewById(R.id.genderValue);
        gender.setText("Female");
        skillevel = (TextView)findViewById(R.id.skillLevelValue);
        skillevel.setText("Rookie");
        location = (TextView)findViewById(R.id.locationValue);
        location.setText("Mississauga");
        averageReview = (TextView)findViewById(R.id.averageReviewValue);
        averageReview.setText("2.5");
        */

        try{
            jwt = Authentication.getJwt(this);
        }
        catch (Exception e){
            // Sign out of app
        }

        user_id = "4";
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
        Utils.getInstance(this).getRequestQueue(this).add(ExtendedProfile.getProfile_request(jwt, user_id, successful_extendedProfile, error_extendedProfile));
    }

    private void ExtendedProfileSuccess(GetExtendedProfileResponse response) {
        Toast.makeText(this, "ExtendedProfile successsful", Toast.LENGTH_SHORT).show();
        age = (TextView)findViewById(R.id.ageValue);
        age.setText(response.age);
        gender = (TextView)findViewById(R.id.genderValue);
        gender.setText(response.gender);
        skillevel = (TextView)findViewById(R.id.skillLevelValue);
        skillevel.setText(response.skillevel);
        location = (TextView)findViewById(R.id.locationValue);
        location.setText(response.location);
        averageReview = (TextView)findViewById(R.id.averageReviewValue);
        averageReview.setText(response.average_review);

    }

    private void ExtendedProfileFailure(String message) {
        Toast.makeText(this, "ExtendedProfile failed: " + message, Toast.LENGTH_SHORT).show();

    }

}




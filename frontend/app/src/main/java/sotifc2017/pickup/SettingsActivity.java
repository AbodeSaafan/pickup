package sotifc2017.pickup;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.SystemClock;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.GetPrivateProfileRequest;
import sotifc2017.pickup.api.PrivateProfile;
import sotifc2017.pickup.api.contracts.GetPrivateProfileResponse;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment {

        private ProgressDialog progressDialog;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            progressDialog = new ProgressDialog(getActivity(),
                    R.style.AppTheme_Dark);

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");

            Window window = progressDialog.getWindow();
            window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            progressDialog.setCancelable(false);
            progressDialog.show();

            LoadSettings(getActivity());

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

        public void LoadSettings(Activity act){
            try {
                String jwt = Authentication.getJwt(act);
                Utils.getInstance(act).getRequestQueue(act).add(PrivateProfile.get_private_profile_request(new GetPrivateProfileRequest(jwt), successful_profile, error_profile));
            } catch (Exception e){
                progressDialog.cancel();
                Authentication.logout(act);
                Intent intent = new Intent(act, SignInActivity.class);
                startActivity(intent);
            }
        }

        private void LoadProfileValuesFromResponse(GetPrivateProfileResponse response) {
            EditTextPreference username = (EditTextPreference) findPreference("editTextPref_username");
            EditTextPreference fname = (EditTextPreference) findPreference("editTextPref_firstname");
            EditTextPreference lname = (EditTextPreference) findPreference("editTextPref_lastname");
            EditTextPreference dob = (EditTextPreference) findPreference("editTextPref_dob");
            EditTextPreference gender = (EditTextPreference) findPreference("editTextPref_gender");
            EditTextPreference email  = (EditTextPreference) findPreference("editTextPref_email");
            username.setSummary(response.username);
            fname.setSummary(response.fname);
            lname.setSummary(response.lname);
            dob.setSummary(response.dob);
            gender.setSummary(response.gender.equalsIgnoreCase("M") ? "Male" : response.gender.equalsIgnoreCase("F") ? "Female" : "Other");
            email.setSummary(response.email);
        }

        private Response.Listener<JSONObject> successful_profile = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    LoadProfileValuesFromResponse((Utils.gson.fromJson(response.toString(), GetPrivateProfileResponse.class)));
                    progressDialog.cancel();
                }
                //TODO: Implement Failure
                catch (Exception e){

                }

            }
        };

        private Response.ErrorListener error_profile =  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                    progressDialog.cancel();
                    Authentication.logout(getActivity());
                    Intent intent = new Intent(getActivity(), SignInActivity.class);
                    startActivity(intent);
                }
                //TODO: Implement Failure
                catch (Exception e){

                }
            }
        };
    }


}

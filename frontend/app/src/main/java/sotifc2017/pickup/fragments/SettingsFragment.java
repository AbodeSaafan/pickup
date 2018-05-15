package sotifc2017.pickup.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;


import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import sotifc2017.pickup.R;
import sotifc2017.pickup.activities.SignInActivity;
import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.GetJwt;
import sotifc2017.pickup.api.PrivateProfile;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.ChangePasswordRequest;
import sotifc2017.pickup.api.contracts.GetPrivateProfileRequest;
import sotifc2017.pickup.api.contracts.GetPrivateProfileResponse;
import sotifc2017.pickup.api.contracts.UpdatePrivateProfileRequest;
import sotifc2017.pickup.fragment_interfaces.OnFragmentReplacement;


public class SettingsFragment extends PreferenceFragment implements GetJwt.Callback  {
    int currentFragmentId = R.id.action_settings;
    OnFragmentReplacement mCallback;

    private ProgressDialog progressDialog;
    private String jwt;
    private Calendar myCalendar = Calendar.getInstance();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFragmentReplacement) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "[SettingsFragment] must implement OnFragmentReplacement)");
        }
    }

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

        new GetJwt(this).execute(getActivity());

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        mCallback.configureMenuItemSelection(currentFragmentId, true);

        super.onResume();
    }

    @Override
    public void jwtSuccess(String jwt) {
        LoadSettings(getActivity(), jwt);
        this.jwt = jwt;
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

    public void LoadSettings(Activity act, String jwt) {
        try {
            Utils.getInstance(act).getRequestQueue(act).add(PrivateProfile.get_private_profile_request(new GetPrivateProfileRequest(jwt), successful_profile, error_profile));
        } catch (Exception e) {
            progressDialog.cancel();
            Authentication.logout(act);
            Intent intent = new Intent(act, SignInActivity.class);
            startActivity(intent);
        }
    }



    private void LoadProfileValuesFromResponse(GetPrivateProfileResponse response) {
        EditTextPreference username = (EditTextPreference) findPreference("editTextPref_username");
        username.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updatePrivateProfile(jwt, null, newValue.toString(), null, null, null, null, null);
                return false;
            }

        });
        EditTextPreference fname = (EditTextPreference) findPreference("editTextPref_firstname");
        fname.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updatePrivateProfile(jwt, null, null, newValue.toString(), null, null, null, null);
                return false;
            }
        });
        EditTextPreference lname = (EditTextPreference) findPreference("editTextPref_lastname");
        lname.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updatePrivateProfile(jwt, null, null, null, newValue.toString(), null, null, null);
                return false;
            }
        });
        final Preference dob_preference = findPreference("editTextPref_dob");

        dob_preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String[] birthday_details = dob_preference.getSummary().toString().split("/");
                int year = Integer.parseInt(birthday_details[2]);
                int month = Integer.parseInt(birthday_details[0]) - 1;
                int dayOfMonth = Integer.parseInt(birthday_details[1]);

                DatePickerDialog dp = new DatePickerDialog(getActivity(), date, year, month, dayOfMonth);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -18);
                dp.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                dp.show();

                return false;
            }
        });


        final ListPreference gender = (ListPreference) findPreference("editTextPref_gender");
        gender.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String gender;
                switch (o.toString()) {
                    case "Male":
                        gender = "M";
                        break;
                    case "Female":
                        gender = "F";
                        break;
                    default:
                        gender = "O";
                }
                updatePrivateProfile(jwt, null, null, null, null, gender, null, null);
                return false;
            }
        });

        final Preference email = findPreference("editTextPref_email");
        email.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.fragment_email_dialog, null);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptsView);
                final EditText emailInput = promptsView.findViewById(R.id.email_val);
                final EditText passwordInput = promptsView.findViewById(R.id.password_val);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setTitle("Change Email")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        boolean valid_email = email_validation(emailInput, passwordInput);

                                        if (valid_email) {
                                            // Keep alert dialog open for invalid entries

                                            block_alert_dialog(dialog, false);

                                        } else {
                                            block_alert_dialog(dialog, true);
                                            updatePrivateProfile(jwt, emailInput.getText().toString(), null,
                                                    null, null, null, null, passwordInput.getText().toString());
                                        }

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        //unblock alert dialog (if blocked)
                                        
                                        block_alert_dialog(dialog, true);
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

                return false;
            }

        });
        final Preference password = findPreference("editTextPref_password");
        password.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.fragment_change_password, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptsView);
                final EditText oldPasswordInput = promptsView.findViewById(R.id.old_password_val);
                final EditText newpasswordInput = promptsView.findViewById(R.id.new_password_val);
                final EditText confirmpasswordInput = promptsView.findViewById(R.id.confirm_password_val);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        boolean valid_password_change = password_validation(oldPasswordInput, newpasswordInput, confirmpasswordInput);
                                        if (valid_password_change) {
                                            // Keep alert dialog open for invalid entries

                                            block_alert_dialog(dialog, false);

                                        } else {

                                            block_alert_dialog(dialog, true);
                                            changePassword(jwt, oldPasswordInput.getText().toString(),
                                                    newpasswordInput.getText().toString());

                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        block_alert_dialog(dialog, true);
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

                return false;
            }

        });

        username.setSummary(response.username);
        username.setText(username.getSummary().toString());
        fname.setSummary(response.fname);
        fname.setText(fname.getSummary().toString());
        lname.setSummary(response.lname);
        lname.setText(lname.getSummary().toString());
        dob_preference.setSummary(response.dob);
        gender.setSummary(response.gender.equalsIgnoreCase("M") ? "Male" : response.gender.equalsIgnoreCase("F") ? "Female" : "Other");
        email.setSummary(response.email);



    }

    private void block_alert_dialog(DialogInterface dialog, boolean val) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, val);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean email_validation (EditText email, EditText password) {
        email.setError(null);
        password.setError(null);

        String email_val = email.getText().toString();
        String password_val = password.getText().toString();
        boolean cancel = false;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email_val)) {
            email.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if (!isEmailValid(email_val)) {
            email.setError(getString(R.string.error_invalid_email));
            cancel = true;
        }

        // Check for password.
        if (TextUtils.isEmpty(password_val)) {
            password.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (!TextUtils.isEmpty(password_val) && !isPasswordValid(password_val)) {
            password.setError(getString(R.string.error_invalid_password));
            cancel = true;
        }

        return cancel;

    }

    private boolean password_validation(EditText old_passsword, EditText new_password, EditText confirm_password) {
        old_passsword.setError(null);
        new_password.setError(null);
        confirm_password.setError(null);

        String oldPassword = old_passsword.getText().toString();
        String newPassword = new_password.getText().toString();
        String confirmPassword = confirm_password.getText().toString();
        boolean cancel = false;


        // Check for password.
        if (TextUtils.isEmpty(oldPassword)) {
            old_passsword.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(oldPassword) && !isPasswordValid(oldPassword)) {
            old_passsword.setError(getString(R.string.error_invalid_password));
            cancel = true;
        }

        // Check for new password.
        if (TextUtils.isEmpty(newPassword)) {
            new_password.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        // Check for a valid new password, if the user entered one.
        if (!TextUtils.isEmpty(newPassword) && !isPasswordValid(newPassword)) {
            new_password.setError(getString(R.string.error_invalid_password));
            cancel = true;
        }

        // Check for confirm password.
        if (TextUtils.isEmpty(confirmPassword)) {
            confirm_password.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        // Check for confirm password, if the user entered one.
        if (!TextUtils.isEmpty(confirmPassword) && !TextUtils.isEmpty(confirmPassword) && !isPasswordMatch(newPassword, confirmPassword)) {
            confirm_password.setError(getString(R.string.error_mismatch_password));
            cancel = true;
        }

        return cancel;
    }

    private boolean isEmailValid(String email) {
        return email.matches("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
    }

    private boolean isPasswordValid(String password) {

        return password.matches("^[a-z0-9A-Z?!~$#%*]{6,80}$");
    }

    private boolean isPasswordMatch(String password, String confirmPassword) {

        return password.equals(confirmPassword);
    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);
        String newValue = sdf.format(myCalendar.getTime());
        updatePrivateProfile(jwt, null, null, null, null, null, newValue.toString(), null);
    }


    private Response.Listener<JSONObject> successful_profile = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                LoadProfileValuesFromResponse((Utils.gson.fromJson(response.toString(), GetPrivateProfileResponse.class)));
                progressDialog.cancel();
            }
            //TODO: Implement Failure
            catch (Exception e) {

            }

        }
    };

    private Response.ErrorListener error_profile = new Response.ErrorListener() {
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
            catch (Exception e) {

            }
        }
    };

    public void updatePrivateProfile(String jwt, String email, String username, String fname, String lname, String gender, String dob, String password) {

        Utils.getInstance(getActivity()).getRequestQueue(getActivity()).add(PrivateProfile.update_profile_request(new UpdatePrivateProfileRequest(jwt, email, username, fname, lname, gender, dob, password), update_successful_profile, update_error_profile));

    }

    public void changePassword(String jwt, String oldPassword, String newPassword) {

        Utils.getInstance(getActivity()).getRequestQueue(getActivity()).add(Authentication.change_password_request(new ChangePasswordRequest(jwt, oldPassword, newPassword), changePassword_successful, changePassword_failure));

    }

    private Response.Listener<JSONObject> update_successful_profile = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                LoadProfileValuesFromResponse((Utils.gson.fromJson(response.toString(), GetPrivateProfileResponse.class)));
                progressDialog.cancel();
            }
            //TODO: Implement Failure
            catch (Exception e) {
                //UpdateProfileFailure(e.getMessage());
            }

        }
    };


    private Response.ErrorListener update_error_profile = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                UpdateProfileFailure(errorJSON.getString("error"));
            }
            //TODO: Implement Failure
            catch (Exception e) {
                UpdateProfileFailure(e.getMessage());
            }
        }
    };


    private Response.Listener<JSONObject> changePassword_successful = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                LoadProfileValuesFromResponse((Utils.gson.fromJson(response.toString(), GetPrivateProfileResponse.class)));
                progressDialog.cancel();
            }
            //TODO: Implement Failure
            catch (Exception e) {
                //ChangePasswordFailure(e.getMessage());
            }

        }
    };


    private Response.ErrorListener changePassword_failure = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                ChangePasswordFailure(errorJSON.getString("error"));
            }
            //TODO: Implement Failure
            catch (Exception e) {
                ChangePasswordFailure(e.getMessage());
            }
        }
    };

    private void ChangePasswordFailure(String message) {

        Toast.makeText(getActivity(), "Change Password failed: " + message, Toast.LENGTH_SHORT).show();

    }

    private void UpdateProfileFailure(String message) {

        Toast.makeText(getActivity(), "Update Profile failed: " + message, Toast.LENGTH_SHORT).show();

    }


}
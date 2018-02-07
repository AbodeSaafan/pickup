package sotifc2017.pickup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

// Custom Implementation
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
// Custom Implementation

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.ExtendedProfile;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.GetExtendedProfileRequest;
import sotifc2017.pickup.api.contracts.GetExtendedProfileResponse;
import sotifc2017.pickup.api.contracts.RegisterRequest;
import sotifc2017.pickup.api.contracts.RegisterResponse;

//import static sotifc2017.pickup.R.id.place_autocomplete_fragment;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final String FC_TAG = "sotifc2017";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
//    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private EditText mUsernameView;
    private EditText mFirstnameView;
    private EditText mLastnameView;
    private DiscreteSeekBar skillLevelBar;
    private EditText cPasswordView;
    private TextView skillLevel;
    private String dob;
    private ProgressDialog progressDialog;
//    private PlacesAutocompleteTextView placesAutocomplete;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;
    private String gender;
    public static String[] skillLevels = {"Just for Fun!", "Rookie", "All Star", "Super Star", "Hall of Fame", "God of Basketball"};

    Button next0;
    Button next1;
    Button back1;
    Button back2;
    ViewFlipper VF;
    Spinner genderSpinner;
    EditText DobLabel;
    String address;
    Geocoder coder;
    double lat;
    double lng;
    int skilllevel_Value;

    // Custom Implementation
    protected GeoDataClient mGeoDataClient;
    // Custom Implementation

    private OnClickListener page_switch_listener = new OnClickListener() {
        public void onClick(View v) {
            boolean cancel = false;
            View focusView = null;
            switch (v.getId()) {
                case R.id.next0:
                    mUsernameView.setError(null);
                    mFirstnameView.setError(null);
                    firstname = mFirstnameView.getText().toString();
                    lastname = mLastnameView.getText().toString();
                    // Check for firstname.
                    if (TextUtils.isEmpty(firstname)) {
                        mFirstnameView.setError(getString(R.string.error_field_required));
                        focusView = mFirstnameView;
                        cancel = true;
                    }
                    if (!isNameValid(firstname)) {
                        mFirstnameView.setError(getString(R.string.error_invalid_name));
                        focusView = mFirstnameView;
                        cancel = true;
                    }
                    // Check for lastname.
                    if (TextUtils.isEmpty(lastname)) {
                        mLastnameView.setError(getString(R.string.error_field_required));
                        focusView = mLastnameView;
                        cancel = true;
                    }
                    if (!isNameValid(lastname)) {
                        mLastnameView.setError(getString(R.string.error_invalid_name));
                        focusView = mLastnameView;
                        cancel = true;
                    }
                    if (cancel) {
                        // There was an error; don't attempt login and focus the first
                        // form field with an error.
                        focusView.requestFocus();
                        return;
                    }
                    VF.setDisplayedChild(1);
                    break;
                case R.id.next1:
                    DobLabel.setError(null);
                    if (dob == null) {
                        DobLabel.setError(getString(R.string.error_field_required));
                        DobLabel.requestFocus();
                        return;
                    }
                    VF.setDisplayedChild(2);
                    break;
                case R.id.back1:
                    VF.setDisplayedChild(0);
                    break;
                case R.id.back2:
                    VF.setDisplayedChild(1);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        coder = new Geocoder(this);

        next0 = findViewById(R.id.next0);
        next1 = findViewById(R.id.next1);
        back1 = findViewById(R.id.back1);
        back2 = findViewById(R.id.back2);
        VF = findViewById(R.id.RegisterViewFlipper);
        VF.setDisplayedChild(0);
        next0.setOnClickListener(page_switch_listener);
        next1.setOnClickListener(page_switch_listener);
        back1.setOnClickListener(page_switch_listener);
        back2.setOnClickListener(page_switch_listener);

        DobLabel = findViewById(R.id.Dob);
        genderSpinner = findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setOnItemSelectedListener(this);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        cPasswordView = (EditText) findViewById(R.id.confirmPassword);
        cPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mUsernameView = (EditText) findViewById(R.id.username);
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mFirstnameView = findViewById(R.id.fname);
        mLastnameView = findViewById(R.id.lname);
        skillLevel = findViewById(R.id.skill_level);
        skillLevelBar = findViewById(R.id.skill_level_bar);
        skillLevelBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                skillLevel.setText(skillLevels[value / 2]);
                skilllevel_Value = value / 2;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
        mLoginFormView = findViewById(R.id.RegisterViewFlipper);
        mProgressView = findViewById(R.id.login_progress);

        progressDialog = new ProgressDialog(SignUpActivity.this,
                R.style.AppTheme_Dark);

        // Custom Implementation
        // Construct a GeoDataClient
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        // mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        final AutocompleteFilter mPlaceFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();

        // List view for locations
        final ListView locationsList = findViewById(R.id.list_view_locations);

        // Set listener on edit view
        final EditText location = findViewById(R.id.location_autocomplete);

        location.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Log.d(FC_TAG, String.valueOf(location.getText()));
                if (location.getText().length() > 2) {
                    // Query user typed information using a Google API
                    LatLngBounds mBounds = null;
                    String query = String.valueOf(location.getText());
                    Task<AutocompletePredictionBufferResponse> results =
                            mGeoDataClient.getAutocompletePredictions(query, mBounds, mPlaceFilter);

                    results.addOnSuccessListener(new OnSuccessListener<AutocompletePredictionBufferResponse>() {
                        @Override
                        public void onSuccess(AutocompletePredictionBufferResponse autocompletePredictions) {
                            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
//                            ArrayList<String> locations = new ArrayList<String>(autocompletePredictions.getCount());
//                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.activity_list_item, locations);
//                            locationsList.setAdapter(adapter);
                            if (iterator.hasNext()) {
                                while (iterator.hasNext()) {
                                    AutocompletePrediction prediction = iterator.next();
                                    StyleSpan normal = new StyleSpan(Typeface.NORMAL);
                                    CharSequence fullText = prediction.getFullText(normal);
                                    CharSequence primaryText = prediction.getPrimaryText(normal);
                                    CharSequence secondaryText = prediction.getSecondaryText(normal);

                                    // Add to a list view somehow
//                                    locations.add((String) fullText);
//                                    adapter.notifyDataSetChanged();

                                    Log.d(FC_TAG, "Query was successful" + fullText + primaryText + secondaryText);
                                }
                            } else {
                                Log.d(FC_TAG, "No data found for that location");
                            }
                            autocompletePredictions.release();
                        }
                    });

                    results.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(FC_TAG, "Could not get suggestions" + e);
                        }

                    });
                }
            }
        });


        // Custom Implementation

        // Location using Google widget
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();

        autocompleteFragment.setFilter(typeFilter);

        EditText locationView = (EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input);
        locationView.setHintTextColor(-1);
        locationView.setHint("Select Location");
        locationView.setTextColor(-1);



        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latlng = place.getLatLng();
                lat = latlng.latitude;
                lng = latlng.longitude;
                Log.i(FC_TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(FC_TAG, "An error occurred: " + status);
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        switch (genderSpinner.getSelectedItem().toString()) {
            case "Male":
                gender = "M";
                break;
            case "Female":
                gender = "F";
                break;
            default:
                gender = "O";
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        cPasswordView.setError(null);
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();
        String confirmPassword = cPasswordView.getText().toString();
        username = mUsernameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for confirm password.
        if (TextUtils.isEmpty(confirmPassword)) {
            cPasswordView.setError(getString(R.string.error_field_required));
            focusView = cPasswordView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for confirm password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword) && !isPasswordMatch(password, confirmPassword)) {
            cPasswordView.setError(getString(R.string.error_mismatch_password));
            focusView = cPasswordView;
            cancel = true;
        }

        // Check for username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");

            Window window = progressDialog.getWindow();
            window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            progressDialog.setCancelable(false);
            progressDialog.show();

            ExecuteLogin(email, firstname, lastname, password, gender, username, dob);

        }


    }

    private boolean isNameValid(String name) {
        return name.matches("^[a-zA-Z'-]*$");
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



    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    Calendar myCalendar = Calendar.getInstance();


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(SignUpActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
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
        dob = sdf.format(myCalendar.getTime());
        DobLabel.setText(sdf.format(myCalendar.getTime()));
    }

    public void dobLabelClick(View view) {
        new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private Response.Listener<JSONObject> successful_register = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try{
                registerSuccess(Utils.gson.fromJson(response.toString(), RegisterResponse.class));
            }
            //TODO: Implement Failure
            catch (Exception e){
                registerFailure(e.getMessage());
            }

        }
    };

    private Response.ErrorListener error_register =  new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                registerFailure(errorJSON.getString("error"));
            }
            //TODO: Implement Failure
            catch (Exception e){
                registerFailure(e.getMessage());
            }
        }
    };

    private void ExecuteLogin(String email, String firstname, String lastname, String password, String gender, String username, String dob) {

        Utils.getInstance(this).getRequestQueue(this).add(Authentication.register_request(new RegisterRequest(email, password, username, firstname, lastname, gender, dob), successful_register, error_register));
    }



    private void registerSuccess(RegisterResponse response) {
        Toast.makeText(this, "Register successsful", Toast.LENGTH_SHORT).show();

        Authentication.saveJwt(this, response.token);
        Authentication.saveRefresh(this, response.refresh);
        Authentication.saveUserId(this, response.user_id);

        UpdateExtendedProfile(response.token);

    }

    private void registerFailure(String message) {
        Toast.makeText(this, "Sign in failed: " + message, Toast.LENGTH_SHORT).show();

        progressDialog.cancel();
    }

    private Response.Listener<JSONObject> successful_Update = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try{
                updateProfileSuccess(Utils.gson.fromJson(response.toString(), GetExtendedProfileResponse.class));
            }
            //TODO: Implement Failure
            catch (Exception e){
                updateProfileFailure(e.getMessage());
            }

        }
    };

    private Response.ErrorListener error_Update=  new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                updateProfileFailure(errorJSON.getString("error"));
            }
            //TODO: Implement Failure
            catch (Exception e) {
                updateProfileFailure(e.getMessage());
            }
        }
    };

    private void UpdateExtendedProfile (String jwt) {
        Utils.getInstance(this).getRequestQueue(this).add(ExtendedProfile.updateProfile_request(new GetExtendedProfileRequest(jwt, lat, lng, skilllevel_Value), successful_Update, error_Update));
    }



    private void updateProfileFailure(String message) {
        Toast.makeText(this, "Extended Profile Update failed: " + message, Toast.LENGTH_SHORT).show();
        progressDialog.cancel();
    }

    private void updateProfileSuccess(GetExtendedProfileResponse getExtendedProfileResponse) {
        Toast.makeText(this, "Extended Profile Updated successsfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        progressDialog.cancel();
        finish();

    }



}




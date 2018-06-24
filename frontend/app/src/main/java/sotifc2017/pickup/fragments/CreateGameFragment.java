package sotifc2017.pickup.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appyvet.materialrangebar.RangeBar;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import sotifc2017.pickup.CommonComponents;
import sotifc2017.pickup.R;
import sotifc2017.pickup.activities.HostingActivity;
import sotifc2017.pickup.activities.SignInActivity;
import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.Games;
import sotifc2017.pickup.api.GetJwt;
import sotifc2017.pickup.api.Search;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.CreateGameRequest;
import sotifc2017.pickup.api.contracts.CreateGameResponse;
import sotifc2017.pickup.api.contracts.GetSearchRequest;
import sotifc2017.pickup.api.enums.ENFORCED_PARAMS;
import sotifc2017.pickup.api.enums.GAME_TYPE;
import sotifc2017.pickup.api.models.GameModel;
import sotifc2017.pickup.fragment_interfaces.OnFragmentReplacement;

import static sotifc2017.pickup.Common.Defaults.FC_TAG;

public class CreateGameFragment extends Fragment implements GetJwt.Callback {

    int currentFragmentId = R.id.action_create_game;
    OnFragmentReplacement mCallback;
    private String jwtToken;

    private GameModel gameModel;

    private ImageButton detailsHeaderToggle;
    private RelativeLayout gameDetailsSection;
    private ImageButton restrictionsHeaderToggle;
    private RelativeLayout gameRestrictionsSection;

    private EditText gameName;
    private EditText gameDescription;

    private RadioButton seriousGameRadio;

    private TextView skillOffsetRangeText;
    private RangeBar skillOffsetRange;

    private RangeBar totalPlayerSeekBar;
    private TextView totalPlayerText;

    private Calendar gameCalendar;

    private final String dateFormat = "MM/dd/yyyy";
    private final String timeFormat = "HH:mm:ss";
    Date startDefaultDate = new Date(System.currentTimeMillis());
    Date endDefaultDate = new Date(System.currentTimeMillis() + 360000);
    private EditText dateRangeText;
    private DatePickerDialog.OnDateSetListener gameDateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            gameCalendar.set(Calendar.YEAR, year);
            gameCalendar.set(Calendar.MONTH, monthOfYear);
            gameCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateDateRangeLabel(gameCalendar.getTime());
        }

    };

    private EditText timeSelector;

    private EditText locationSelector;
    private EditText gameLocationNotes;

    private CheckBox checkboxRestrictAge;

    private CheckBox checkboxRestrictGender;

    public CreateGameFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFragmentReplacement) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "[CreateGameFragment] must implement OnFragmentReplacement)");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetJwt(this).execute(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_new_game, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        gameModel = new GameModel();

        detailsHeaderToggle = view.findViewById(R.id.details_header_toggle);
        gameDetailsSection = view.findViewById(R.id.game_details_section);
        detailsHeaderToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameDetailsSection.isShown()) {
                    detailsHeaderToggle.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_expand_more_black_24dp));
                    gameDetailsSection.animate().translationY(-1 * gameDetailsSection.getHeight()).setDuration(300).alpha(0.0f).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            gameDetailsSection.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    detailsHeaderToggle.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_expand_less_black_24dp));
                    gameDetailsSection.animate().translationY(0).setDuration(300).alpha(1.0f).withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            gameDetailsSection.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

        restrictionsHeaderToggle = view.findViewById(R.id.restrictions_header_toggle);
        gameRestrictionsSection = view.findViewById(R.id.game_restrictions_section);
        restrictionsHeaderToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameRestrictionsSection.isShown()) {
                    restrictionsHeaderToggle.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_expand_more_black_24dp));
                    gameRestrictionsSection.animate().translationY(-1 * gameRestrictionsSection.getHeight()).setDuration(300).alpha(0.0f).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            gameRestrictionsSection.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    restrictionsHeaderToggle.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_expand_less_black_24dp));
                    gameRestrictionsSection.animate().translationY(0).setDuration(300).alpha(1.0f).withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            gameRestrictionsSection.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

        gameName = view.findViewById(R.id.complete_text_view_game_name);

        seriousGameRadio = view.findViewById(R.id.radio_serious_game);
        seriousGameRadio.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean seriousGameChecked) {
                skillOffsetRange.setEnabled(seriousGameChecked);
            }
        });

        gameDescription = view.findViewById(R.id.multiTextViewDescription);
        gameDescription.setMovementMethod(new ScrollingMovementMethod());
        gameDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean enteredFocus) {
                if (enteredFocus &&
                        getString(R.string.create_new_game_description_hint).equals(gameDescription.getText().toString())) {
                    gameDescription.setText("");
                }
            }
        });

        skillOffsetRange = view.findViewById(R.id.range_bar_skill_offset);
        skillOffsetRangeText = view.findViewById(R.id.text_skill_offset_level);
        skillOffsetRangeText.setText(String.format(getString(R.string.create_new_game_skill_offset_message), 9));
        skillOffsetRange.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex, String leftPinValue, String rightPinValue) {
                skillOffsetRangeText.setText(String.format(getString(R.string.create_new_game_skill_offset_message), Integer.parseInt(rightPinValue)));
            }

        });

        totalPlayerSeekBar = view.findViewById(R.id.range_bar_total_players);
        totalPlayerText = view.findViewById(R.id.text_total_players);
        totalPlayerText.setText(String.format(getString(R.string.create_new_game_total_players_message), 20));
        totalPlayerSeekBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex, String leftPinValue, String rightPinValue) {
                totalPlayerText.setText(String.format(getString(R.string.create_new_game_total_players_message), Integer.parseInt(rightPinValue)));
            }
        });

        dateRangeText = view.findViewById(R.id.create_game_date);
        dateRangeText.setInputType(InputType.TYPE_NULL);
        gameCalendar = Calendar.getInstance();
        View.OnClickListener onDateClick = new View.OnClickListener() {
            //  Could/should  possibly change this to one date picker, that triggers another, that way
            // we have one field for the range of dates
            @Override
            public void onClick(View v) {
                long minDate  = System.currentTimeMillis();

                DatePickerDialog dateFromDatePicker = new DatePickerDialog(getActivity(), gameDateListener, gameCalendar
                        .get(Calendar.YEAR), gameCalendar.get(Calendar.MONTH),
                        gameCalendar.get(Calendar.DAY_OF_MONTH));

                dateFromDatePicker.getDatePicker().setMinDate(minDate);

                dateFromDatePicker.setMessage(getString(R.string.game_search_date_range_start_message));
                dateFromDatePicker.show();
            }
        };

        dateRangeText.setOnClickListener(onDateClick);

        updateDateRangeLabel(startDefaultDate);

        timeSelector = view.findViewById(R.id.edit_text_time_selector);
        timeSelector.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showCustomDialogTimePicker();
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.US);
        timeSelector.setText(String.format(getString(R.string.create_new_game_time_hint),
                sdf.format(startDefaultDate),
                sdf.format(endDefaultDate)));

        locationSelector = view.findViewById(R.id.edit_text_location_selector);
        locationSelector.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showPlacePicker();
            }
        });

        checkboxRestrictAge =  view.findViewById(R.id.checkbox_restrict_age);
        checkboxRestrictGender = view.findViewById(R.id.checkbox_restrict_gender);

        Button createGameSubmitButton = view.findViewById(R.id.button_create_game_submit);
        createGameSubmitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onCreateGameButtonClick();
            }
        });

        gameLocationNotes = view.findViewById(R.id.complete_text_location_notes);

        super.onViewCreated(view, savedInstanceState);
    }

    private void updateDateRangeLabel(Date startDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        dateRangeText.setText(sdf.format(startDate));
    }

    @Override
    public void onResume() {
        mCallback.configureMenuItemSelection(currentFragmentId, true);

        super.onResume();
    }

    //TODO don't do this, avoid it by calling get jwt when a user hits create game
    @Override
    public void jwtSuccess(String jwt) {
        jwtToken = jwt;
    }

    @Override
    public void jwtFailure(GetJwt.JwtOutcome outcome) {
        switch(outcome){
            case NoRefresh:
            case BadJwtRetrieval:
                Authentication.logout(getActivity());
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            case ServerFault:
            default:
                GetJwt.exitAppDialog(getActivity()).show();
        }
    }

    public void showCustomDialogTimePicker()
    {
        // Create an instance of the dialog fragment and show it
        RangeTimePickerDialog dialog = new RangeTimePickerDialog();
        dialog.newInstance();
        dialog.setIs24HourView(false);
        dialog.setRadiusDialog(20);
        dialog.setTextTabStart("Start");
        dialog.setTextTabEnd("End");
        dialog.setTextBtnPositive("Accept");
        dialog.setTextBtnNegative("Close");
        dialog.setValidateRange(true);
        dialog.setMessageErrorRangeTime("The start time cannot come after the end time");
        dialog.setColorBackgroundHeader(R.color.paleturquoise);
        dialog.setColorBackgroundTimePickerHeader(R.color.paleturquoise);
        dialog.setColorTextButton(R.color.colorPrimaryDark);
        dialog.setColorTextButton(R.color.paleturquoise);
        dialog.setColorTabSelected(R.color.light_orange);
        FragmentManager fragmentManager = getFragmentManager();
        dialog.show(fragmentManager, "");
    }

    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd)
    {
        String partialStartTime = hourStart + ":" + minuteStart + ":00";
        String partialEndTime = hourEnd + ":" + minuteEnd + ":00";
        Log.d(FC_TAG, "Start: " + partialStartTime + "\nEnd: " + partialEndTime);

        timeSelector.setText(String.format(getString(R.string.create_new_game_time_hint),
                partialStartTime,
                partialEndTime));

        gameModel.setPartialStartTime(partialStartTime);
        gameModel.setPartialEndTime(partialEndTime);
    }

    private void showPlacePicker() {
        mCallback.startPlacePickerActivity();
    }

    public void onSelectedLocation(LatLngBounds locationChosen, Intent intent)
    {
        locationSelector.setText(PlacePicker.getPlace(this.getActivity(), intent).getAddress());

        final LatLng latLong = locationChosen.getCenter();
        gameModel.setLocation(new HashMap<String, Double>(){{
            this.put("lng", latLong.longitude);
            this.put("lat", latLong.latitude);
        }});
    }

    public void onCreateGameButtonClick() {
        Activity activity = getActivity();
        //TODO no need to use a global variable gameModel, could do it in place
        gatherUserInput();

        CreateGameRequest req = new CreateGameRequest(
                gameModel.getName(),
                gameModel.getType(),
                gameModel.getOffsetSkill(),
                gameModel.getTotalPlayersRequired(),
                gameModel.getStartTime(),
                gameModel.getDuration(),
                gameModel.getLocation(),
                gameModel.getLocationNotes(),
                gameModel.getDescription(),
                gameModel.getEnforcedParams(),
                jwtToken);
        CommonComponents.getLoadingProgressDialog((activity)).show();
        JsonObjectRequest request = Games.createGameRequest(req, successful_create_game_profile, error_create_game_profile);

        if (request != null) {
            Utils.getInstance(activity).getRequestQueue(activity).add(request);
        }
    }

    private void gatherUserInput() {
        gameModel.setName(gameName.getText().toString());

        GAME_TYPE game_type = seriousGameRadio.isChecked() ? GAME_TYPE.serious : GAME_TYPE.casual;
        gameModel.setType(game_type);

        gameModel.setDescription(gameDescription.getText().toString());

        int  offsetSkill = seriousGameRadio.isChecked() && skillOffsetRange.isEnabled() ? Integer.parseInt(skillOffsetRange.getRightPinValue()) : -1;
        gameModel.setOffsetSkill(offsetSkill);

        int totalPlayersRequired = Integer.parseInt(totalPlayerSeekBar.getRightPinValue());
        gameModel.setTotalPlayersRequired(totalPlayersRequired);

        String[] dates = dateRangeText.getText().toString().split(" - ");
        String startDate = dates[0];
        String partialStartTime = gameModel.getPartialStartTime();
        String partialEndTime = gameModel.getPartialEndTime();
        try {
            long startTime = createFinalTime(startDate, partialStartTime);
            gameModel.setStartTime(startTime);
            long endTime = createFinalTime(startDate, partialEndTime);
            gameModel.setEndTime(endTime);

            gameModel.setDuration(endTime - startTime);
        } catch (ParseException e) {
            Log.e(FC_TAG, "Parsing time: " + e);
        }

        gameModel.setLocationNotes(gameLocationNotes.getText().toString());
        ArrayList<ENFORCED_PARAMS> list = new ArrayList<>();
        if(checkboxRestrictGender.isChecked()) list.add(ENFORCED_PARAMS.gender );
        if(checkboxRestrictAge.isChecked()) list.add(ENFORCED_PARAMS.age );


        gameModel.setEnforcedParams(list.size() == 0 ? new ENFORCED_PARAMS[]{} : (ENFORCED_PARAMS[]) list.toArray());
    }

    private long createFinalTime(String startDate, String startTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        Date date = sdf.parse(startDate + " " + startTime);

        return date.getTime() / 1000;
    }

    private Response.Listener<JSONObject> successful_create_game_profile = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try{
                CreateGameSuccess(Utils.gson.fromJson(response.toString(), CreateGameResponse.class));
            }
            catch (Exception e){
                CreateGameFailure(e.getMessage());
            }

        }
    };

    private Response.ErrorListener error_create_game_profile =  new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            CommonComponents.getLoadingProgressDialog((getActivity())).hide();
            try {
                String message = new JSONObject(new String(error.networkResponse.data, "UTF-8")).toString();
                CreateGameFailure(message);
            }
            catch (Exception e){
                CreateGameFailure(e.getMessage());
            }
        }
    };

    private String getErrorMessage(VolleyError error) throws UnsupportedEncodingException, JSONException {
        JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));

        String message;
        if (errorJSON.has("jwtFailure")) {
            message = errorJSON.getString("jwtFailure");
        } else {
            message = "Status: " + error.networkResponse.statusCode;
        }
        Log.e(FC_TAG, message + " Error: " + errorJSON.getString("error"));

        return message;
    }

    private void CreateGameSuccess(CreateGameResponse response) {
        Toast.makeText(getActivity(), "CreateGameResponse successful. GameId: " + response.game_id, Toast.LENGTH_SHORT).show();

        JsonObjectRequest request = Search.getSearch_request(GetSearchRequest.CreateGameRequest(jwtToken, response.game_id), successful_get_game, error_get_game);

        Utils.getInstance(getActivity()).getRequestQueue(getActivity()).add(request);
    }

    private void CreateGameFailure(String message) {
        Toast.makeText(getActivity(), "CreateGameResponse failed: " + message, Toast.LENGTH_SHORT).show();
    }

    private Response.Listener<JSONObject> successful_get_game = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                Bundle bundle = new Bundle();
                GameModel[] games = Utils.gson.fromJson(response.get("games").toString(), GameModel[].class);
                String gameJson = Utils.gson.toJson(games[0]);
                bundle.putString("gameJson", gameJson);

                GameViewFragment gameViewFragment = new GameViewFragment();
                gameViewFragment.setArguments(bundle);
                CommonComponents.getLoadingProgressDialog((getActivity())).hide();
                ((HostingActivity) getActivity()).replaceFragment(gameViewFragment, true, -1);
            }
            catch (Exception e){
                CreateGameFailure(e.getMessage());
            }

        }
    };

    private Response.ErrorListener error_get_game =  new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            CommonComponents.getLoadingProgressDialog((getActivity())).hide();
            try {
                String message = getErrorMessage(error);
                CreateGameFailure(message);
            }
            catch (Exception e){
                CreateGameFailure(e.getMessage());
            }
        }
    };
}

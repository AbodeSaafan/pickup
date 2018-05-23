package sotifc2017.pickup.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appyvet.materialrangebar.RangeBar;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import sotifc2017.pickup.R;
import sotifc2017.pickup.activities.SignInActivity;
import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.Games;
import sotifc2017.pickup.api.GetJwt;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.CreateGameRequest;
import sotifc2017.pickup.api.contracts.CreateGameResponse;
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

    private EditText gameName;
    private EditText gameDescription;

    private CheckBox casualGameCheck;
    private CheckBox seriousGameCheck;

    private TextView skillRangeText;
    private RangeBar skillRange;

    private RangeBar minPlayerSeekBar;
    private TextView minPlayerText;

    private Calendar fromCalendar;
    private Calendar toCalendar;
    private final String dateFormat = "MM/dd/yy";
    private EditText dateRangeText;
    private DatePickerDialog.OnDateSetListener dateListenerFrom = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            fromCalendar.set(Calendar.YEAR, year);
            fromCalendar.set(Calendar.MONTH, monthOfYear);
            fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            long minDate = System.currentTimeMillis() > fromCalendar.getTimeInMillis()? System.currentTimeMillis() : fromCalendar.getTimeInMillis();

            DatePickerDialog dateToDatePicker = new DatePickerDialog(getActivity(), dateListenerTo, toCalendar
                    .get(Calendar.YEAR), toCalendar.get(Calendar.MONTH),
                    toCalendar.get(Calendar.DAY_OF_MONTH));

            dateToDatePicker .getDatePicker().setMinDate(minDate);
            dateToDatePicker.setMessage(getString(R.string.game_search_date_range_end_message));

            dateToDatePicker.show();
        }

    };
    private DatePickerDialog.OnDateSetListener dateListenerTo = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            toCalendar.set(Calendar.YEAR, year);
            toCalendar.set(Calendar.MONTH, monthOfYear);
            toCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateDateRangeLabel(fromCalendar.getTime(), toCalendar.getTime());
        }

    };

    private EditText gameLocationNotes;

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
        EditText timeSelector = view.findViewById(R.id.edit_text_time_selector);
        timeSelector.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showCustomDialogTimePicker();
            }
        });

        EditText locationSelector = view.findViewById(R.id.edit_text_location_selector);
        locationSelector.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showPlacePicker();
            }
        });

        Button createGameSubmitButton = view.findViewById(R.id.button_create_game_submit);
        createGameSubmitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onCreateGameButtonClick();
            }
        });

        gameModel = new GameModel();
        gameName = view.findViewById(R.id.complete_text_view_game_name);

        casualGameCheck = view.findViewById(R.id.checkbox_casual_game_type);
        casualGameCheck.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean casualGameChecked) {
                updateGameTypeCheckboxes();
            }
        });
        seriousGameCheck = view.findViewById(R.id.checkbox_serious_game_type);
        seriousGameCheck.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean seriousGameChecked) {
                updateGameTypeCheckboxes();
            }
        });

        gameDescription = view.findViewById(R.id.multiTextViewDescription);

        skillRange = view.findViewById(R.id.range_bar_skill);
        skillRangeText = view.findViewById(R.id.text_skill_level);
        skillRangeText.setText(String.format(getString(R.string.game_search_skill_range), 1, 10));
        skillRange.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex, String leftPinValue, String rightPinValue) {
                skillRangeText.setText(String.format(getString(R.string.game_search_skill_range), Integer.parseInt(leftPinValue), Integer.parseInt(rightPinValue)));
            }

        });

        minPlayerSeekBar = view.findViewById(R.id.range_bar_min_players);
        minPlayerText = view.findViewById(R.id.text_minimum_players);
        minPlayerText.setText(String.format(getString(R.string.game_search_minimum_player_message), 20));
        minPlayerSeekBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex, String leftPinValue, String rightPinValue) {
                minPlayerText.setText(String.format(getString(R.string.game_search_minimum_player_message), Integer.parseInt(rightPinValue)));
            }
        });

        dateRangeText = view.findViewById(R.id.create_game_date_range_from);
        dateRangeText.setInputType(InputType.TYPE_NULL);
        fromCalendar = Calendar.getInstance();
        toCalendar = Calendar.getInstance();
        View.OnClickListener onDateClick = new View.OnClickListener() {
            //  Could/should  possibly change this to one date picker, that triggers another, that way
            // we have one field for the range of dates
            @Override
            public void onClick(View v) {
                long minDate  = System.currentTimeMillis();

                DatePickerDialog dateFromDatePicker = new DatePickerDialog(getActivity(), dateListenerFrom, fromCalendar
                        .get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH),
                        fromCalendar.get(Calendar.DAY_OF_MONTH));

                dateFromDatePicker.getDatePicker().setMinDate(minDate);

                dateFromDatePicker.setMessage(getString(R.string.game_search_date_range_start_message));
                dateFromDatePicker.show();
            }
        };

        dateRangeText.setOnClickListener(onDateClick);
        // Set default date for search (1 week = 1000ms*60s*60min*24hr*7days = 604800000)
        updateDateRangeLabel(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 604800000));

        gameLocationNotes = view.findViewById(R.id.complete_text_location_notes);

        super.onViewCreated(view, savedInstanceState);
    }

    private void updateDateRangeLabel(Date startDate, Date endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        dateRangeText.setText(String.format(getString(R.string.game_search_date_range_display_text_label),sdf.format(startDate),sdf.format(endDate)));
    }

    @Override
    public void onResume() {
        mCallback.configureMenuItemSelection(currentFragmentId, true);

        super.onResume();
    }

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

    private void updateGameTypeCheckboxes(){
        if(!casualGameCheck.isChecked() && !seriousGameCheck.isChecked()){
            casualGameCheck.setChecked(true);
            seriousGameCheck.setChecked(true);
        }
        skillRange.setEnabled(seriousGameCheck.isChecked());
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
        dialog.setValidateRange(false);
        dialog.setColorBackgroundHeader(R.color.colorPrimary);
        dialog.setColorBackgroundTimePickerHeader(R.color.colorPrimary);
        dialog.setColorTextButton(R.color.colorPrimaryDark);
        FragmentManager fragmentManager = getFragmentManager();
        dialog.show(fragmentManager, "");
    }

    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd)
    {
        Log.d(FC_TAG, "Start: " + hourStart + ":" + minuteStart + "\nEnd: " + hourEnd + ":" + minuteEnd);

        gameModel.setStartTime(hourStart + ":" + minuteStart);
        gameModel.setEndTime(hourEnd + ":" + minuteEnd);
    }

    private void showPlacePicker() {
        mCallback.startPlacePickerActivity();
    }

    public void onSelectedLocation(LatLngBounds locationChosen)
    {
        final LatLng latLong = locationChosen.getCenter();
        gameModel.setLocation(new HashMap<String, Double>(){{
            this.put("lat", latLong.latitude);
            this.put("lng", latLong.longitude);
        }});
    }

    public void onAgeRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {
            ENFORCED_PARAMS[] newEnforcedParams = getEnforcedParamFor(ENFORCED_PARAMS.age);
            gameModel.setEnforcedParams(newEnforcedParams);
            switch(view.getId()) {
                case R.id.radio_18_range:
                    gameModel.setAgeRange(new int[] {18, 25});
                    break;
                case R.id.radio_25_range:
                    gameModel.setAgeRange(new int[] {25, 35});
                    break;
                case R.id.radio_35_range:
                    gameModel.setAgeRange(new int[] {35, 45});
                    break;
                case R.id.radio_45_range:
                    gameModel.setAgeRange(new int[] {45, 123});
                    break;
            }
        }
    }

    public void onGenderRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {
            ENFORCED_PARAMS[] newEnforcedParams = getEnforcedParamFor(ENFORCED_PARAMS.gender);
            gameModel.setEnforcedParams(newEnforcedParams);
            switch(view.getId()) {
                case R.id.radio_male_gender:
                    gameModel.setGender("m");
                    break;
                case R.id.radio_female_gender:
                    gameModel.setGender("f");
                    break;
                case R.id.radio_other_gender:
                    gameModel.setGender("a");
                    break;
            }
        }
    }

    private ENFORCED_PARAMS[] getEnforcedParamFor(ENFORCED_PARAMS paramToEnforce) {
        boolean alreadyEnforced = false;
        ENFORCED_PARAMS[] prevEnforcedParams = gameModel.getEnforcedParams();
        for (ENFORCED_PARAMS param : prevEnforcedParams) {
            if (param.equals(paramToEnforce)) {
                alreadyEnforced = true;
            }
        }

        if (alreadyEnforced) {
            return prevEnforcedParams;
        } else {
            return combine(prevEnforcedParams, new ENFORCED_PARAMS[] {paramToEnforce});
        }
    }

    // https://javarevisited.blogspot.ca/2013/02/combine-integer-and-string-array-java-example-tutorial.html
    public static ENFORCED_PARAMS[] combine(ENFORCED_PARAMS[] a, ENFORCED_PARAMS[] b){
        int length = a.length + b.length;
        ENFORCED_PARAMS[] result = new ENFORCED_PARAMS[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public void onCreateGameButtonClick() {
        Activity activity = getActivity();
        gameModel.setCreatorId(Authentication.getUserId(activity));
        gatherUserInput();

        CreateGameRequest req = new CreateGameRequest(
                gameModel.getName(),
                gameModel.getType(),
                gameModel.getMaxSkill() - gameModel.getMinSkill(),
                gameModel.totalPlayersRequired,
                gameModel.getFinalStartTime(),
                gameModel.getFinalEndTime() - gameModel.getFinalStartTime(),
                gameModel.getLocation(),
                gameModel.getLocationNotes(),
                gameModel.getDescription(),
                gameModel.getGender(),
                gameModel.getAgeRange(),
                gameModel.getEnforcedParams(),
                jwtToken);

        JsonObjectRequest request = Games.createGameRequest(req, successful_create_game_profile, error_create_game_profile);

        if (request != null) {
            Utils.getInstance(activity).getRequestQueue(activity).add(request);
        }
    }

    private void gatherUserInput() {
        gameModel.setName(gameName.getText().toString());

        // If both options are selected treat the game as serious for now.
        GAME_TYPE game_type = seriousGameCheck.isChecked() ? GAME_TYPE.serious : GAME_TYPE.casual;
        gameModel.setType(game_type.name());

        gameModel.setDescription(gameDescription.getText().toString());

        int  minSkill = seriousGameCheck.isChecked() && skillRange.isEnabled() ? Integer.parseInt(skillRange.getLeftPinValue()) : -1;
        gameModel.setMinSkill(minSkill);
        int  maxSkill = seriousGameCheck.isChecked() && skillRange.isEnabled() ? Integer.parseInt(skillRange.getRightPinValue()) : -1;
        gameModel.setMaxSkill(maxSkill);

        int totalPlayersRequired = Integer.parseInt(minPlayerSeekBar.getLeftPinValue());
        gameModel.setTotalPlayersRequired(totalPlayersRequired);

        String[] dates = dateRangeText.getText().toString().split(" - ");
        String startDate = dates[0];
        String startTime = gameModel.getStartTime();
        String endDate = dates[1];
        String endTime = gameModel.getEndTime();
        try {
            int finalStartTime = (int) createFinalTime(startDate, startTime);
            gameModel.setFinalStartTime(finalStartTime);
            int finalEndTime = (int) createFinalTime(endDate, endTime);
            gameModel.setFinalEndTime(finalEndTime);
        } catch (ParseException e) {
            Log.e(FC_TAG, "Parsing time: " + e);
        }

        gameModel.setLocationNotes(gameLocationNotes.getText().toString());

        gameModel.setTimeCreated((int) System.currentTimeMillis());
    }

    private long createFinalTime(String startDate, String startTime) throws ParseException {
        String finalDate = startDate + " " + startTime;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date date = sdf.parse(finalDate);

        return date.getTime();
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
            try {
                String message = getErrorMessage(error);
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

    private void CreateGameSuccess(CreateGameResponse response) throws IOException {
        Toast.makeText(getActivity(), "CreateGameResponse successsful. GameId: " + response.game_id, Toast.LENGTH_SHORT).show();

    }

    private void CreateGameFailure(String message) {
        Toast.makeText(getActivity(), "CreateGameResponse failed: " + message, Toast.LENGTH_SHORT).show();
    }
}

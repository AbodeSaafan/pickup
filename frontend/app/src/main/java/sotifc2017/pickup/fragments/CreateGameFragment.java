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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sotifc2017.pickup.R;
import sotifc2017.pickup.activities.SignInActivity;
import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.Games;
import sotifc2017.pickup.api.GetJwt;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.CreateGameRequest;
import sotifc2017.pickup.api.contracts.CreateGameResponse;
import sotifc2017.pickup.api.models.GameModel;
import sotifc2017.pickup.fragment_interfaces.OnFragmentReplacement;

import static sotifc2017.pickup.Common.Defaults.FC_TAG;

public class CreateGameFragment extends Fragment implements GetJwt.Callback {

    int currentFragmentId = R.id.action_create_game;
    OnFragmentReplacement mCallback;

    private EditText gameName;
    private EditText gameDescription;

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

    private GameModel gameModel;
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
        gameDescription = view.findViewById(R.id.multiTextViewDescription);

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
        dialog.setValidateRange(false);
        dialog.setColorBackgroundHeader(R.color.colorPrimary);
        dialog.setColorBackgroundTimePickerHeader(R.color.colorPrimary);
        dialog.setColorTextButton(R.color.colorPrimaryDark);
        FragmentManager fragmentManager = getFragmentManager();
        dialog.show(fragmentManager, "");
    }

    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd)
    {
        Log.d(FC_TAG, "Start: "+hourStart+":"+minuteStart+"\nEnd: "+hourEnd+":"+minuteEnd);
    }

    private void showPlacePicker() {
        mCallback.startPlacePickerActivity();
    }

    public void onAgeRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {
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
            switch(view.getId()) {
                case R.id.radio_male_gender:
                    gameModel.setGender("male");
                    break;
                case R.id.radio_female_gender:
                    gameModel.setGender("female");
                    break;
                case R.id.radio_other_gender:
                    gameModel.setGender("other");
                    break;
            }
        }
    }

    public void onPlayerRestrictedRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {
            switch(view.getId()) {
                case R.id.radio_restricted:
                    gameModel.setPlayerRestricted(true);
                    break;
                case R.id.radio_not_restricted:
                    gameModel.setPlayerRestricted(false);
                    break;
            }
        }
    }

    public void onCreateGameButtonClick() {
        Activity activity = getActivity();
        gameModel.setCreator_id(Authentication.getUserId(activity));
        gatherUserInput();
        // TODO: Replace request call with data gathered from user input
        CreateGameRequest req = new CreateGameRequest();
        JsonObjectRequest request = Games.createGame_request(req, successful_create_game_profile, error_create_game_profile);

        if (request != null) {
            Utils.getInstance(activity).getRequestQueue(activity).add(request);
        }
    }

    private void gatherUserInput() {
        gameModel.setName(gameName.getText().toString());
        gameModel.setDescription(gameDescription.getText().toString());
        gameModel.setLocationNotes(gameLocationNotes.getText().toString());
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
                JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                CreateGameFailure(errorJSON.getString("jwtFailure"));
            }
            catch (Exception e){
                CreateGameFailure(e.getMessage());
            }
        }
    };

    private void CreateGameSuccess(CreateGameResponse response) throws IOException {
        Toast.makeText(getActivity(), "CreateGameResponse successsful. GameId: " + response.game_id, Toast.LENGTH_SHORT).show();

    }

    private void CreateGameFailure(String message) {
        Toast.makeText(getActivity(), "CreateGameResponse failed: " + message, Toast.LENGTH_SHORT).show();
    }
}

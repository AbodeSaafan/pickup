package sotifc2017.pickup.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import org.json.JSONObject;

import java.io.IOException;

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
        gameLocationNotes = view.findViewById(R.id.complete_text_location_notes);

        super.onViewCreated(view, savedInstanceState);
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

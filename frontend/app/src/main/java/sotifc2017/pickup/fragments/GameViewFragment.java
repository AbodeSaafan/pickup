package sotifc2017.pickup.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import sotifc2017.pickup.R;
import sotifc2017.pickup.activities.SignInActivity;
import sotifc2017.pickup.adapters.UserListAdapterT;
import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.Game;
import sotifc2017.pickup.api.GetJwt;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.GetUsersRequest;
import sotifc2017.pickup.api.contracts.SimpleJWTRequest;
import sotifc2017.pickup.api.enums.GAME_TYPE;
import sotifc2017.pickup.api.models.GameModel;
import sotifc2017.pickup.api.models.UserModel;
import sotifc2017.pickup.fragment_interfaces.OnFragmentReplacement;
import sotifc2017.pickup.helpers.GameListItemHelper;

/**
 * Created by parezina on 4/4/2018.
 */

public class GameViewFragment extends Fragment implements GetJwt.Callback {

    OnFragmentReplacement mCallback;
    GameModel gameList;
    TextView gameId;
    TextView gameName;
    TextView gameDate;
    TextView gameDescription;
    TextView gameLocation;
    TextView gameLocationNotes;
    double latitude;
    double longitude;
    Geocoder geocoder;
    ListView listview;
    LinearLayout tagLayout;
    String jwtReal;
    Button joinButton;
    Button leaveButton;
    boolean joinGame =false;
    boolean leaveGame = false;
    boolean getUserList = true;
    private GameListItemHelper helper;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFragmentReplacement) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "[Gameviewfragment] must implement OnFragmentReplacement)");
        }
    }


    @Override
    public void onResume() {
        mCallback.configureMenuItemSelection(R.id.hidden, true);

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_game_view, container, false);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        gameList = gameListJsonSerialize(getArguments().getString("gameJson"));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getUserList = true;
        new GetJwt(this).execute(getActivity());

        gameId = (TextView) getView().findViewById(R.id.gameId);
        gameName = (TextView) getView().findViewById(R.id.gameOwner);
        gameDate = (TextView) getView().findViewById(R.id.date);
        gameDescription = (TextView) getView().findViewById(R.id.gameDescription);
        gameLocation = (TextView) getView().findViewById(R.id.address);
        gameLocationNotes = (TextView) getView().findViewById(R.id.locationNote);
        helper = new GameListItemHelper();

        if(gameList.type == GAME_TYPE.both){
            createGameTag("Both");
            createGameTag("Skill: " + gameList.min_skill + " to " + gameList.max_skill);
        }
        else if (gameList.type == GAME_TYPE.casual){
            createGameTag("Casual");
        }
        else if (gameList.type == GAME_TYPE.serious){
            createGameTag("Serious");
            createGameTag("Skill: " + gameList.min_skill + " to " + gameList.max_skill);
        }


        if(!gameList.gender.isEmpty())
        {
            createGameTag(helper.getGender(gameList.gender));
        }
        if(!(gameList.age_range == null || gameList.age_range.length == 0)) {
            createGameTag(gameList.age_range[0] + "-" + gameList.age_range[1] + " years old");
        }



        String newLocation = "";
        if(!gameList.location.isEmpty()) {
            latitude = gameList.location.get("lat");
            longitude = gameList.location.get("lng");
            newLocation = helper.getLocation(geocoder, latitude, longitude);
        }

        gameId.setText("#" + gameList.game_id);
        if(!gameList.name.isEmpty()) {
            gameName.setText(gameList.name);
        }
        HashMap<String, String> date_time = new HashMap<String, String>();
        date_time = helper.getDate(gameList.start_time, gameList.end_time);
        gameDate.setText(date_time.get("dateTime") +"\n"+ date_time.get("finalTime"));
        if(!gameList.description.isEmpty()) {
            gameDescription.setText(gameList.description);
        }
        gameLocation.setText(newLocation);
        if(!gameList.location_notes.isEmpty()) {
            gameLocationNotes.setText(gameList.location_notes);
        }
    }


    @Override
    public void jwtSuccess(String jwt) {
        if(leaveGame){
            leaveGame = false;
            SimpleJWTRequest request = createSimpleJWTRequest(jwt );
            Utils.getInstance(getActivity()).getRequestQueue(getActivity()).add(Game.leave_game_request(request, Integer.toString(gameList.game_id), successful_leaveGame, error_leaveGame));

        }
        else if(joinGame){
            joinGame = false;
            SimpleJWTRequest request = createSimpleJWTRequest(jwt);
            Utils.getInstance(getActivity()).getRequestQueue(getActivity()).add(Game.join_game_request(request, Integer.toString(gameList.game_id), successful_joinGame, error_joinGame));

        }
        else if (getUserList) {
            getUserList = false;
            GetUsersRequest request = createGetUsersRequest(jwt);
            Utils.getInstance(getActivity()).getRequestQueue(getActivity()).add(Game.getUsers_request(request, successful_userlist, error_userlist));
        }
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

    private Response.Listener<JSONArray> successful_userlist = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            try{
                // get results
                // display results by loading correct list view
                UserModel[] players = Utils.gson.fromJson(response.toString(), UserModel[].class);
                listview = (ListView) getView().findViewById(R.id.userList);

                UserListAdapterT user_adapter = new UserListAdapterT(getActivity(), players);
                int user_id = Integer.parseInt(String.valueOf(Authentication.getUserId(getActivity())));
                Boolean alreadyJoined = false;

                for (UserModel player: players) {
                    if (player.user_id == user_id)
                    {
                        alreadyJoined = true;
                    }
                }

                if(alreadyJoined) {
                    leaveButton = new Button(getActivity());
                    leaveButton.setText("Leave Game");
                    leaveButton.setBackgroundColor(getResources().getColor(R.color.red));
                    leaveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            leaveGame= true;
                            new GetJwt(GameViewFragment.this).execute(getActivity());
                        }
                    });
                    listview.addFooterView(leaveButton);
                }
                else {
                    joinButton = new Button(getActivity());
                    joinButton.setText("Join Game +");
                    joinButton.setBackgroundColor(getResources().getColor(R.color.green));
                    joinButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            joinGame = true;
                            new GetJwt(GameViewFragment.this).execute(getActivity());
                        }
                    });
                    listview.addFooterView(joinButton);
                }

                listview.setAdapter(user_adapter);

            }
            catch (Exception e){
                Log.e("game", "error parsing results");
            }
        }
    };

    private Response.ErrorListener error_userlist =  new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                Log.e("game", errorJSON.toString());
            }
            catch (Exception e){

                Log.e("game", e.getMessage());
                Log.e("game", "error parsing failure Abode");
            }
        }
    };

    private Response.Listener<JSONObject> successful_joinGame = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try{
                Toast.makeText(getActivity(), "Successfully joined game!", Toast.LENGTH_SHORT).show();
                joinButton.setBackgroundColor(getResources().getColor(R.color.darkgreen));
                joinButton.setEnabled(false);
                //Reload this page
            }
            catch (Exception e){
                Log.e("game", "error parsing results");
            }

        }
    };

    private Response.ErrorListener error_joinGame =  new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                Toast.makeText(getActivity(), "Join Game failed: " + errorJSON.getString("error"), Toast.LENGTH_SHORT).show();
            }
            //TODO: Implement Failure
            catch (Exception e){
                Toast.makeText(getActivity(), "Join Game failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Response.Listener<JSONObject> successful_leaveGame = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try{
                Toast.makeText(getActivity(), "Successfully left game.", Toast.LENGTH_SHORT).show();
                leaveButton.setBackgroundColor(getResources().getColor(R.color.darkred));
                leaveButton.setEnabled(false);
                //Reload this page
            }
            catch (Exception e){
                Log.e("game", "error parsing results");
            }

        }
    };

    private Response.ErrorListener error_leaveGame =  new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                Toast.makeText(getActivity(), "Leave Game failed: " + errorJSON.getString("jwtFailure"), Toast.LENGTH_SHORT).show();
            }
            //TODO: Implement Failure
            catch (Exception e){
                Toast.makeText(getActivity(), "Leave Game failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private GameModel gameListJsonSerialize(String json){
        return Utils.gson.fromJson(json, GameModel.class);
    }

    private GetUsersRequest createGetUsersRequest(String jwt){
        return GetUsersRequest.CreateUserListRequest(jwt, gameList.game_id);
    }

    private SimpleJWTRequest createSimpleJWTRequest(String jwt){
        return SimpleJWTRequest.CreateSimpleJWTRequest(jwt);
    }

    private void createGameTag(String text)
    {
        tagLayout = (LinearLayout) getView().findViewById(R.id.tagLayout);
        LayoutParams lparams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        TextView tv=new TextView(getActivity());
        tv.setLayoutParams(lparams);
        tv.setPadding(50,0,50,0);
        tv.setBackgroundResource(R.drawable.rounded_corner);
        tv.setText(text);
        tv.setTextColor(getResources().getColor(R.color.primary_dark));
        this.tagLayout.addView(tv);
    }

    }

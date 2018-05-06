package sotifc2017.pickup.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.net.HttpURLConnection;

import sotifc2017.pickup.CommonComponents;
import sotifc2017.pickup.R;
import sotifc2017.pickup.activities.HostingActivity;
import sotifc2017.pickup.activities.MainActivity;
import sotifc2017.pickup.activities.SignInActivity;
import sotifc2017.pickup.android_modified_source.FragmentPagerAdapter;
import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.GetJwt;
import sotifc2017.pickup.api.Search;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.GetSearchRequest;
import sotifc2017.pickup.api.models.GameModel;
import sotifc2017.pickup.api.models.UserModel;
import sotifc2017.pickup.fragment_interfaces.OnFragmentReplacement;
import sotifc2017.pickup.fragment_interfaces.SearchFragment;

/**
 * Created by Abode on 3/3/2018.
 */

public class MainSearchFragment extends Fragment implements GetJwt.Callback {
    int currentFragmentId = R.id.action_search;
    OnFragmentReplacement mCallback;

    private final static int SEARCH_GAMES_TAB_NUMBER = 0;
    private final static int SEARCH_USERS_TAB_NUMBER = 1;
    private ViewPager vp;
    private Button searchButton;
    private Fragment gameSearchFragment;
    private Fragment userSearchFragment;
    private ProgressDialog loadingResponse;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFragmentReplacement) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "[MainSearchFragment] must implement OnFragmentReplacement)");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        gameSearchFragment = new SearchGamesFragment();
        userSearchFragment = new SearchUsersFragment();

        vp = getView().findViewById(R.id.main_search_pager);
        vp.setAdapter(new SectionsPagerAdapter(getChildFragmentManager()));
        TabLayout tabLayout = getView().findViewById(R.id.search_tabLayout);
        tabLayout.setupWithViewPager(vp);

        searchButton = view.findViewById(R.id.main_search_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveJwt();
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case SEARCH_GAMES_TAB_NUMBER:
                    return gameSearchFragment;
                case SEARCH_USERS_TAB_NUMBER:
                    return userSearchFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case SEARCH_GAMES_TAB_NUMBER:
                    return getString(R.string.main_search_games_tab_title);
                case SEARCH_USERS_TAB_NUMBER:
                    return getString(R.string.main_search_users_tab_title);
            }
            return null;
        }
    }

    @Override
    public void onResume() {
        mCallback.configureMenuItemSelection(currentFragmentId, true);

        super.onResume();
    }

    private void retrieveJwt(){
        // loading spinner
        loadingResponse = CommonComponents.getLoadingProgressDialog(getActivity());
        loadingResponse.show();
        // get jwt
        new GetJwt(this).execute(getActivity());

    }

    @Override
    public void jwtSuccess(String jwt) {
        GetSearchRequest searchRequest;
        Response.Listener<JSONObject> successful_search;
        Response.ErrorListener error_search;

        // construct search
        switch(vp.getCurrentItem()){
            case SEARCH_GAMES_TAB_NUMBER:
                searchRequest = ((SearchFragment) gameSearchFragment).constructSearchRequest(jwt);
                successful_search = successful_game_search;
                error_search = error_game_search;
                break;
            case SEARCH_USERS_TAB_NUMBER:
                searchRequest = ((SearchFragment) userSearchFragment).constructSearchRequest(jwt);
                successful_search = successful_user_search;
                error_search = error_user_search;
                break;
            default:
                //TODO fail out of here
                return;
        }
        Utils.getInstance(getActivity()).getRequestQueue(getActivity()).add(Search.getSearch_request(searchRequest, successful_search, error_search));
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

    private Response.Listener<JSONObject> successful_game_search = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try{
                // get results
                // display results by loading correct list view
                ((HostingActivity) getActivity()).onDisplayGameSearchResults(response.get("games").toString());

            }
            catch (Exception e){
                Log.e("search", "error parsing results");
            }

        }
    };

    private Response.ErrorListener error_game_search =  new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                if(error.networkResponse.statusCode == HttpURLConnection.HTTP_BAD_REQUEST){
                    searchDoesNotHaveResults();
                } else{
                    Log.v("search", "panic?");
                }
                Log.e("search", errorJSON.toString());
            }
            catch (Exception e){
                Log.e("search", "error parsing failure");
            }
        }
    };

    private Response.Listener<JSONObject> successful_user_search = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response){
            try{
                // get results
                // display results by loading correct list view
                ((HostingActivity) getActivity()).onDisplayUserSearchResults(response.get("users").toString());
            }
            catch (Exception e){
                Log.e("search", "error parsing results");
            }
        }
    };

    private Response.ErrorListener error_user_search =  new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                if(error.networkResponse.statusCode == HttpURLConnection.HTTP_BAD_REQUEST){
                    searchDoesNotHaveResults();
                } else{
                    Log.v("search", "panic?");
                }
                Log.e("search", errorJSON.toString());
            }
            catch (Exception e){
                Log.e("search", "error parsing failure");
            }
        }
    };

    private void searchDoesNotHaveResults(){
        loadingResponse.cancel();
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(getActivity()).
                setMessage(getActivity().getString(R.string.main_search_nothing_matches)).
                setCancelable(true).
                setPositiveButton(
                        "Okay",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        exitDialog.create().show();
    }
}

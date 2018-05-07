package sotifc2017.pickup.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import sotifc2017.pickup.Adapters.GameListAdapter;
import sotifc2017.pickup.Adapters.UserListAdapter;
import sotifc2017.pickup.R;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.models.GameModel;
import sotifc2017.pickup.api.models.UserModel;

/**
 * Created by rkrishnan on 5/7/2018.
 */

public class ListViewFragment extends Fragment {

    ListView listview;
    View rootView;


    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        listview = (ListView) rootView.findViewById(R.id.list);


        if (getArguments().containsKey("gameListJson")) {

            GameModel[] gameList = gameListJsonSerialize(getArguments().getString("gameListJson"));
            GameListAdapter game_adapter = new GameListAdapter(getActivity(), gameList);
            listview.setAdapter(game_adapter);

        } else if (getArguments().containsKey("userListJson")) {

            UserModel[] userList = userListJsonSerialize(getArguments().getString("userListJson"));

            UserListAdapter user_adapter = new UserListAdapter(getActivity(), userList);
            listview.setAdapter(user_adapter);

        }

        return rootView;


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private GameModel[] gameListJsonSerialize(String json){
        return Utils.gson.fromJson(json, GameModel[].class);
    }

    private UserModel[] userListJsonSerialize(String json){
        return Utils.gson.fromJson(json, UserModel[].class);
    }



}

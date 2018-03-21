package sotifc2017.pickup.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sotifc2017.pickup.R;
import sotifc2017.pickup.activities.SignInActivity;
import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.GetJwt;
import sotifc2017.pickup.api.models.GameModel;
import sotifc2017.pickup.adapters.GameListAdapter;

/**
 * Created by radhika on 2018-03-10.
 */

public class GamesListViewFragment extends Fragment implements GetJwt.Callback {

    ListView listview;
    View rootView;
    ArrayList<GameModel> all_games;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_game_list_view, container, false);
        listview = (ListView) rootView.findViewById(R.id.gamelist);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get Search results here

        new GetJwt(this).execute(getActivity());


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void jwtSuccess(String jwt) {

        //Send search results as params into createListView

        all_games= new ArrayList<GameModel>();
        createListView();
        GameListAdapter adapter = new GameListAdapter(getContext(), all_games);
        listview.setAdapter(adapter);

    }

    @Override
    public void jwtFailure(Exception e) {
        Log.e("jwt", e.getMessage());
        Authentication.logout(getActivity());
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);
    }

    public void createListView (){

        // create the hard coded game Objects:

        Map <String, Double> location_1 = new HashMap<String, Double>();
        location_1.put("lat", 43.619257);
        location_1.put("lng", -79.673967);


        Map <String, Double> location_2 = new HashMap<String, Double>();
        location_2.put("lat", 43.661689);
        location_2.put("lng", -79.723295);

        Map <String, Double> location_3 = new HashMap<String, Double>();
        location_3.put("lat", 43.122110);
        location_3.put("lng", -79.093014);

        Map <String, Double> location_4 = new HashMap<String, Double>();
        location_4.put("lat", 44.414352);
        location_4.put("lng", -79.685250);

        int[] game_id = new int[] {1, 2, 3, 4};
        String[] game_name = new String[]{"radhika's game", "radhika's game pt II", "radhika's game pt III", "radhika's game pt IV"};

        String[] game_type = new String[]{"casual", "serious", "casual", "serious"};

        int[] skill_min = new int[] {5, 3, 2, 4};
        int[] skill_max = new int[] {7, 10, 8, 9};
        int[] total_players_required = new int[] {15, 10, 20, 12};
        int[] total_players_added = new int[] {12, 5, 11, 5};
        //1522411200 => March 30 (12:00)
        //1522947600 => April 5 (1:00 PM)
        //1526846400 => May 20 (4:00 PM)
        //1528668000 => June 10 (6:00 PM)
        int[] start_time = new int[] {1522411200, 1522947600, 1526846400, 1528668000};
        int[] end_time = new int[] {1522428000, 1522950300, 1526848200, 1528671600};
        //1522428000 => March 30 (12:40 PM)
        //1522950300 => April 5 (1:45 PM)
        //1526848200 => May 20 (4:30 PM)
        //1528671600 => June 10 (7:00 PM)
        List<Map<String, Double>> locations = new ArrayList<Map<String, Double>>();
        locations.add(location_1);
        locations.add(location_2);
        locations.add(location_3);
        locations.add(location_4);
        int[] creator_id = new int[] {1, 2, 3, 4};
        String[] descriptions = new String[] {"Casual basketball game", "Serious basketball game pt II", "Casual basketball game pt III",
                "Serious basketball game pt III"};

        String[] location_notes = new String[] {"Come around the back and knock on the blue door", "Come around the back and knock on the red door",
                "Come around the back and knock on the yellow door", "Come around the back and knock on the purple door"};

        String[] gender = new String[] {"A", "F", "M", "A"};

        List<int[]> age_range = new ArrayList<int[]>();
        age_range.add(new int[]{20, 30});
        age_range.add(new int[]{18, 35});
        age_range.add(new int[]{});
        age_range.add(new int[]{30, 45});

        List<String[]> enforced_params = new ArrayList<String[]>();
        enforced_params.add(new String[]{"age_range"});
        enforced_params.add(new String[]{"gender", "age_range"});
        enforced_params.add(new String[]{"gender"});
        enforced_params.add(new String[]{"age_range"});

        int[] time_created = new int[] {1504272395, 1504272395, 1504272395, 1504272395};

        GameModel game_1 = new GameModel (game_id[0], game_name[0], game_type[0], skill_min[0], skill_max[0], total_players_required[0], total_players_added [0], start_time[0], end_time[0], locations.get(0), creator_id[0], descriptions[0], location_notes[0], gender[0], age_range.get(0), enforced_params.get(0), time_created[0]);

        GameModel game_2 = new GameModel (game_id[1], game_name[1], game_type[1], skill_min[1], skill_max[1], total_players_required[1], total_players_added [1], start_time[1], end_time[1], locations.get(1), creator_id[1], descriptions[1], location_notes[1], gender[1], age_range.get(1), enforced_params.get(1), time_created[1]);

        GameModel game_3 = new GameModel (game_id[2], game_name[2], game_type[2], skill_min[2], skill_max[2], total_players_required[2], total_players_added [2], start_time[2], end_time[2], locations.get(2), creator_id[2], descriptions[2], location_notes[2], gender[2], age_range.get(2), enforced_params.get(2), time_created[2]);

        GameModel game_4 = new GameModel (game_id[3], game_name[3], game_type[3], skill_min[3], skill_max[3], total_players_required[3], total_players_added [3], start_time[3], end_time[3], locations.get(3), creator_id[3], descriptions[3], location_notes[3], gender[3], age_range.get(3), enforced_params.get(3), time_created[3]);

        all_games.add(game_1);
        all_games.add(game_2);
        all_games.add(game_3);
        all_games.add(game_4);

    }



}

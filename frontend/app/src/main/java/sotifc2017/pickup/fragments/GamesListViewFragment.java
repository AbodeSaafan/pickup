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
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.GetSearchRequest;
import sotifc2017.pickup.api.models.GameModel;
import sotifc2017.pickup.adapters.GameListAdapter;

/**
 * Created by radhika on 2018-03-10.
 */

public class GamesListViewFragment extends Fragment {

    ListView listview;
    View rootView;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_game_list_view, container, false);
        listview = (ListView) rootView.findViewById(R.id.gamelist);

        GameModel[] gameList = gameListJsonSerialize(savedInstanceState.getString("gameListJson"));

        GameListAdapter adapter = new GameListAdapter(getActivity(), gameList);
        listview.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get Search results here

    }

    private GameModel[] gameListJsonSerialize(String json){
        return Utils.gson.fromJson(json, GameModel[].class);
    }


    /*public void createExampleListView (){

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

        Map <String, Double> location_5 = new HashMap<String, Double>();
        location_5.put("lat", 43.826523);
        location_5.put("lng", -79.540733);

        Map <String, Double> location_6 = new HashMap<String, Double>();
        location_6.put("lat", 43.482842);
        location_6.put("lng", -79.718851);

        Map <String, Double> location_7 = new HashMap<String, Double>();
        location_7.put("lat", 43.548332);
        location_7.put("lng", -80.219249);

        Map <String, Double> location_8 = new HashMap<String, Double>();
        location_8.put("lat", 43.684139);
        location_8.put("lng", -79.375059);

        Map <String, Double> location_9 = new HashMap<String, Double>();
        location_9.put("lat", 43.677832);
        location_9.put("lng", -79.415400);

        Map <String, Double> location_10 = new HashMap<String, Double>();
        location_10.put("lat", 43.340180);
        location_10.put("lng", -79.884405);

        int[] game_id = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        String[] game_name = new String[]{"radhika's game", "radhika's game pt II",
                "radhika's game pt III", "radhika's game pt IV",
                "radhika's game pt V", "radhika's game pt VI",
                "radhika's game pt VII", "radhika's game pt VIII",
                "radhika's game pt IX", "radhika's game pt X",
        };

        String[] game_type = new String[]{"casual", "serious", "casual", "serious", "serious", "casual", "serious", "casual", "casual", "serious"};

        int[] skill_min = new int[] {5, 3, 2, 4, 2, 3, 5, 3, 2, 4};
        int[] skill_max = new int[] {7, 10, 8, 9, 7, 8, 5, 10, 8, 9};
        int[] total_players_required = new int[] {15, 10, 20, 12, 10, 8, 25, 16, 30, 15};
        int[] total_players_added = new int[] {12, 5, 11, 5, 9, 5, 22, 5, 22, 10};
        //1522411200 => March 30 (12:00)
        //1522947600 => April 5 (5:00 PM)
        //1526846400 => May 20 (8:00 PM)
        //1528668000 => June 10 (10:00 PM)
        //1526063400 => May 11 (6:30 PM)
        //1524769200 => April 26 (7:00 PM)
        //1527529500 => May 28 (5:45 PM)
        //1530376200 => June 30 (4:30 PM)
        //1531236600 => July 10 (3:30PM)
        //1533487500 => August 5 (4:45 PM)
        int[] start_time = new int[] {1522411200, 1522947600, 1526846400, 1528668000, 1526063400, 1524769200, 1527529500, 1530376200, 1531236600, 1533487500};
        int[] end_time = new int[] {1522428000, 1522950300, 1526848200, 1528671600, 1526067000, 1524774600, 1527532200, 1530378900, 1531240200, 1533490200};
        //1522428000 => March 30 (4:40 PM)
        //1522950300 => April 5 (5:45 PM)
        //1526848200 => May 20 (8:30 PM)
        //1528671600 => June 10 (11:00 PM)
        //1526067000 => May 11 (7:30 PM)
        //1524774600 => April 26 (8:30 PM)
        //1527532200 => May 28 (6:30 PM)
        //1530378900 => June 30 (5:15 PM)
        //1531240200 => July 10 (4:30 PM)
        //1533490200 => August 5 (5:30 PM)


        List<Map<String, Double>> locations = new ArrayList<Map<String, Double>>();
        locations.add(location_1);
        locations.add(location_2);
        locations.add(location_3);
        locations.add(location_4);
        locations.add(location_5);
        locations.add(location_6);
        locations.add(location_7);
        locations.add(location_8);
        locations.add(location_9);
        locations.add(location_10);
        int[] creator_id = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        String[] descriptions = new String[] {"Casual basketball game I", "Serious basketball game pt II",
                "Casual basketball game pt III", "Serious basketball game pt IV",
                "Serious basketball game pt V", "Casual basketball game pt VI",
                "Serious basketball game pt VII", "Casual basketball game pt VIII",
                "Serious basketball game pt IX", "Casual basketball game pt X"
        };

        String[] location_notes = new String[] {"Come around the back and knock on the blue door", "Come around the back and knock on the red door",
                "Come around the back and knock on the yellow door", "Come around the back and knock on the purple door",
                "Come around the back and knock on the orange door", "Come around the back and knock on the black door",
                "Come around the back and knock on the blue door", "Come around the back and knock on the white door",
                "Come around the back and knock on the yellow door", "Come around the back and knock on the purple door"};

        String[] gender = new String[] {"A", "F", "M", "A", "F", "M", "A", "A", "F", "M"};

        List<int[]> age_range = new ArrayList<int[]>();
        age_range.add(new int[]{20, 30});
        age_range.add(new int[]{18, 35});
        age_range.add(new int[]{});
        age_range.add(new int[]{30, 45});
        age_range.add(new int[]{20, 35});
        age_range.add(new int[]{13, 20});
        age_range.add(new int[]{});
        age_range.add(new int[]{13, 18});
        age_range.add(new int[]{18, 25});
        age_range.add(new int[]{15, 28});


        List<String[]> enforced_params = new ArrayList<String[]>();
        enforced_params.add(new String[]{"age_range"});
        enforced_params.add(new String[]{"gender", "age_range"});
        enforced_params.add(new String[]{"gender"});
        enforced_params.add(new String[]{"age_range"});
        enforced_params.add(new String[]{"gender", "age_range"});
        enforced_params.add(new String[]{"gender", "age_range"});
        enforced_params.add(new String[]{});
        enforced_params.add(new String[]{"age_range"});
        enforced_params.add(new String[]{"gender", "age_range"});
        enforced_params.add(new String[]{"gender", "age_range"});

        int[] time_created = new int[] {1504272395, 1504272395, 1504272395, 1504272395, 1504292395, 1505272395, 1504372395, 1504272495, 1504272595, 1504572495};
        boolean[] player_restricted = new boolean[] {true, false, true, true, true, true, false, false, false, false};

        GameModel game_1 = new GameModel (game_id[0], game_name[0], game_type[0], skill_min[0], skill_max[0], total_players_required[0], total_players_added [0], start_time[0], end_time[0], locations.get(0), creator_id[0], descriptions[0], location_notes[0], gender[0], age_range.get(0), enforced_params.get(0), time_created[0], player_restricted[0]);

        GameModel game_2 = new GameModel (game_id[1], game_name[1], game_type[1], skill_min[1], skill_max[1], total_players_required[1], total_players_added [1], start_time[1], end_time[1], locations.get(1), creator_id[1], descriptions[1], location_notes[1], gender[1], age_range.get(1), enforced_params.get(1), time_created[1], player_restricted[1]);

        GameModel game_3 = new GameModel (game_id[2], game_name[2], game_type[2], skill_min[2], skill_max[2], total_players_required[2], total_players_added [2], start_time[2], end_time[2], locations.get(2), creator_id[2], descriptions[2], location_notes[2], gender[2], age_range.get(2), enforced_params.get(2), time_created[2], player_restricted[2]);

        GameModel game_4 = new GameModel (game_id[3], game_name[3], game_type[3], skill_min[3], skill_max[3], total_players_required[3], total_players_added [3], start_time[3], end_time[3], locations.get(3), creator_id[3], descriptions[3], location_notes[3], gender[3], age_range.get(3), enforced_params.get(3), time_created[3], player_restricted[3]);

        GameModel game_5 = new GameModel (game_id[4], game_name[4], game_type[4], skill_min[4], skill_max[4], total_players_required[4], total_players_added [4], start_time[4], end_time[4], locations.get(4), creator_id[4], descriptions[4], location_notes[4], gender[4], age_range.get(4), enforced_params.get(4), time_created[4], player_restricted[4]);

        GameModel game_6 = new GameModel (game_id[5], game_name[5], game_type[5], skill_min[5], skill_max[5], total_players_required[5], total_players_added [5], start_time[5], end_time[5], locations.get(5), creator_id[5], descriptions[5], location_notes[5], gender[5], age_range.get(5), enforced_params.get(5), time_created[5], player_restricted[5]);

        GameModel game_7 = new GameModel (game_id[6], game_name[6], game_type[6], skill_min[6], skill_max[6], total_players_required[6], total_players_added [6], start_time[6], end_time[6], locations.get(6), creator_id[6], descriptions[6], location_notes[6], gender[6], age_range.get(6), enforced_params.get(6), time_created[6], player_restricted[6]);

        GameModel game_8 = new GameModel (game_id[7], game_name[7], game_type[7], skill_min[7], skill_max[7], total_players_required[7], total_players_added [7], start_time[7], end_time[7], locations.get(7), creator_id[7], descriptions[7], location_notes[7], gender[7], age_range.get(7), enforced_params.get(7), time_created[7], player_restricted[7]);

        GameModel game_9 = new GameModel (game_id[8], game_name[8], game_type[8], skill_min[8], skill_max[8], total_players_required[8], total_players_added [8], start_time[8], end_time[8], locations.get(8), creator_id[8], descriptions[8], location_notes[8], gender[8], age_range.get(8), enforced_params.get(8), time_created[8], player_restricted[8]);

        GameModel game_10 = new GameModel (game_id[9], game_name[9], game_type[9], skill_min[9], skill_max[9], total_players_required[9], total_players_added [9], start_time[9], end_time[9], locations.get(9), creator_id[9], descriptions[9], location_notes[9], gender[9], age_range.get(9), enforced_params.get(9), time_created[9], player_restricted[9]);


        all_games.add(game_1);
        all_games.add(game_2);
        all_games.add(game_3);
        all_games.add(game_4);
        all_games.add(game_5);
        all_games.add(game_6);
        all_games.add(game_7);
        all_games.add(game_8);
        all_games.add(game_9);
        all_games.add(game_10);
    }*/



}

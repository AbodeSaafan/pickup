package sotifc2017.pickup.api.contracts;

import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Abode on 4/23/2018.
 */

/**
 * Search request object to search for users or games
 */
public class GetSearchRequest {
    public String jwt;
    public SEARCH_TYPE search_object;
    public int results_max; //Not implemented in UI yet
    public int game_id;
    public String game_name;
    public GAME_TYPE game_type;
    public int game_skill_min;
    public int game_skill_max;
    public int game_total_players;
    public long game_start_time;
    public long game_end_time;
    public Map<String, Double> game_location= new HashMap<String, Double>();
    public int game_location_range;
    public String username;


    public enum SEARCH_TYPE {
        game, user
    }

    public enum GAME_TYPE {
        casual, serious, both
    }

    private GetSearchRequest(String jwt, SEARCH_TYPE search_object){
        this.jwt = jwt;
        this.search_object = search_object;
    }

    /**
     *
     * @param jwt jwt token for request
     * @param game_id the game id for the game
     * @return game search request
     */
    public static GetSearchRequest CreateGameRequest(String jwt, int game_id) {
        GetSearchRequest request = new GetSearchRequest(jwt, SEARCH_TYPE.game);
        request.game_id = game_id;
        return request;
    }

    /**
     *
     * @param jwt jwt token for request
     * @param game_name name of the game, empty if not specified
     * @param game_type the type of game
     * @param game_skill_min the min skill, must be specified with skill max or -1
     * @param game_skill_max the max skill, must be specified with skill min or -1
     * @param game_total_players minimum total players, -1 if not specified
     * @param game_start_time start date of game, must be specified with end date or -1
     * @param game_end_time end date of game, must be specified with start date or -1
     * @param game_location location of game, must be specified and valid
     * @param game_location_range range of location search, -1 if not specified
     * @return game search request
     */
    public static GetSearchRequest CreateGameRequest(String jwt, String game_name, GAME_TYPE game_type,
                                                     int game_skill_min, int game_skill_max, int game_total_players,
                                                     long game_start_time, long game_end_time,
                                                     Map<String, Double> game_location, int game_location_range){
        GetSearchRequest request = new GetSearchRequest(jwt, SEARCH_TYPE.game);
        if(game_name != null && !game_name.isEmpty()){
            request.game_name = game_name;
        }
        if(game_type != GAME_TYPE.both){
            request.game_type = game_type;
        }
        if(game_skill_min != -1 & game_skill_max != -1){
            request.game_skill_min = game_skill_min;
            request.game_skill_max = game_skill_max;
        }
        if(game_total_players != -1){
            request.game_total_players = game_total_players;
        }
        if(game_start_time != -1 & game_end_time != -1){
            request.game_start_time = game_start_time;
            request.game_end_time = game_end_time;
        }
        request.game_location = game_location;
        if(request.game_location_range != -1){
            request.game_location_range = game_location_range;
        }

        return request;
    }

    /**
     *
     * @param jwt jwt token for request
     * @param username the username for the search
     * @return user search request
     */
    public static GetSearchRequest CreateUserRequest(String jwt, String username){
        GetSearchRequest request = new GetSearchRequest(jwt, SEARCH_TYPE.user);
        request.username = username;
        return request;
    }

    /**
     * Used to convert this object into a set of url parameters for a get request
     * @return Url parameter string
     */
    public String ToUrlParameter(){
        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter("jwt", this.jwt);
        builder.appendQueryParameter("search_object", this.search_object.toString());

        switch(this.search_object){
            case game:
                //TODO fill this in
                builder.appendQueryParameter("", "");
                break;
            case user:
                builder.appendQueryParameter("username", this.username);
                break;
        }

        return builder.toString();
    }


}



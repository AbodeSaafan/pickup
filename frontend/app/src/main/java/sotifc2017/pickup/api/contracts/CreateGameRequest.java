package sotifc2017.pickup.api.contracts;

import java.util.HashMap;

import sotifc2017.pickup.api.enums.API_GENDER;
import sotifc2017.pickup.api.enums.ENFORCED_PARAMS;
import sotifc2017.pickup.api.enums.GAME_TYPE;

/**
 * Created by Abode on 5/7/2018.
 */

public class CreateGameRequest {
     public String jwt;
     public String name;
     public GAME_TYPE type;
     public int skill;
     public int total_players_required;
     public long start_time;
     public long duration;
     public HashMap<String, Double> location;
     public String location_notes;
     public String description;
     public API_GENDER gender;
     public int[] age_range;
     public ENFORCED_PARAMS[] enforced_params;
}

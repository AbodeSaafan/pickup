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

    public CreateGameRequest() {

    }

     public CreateGameRequest(String jwt,
                              String name,
                              GAME_TYPE type,
                              int skill,
                              int total_players_required,
                              long start_time,
                              long duration,
                              HashMap<String, Double> location,
                              String location_notes,
                              String description,
                              API_GENDER gender,
                              int[] age_range,
                              ENFORCED_PARAMS[] enforced_params) {
          this.jwt = jwt;
          this.name = name;
          this.type = type;
          this.skill = skill;
          this.total_players_required = total_players_required;
          this.start_time = start_time;
          this.duration = duration;
          this.location = location;
          this.location_notes = location_notes;
          this.description = description;
          this.gender = gender;
          this.age_range = age_range;
          this.enforced_params = enforced_params;
     }
}

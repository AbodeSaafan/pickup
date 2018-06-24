package sotifc2017.pickup.api.contracts;

import java.util.HashMap;
import java.util.Map;

import sotifc2017.pickup.api.enums.ENFORCED_PARAMS;
import sotifc2017.pickup.api.enums.GAME_TYPE;

/**
 * Created by Abode on 5/7/2018.
 */

public class CreateGameRequest {
    public String jwt;
    public String name;
    public GAME_TYPE type;
    public int skill_offset;
    public int total_players_required;
    public long start_time;
    public long duration;
    public Map<String, Double> location;
    public String location_notes;
    public String description;
    public String gender;
    public ENFORCED_PARAMS[] enforced_params;

     public CreateGameRequest(String name,
                              GAME_TYPE type,
                              int skillOffset,
                              int total_players_required,
                              long start_time,
                              long duration,
                              HashMap<String, Double> location,
                              String location_notes,
                              String description,
                              ENFORCED_PARAMS[] enforced_params,
                              String jwt) {
          this.name = name;
          this.type = type;
          this.skill_offset = skillOffset;
          this.total_players_required = total_players_required;
          this.start_time = start_time;
          this.duration = duration;
          this.location = location;
          this.location_notes = location_notes;
          this.description = description;
          this.enforced_params = enforced_params;
          this.jwt = jwt;
     }
}

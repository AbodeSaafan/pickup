package sotifc2017.pickup.api.models;

import java.util.Map;

/**
 * Created by asaafan on 3/8/2018.
 */

public class GameModel {
    public int game_id;
    public String name;
    public String type;
    public int min_skill;
    public int max_skill;
    public int total_players_required;
    public int total_players_added;
    public int start_time;
    public int end_time;
    public Map<String, Double> location;
    public int creator_id;
    public String description;
    public String location_notes;
    public String gender;
    public int[] age_range;
    public String[] enforced_params;
    public int time_created;

    //function for time-being

    public GameModel (int game_id, String name, String type, int min_skill, int max_skill, int total_players_required,
                      int total_players_added, int start_time, int end_time, Map<String, Double> location, int creator_id,
                      String description, String location_notes, String gender, int[] age_range, String[] enforced_params, int time_created) {

    this.game_id = game_id;
    this.name = name;
    this.type = type;
    this.min_skill = min_skill;
    this.total_players_required = total_players_required;
    this.total_players_added = total_players_added;
    this.start_time = start_time;
    this.end_time = end_time;
    this.location = location;
    this.creator_id = creator_id;
    this.description = description;
    this.location_notes = location_notes;
    this.gender = gender;
    this.age_range = age_range;
    this.enforced_params = enforced_params;
    this.time_created = time_created;

    }

}

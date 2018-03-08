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
}

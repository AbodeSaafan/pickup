package sotifc2017.pickup.api.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by asaafan on 3/8/2018.
 */

public class GameModel {
    private final int DEFAULT_INT = -1;
    private final String DEFAULT_STRING = "";
    private final boolean DEFAULT_BOOLEAN = false;
    
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
    public boolean player_restricted;

    public GameModel () {
        this.game_id = DEFAULT_INT;
        this.name = DEFAULT_STRING ;
        this.type = DEFAULT_STRING ;
        this.min_skill = DEFAULT_INT;
        this.total_players_required = DEFAULT_INT;
        this.total_players_added = DEFAULT_INT;
        this.start_time = DEFAULT_INT;
        this.end_time = DEFAULT_INT;
        this.location = new HashMap<String, Double>();
        this.creator_id = DEFAULT_INT;
        this.description = DEFAULT_STRING;
        this.location_notes = DEFAULT_STRING;
        this.gender = DEFAULT_STRING;
        this.age_range = new int[] { DEFAULT_INT, DEFAULT_INT };
        this.enforced_params = new String[] {};
        this.time_created = DEFAULT_INT;
        this.player_restricted = DEFAULT_BOOLEAN;
    }


    //function for time-being
    public GameModel (int game_id, String name, String type, int min_skill, int max_skill, int total_players_required,
                      int total_players_added, int start_time, int end_time, Map<String, Double> location, int creator_id,
                      String description, String location_notes, String gender, int[] age_range, String[] enforced_params, int time_created, boolean player_restricted) {

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
        this.player_restricted = player_restricted;

    }

    public int getGameId() {
        return game_id;
    }

    public void setGameId(int game_id) {
        this.game_id = game_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMinSkill() {
        return min_skill;
    }

    public void setMinSkill(int min_skill) {
        this.min_skill = min_skill;
    }

    public int getMaxSkill() {
        return max_skill;
    }

    public void setMaxSkill(int max_skill) {
        this.max_skill = max_skill;
    }

    public int getTotalPlayersRequired() {
        return total_players_required;
    }

    public void setTotalPlayersRequired(int total_players_required) {
        this.total_players_required = total_players_required;
    }

    public int getTotalPlayersAdded() {
        return total_players_added;
    }

    public void setTotalPlayersAdded(int total_players_added) {
        this.total_players_added = total_players_added;
    }

    public int getStartTime() {
        return start_time;
    }

    public void setStartTime(int start_time) {
        this.start_time = start_time;
    }

    public int getEndTime() {
        return end_time;
    }

    public void setEndTime(int end_time) {
        this.end_time = end_time;
    }

    public Map<String, Double> getLocation() {
        return location;
    }

    public void setLocation(Map<String, Double> location) {
        this.location = location;
    }

    public int getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(int creator_id) {
        this.creator_id = creator_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationNotes() {
        return location_notes;
    }

    public void setLocationNotes(String location_notes) {
        this.location_notes = location_notes;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int[] getAgeRange() {
        return age_range;
    }

    public void setAgeRange(int[] age_range) {
        this.age_range = age_range;
    }

    public String[] getEnforcedParams() {
        return enforced_params;
    }

    public void setEnforcedParams(String[] enforced_params) {
        this.enforced_params = enforced_params;
    }

    public int getTimeCreated() {
        return time_created;
    }

    public void setTimeCreated(int time_created) {
        this.time_created = time_created;
    }

    public boolean isPlayerRestricted() {
        return player_restricted;
    }

    public void setPlayerRestricted(boolean player_restricted) {
        this.player_restricted = player_restricted;
    }
}

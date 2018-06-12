package sotifc2017.pickup.api.models;

import java.util.HashMap;

import sotifc2017.pickup.api.enums.ENFORCED_PARAMS;

/**
 * Created by asaafan on 3/8/2018.
 */

public class GameModel {
    private final int DEFAULT_INT = -1;
    private final boolean DEFAULT_BOOLEAN = false;
    private final int START_NUM_PLAYERS_ADDED = 0;
    
    public int game_id;
    public String name;
    public String type;
    public int offsetSkill;
    public int total_players_required;
    public int total_players_added;
    public String start_time;
    public String end_time;
    public int finalStartTime;
    public int finalEndTime;
    public HashMap<String, Double> location;
    public int creator_id;
    public String description;
    public String location_notes;
    public String gender;
    public int[] age_range;
    public ENFORCED_PARAMS[] enforced_params;
    public int time_created;
    public boolean player_restricted;

    public GameModel () {
        this.game_id = DEFAULT_INT;
        this.name = "My game";
        this.type = "casual";
        this.offsetSkill = DEFAULT_INT;
        this.total_players_required = DEFAULT_INT;
        this.total_players_added = START_NUM_PLAYERS_ADDED;
        this.start_time = String.valueOf(DEFAULT_INT);
        this.end_time = String.valueOf(DEFAULT_INT);
        this.finalStartTime = DEFAULT_INT;
        this.finalEndTime = DEFAULT_INT;
        this.location = new HashMap<String, Double>() {};
        this.creator_id = DEFAULT_INT;
        this.description = "Casual basketball game";
        this.location_notes = "Come around the back and knock on the blue door";
        this.gender = "f";
        this.age_range = new int[] { 20, 30 };
        this.enforced_params = new ENFORCED_PARAMS[] {};
        this.time_created = DEFAULT_INT;
        this.player_restricted = DEFAULT_BOOLEAN;
    }


    //function for time-being
    public GameModel (int game_id, String name, String type, int offsetSkill, int totalPlayersRequired,
                      int totalPlayersAdded, String startTime, String endTime,
                      int finalStartTime, int finalEndTime, HashMap<String, Double> location,
                      int creatorId, String description, String location_notes, String gender,
                      int[] ageRange, ENFORCED_PARAMS[] enforced_params, int time_created, boolean player_restricted) {

        this.game_id = game_id;
        this.name = name;
        this.type = type;
        this.offsetSkill = offsetSkill;
        this.total_players_required = totalPlayersRequired;
        this.total_players_added = totalPlayersAdded;
        this.start_time = startTime;
        this.end_time = endTime;
        this.finalStartTime = finalStartTime;
        this.finalEndTime = finalEndTime;
        this.location = location;
        this.creator_id = creatorId;
        this.description = description;
        this.location_notes = location_notes;
        this.gender = gender;
        this.age_range = ageRange;
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

    public int getOffsetSkill() {
        return offsetSkill;
    }

    public void setOffsetSkill(int offsetSkill) {
        this.offsetSkill = offsetSkill;
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

    public String getStartTime() {
        return start_time;
    }

    public void setStartTime(String start_time) {
        this.start_time = start_time;
    }

    public String getEndTime() {
        return end_time;
    }

    public void setEndTime(String end_time) {
        this.end_time = end_time;
    }

    public int getFinalStartTime() {
        return finalStartTime;
    }

    public void setFinalStartTime(int final_start_time) {
        this.finalStartTime = final_start_time;
    }

    public int getFinalEndTime() {
        return finalEndTime;
    }

    public void setFinalEndTime(int final_end_time) {
        this.finalEndTime = final_end_time;
    }

    public HashMap<String, Double> getLocation() {
        return location;
    }

    public void setLocation(HashMap<String, Double> location) {
        this.location = location;
    }

    public int getCreatorId() {
        return creator_id;
    }

    public void setCreatorId(int creatorId) {
        this.creator_id = creatorId;
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

    public ENFORCED_PARAMS[] getEnforcedParams() {
        return enforced_params;
    }

    public void setEnforcedParams(ENFORCED_PARAMS[] enforced_params) {
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

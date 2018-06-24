package sotifc2017.pickup.api.models;

import java.util.HashMap;

import sotifc2017.pickup.api.enums.ENFORCED_PARAMS;
import sotifc2017.pickup.api.enums.GAME_TYPE;

/**
 * Created by asaafan on 3/8/2018.
 */

public class GameModel {
    private final int DEFAULT_INT = -1;
    private final boolean DEFAULT_BOOLEAN = false;
    private final int START_NUM_PLAYERS_ADDED = 0;

    public int game_id;
    public String name;
    public GAME_TYPE type;
    public int offsetSkill;
    public int total_players_required;
    public int total_players_added;
    public long start_time;
    public long end_time;
    public long duration;
    public HashMap<String, Double> location;
    public int creator_id;
    public String description;
    public String location_notes;
    public String gender;
    public int[] age_range;
    public ENFORCED_PARAMS[] enforced_params;
    public int time_created;
    public boolean player_restricted;
    public int min_skill;
    public int max_skill;

    // Variables holding values for the computation of final results
    public String partialStartTime;
    public String partialEndTime;

    public GameModel () {
        this.game_id = DEFAULT_INT;
        this.name = "My game";
        this.type = GAME_TYPE.both;
        this.offsetSkill = DEFAULT_INT;
        this.total_players_required = DEFAULT_INT;
        this.total_players_added = START_NUM_PLAYERS_ADDED;
        this.start_time = DEFAULT_INT;
        this.end_time = DEFAULT_INT;
        this.duration = DEFAULT_INT;
        this.location = new HashMap<String, Double>() {};
        this.creator_id = DEFAULT_INT;
        this.description = "Casual basketball game";
        this.location_notes = "Come around the back and knock on the blue door";
        this.gender = "f";
        this.age_range = new int[] { 20, 30 };
        this.enforced_params = new ENFORCED_PARAMS[] {};
        this.time_created = DEFAULT_INT;
        this.player_restricted = DEFAULT_BOOLEAN;

        this.partialStartTime = String.valueOf(DEFAULT_INT);
        this.partialEndTime = String.valueOf(DEFAULT_INT);
        this.min_skill = DEFAULT_INT;
        this.max_skill = DEFAULT_INT;
    }


    //function for time-being
    public GameModel (int game_id, String name, GAME_TYPE type, int offsetSkill, int totalPlayersRequired,
                      int totalPlayersAdded, long startTime, long endTime, long duration, HashMap<String, Double> location,
                      int creatorId, String description, String location_notes,
                      ENFORCED_PARAMS[] enforced_params, int time_created, boolean player_restricted,
                      String partialStartTime, String partialEndTime, int min_skill, int max_skill) {

        this.game_id = game_id;
        this.name = name;
        this.type = type;
        this.offsetSkill = offsetSkill;
        this.total_players_required = totalPlayersRequired;
        this.total_players_added = totalPlayersAdded;
        this.start_time = startTime;
        this.end_time = endTime;
        this.duration = duration;
        this.location = location;
        this.creator_id = creatorId;
        this.description = description;
        this.location_notes = location_notes;
        this.enforced_params = enforced_params;
        this.time_created = time_created;
        this.player_restricted = player_restricted;

        this.partialStartTime = partialStartTime;
        this.partialEndTime = partialEndTime;

        this.min_skill = min_skill;
        this.max_skill = max_skill;
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

    public GAME_TYPE getType() {
        return type;
    }

    public void setType(GAME_TYPE type) {
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

    public long getStartTime() {
        return start_time;
    }

    public void setStartTime(long start_time) {
        this.start_time = start_time;
    }

    public long getEndTime() {
        return end_time;
    }

    public void setEndTime(long end_time) {
        this.end_time = end_time;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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

    public boolean isPlayerRestricted() {
        return player_restricted;
    }

    public void setPlayerRestricted(boolean player_restricted) {
        this.player_restricted = player_restricted;
    }

    public String getPartialStartTime() {
        return partialStartTime;
    }

    public void setPartialStartTime(String partialStartTime) {
        this.partialStartTime = partialStartTime;
    }

    public String getPartialEndTime() {
        return partialEndTime;
    }

    public void setPartialEndTime(String partialEndTime) {
        this.partialEndTime = partialEndTime;
    }

    public int getMinSkill() { return min_skill; }

    public int getMaxSkill() {return max_skill;}
}

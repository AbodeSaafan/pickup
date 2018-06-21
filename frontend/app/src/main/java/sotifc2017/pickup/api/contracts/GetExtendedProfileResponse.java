package sotifc2017.pickup.api.contracts;

import sotifc2017.pickup.api.enums.API_GENDER;
import sotifc2017.pickup.api.models.GameModel;

/**
 * Created by rkrishnan on 1/16/2018.
 */

public class GetExtendedProfileResponse {
    public int age;
    public int skilllevel;
    public API_GENDER gender;
    //TODO we need to change this to not a string and send/get location consistently across api
    public String location;
    public float average_review;
    public String username;
    public int games_created;
    public int games_joined;
    public String top_tag;
    public int top_tag_count;
    public GameModel[] recentGames;
}

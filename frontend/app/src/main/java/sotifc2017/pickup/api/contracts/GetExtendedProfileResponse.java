package sotifc2017.pickup.api.contracts;

/**
 * Created by rkrishnan on 1/16/2018.
 */

public class GetExtendedProfileResponse {
    public int age;
    public int skilllevel;
    public String gender;
    //TODO we need to change this to not a string and send/get location consistently across api
    public String location;
    public float average_review;
    public String username;
    public int games_created;
    public int games_joined;
    public String top_tag;
    public int top_tag_count;
}

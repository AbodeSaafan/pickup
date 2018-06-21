package sotifc2017.pickup.api.models;

/**
 * Created by Abode on 4/29/2018.
 */

public class UserModel {
    public int user_id;
    public String username;
    public String fname;
    public Boolean reviewed;

    public UserModel(int user_id, String username, Boolean reviewed, String fname)
    {
        this.user_id = user_id;
        this.username = username;
        this.fname = fname;
        this.reviewed = reviewed;
    }
}

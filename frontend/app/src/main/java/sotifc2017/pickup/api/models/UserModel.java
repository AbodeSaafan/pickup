package sotifc2017.pickup.api.models;

/**
 * Created by Abode on 4/29/2018.
 */

public class UserModel {
    public int user_id;
    public String username;
    public String fname;
    public Boolean ifReviewed;

    public UserModel(int user_id, String user_name, Boolean ifReviewed, String fname)
    {
        this.user_id = user_id;
        this.username = user_name;
        this.fname = fname;
        this.ifReviewed = ifReviewed;
    }
}

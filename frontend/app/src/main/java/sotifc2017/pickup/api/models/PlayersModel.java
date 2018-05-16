package sotifc2017.pickup.api.models;

/**
 * Created by parezina on 5/7/2018.
 */

public class PlayersModel {
    public int user_id;
    public String user_name;
    public Boolean ifReviewed;

    public PlayersModel(int user_id, String user_name, Boolean ifReviewed)
    {
        this.user_id = user_id;
        this.user_name = user_name;
        this.ifReviewed = ifReviewed;
    }
}

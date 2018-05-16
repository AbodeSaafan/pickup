package sotifc2017.pickup.api.contracts;

/**
 * Created by parezina on 5/6/2018.
 */

public class GetUsersRequest {
    private String jwt;
    private String game_id;

    public GetUsersRequest(String jwt, String game_id)
    {
        this.game_id = game_id;
        this.jwt = jwt;
    }

    public static GetUsersRequest CreateUserListRequest(String jwt, int game_id) {
        GetUsersRequest request = new GetUsersRequest(jwt, Integer.toString(game_id));
        return request;
    }
}

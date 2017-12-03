package sotifc2017.pickup.api;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Radhika on 11/27/2017.
 */
/*
public class Friends {
    private static final String SendFriendRequest_ENDPOINT = Utils.BASE_API + "friends";
    private static final String AcceptFriendRequest_ENDPOINT = Utils.BASE_API + "friends/accept";
    private static final String DeleteFriend_ENDPOINT = Utils.BASE_API + "friends/delete";
    private static final String BlockFriend_ENDPOINT = Utils.BASE_API + "friends/block";
    private static final String ListFriends_ENDPOINT = Utils.BASE_API + "friends/listFriends";
    private static final String ListBlockedUsers_ENDPOINT = Utils.BASE_API + "friends/listBlockedUsers";
    private static final String ListFriendRequests_ENDPOINT = Utils.BASE_API + "friends/listFriendRequest";


    public static JsonObjectRequest sendFriend_request(String jwt, int user_id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        HashMap<String, String> params = new HashMap<>();
        params.put("jwt", jwt);
        params.put("userId", user_id);

        JsonObjectRequest SendFriendRequest = new JsonObjectRequest
                (Request.Method.POST, SendFriendRequest_ENDPOINT, new JSONObject(params), responseListener, errorListener);

        return SendFriendRequest;
    }

    public static JsonObjectRequest acceptFriend_request(String jwt, int user_id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){

        JsonObjectRequest acceptFriendRequest = new JsonObjectRequest
                (Request.Method.PUT, AcceptFriendRequest_ENDPOINT+"?jwt="+jwt+"&userId="+user_id, responseListener, errorListener);

        return acceptFriendRequest;
    }

    public static JsonObjectRequest deleteFriend_request(String jwt, int user_id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){

        JsonObjectRequest deleteFriendRequest = new JsonObjectRequest
                (Request.Method.PUT, DeleteFriend_ENDPOINT+"?jwt="+jwt+"&userId="+user_id, responseListener, errorListener);

        return deleteFriendRequest;
    }

    public static JsonObjectRequest blockFriend_request(String jwt, int user_id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){

        JsonObjectRequest blockFriendRequest = new JsonObjectRequest
                (Request.Method.PUT, BlockFriend_ENDPOINT+"?jwt="+jwt+"&userId="+user_id, responseListener, errorListener);

        return blockFriendRequest;
    }

    public static JsonObjectRequest listFriends_request(String jwt, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){

        JsonObjectRequest listFriendRequest = new JsonObjectRequest
                (Request.Method.GET, ListFriends_ENDPOINT+"?jwt="+jwt, responseListener, errorListener);

        return listFriendRequest;
    }

    public static JsonObjectRequest listBlockedFriend_request(String jwt, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){

        JsonObjectRequest listBlockedFriendRequest = new JsonObjectRequest
                (Request.Method.GET, ListBlockedUsers_ENDPOINT+"?jwt="+jwt, responseListener, errorListener);

        return listBlockedFriendRequest;
    }

    public static JsonObjectRequest listFriendRequest_request(String jwt, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){

        JsonObjectRequest listFriendRequest = new JsonObjectRequest
                (Request.Method.GET, ListFriendRequests_ENDPOINT+"?jwt="+jwt, responseListener, errorListener);

        return listFriendRequest;
    }

}
*/
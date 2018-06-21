package sotifc2017.pickup.api;

import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import sotifc2017.pickup.api.contracts.GetUsersRequest;
import sotifc2017.pickup.api.contracts.SimpleJWTRequest;

/**
 * Created by parezina on 5/7/2018.
 */

public class Game {
    private static final String GetUser_ENDPOINT = Utils.BASE_API + "games/getUsers";
    private static final String GameBase_ENDPOINT = Utils.BASE_API + "games/";

    @NonNull
    public static JsonArrayRequest getUsers_request(GetUsersRequest req, Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener) {
        try{
            return new JsonArrayRequest(Request.Method.GET, GetUser_ENDPOINT + Utils.jsonToUrlParam(req), null, responseListener, errorListener);
        }
        catch (Exception e){
            errorListener.onErrorResponse(new VolleyError(e.getMessage()));
            return null;
        }
    }


    public static JsonObjectRequest join_game_request(SimpleJWTRequest req, String gameId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try{
            return new JsonObjectRequest (Request.Method.PUT, GameBase_ENDPOINT + gameId + "/join"+ Utils.jsonToUrlParam(req), new JSONObject(Utils.gson.toJson(req)), responseListener, errorListener);
        }
        catch (Exception e) {
            errorListener.onErrorResponse(new VolleyError(e.getMessage()));
            return null;
        }
    }

    public static JsonObjectRequest leave_game_request(SimpleJWTRequest req, String gameId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try{
            return new JsonObjectRequest (Request.Method.DELETE, GameBase_ENDPOINT + gameId + "/leave"+ Utils.jsonToUrlParam(req), new JSONObject(Utils.gson.toJson(req)), responseListener, errorListener);
        }
        catch (Exception e) {
            errorListener.onErrorResponse(new VolleyError(e.getMessage()));
            return null;
        }
    }


}

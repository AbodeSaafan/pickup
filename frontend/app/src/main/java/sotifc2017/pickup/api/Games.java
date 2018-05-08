package sotifc2017.pickup.api;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import sotifc2017.pickup.api.contracts.CreateGameRequest;

/**
 * Created by Abode on 5/7/2018.
 */

public class Games {
    private static final String createGame_ENDPOINT = Utils.BASE_API + "games";

    public static JsonObjectRequest createGame_request(CreateGameRequest req, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try{
            return new JsonObjectRequest (Request.Method.POST, createGame_ENDPOINT, new JSONObject(Utils.gson.toJson(req)), responseListener, errorListener);
        }
        catch (Exception e){
            errorListener.onErrorResponse(new VolleyError(e.getMessage()));
            return null;
        }
    }
}

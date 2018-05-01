package sotifc2017.pickup.api;

import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import sotifc2017.pickup.api.contracts.GetSearchRequest;

/**
 * Created by Abode on 4/29/2018.
 */

public class Search {
    private static final String Search_ENDPOINT = Utils.BASE_API + "search";

    @NonNull
    public static JsonObjectRequest getSearch_request(GetSearchRequest req, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try{
            return new JsonObjectRequest (Request.Method.GET, Search_ENDPOINT + req.ToUrlParameter(), null, responseListener, errorListener);
        }
        catch (Exception e){
            errorListener.onErrorResponse(new VolleyError(e.getMessage()));
            return null;
        }
    }
}

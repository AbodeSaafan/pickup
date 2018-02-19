package sotifc2017.pickup.api;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import sotifc2017.pickup.api.contracts.GetPrivateProfileRequest;
import sotifc2017.pickup.api.contracts.UpdatePrivateProfileRequest;

/**
 * Created by Abode on 1/21/2018.
 */

public class PrivateProfile {
    private static final String PROFILE_ENDPOINT = Utils.BASE_API + "profile";

    public static JsonObjectRequest get_private_profile_request(GetPrivateProfileRequest req, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try{
            return new JsonObjectRequest (Request.Method.GET, PROFILE_ENDPOINT + Utils.jsonToUrlParam(req), null, responseListener, errorListener);
        }
        catch (Exception e) {
            errorListener.onErrorResponse(new VolleyError(e.getMessage()));
            return null;
        }
    }

    public static JsonObjectRequest update_profile_request(UpdatePrivateProfileRequest req, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try{
            return new JsonObjectRequest (Request.Method.PUT, PROFILE_ENDPOINT, new JSONObject(Utils.gson.toJson(req)), responseListener, errorListener);
        }
        catch (Exception e) {
            errorListener.onErrorResponse(new VolleyError(e.getMessage()));
            return null;
        }
    }
}

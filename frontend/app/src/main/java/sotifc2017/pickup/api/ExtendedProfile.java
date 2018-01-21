package sotifc2017.pickup.api;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import sotifc2017.pickup.api.contracts.GetExtendedProfileRequest;

/**
 * Created by Radhika on 11/27/2017.
 */

public class ExtendedProfile {
    private static final String ExtendedProfile_ENDPOINT = Utils.BASE_API + "extended_profile";

    /*
    public static JsonObjectRequest updateProfile_request(String jwt, int skill_level, Object location, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        HashMap<String, String> params = new HashMap<>();
        params.put("jwt", jwt);
        params.put("skill_level", skill_level);
        params.put("location", location);

        JsonObjectRequest UpdateRequest = new JsonObjectRequest
                (Request.Method.PUT, ExtendedProfile_ENDPOINT, new JSONObject(params), responseListener, errorListener);

        return UpdateRequest;
    }
    */

    public static JsonObjectRequest updateProfile_request(GetExtendedProfileRequest req, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try{
            return new JsonObjectRequest (Request.Method.PUT, ExtendedProfile_ENDPOINT, new JSONObject(Utils.gson.toJson(req)), responseListener, errorListener);
        }
        catch (Exception e){
            errorListener.onErrorResponse(new VolleyError(e.getMessage()));
            return null;
        }
    }

    public static JsonObjectRequest getProfile_request(String jwt, String userID, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){

        JsonObjectRequest GetRequest = new JsonObjectRequest
                (Request.Method.GET, ExtendedProfile_ENDPOINT+"?jwt="+jwt+"&userID="+userID, null, responseListener, errorListener);

        return GetRequest;
    }

}

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

public class ExtendedProfile {
    private static final String ExtendedProfile_ENDPOINT = Utils.BASE_API + "extended_profile";

    public static JsonObjectRequest updateProfile_request(String jwt, int skill_level, Object location, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        HashMap<String, String> params = new HashMap<>();
        params.put("jwt", jwt);
        params.put("skill_level", skill_level);
        params.put("location", location);

        JsonObjectRequest UpdateRequest = new JsonObjectRequest
                (Request.Method.PUT, ExtendedProfile_ENDPOINT, new JSONObject(params), responseListener, errorListener);

        return UpdateRequest;
    }

    public static JsonObjectRequest getProfile_request(String jwt, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){

        JsonObjectRequest GetRequest = new JsonObjectRequest
                (Request.Method.GET, ExtendedProfile_ENDPOINT+"?jwt="+jwt, responseListener, errorListener);

        return GetRequest;
    }

}

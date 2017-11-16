package sotifc2017.pickup.api;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by Abode on 11/15/2017.
 */

public class Authentication {
    private static final String LOGIN_ENDPOINT = Utils.BASE_API + "login";

    public static JsonObjectRequest login_request(String email, String password, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        JsonObjectRequest loginRequest = new JsonObjectRequest
                (Request.Method.POST, LOGIN_ENDPOINT, new JSONObject(params), responseListener, errorListener);

        return loginRequest;
    }

}

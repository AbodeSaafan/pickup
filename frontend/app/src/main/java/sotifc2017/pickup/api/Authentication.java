package sotifc2017.pickup.api;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Abode on 11/15/2017.
 */

public class Authentication {
    private static final String LOGIN_ENDPOINT = Utils.BASE_API + "login";
    private static final String REGISTER_ENDPOINT = Utils.BASE_API + "register";

    public static JsonObjectRequest login_request(String email, String password, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        JsonObjectRequest loginRequest = new JsonObjectRequest
                (Request.Method.POST, LOGIN_ENDPOINT, new JSONObject(params), responseListener, errorListener);

        return loginRequest;
    }

    public static JsonObjectRequest register_request(String username, String fname, String lname, String gender, Date dob, String email, String password, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("username", username);
        params.put("fname", fname);
        params.put("lname", lname);
        params.put("gender", gender);
        params.put("dob", dob.toString());

        JsonObjectRequest registerRequest = new JsonObjectRequest
                (Request.Method.POST, REGISTER_ENDPOINT, new JSONObject(params), responseListener, errorListener);

        return registerRequest;
    }

}

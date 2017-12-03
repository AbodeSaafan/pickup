package sotifc2017.pickup.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import sotifc2017.pickup.api.contracts.LoginRequest;
import sotifc2017.pickup.api.contracts.RegisterRequest;

/**
 * Created by Abode on 11/15/2017.
 */

public class Authentication {
    private static final String LOGIN_ENDPOINT = Utils.BASE_API + "login";
    private static final String REGISTER_ENDPOINT = Utils.BASE_API + "register";
    private static final String REFRESH_ENDPOINT = Utils.BASE_API + "refresh";

    private static final int CALL_DELAY = 5; // 5 second delay so less calls fail due to expired jwt

    public static JsonObjectRequest login_request(LoginRequest req, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try{
            return new JsonObjectRequest (Request.Method.POST, LOGIN_ENDPOINT, new JSONObject(Utils.gson.toJson(req)), responseListener, errorListener);
        }
        catch (Exception e){
            errorListener.onErrorResponse(new VolleyError(e.getMessage()));
            return null;
        }
    }

    public static JsonObjectRequest register_request(RegisterRequest req, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try{
            return new JsonObjectRequest (Request.Method.POST, REGISTER_ENDPOINT, new JSONObject(Utils.gson.toJson(req)), responseListener, errorListener);
        }
        catch (Exception e) {
            errorListener.onErrorResponse(new VolleyError(e.getMessage()));
            return null;
        }
    }

    public static String getJwt(Context ctx) throws Exception{
        SharedPreferences prefs = ctx.getSharedPreferences(
                "sotifc2017.pickup", Context.MODE_PRIVATE);

        String jwt_tok = prefs.getString("jwt", null);
        String refresh_tok = prefs.getString("refresh", null);

        if (jwt_tok != null && !jwt_tok.isEmpty() && refresh_tok != null && !refresh_tok.isEmpty()) {
            //check date and return if good, move on if bad
            int expiry = prefs.getInt("jwt_expiry", 0);
            if (expiry >= (System.currentTimeMillis() / 1000) + CALL_DELAY) {
                return jwt_tok;
            }
        } else {
            //never had a jwt or refresh so "sign out"
            throw new Exception("Bad JWT");
        }
        // request new one
        RequestFuture<JSONObject> requestFuture= RequestFuture.newFuture();
        Utils.getInstance(ctx).addToRequestQueue(jwt_request(refresh_tok, jwt_tok, requestFuture, requestFuture));
        try {
            JSONObject response = requestFuture.get(10, TimeUnit.SECONDS);
            String jwt = response.getString("token");
            saveJwt(ctx, jwt);
            return jwt;

        } catch (Exception e) {
            // idek what to do here, probably check if we have internet access, display correct err
            throw new Exception("Could not get a new JWT");
        }
    }

    public static void saveJwt(Context ctx, String tok){
        SharedPreferences prefs = ctx.getSharedPreferences(
                "sotifc2017.pickup", Context.MODE_PRIVATE);
        prefs.edit().putString("jwt", tok);
    }

    public static void saveRefresh(Context ctx, String refresh){
        SharedPreferences prefs = ctx.getSharedPreferences(
                "sotifc2017.pickup", Context.MODE_PRIVATE);
        prefs.edit().putString("refresh", refresh);
    }

    public static String getRefresh(Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences(
                "sotifc2017.pickup", Context.MODE_PRIVATE);

        String ref = prefs.getString("refresh", null);

        if(ref != null && !ref.isEmpty()){
            return ref;
        } else {
            // Log out as we have no refresh token
            return ""; // making error silent
        }
    }

    private static JsonObjectRequest jwt_request(String refresh, String expired_jwt, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        String params = "?jwt=" + expired_jwt + "&refresh=" + refresh;

        JsonObjectRequest loginRequest = new JsonObjectRequest
                (Request.Method.GET, REFRESH_ENDPOINT + params, null, responseListener, errorListener);

        return loginRequest;
    }

}
package sotifc2017.pickup.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
    public static final String SHARED_PREF_KEY = "sotifc2017.pickup";

    private static final String LOGIN_ENDPOINT = Utils.BASE_API + "login";
    private static final String REGISTER_ENDPOINT = Utils.BASE_API + "register";
    private static final String REFRESH_ENDPOINT = Utils.BASE_API + "refresh";

    private static final long JWT_LIFETIME = 1000 * 60 * 14;
    private static final long JWT_BUFFER = 1000 * 60 * 2;

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

    public static String getJwt(Activity activity) throws Exception{
        SharedPreferences prefs = activity.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);

        String jwt_tok = prefs.getString("jwt", null);
        String refresh_tok = prefs.getString("refresh", null);

        if (jwt_tok != null && !jwt_tok.isEmpty() && refresh_tok != null && !refresh_tok.isEmpty()) {
            //check date and return if good, move on if bad
            long expiry = prefs.getLong("jwt_expiry", Long.MIN_VALUE);
            // Expired or soon to be
            if (expiry < System.currentTimeMillis() + JWT_BUFFER) {
                refreshJwt(activity);
            }

            return jwt_tok;
        }
        //never had a jwt or refresh so "sign out"
        throw new Exception("Bad JWT");
    }

    private static Response.Listener<JSONObject> successful_refresh(final Activity activity){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String jwt = response.getString("token");
                    saveJwt(activity, jwt);
                }
                catch (Exception e){
                    Log.e("jwt", "refresh api error");
                }

            }
        };
    }

    private static Response.ErrorListener error_refresh =  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                    Log.e("jwt", errorJSON.getString("error"));
                }
                catch (Exception e) {
                    Log.e("jwt", e.getMessage());
                }
            }
    };

    public static void refreshJwt(Activity activity){
        SharedPreferences prefs = activity.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);

        String jwt_tok = prefs.getString("jwt", null);
        String refresh_tok = prefs.getString("refresh", null);

        // request new one
        Utils.getInstance(activity).getRequestQueue(activity).add(jwt_request(refresh_tok, jwt_tok, successful_refresh(activity), error_refresh));
    }

    public static void saveJwt(Activity activity, String tok){
        SharedPreferences prefs = activity.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        prefs.edit().putString("jwt", tok).apply();
        // Current time + 14 minutes converted into milliseconds
        prefs.edit().putLong("jwt_expiry", System.currentTimeMillis() + JWT_LIFETIME).apply();
    }

    public static void saveRefresh(Activity activity, String refresh){
        SharedPreferences prefs = activity.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        prefs.edit().putString("refresh", refresh).apply();
    }

    public static String getRefresh(Activity activity){
        SharedPreferences prefs = activity.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);

        String ref = prefs.getString("refresh", null);

        if(ref != null && !ref.isEmpty()){
            return ref;
        } else {
            // Log out as we have no refresh token
            return ""; // making error silent
        }
    }

    public static void saveUserId(Activity activity, int userId){
        SharedPreferences prefs = activity.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);

        prefs.edit().putInt("userId", userId).apply();
    }

    public static int getUserId(Activity activity){
        SharedPreferences prefs = activity.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);

        return prefs.getInt("userId", -1);
    }

    public static void logout(Activity activity){
        activity.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE).edit().clear().commit();
    }

    private static JsonObjectRequest jwt_request(String refresh, String expired_jwt, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        String params = "?jwt=" + expired_jwt + "&refresh=" + refresh;

        JsonObjectRequest loginRequest = new JsonObjectRequest
                (Request.Method.GET, REFRESH_ENDPOINT + params, null, responseListener, errorListener);

        return loginRequest;
    }

}
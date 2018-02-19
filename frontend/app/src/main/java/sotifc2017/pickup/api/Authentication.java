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

import sotifc2017.pickup.api.contracts.ChangePasswordRequest;
import sotifc2017.pickup.api.contracts.LoginRequest;
import sotifc2017.pickup.api.contracts.RegisterRequest;

import static sotifc2017.pickup.api.Authentication.JWT_BUFFER;
import static sotifc2017.pickup.api.Authentication.SHARED_PREF_KEY;
import static sotifc2017.pickup.api.Authentication.jwt_request;

/**
 * Created by Abode on 11/15/2017.
 */

public class Authentication {
    public static final String SHARED_PREF_KEY = "sotifc2017.pickup";

    private static final String LOGIN_ENDPOINT = Utils.BASE_API + "login";
    private static final String REGISTER_ENDPOINT = Utils.BASE_API + "register";
    private static final String REFRESH_ENDPOINT = Utils.BASE_API + "refresh";
    private static final String CHANGE_PASSWORD_ENDPOINT = Utils.BASE_API + "changePassword";

    private static final long JWT_LIFETIME = 1000 * 60 * 14;
    protected static final long JWT_BUFFER = 1000 * 60 * 1;


    public static JsonObjectRequest login_request(LoginRequest req, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try {
            return new JsonObjectRequest(Request.Method.POST, LOGIN_ENDPOINT, new JSONObject(Utils.gson.toJson(req)), responseListener, errorListener);
        } catch (Exception e) {
            errorListener.onErrorResponse(new VolleyError(e.getMessage()));
            return null;
        }
    }

    public static JsonObjectRequest register_request(RegisterRequest req, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try {
            return new JsonObjectRequest(Request.Method.POST, REGISTER_ENDPOINT, new JSONObject(Utils.gson.toJson(req)), responseListener, errorListener);
        } catch (Exception e) {
            errorListener.onErrorResponse(new VolleyError(e.getMessage()));
            return null;
        }
    }

    public static void saveJwt(Context ctx, String tok) {
        SharedPreferences prefs = ctx.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        prefs.edit().putString("jwt", tok).apply();
        // Current time + 14 minutes converted into milliseconds
        prefs.edit().putLong("jwt_expiry", System.currentTimeMillis() + JWT_LIFETIME).apply();
    }

    public static void saveRefresh(Context ctx, String refresh) {
        SharedPreferences prefs = ctx.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        prefs.edit().putString("refresh", refresh).apply();
    }

    public static String getRefresh(Context ctx) {
        SharedPreferences prefs = ctx.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);

        String ref = prefs.getString("refresh", null);

        if (ref != null && !ref.isEmpty()) {
            return ref;
        } else {
            // Log out as we have no refresh token
            return ""; // making jwtFailure silent
        }
    }

    public static void saveUserId(Context ctx, int userId) {
        SharedPreferences prefs = ctx.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);

        prefs.edit().putInt("userId", userId).apply();
    }

    public static int getUserId(Context ctx) {
        SharedPreferences prefs = ctx.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);

        return prefs.getInt("userId", -1);
    }

    public static void logout(Context ctx) {
        ctx.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE).edit().clear().commit();
    }

    protected static JsonObjectRequest jwt_request(String refresh, String expired_jwt, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        String params = "?jwt=" + expired_jwt + "&refresh=" + refresh;

        JsonObjectRequest loginRequest = new JsonObjectRequest
                (Request.Method.GET, REFRESH_ENDPOINT + params, null, responseListener, errorListener);

        return loginRequest;
    }

    public static JsonObjectRequest change_password_request(ChangePasswordRequest req, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try {
            return new JsonObjectRequest(Request.Method.PUT, CHANGE_PASSWORD_ENDPOINT, new JSONObject(Utils.gson.toJson(req)), responseListener, errorListener);
        } catch (Exception e) {
            errorListener.onErrorResponse(new VolleyError(e.getMessage()));
            return null;
        }
    }
}
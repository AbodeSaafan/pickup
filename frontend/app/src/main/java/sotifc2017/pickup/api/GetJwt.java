package sotifc2017.pickup.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import static sotifc2017.pickup.api.Authentication.JWT_BUFFER;
import static sotifc2017.pickup.api.Authentication.SHARED_PREF_KEY;
import static sotifc2017.pickup.api.Authentication.jwt_request;

/**
 * Created by Abode on 2/2/2018.
 */
public class GetJwt extends AsyncTask<Context, Integer, Void> {
    public interface Callback {
        void jwtSuccess(String jwt);
        void jwtFailure(JwtOutcome outcome);
    }

    private final Callback callback;

    public GetJwt(Callback callback) {
        this.callback = callback;
    }

    protected Void doInBackground(Context... contexts) {
        for(Context ctx : contexts){
            SharedPreferences prefs = ctx.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);

            String jwt_tok = prefs.getString("jwt", null);
            String refresh_tok = prefs.getString("refresh", null);

            if (jwt_tok != null && !jwt_tok.isEmpty() && refresh_tok != null && !refresh_tok.isEmpty()) {
                //check date and return if good, move on if bad
                long expiry = prefs.getLong("jwt_expiry", Long.MIN_VALUE);
                // Soon to be expired or expired
                if (expiry < System.currentTimeMillis() + JWT_BUFFER) {
                    RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
                    Utils.getInstance(ctx).getRequestQueue(ctx).add(jwt_request(refresh_tok, jwt_tok, requestFuture, requestFuture));
                    try {
                        JSONObject response = requestFuture.get(10, TimeUnit.SECONDS);
                        String jwt = response.getString("token");
                        Authentication.saveJwt(ctx, jwt);
                        callback.jwtSuccess(jwt); return null;

                    } catch (Exception e) {
                        VolleyError err = (VolleyError) e.getCause();
                        if(err != null & err.networkResponse != null && err.networkResponse.statusCode == HttpURLConnection.HTTP_BAD_REQUEST){
                            Log.e("jwt", new String(err.networkResponse.data));
                            callback.jwtFailure(JwtOutcome.BadJwtRetrieval); return null;
                        }
                        else{
                            callback.jwtFailure(JwtOutcome.ServerFault); return null;
                        }
                    }
                }
                else {
                    callback.jwtSuccess(jwt_tok); return null;
                }
            }
            else {
                //never had a jwt or refresh so "sign out"
                callback.jwtFailure(JwtOutcome.NoRefresh); return null;
            }
        }
        return null;
    }

    public enum JwtOutcome {
        NoRefresh,          //No refresh token in app (not signed in)
        BadJwtRetrieval,    //Attempt to refresh JWT has failed (400)
        ServerFault         //Server fault while refreshing JWT
    }
}
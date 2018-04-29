package sotifc2017.pickup.api;

import android.content.Context;
import android.net.Uri;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Created by Abode on 11/14/2017.
 */
public class Utils {
    private static Utils mInstance;
    private RequestQueue mRequestQueue;
    public static final String BASE_API = "https://pickup-app-api.herokuapp.com/api/";
    public static final Gson gson = new Gson();

    private Utils(Context context) {
        mRequestQueue = getRequestQueue(context);
    }

    public static synchronized Utils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Utils(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(Context mCtx) {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public static String jsonToUrlParam(Object contractObj){
        HashMap<String,Object> map = gson.fromJson(gson.toJson(contractObj), HashMap.class);

        Uri.Builder builder = new Uri.Builder();

        for (Entry<String, Object> entry : map.entrySet())
        {
            builder.appendQueryParameter(entry.getKey(), entry.getValue().toString());
        }

        return builder.toString();
    }
}
package sotifc2017.pickup.api;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;


/**
 * Created by Abode on 11/14/2017.
 */
public class Utils {
    private static Utils mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    public static final String BASE_API = "https://pickup-app-api.herokuapp.com/api/";

    private Utils(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized Utils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Utils(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
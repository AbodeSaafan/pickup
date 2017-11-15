package sotifc2017.pickup;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;


/**
 * Created by Abode on 11/14/2017.
 */
public class HttpUtils {
    private static HttpUtils mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    private static final String BASE_API = "https://pickup-app-api.herokuapp.com/api/";
    public static final String LOGIN_ENDPOINT = BASE_API + "login";

    private HttpUtils(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized HttpUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HttpUtils(context);
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
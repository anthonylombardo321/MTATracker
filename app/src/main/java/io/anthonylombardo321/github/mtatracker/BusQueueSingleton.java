package io.anthonylombardo321.github.mtatracker;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

//Java Class is used to limit the Volley Request Queue to one instance for app efficiency
public class BusQueueSingleton {
    private static BusQueueSingleton instance;
    private RequestQueue requestQueue;
    private static Context context;

    private BusQueueSingleton(Context context){
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized BusQueueSingleton getInstance(Context context){
        if(instance == null){
            instance = new BusQueueSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }
}

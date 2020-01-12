package com.example.healtherar;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Consultor {
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonRequest;
    private final JSONObject[] y;
    private String url = "http://192.168.1.4:8080/profile/withUserToken";
    protected Context ctx;
    private String token;
    public Consultor(String token, Context ctx){
        this.token = token;
        this.ctx = ctx;
        y = new JSONObject[1];
    }
    public void sendRequest(String userToken) {
        HttpsTrustManager.allowAllSSL();
        final HashMap<String, String>[] x = new HashMap[]{new HashMap<String, String>()};
        x[0].put("userToken", userToken);
        Log.i("IMAGERESPONSE",userToken);
        mRequestQueue = Volley.newRequestQueue(this.ctx);
        mJsonRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(x[0]),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("INFORMATION",response.toString());
                        try {
                            y[0] = new JSONObject(response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("INFORMATION", error.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map <String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+token);
                Log.e("INFORMATION", params.toString());
                return params;
            }
        };
        mRequestQueue.add(mJsonRequest);
    }

    public JSONObject[] getY() {
        return y;
    }
}

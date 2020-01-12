package com.example.healtherar;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class PhotoSender {
    private byte[] imageToSend;
    private String url;
    private final String[] ans;
    private RequestQueue mRequestQueue;
    protected Context ctx;
    public PhotoSender(byte[] x , String i, Context ctx){
        this.imageToSend = x;
        this.url = i;
        this.ctx = ctx;
        ans = new String[1];
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendImage(){
        mRequestQueue = Volley.newRequestQueue(this.ctx);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


        String encodedImgae =  Base64.getEncoder().encodeToString(this.imageToSend);
        Log.i("IMGSEND", String.valueOf(byteArrayOutputStream.size()));
        JSONObject jsonObject = new JSONObject();
        try{
            String imgName = "curr";
            jsonObject.put("name", imgName);
            jsonObject.put("image", encodedImgae);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, this.url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("aaaa", response.toString());
                try {
                    ans[0] = (String) response.get("UserToken");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Error","Error :" + error.toString());
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }
    public String getAsn(){
        return (this.ans[0]);
    }
}

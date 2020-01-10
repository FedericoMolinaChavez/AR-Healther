package com.example.healtherar;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

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
    private Bitmap imageToSend;
    private String url;
    private RequestQueue mRequestQueue;
    protected Context ctx;
    public PhotoSender(Bitmap x , String i, Context ctx){
        this.imageToSend = x;
        this.url = i;
        this.ctx = ctx;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendImage(){
        mRequestQueue = Volley.newRequestQueue(this.ctx);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        this.imageToSend.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        String encodedImgae =  Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
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
                Log.e("aaaa", jsonObject.toString());
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Error","Error :" + error.toString());
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }
}

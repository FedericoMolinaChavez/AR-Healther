package com.example.healtherar;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

public class UserLogin implements Serializable {
   private RequestQueue mRequestQueue;
   private JsonObjectRequest mJsonRequest;
   private String url = "https://login.keepsafeco.org/register/login";
   protected Context ctx;
   private final JSONObject[] y;
   public UserLogin(Context ctx){
       this.ctx = ctx;
       y = new JSONObject[1];
   }
   public JSONObject sendRequest(String user, String password){
       HttpsTrustManager.allowAllSSL();
       final HashMap<String, String>[] x = new HashMap[]{new HashMap<String, String>()};
       x[0].put("email", user);
       x[0].put("password", password);
       Log.i("Error", x[0].toString());
       mRequestQueue = Volley.newRequestQueue(this.ctx);
       mJsonRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(x[0]),
           new Response.Listener<JSONObject>(){
           @Override
           public void onResponse(JSONObject response) {
               Log.i("Error", response.toString());
               try {
                   y[0] = new JSONObject(response.toString());
                    y[0] = new JSONObject(y[0].getString("user"));
                    if(y[0] != null){
                        Intent goTo = new Intent(ctx, Augmented_Faces.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(goTo);
                    }
               } catch (JSONException e) {
                   e.printStackTrace();
               }

           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {

               Log.i("Error","Error :" + error.toString());
           }
       });
       mRequestQueue.add(mJsonRequest);
       return y[0];
   }

   public Boolean consultJson(){
       if(y[0] != null){
           return true;
       }
       else{
           return false;
       }
   }


}

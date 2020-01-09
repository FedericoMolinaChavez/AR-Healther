package com.example.healtherar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button SignIn = (Button) findViewById(R.id.button);
        EditText userName = findViewById(R.id.UserText);
        EditText passwordText = findViewById(R.id.PasswordText);
        SignIn.setOnClickListener(new View.OnClickListener(){
            private JSONObject userLogged;

            public void onClick(View v){
                UserLogin log = new UserLogin(getApplicationContext());

                this.userLogged = log.sendRequest(userName.getText().toString().trim(),passwordText.getText().toString().trim());
                if(this.userLogged != null){
                    Log.i("Error", "works");
                }else{
                    Log.i("Error", "not");
                }

            }
        });


    }

}

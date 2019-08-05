package com.babbangona.accesscontrol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.util.HashMap;

public class Main2Activity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH =   1000;
    private         final int SPLASH_DISPLAY_LENGTH_2 = 2000;
    private final int SPLASH_DISPLAY_LENGTH_3 = 3000;

    String login_status;
    Transition transition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

       try {
           SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
           HashMap<String, String> user = sessionManagement.getUserDetails();
           login_status = user.get(SessionManagement.KEY_LOGIN);

           if(login_status.equalsIgnoreCase("true")){

               Intent intent = new Intent(getApplicationContext(),ApplicationDetails.class);
               startActivity(intent);
           }



           @SuppressLint("StaticFieldLeak") SyncData.SyncVersion syncAppStatus = new SyncData.SyncVersion(getApplicationContext()){

               @Override
               protected void onPostExecute(String s){

                   Log.d("--H--E",s);


               }
           };

           syncAppStatus.execute();

       }catch(Exception e){

       }
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                   startActivity(new Intent(getApplicationContext(),MainActivity.class));


            }
        }, 3000);
    }
}

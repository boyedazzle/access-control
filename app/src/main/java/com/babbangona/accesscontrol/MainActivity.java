package com.babbangona.accesscontrol;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.transition.Transition;
import android.util.Log;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private final int SPLASH_DISPLAY_LENGTH_2 = 2000;
    private final int SPLASH_DISPLAY_LENGTH_3 = 3000;

    String login_status;
    String staff_id;
    String staff_template;
    Transition transition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        // try {
        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = sessionManagement.getUserDetails();
        login_status = user.get(SessionManagement.KEY_LOGIN);
        staff_template = user.get(SessionManagement.KEY_TEMPLATE).split("___")[1];
        staff_id = user.get(SessionManagement.KEY_STAFF_ID);


        Log.d("MUFFASSAA", staff_id + " " + staff_template + " " + staff_id.length() + " " + staff_template.length());


        if (login_status.matches("true")) {

            Intent intent = new Intent(getApplicationContext(), ApplicationDetails.class);
            startActivity(intent);
        } else if (staff_id.matches(staff_template)) {
            startActivity(new Intent(getApplicationContext(), ResumeActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), Main2Activity.class));
        }




     /*  }catch(Exception e){
           Log.d("--H--E",e.toString());
       }
*/
    }
}

package com.babbangona.accesscontrol;



import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.babbangona.bgfr.BGFRMode;
import com.babbangona.bgfr.CustomLuxandActivity;

import java.util.HashMap;


public class    CaptureActivity extends CustomLuxandActivity {

    boolean auth = false;
    @Override
    public Boolean setDetectFakeFaces() {
        //SET TO TRUE WHEN WE ARE READY TO DETECT FAKE FACES
        return false;
    }

    @Override
    public Long setTimer() {
        //SET TO WHATEVER COUNT DOWN TIME YOU WANT
        return new Long(15000);

    }

    @Override
    public Boolean setKeepFaces() {
        //SET TO TRUE FOR THE INCREASED LUXAND TRACKER SIZE
        return false;
    }


    @Override
    public void TimerFinished() {
        //IF NO FACE FOUND
        //TODO: WHAT YOU WANT TO DO WHEN NO FACE FOUND AND TIMER TIMES OUT
        if (!getBGFR_FACEFOUND()){
            showErrorAndClose("No face found, retry again or type password");

        }
        else
        {
            showErrorAndClose("No match");
          /*  Intent i = new Intent(getApplicationContext(),ResumeActivity.class);
            startActivity(i);*/
        }



    }

    @Override
    public void MyFlow() {

            LoadTracker(getTemplate(), BGFRMode.AUTHENTICATE );

    }

    @Override
    public void Authenticated() {
        StopTimer();
        //move
        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        sessionManagement.SaveLoginStatus("true");

        Intent intent = new Intent(getApplicationContext(),ApplicationDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);



    }

    @Override
    public void TrackerSaved() {

    }



  @Override
  public String buttonText(){
      return " START VERIFICATION";
  }

    @Override
    public String headerText(){
        return "VERIFICATION";
    }

    public String getTemplate(){
        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        HashMap<String,String> user = sessionManagement.getUserDetails();
        String template = user.get(SessionManagement.KEY_TEMPLATE).split("___")[0];

        return template;
    }


    @Override
    public void onBackPressed(){
        Toast.makeText(this, "Click the back button above", Toast.LENGTH_SHORT).show();
    }



}

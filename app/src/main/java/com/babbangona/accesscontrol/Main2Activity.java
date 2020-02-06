package com.babbangona.accesscontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.cardview.widget.CardView;

public class Main2Activity extends Activity {

    AutoCompleteTextView username;
    EditText password;
    List db_username;
    ControlDB controlDB;
    ArrayList<String> rodent;
    SessionManagement sessionManagement;
    String login_status;
    private JobScheduler jobScheduler;
    private JobInfo jobInfo;
    ComponentName componentName;
    HashMap map3;
    private static final int JOB_ID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        username = (AutoCompleteTextView) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        controlDB = ControlDB.getInstance(this);
        controlDB.open();
        db_username = controlDB.get_db_username();
        controlDB.close();

        rodent = new ArrayList<>();
        for (int i = 0; i < db_username.size(); i++) {
            String x = (String) db_username.get(i);
            // Log.d("KKEEYY", String.valueOf(x));
            x = x.replaceAll(" ", "_");
            //  Log.d("KKEEYY", String.valueOf(x));
            rodent.add(x);


        }
        Log.d("KKEEYY", String.valueOf(rodent));

        try {


            String URL = "content://com.babbangona.bgrecruitment/tgl";
            Uri students = Uri.parse(URL);
            map3 = new HashMap<String, String>();

            Map<String, String> wordList;
            try {


                try {
                    Cursor cursor = managedQuery(students, null, null, null, "_id DESC");
                    if (cursor.moveToFirst()) {
                        HashMap<String, String> map;
                        do {
                            map = new HashMap<String, String>();
                            map.put("id", cursor.getString(0));
                            map.put("ik_number", cursor.getString(1));
                            map.put("tgl_name", cursor.getString(2));
                            map.put("qus1", cursor.getString(3));
                            map.put("qus2", cursor.getString(4));
                            map.put("qus3", cursor.getString(5));
                            map.put("qus4", cursor.getString(6));
                            map.put("qus5", cursor.getString(7));
                            map.put("qus6", cursor.getString(8));
                            map.put("qus7", cursor.getString(9));
                            map.put("qus8", cursor.getString(10));
                            map.put("qus9", cursor.getString(11));
                            map.put("qus10", cursor.getString(12));
                            map.put("qus11", cursor.getString(13));
                            map.put("qus12", cursor.getString(14));
                            map.put("score", cursor.getString(15));
                            map.put("interviewer", cursor.getString(16));
                            map.put("date", cursor.getString(17));
                            map.put("status", cursor.getString(18));
                            map.put("village", cursor.getString(19));
                            map.put("amik", cursor.getString(20));
                            map.put("tfm_date", cursor.getString(21));
                            map.put("tfm_time", cursor.getString(22));
                            map3.putAll(map);
                            break;
                        } while (cursor.moveToNext());

                    }
                } catch (Exception ex) {
                    System.out.println("The ID must be a number" + ex);
                }
            } catch (Exception e) {
                System.out.println("View Record  " + e);
            }

            Gson gson = new GsonBuilder().create();
            //Use GSON to serialize Array List to JSON


        } catch (Exception e) {
            Log.d("CONTENT___PROVIDER", String.valueOf(e));
        }

        Log.d("CONTENT___PROVIDER", String.valueOf(map3));

        ArrayAdapter name = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, db_username);

        username.setAdapter(name);
        username.setThreshold(1);


        componentName = new ComponentName(this, BackgroundSync.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
            builder.setMinimumLatency(1 * 60 * 1000);

            //builder.setTriggerContentMaxDelay(100000);
            builder.setPersisted(true);
            jobInfo = builder.build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);

        } else {

            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
            builder.setPeriodic(1 * 60 * 1000);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setPersisted(true);
            jobInfo = builder.build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);

        }


    }

    public void SignIn(View view) {
        String user_name = username.getText().toString();
        String pass_word = password.getText().toString();

        if (user_name.matches("")) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
        } else if (pass_word.matches("")) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else {
            controlDB.open();


            String[] user = user_name.split(" ");
            // Toast.makeText(this, user.length+"", Toast.LENGTH_SHORT).show();
            Log.d("ERROR___APP", user.length + "");


            int x = user.length - 1;
            String staffid = user[x];
            String staffname1 = user[0];
            String staffname2 = user[1];


            int controller = controlDB.login_controller(staffid, pass_word);
            if (controller > 0) {
                login_status = "true";

                sessionManagement = new SessionManagement(getApplicationContext());
                sessionManagement.CreateLoginSession(staffname1 + " " + staffname2, staffid, login_status);

                Intent intent = new Intent(getApplicationContext(), ApplicationDetails.class);
                startActivity(intent);

            } else {
                Toast.makeText(this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            componentName = new ComponentName(this, BackgroundSync.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
            builder.setMinimumLatency(1 * 60 * 1000);

            //builder.setTriggerContentMaxDelay(100000);
            builder.setPersisted(true);
            jobInfo = builder.build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);
            // Toast.makeText(this, "Job Scheduled", Toast.LENGTH_SHORT).show();

        } else {

            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
            builder.setPeriodic(1 * 60 * 1000);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setPersisted(true);
            jobInfo = builder.build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);

        }
    }

    public void UpdateList(View view) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading new users \nPlease wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        @SuppressLint("StaticFieldLeak") SyncData.StaffList downloadApplication = new SyncData.StaffList(getApplicationContext()) {

            @Override
            protected void onPostExecute(String s) {
                progressDialog.dismiss();
                //Toast.makeText(mss_page.this, s, Toast.LENGTH_SHORT).show();
                if (s != null && s.trim().equalsIgnoreCase("done")) {


                    Toast.makeText(context, "New Users Added", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                    startActivity(intent);

                } else if (s != null && s.trim().equalsIgnoreCase("failed")) {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();

                } else if (s != null && s.trim().equalsIgnoreCase("updated")) {
                    Toast.makeText(context, "Staff List is up to date", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("ACTIVITY_SYNC", s);
                    Toast.makeText(context, "Error: Couldn't communicate with the online database", Toast.LENGTH_SHORT).show();

                }
            }
        };


        downloadApplication.execute();

    }


    public void IdSignIn(View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        // ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.id_signin, null);
        dialogBuilder.setView(dialogView);


        final EditText username = dialogView.findViewById(R.id.username);
        final EditText password = dialogView.findViewById(R.id.password);

        final CardView submit = dialogView.findViewById(R.id.crdvSignIn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().matches(""))
                    username.setError("Enter last 5 digits of your staff id");
                else if (password.getText().toString().matches(""))
                    password.setError("Enter your password");
                else if (username.getText().toString().length() != 5)
                    username.setError("Ensure you enter last 5 digits of your staff id");
                else {

                    ControlDB controlDB = ControlDB.getInstance(getApplicationContext());
                    controlDB.open();
                    String getStaffId = username.getText().toString().trim();
                    String getPassword = password.getText().toString().trim();

                    String name = controlDB.staffid_login_controller("T-10000000000" + getStaffId, getPassword);

                    if (name.trim().equalsIgnoreCase("no_user_found")) {
                        Toast.makeText(Main2Activity.this, "Wrong login credentials", Toast.LENGTH_SHORT).show();
                    } else {

                        login_status = "true";

                        sessionManagement = new SessionManagement(getApplicationContext());
                        sessionManagement.CreateLoginSession(name, "T-10000000000" + getStaffId, login_status);

                        Intent intent = new Intent(getApplicationContext(), ApplicationDetails.class);
                        startActivity(intent);

                    }
                }


            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}



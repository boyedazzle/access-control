package com.babbangona.accesscontrol;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationDetails extends Activity {

    RecyclerView recyclerView;
    List<ApplicationClass> productList;
    SessionManagement sessionManagement;
    ControlDB controlDb_;
    ArrayList map;
    TextView staff_info, tvStaffId;
    TextView access_version;
    String package_name;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_details);

        access_version = findViewById(R.id.access_control_version);

        access_version.setText("");
        access_version.setText(access_version.getText() + "Access Control v" + BuildConfig.VERSION_NAME);

        recyclerView = (RecyclerView) findViewById(R.id.recylcerView2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        sessionManagement = new SessionManagement(getApplicationContext());
        staff_info = (TextView) findViewById(R.id.staff_info);
        tvStaffId = findViewById(R.id.staff_id);


        HashMap<String, String> user = sessionManagement.getUserDetails();
        String staff_id = user.get(SessionManagement.KEY_STAFF_ID);
        String staff_name = user.get(SessionManagement.KEY_STAFF_NAME);


        if (checkVersion().length() > 0) sendNotification(checkVersion(), "Access Control", 1);


        if (!checkPermission()) {
            requestPermission();
        }

        try {
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            package_name = (String) b.get("package_name");

            controlDb_ = ControlDB.getInstance(getApplicationContext());
            controlDb_.open();
            String status = controlDb_.status_by_packagename(package_name);
            if (status.trim().equalsIgnoreCase("1")) {

                Intent intent1 = new Intent(Intent.ACTION_MAIN);
                intent1.setComponent(new ComponentName(package_name, package_name + ".Main2Activity"));
                startActivity(intent);

            } else if (status.trim().equalsIgnoreCase("0")) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
                builder1.setTitle("ACCESS REVOKED");
                builder1.setIcon(R.drawable.company_logo);
                builder1.setMessage("You cannot open this application, contact enterprise system administrator for more info");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "CONTACT ADMIN",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                            }
                        });

                builder1.setNegativeButton(
                        "CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }

        } catch (Exception e) {
            staff_info.setText(staff_info.getText() + "Staff Name: " + staff_name + "\nStaff ID: " + staff_id);
            productList = new ArrayList<>();

            controlDb_ = ControlDB.getInstance(getApplicationContext());
            controlDb_.open();
            map = controlDb_.load_applications(staff_id);

            try {
                JSONArray jsonArray = new JSONArray(map);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject Quiz = jsonArray.getJSONObject(i);
                    productList.add(new ApplicationClass(
                            Quiz.getString("app_name"),
                            Quiz.getString("package_name"),
                            Quiz.getString("status"),
                            Quiz.getString("staff_id"),
                            Quiz.getString("staff_name"),
                            Quiz.getString("download_version"),
                            Quiz.getString("new_version"),
                            Quiz.getString("what_new"),
                            Quiz.getString("staff_role"),
                            Quiz.getString("hub"),
                            Quiz.getString("app_version"),
                            Quiz.getString("staff_program")
                    ));
                }
            } catch (JSONException e1) {
                Log.e("FAILED", "Json parsing error: " + e.getMessage());
            }
            ApplicationAdapter adapter = new ApplicationAdapter(ApplicationDetails.this, productList);
            recyclerView.setAdapter(adapter);

        }


    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(ApplicationDetails.this).create();
        alertDialog.setTitle("Access Control");
        alertDialog.setMessage("Click exit to close this application");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Exit",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                        dialog.cancel();
                    }

                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        onResume();
                        dialog.dismiss();
                    }

                });

        alertDialog.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_application_details);

        recyclerView = (RecyclerView) findViewById(R.id.recylcerView2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sessionManagement = new SessionManagement(getApplicationContext());
        staff_info = (TextView) findViewById(R.id.staff_info);
        tvStaffId = findViewById(R.id.staff_id);

        HashMap<String, String> user = sessionManagement.getUserDetails();
        String staff_id = user.get(SessionManagement.KEY_STAFF_ID);
        String staff_name = user.get(SessionManagement.KEY_STAFF_NAME);

        access_version = findViewById(R.id.access_control_version);

        access_version.setText("");
        access_version.setText(access_version.getText() + "Access Control v" + BuildConfig.VERSION_NAME);


        try {
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            package_name = (String) b.get("package_name");

            controlDb_ = ControlDB.getInstance(getApplicationContext());
            controlDb_.open();
            String status = controlDb_.status_by_packagename(package_name);
            if (status.trim().equalsIgnoreCase("1")) {

                Intent intent1 = new Intent(Intent.ACTION_MAIN);
                intent1.setComponent(new ComponentName(package_name, package_name + ".Main2Activity"));
                startActivity(intent);

            } else if (status.trim().equalsIgnoreCase("0")) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
                builder1.setTitle("ACCESS REVOKED");
                builder1.setIcon(R.drawable.company_logo);
                builder1.setMessage("You cannot open this application, contact enterprise system administrator for more info");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "CONTACT ADMIN",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                builder1.setNegativeButton(
                        "CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }

        } catch (Exception e) {

            staff_info.setText("");
            tvStaffId.setText("");

            staff_info.setText(staff_info.getText() + staff_name);
            tvStaffId.setText(tvStaffId.getText() + staff_id);

            productList = new ArrayList<>();

            controlDb_ = ControlDB.getInstance(getApplicationContext());
            controlDb_.open();
            map = controlDb_.load_applications(staff_id);

            try {
                JSONArray jsonArray = new JSONArray(map);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject Quiz = jsonArray.getJSONObject(i);
                    productList.add(new ApplicationClass(
                            Quiz.getString("app_name"),
                            Quiz.getString("package_name"),
                            Quiz.getString("status"),
                            Quiz.getString("staff_id"),
                            Quiz.getString("staff_name"),
                            Quiz.getString("download_version"),
                            Quiz.getString("new_version"),
                            Quiz.getString("what_new"),
                            Quiz.getString("staff_role"),
                            Quiz.getString("hub"),
                            Quiz.getString("app_version"),
                            Quiz.getString("staff_program")

                    ));
                }
            } catch (JSONException e1) {
                Log.e("FAILED", "Json parsing error: " + e.getMessage());
            }
            ApplicationAdapter adapter = new ApplicationAdapter(ApplicationDetails.this, productList);
            recyclerView.setAdapter(adapter);
        }

    }

    public void Refresh(View view) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Applications \nPlease wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();


        @SuppressLint("StaticFieldLeak") SyncData.DownloadTemplate downloadTemplate = new SyncData.DownloadTemplate(getApplicationContext()) {

            @Override
            protected void onPostExecute(String s) {

            }
        };

        downloadTemplate.execute();

        @SuppressLint("StaticFieldLeak") SyncData.DownloadApplication downloadApplication = new SyncData.DownloadApplication(getApplicationContext()) {

            @Override
            protected void onPostExecute(String s) {
                progressDialog.dismiss();

                if (s.trim().equalsIgnoreCase("done")) {
                    Toast.makeText(context, "Refreshed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), ApplicationDetails.class);
                    startActivity(intent);

                } else if (s.trim().equalsIgnoreCase("not inserted")) {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                }
            }
        };

        downloadApplication.execute();


    }

    private boolean checkPermission() {

        //Check for READ_EXTERNAL_STORAGE access, using ContextCompat.checkSelfPermission()//

        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //If the app does have this permission, then return true//

        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(ApplicationDetails.this,
                            "You are required to enable this permission", Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    public void Logout(View view) {

        sessionManagement = new SessionManagement(getApplicationContext());
        sessionManagement.SaveLoginStatus("false");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void DirectReport(View view) {

        startActivity(new Intent(getApplicationContext(), VersionControl.class));
    }


    public String checkVersion() {

        ArrayList<Map<String, String>> VersionList = new ArrayList<>();
        AppVersionDatabaseController.DatabaseHelper databaseHelper = new AppVersionDatabaseController.DatabaseHelper(getApplicationContext());
        VersionList = databaseHelper.getAppVersion();
        JSONArray jsonArray = new JSONArray(VersionList);
        JSONObject jsonObject = null;

        List<String> app_names = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                String UserAppVersion = jsonObject.getString("app_version");
                String AccessControlVersion = jsonObject.getString("access_control_version");
                String AppName = jsonObject.getString("app_name");

                Log.d("Version_Response", UserAppVersion + " " + AccessControlVersion);

                try {
                    String[] x = UserAppVersion.split("\\.");
                    String[] y = AccessControlVersion.split("\\.");

                    int a = Integer.valueOf(x[0]) - Integer.valueOf(y[0]);
                    int b = Integer.valueOf(x[1]) - Integer.valueOf(y[1]);
                    int c = Integer.valueOf(x[2]) - Integer.valueOf(y[2]);
                    Log.d("Version_Response", a + " " + b + " " + c);


                    if (a == 0) {
                        if (b == 0) {
                            if (c == 0) {
                            } else if (c < 0) {
                                app_names.add(AppName);
                            }
                        } else if (b < 0) {
                            app_names.add(AppName);
                        }
                    } else if (a < 0) {
                        app_names.add(AppName);
                    }


                } catch (Exception e) {
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (app_names.size() != 0) {
            String appNames = "";
            for (int i = 0; i < app_names.size(); i++) {
                appNames += app_names.get(i) + ",";
            }
            Log.d("Version_Response", appNames + " ");
            return appNames;
        }

        return "";
    }

    private void sendNotification(String message, String title, int id) {

        Intent intent = new Intent(getApplicationContext(), ApplicationDetails.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */,
                intent, PendingIntent.FLAG_ONE_SHOT);

/*
      Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.time);
        notificationBuildeer.setPriority(NotificationCompat.PRIORITY_MAX);
        notificationBuildeer.setSound(soundUri);*/

        Uri defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.time);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.component_babbangona_logo)
                .setContentTitle(title)
                .setContentText("Updates available for  " + message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(defaultSoundUri)

                .setContentIntent(pendingIntent);
        Notification notification = notificationBuilder.build();
        // notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id /* ID of notification */,
                notification);
    }
}









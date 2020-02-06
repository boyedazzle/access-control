package com.babbangona.accesscontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VersionControl extends AppCompatActivity {
    RecyclerView recyclerView;
    ControlDB controlDB;
    List<VersionControlClass> productList;
    ArrayList<Map<String, String>> appRecords;
    String staff_id;
    TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        recyclerView = (RecyclerView) findViewById(R.id.recylcerView2);
        tvStatus = findViewById(R.id.tvRecordStatus);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder((Activity) view.getContext());
                alertDialog.setTitle("Access Control");
                alertDialog.setMessage("");
                alertDialog.setIcon(R.drawable.component_babbangona_logo);
                alertDialog.setPositiveButton(
                        "Sync Data",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                final ProgressDialog progressDialog = new ProgressDialog(VersionControl.this);
                                progressDialog.setMessage("Syncing Log \nPlease wait...");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.show();

                                @SuppressLint("StaticFieldLeak") SyncData.SyncAppStatus syncAppStatus = new SyncData.SyncAppStatus(getApplicationContext()) {

                                    @Override
                                    protected void onPostExecute(String s) {

                                        Log.d("--H--E", s);
                                        progressDialog.dismiss();

                                    }
                                };

                                syncAppStatus.execute();

                            }
                        }
                );
                alertDialog.setNegativeButton(
                        "Load Data",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                final ProgressDialog progressDialog = new ProgressDialog(VersionControl.this);
                                progressDialog.setMessage("Loading Log \nPlease wait...");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.show();

                                @SuppressLint("StaticFieldLeak") SyncData.AppVersionDownload appVersionDownload = new SyncData.AppVersionDownload(getApplicationContext()) {

                                    @Override
                                    protected void onPostExecute(String s) {
                                        progressDialog.dismiss();
                                        if (s.trim().equalsIgnoreCase("done")) {
                                            Toast.makeText(context, "Refreshed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), VersionControl.class);
                                            startActivity(intent);

                                        } else if (s.trim().equalsIgnoreCase("not inserted")) {
                                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                };

                                appVersionDownload.execute();


                            }
                        }
                );


                alertDialog.show();


            }
        });

        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = sessionManagement.getUserDetails();
        staff_id = user.get(SessionManagement.KEY_STAFF_ID);

        controlDB = ControlDB.getInstance(getApplicationContext());
        controlDB.open();

        appRecords = new ArrayList<>();
        appRecords = controlDB.loadVersion(staff_id);

        if (appRecords.size() == 0) {
            tvStatus.setVisibility(View.VISIBLE);
        } else {
            tvStatus.setVisibility(View.GONE);
        }


        Log.d("ZZZZ", appRecords + "");

        productList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(appRecords);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject Quiz = jsonArray.getJSONObject(i);
                productList.add(new VersionControlClass(
                        Quiz.getString("appid_staffid"),
                        Quiz.getString("app_name"),
                        Quiz.getString("app_id"),
                        Quiz.getString("staff_name"),
                        Quiz.getString("staff_id"),
                        Quiz.getString("supervisor_id"),
                        Quiz.getString("lead_id"),
                        Quiz.getString("access_control_version"),
                        Quiz.getString("user_app_version")

                ));
            }
        } catch (JSONException e1) {
            Log.e("FAILED", "Json parsing error: " + e1.getMessage());
        }
        VersionControlAdapter adapter = new VersionControlAdapter(getApplicationContext(), productList);
        recyclerView.setAdapter(adapter);


    }

}

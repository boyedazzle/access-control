package com.babbangona.accesscontrol;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;


public class BackgroundSync extends JobService {

    SyncData.DownloadApplication downloadApplication;
    SyncData.SyncVersion syncAppStatus;

    @SuppressLint("StaticFieldLeak")

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        syncAppStatus = new SyncData.SyncVersion(getApplicationContext()) {

            @Override
            protected void onPostExecute(String s) {

                Log.d("ACTIVITY_SYNC", s);
                // Toast.makeText(context, "Error: Couldnt communicate with the online database", Toast.LENGTH_SHORT).show();
                jobFinished(jobParameters, true);

            }
        };
        syncAppStatus.execute();

        downloadApplication = new SyncData.DownloadApplication(getApplicationContext()) {

            @Override
            protected void onPostExecute(String s) {
                //Toast.makeText(mss_page.this, s, Toast.LENGTH_SHORT).show();
                if (s != null && s.trim().equalsIgnoreCase("done")) {
                    //Toast.makeText(context, "Refreshed", Toast.LENGTH_SHORT).show();
                    jobFinished(jobParameters, true);
                    /*Intent intent = new Intent(getApplicationContext(),ApplicationDetails.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //startActivity(intent);*/

                } else if (s != null && s.trim().equalsIgnoreCase("not inserted")) {
                    //Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    jobFinished(jobParameters, true);
                } else {
                    Log.d("ACTIVITY_SYNC", s);
                    // Toast.makeText(context, "Error: Couldnt communicate with the online database", Toast.LENGTH_SHORT).show();
                    jobFinished(jobParameters, true);
                }
            }
        };
        downloadApplication.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        //downloadApplication.cancel(true);
        return false;
    }
}
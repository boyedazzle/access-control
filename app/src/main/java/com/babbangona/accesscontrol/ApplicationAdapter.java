package com.babbangona.accesscontrol;


import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ProductViewHolder> {


    private Context mCtx;
    private List<ApplicationClass> productList;

    ControlDB controlDB;
    DownloadTask downloadTask;
    String online_status;
    int download_version, new_version;

    private static final int PERMISSION_REQUEST_CODE = 1;
    SessionManagement sessionManagement;
    private ApplicationClass applicationClass;

    public ApplicationAdapter(Context mCtx, List<ApplicationClass> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.application_recycler, null);
        sessionManagement = new SessionManagement(view.getContext());

        return new ProductViewHolder(view);

    }


    @SuppressLint("SetTextI18n")

    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        applicationClass = productList.get(position);
        online_status = applicationClass.getStatus();
        download_version = Integer.parseInt(applicationClass.getDownload_version());


        holder.D_app_name.setText("");
        //holder.D_staff_id.setText("");
        holder.hidden.setText("");
        holder.D_status.setText("");
        holder.app_version.setText("");
        try {
            new_version = Integer.parseInt(applicationClass.getNew_version());
        } catch (Exception e) {

        }


        if (download_version == 1) holder.download.setVisibility(View.INVISIBLE);
        else if (download_version == 0) holder.download.setVisibility(View.VISIBLE);

        //Log.d("PARSE 1","     "+download_version+"     "+pack);


        if (download_version == 0 && (new_version - download_version) > 0)
            holder.update.setVisibility(View.INVISIBLE);

        else if (new_version - download_version == 0) {
            holder.download.setVisibility(View.INVISIBLE);
            holder.update.setVisibility(View.INVISIBLE);
        } else if (download_version > 0 && (new_version - download_version) > 0) {
            holder.update.setVisibility(View.VISIBLE);
            holder.download.setVisibility(View.VISIBLE);
            holder.download.setText(mCtx.getResources().getString(R.string.update));
        } else if ((new_version - download_version) <= 0) holder.update.setVisibility(View.GONE);

        if (Integer.parseInt(online_status) == 1) {
            holder.D_status.setText(holder.D_status.getText() + "" + "ACTIVE");
            holder.D_status.setTextColor(Color.parseColor("#008000"));
        } else {
            holder.D_status.setText(holder.D_status.getText() + "" + "INACTIVE");
            holder.D_status.setTextColor(Color.parseColor("#ff0000"));
        }

        holder.D_app_name.setText(holder.D_app_name.getText() + applicationClass.getAppName());
        holder.hidden.setText(holder.hidden.getText() + "" + applicationClass.getAppName());
        holder.app_version.setText(holder.app_version.getText() + applicationClass.getApp_version());


        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlDB = ControlDB.getInstance(mCtx);
                controlDB.open();
                final String package_name = controlDB.package_name(holder.hidden.getText().toString().trim());
                String what_new = controlDB.what_new(package_name);

                AlertDialog.Builder builder1 = new AlertDialog.Builder(mCtx);
                builder1.setTitle("NEW UPDATE FOR: " + holder.hidden.getText().toString().trim());
                builder1.setIcon(R.drawable.company_logo);
                builder1.setMessage(what_new);
                builder1.setCancelable(true);

                builder1.setNeutralButton(
                        "CLOSE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();


            }
        });

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder((Activity) view.getContext());

                alertDialog.setTitle("Access Control");
                alertDialog.setMessage("Are you sure you want to download this application ? ");
                alertDialog.setIcon(R.drawable.component_babbangona_logo);

                alertDialog.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(mCtx, "Download Manager is preparing to download", Toast.LENGTH_SHORT).show();

                                controlDB = ControlDB.getInstance(mCtx);
                                controlDB.open();
                                String download_link = controlDB.download_link(holder.hidden.getText().toString().trim());
                                String package_name = controlDB.package_name(holder.hidden.getText().toString().trim());


                                downloadTask = new DownloadTask(mCtx);
                                downloadTask.execute(download_link, package_name, holder.D_app_name.getText().toString().trim());

                            }
                        }
                );
                alertDialog.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );


                alertDialog.show();


            }
        });

        holder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                controlDB = ControlDB.getInstance(mCtx);
                controlDB.open();
                final String package_name = controlDB.package_name(holder.hidden.getText().toString().trim());


                final String res = controlDB.status_by_packagename(package_name);

                if (res.trim().equalsIgnoreCase("0")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mCtx);
                    builder1.setTitle("ACCESS REVOKED");
                    builder1.setIcon(R.drawable.company_logo);
                    builder1.setMessage("You cannot open this application, contact enterprise system administrator for more info");
                    builder1.setCancelable(true);

                    builder1.setNeutralButton(
                            "CLOSE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                } else if (res.trim().equalsIgnoreCase("1")) {

                    PackageInfo packageInfo = null;
                    try {
                        SessionManagement sessionManagement = new SessionManagement(mCtx);
                        HashMap<String, String> user = sessionManagement.getUserDetails();
                        String staff_id = user.get(SessionManagement.KEY_STAFF_ID);

                        packageInfo = mCtx.getPackageManager().getPackageInfo(package_name, 0);
                        AppVersionDatabaseController.DatabaseHelper appVersionDatabaseController = new AppVersionDatabaseController.DatabaseHelper(mCtx);
                        appVersionDatabaseController.InsertVersionRecords(package_name, packageInfo.versionName, staff_id);

                    } catch (PackageManager.NameNotFoundException e) {
                        SessionManagement sessionManagement = new SessionManagement(mCtx);
                        HashMap<String, String> user = sessionManagement.getUserDetails();
                        String staff_id = user.get(SessionManagement.KEY_STAFF_ID);


                        AppVersionDatabaseController.DatabaseHelper appVersionDatabaseController = new AppVersionDatabaseController.DatabaseHelper(mCtx);
                        appVersionDatabaseController.InsertVersionRecords(package_name, "0.0.0", staff_id);
                    }

                    try {
                        @SuppressLint("StaticFieldLeak") SyncData.AnalyseVersions downloadApplication = new SyncData.AnalyseVersions(mCtx) {
                            @Override
                            protected void onPostExecute(String s) {
                                if (s.trim().equalsIgnoreCase("outdated")) {

                                    String res1 = controlDB.getAppDate(package_name);
                                    Log.d("DATE_EXPIRE", res1);
                                    String LaunchDate;


                                    try {


                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                                        Date date = format.parse(res1);
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
                                        LaunchDate = simpleDateFormat.format(date);
                                        Date date3 = new SimpleDateFormat("MM/dd/yy").parse(LaunchDate);
                                        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
                                        Date date1 = new Date();
                                        dateFormat.format(date1);

                                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
                                        Calendar c = Calendar.getInstance();
                                        c.setTime(date1); // Now use today date.
                                        c.add(Calendar.DATE, -3); // Adding 5 days
                                        String output = sdf.format(c.getTime());

                                        Date date2 = new SimpleDateFormat("MM/dd/yy").parse(output);

                                        if (date2.after(date3)) {

                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder((Activity) view.getContext());
                                            alertDialog.setTitle("Access Control");
                                            alertDialog.setMessage("This application is outdated, update to version '" + holder.app_version.getText().toString() + "'");
                                            alertDialog.setIcon(R.drawable.component_babbangona_logo);
                                            alertDialog.show();

                                        } else {

                                            SimpleDateFormat sdf1 = new SimpleDateFormat("EE dd,MMM yyyy");
                                            Calendar c1 = Calendar.getInstance();
                                            c1.setTime(date); // Now use today date.
                                            c1.add(Calendar.DATE, +3); // Adding 5 days
                                            String output1 = sdf1.format(c1.getTime());


                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder((Activity) view.getContext());
                                            alertDialog.setTitle("Access Control");
                                            alertDialog.setMessage("This application is outdated , ensure you update to version '" + holder.app_version.getText().toString() + "' by " + output1);
                                            alertDialog.setIcon(R.drawable.component_babbangona_logo);
                                            alertDialog.setPositiveButton(
                                                    "Continue",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            try {
                                                                AppVersionDatabaseController.DatabaseHelper databaseHelper = new AppVersionDatabaseController.DatabaseHelper(context);
                                                                databaseHelper.LastOpened(package_name);

                                                                Intent intent1 = new Intent(Intent.ACTION_MAIN);
                                                                intent1.putExtra("staff_name", applicationClass.getStaffName());
                                                                intent1.putExtra("staff_id", applicationClass.getStaffId());
                                                                intent1.putExtra("staff_role", applicationClass.getStaff_role());
                                                                intent1.putExtra("fpf_id", applicationClass.getStaffId());
                                                                intent1.putExtra("staff_hub", applicationClass.getStaff_hub());
                                                                intent1.putExtra("module", "access_control");
                                                                intent1.putExtra("app_version", applicationClass.getApp_version());
                                                                intent1.putExtra("staff_program", applicationClass.getStaff_program());
                                                                intent1.setComponent(new ComponentName(package_name, package_name + ".Main2Activity"));
                                                                intent1.putExtra("launch", "start");
                                                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                mCtx.startActivity(intent1);
                                                            } catch (Exception e) {
                                                                Toast.makeText(mCtx, "This application is not installed on this phone", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                            );


                                            alertDialog.show();
                                        }

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }


                                } else if (s.trim().equalsIgnoreCase("corrupted")) {

                                    try {
                                        PackageInfo packageInfo = mCtx.getPackageManager().getPackageInfo(package_name, 0);
                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder((Activity) view.getContext());
                                        alertDialog.setTitle("Access Control");
                                        alertDialog.setMessage("This application version '" + packageInfo.versionName + "' is not a standard application version, contact Enterprise Systems");
                                        alertDialog.setIcon(R.drawable.component_babbangona_logo);

                                        alertDialog.show();

                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();
                                    }


                                } else {
                                    try {
                                        AppVersionDatabaseController.DatabaseHelper databaseHelper = new AppVersionDatabaseController.DatabaseHelper(context);
                                        databaseHelper.LastOpened(package_name);
                                        Intent intent1 = new Intent(Intent.ACTION_MAIN);
                                        intent1.putExtra("staff_name", applicationClass.getStaffName());
                                        intent1.putExtra("staff_id", applicationClass.getStaffId());
                                        intent1.putExtra("staff_role", applicationClass.getStaff_role());
                                        intent1.putExtra("fpf_id", applicationClass.getStaffId());
                                        intent1.putExtra("staff_hub", applicationClass.getStaff_hub());
                                        intent1.putExtra("module", "access_control");
                                        intent1.putExtra("app_version", applicationClass.getApp_version());
                                        intent1.putExtra("staff_program", applicationClass.getStaff_program());

                                        intent1.setComponent(new ComponentName(package_name, package_name + ".Main2Activity"));
                                        intent1.putExtra("launch", "start");
                                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        mCtx.startActivity(intent1);
                                    } catch (Exception e) {
                                        Toast.makeText(mCtx, "This application is not installed on this phone", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        };

                        downloadApplication.execute(package_name);

                    } catch (Exception e) {
                        Toast.makeText(mCtx, "This application is not installed on this phone", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });


    }


    @Override
    public int getItemCount() {
        Log.d("product", String.valueOf(productList.size()));
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView D_app_name, D_staff_id, D_status, hidden, update, app_version;
        Button open, download;


        public ProductViewHolder(View itemView) {
            super(itemView);

            D_app_name = (TextView) itemView.findViewById(R.id.app_name);
            D_status = (TextView) itemView.findViewById(R.id.status);
            hidden = (TextView) itemView.findViewById(R.id.hidden);
            open = (Button) itemView.findViewById(R.id.btn);
            download = (Button) itemView.findViewById(R.id.btn2);
            update = (TextView) itemView.findViewById(R.id.update);
            app_version = (TextView) itemView.findViewById(R.id.app_version);

        }


    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;

        ProgressDialog mProgressDialog;
        private PowerManager.WakeLock mWakeLock;
        String package_name, app_name;


        public DownloadTask(Context context) {
            this.context = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            // instantiate it within the onCreate method
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Downloading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);


            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }


        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            package_name = sUrl[1];
            app_name = sUrl[2];
            app_name = app_name.replaceAll("\\s+", "");
            app_name = app_name.toLowerCase();
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download", app_name + ".apk");
                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {

                Log.d("STAGE 1", e.toString());
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    Log.d("STAGE 2", ignored.toString());
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(final Integer... progress) {
            super.onProgressUpdate(progress);

            Log.d("STAGE PROGRESS", String.valueOf(progress));
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancel(true);
                }
            });
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else {


                File fileApkToInstall = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download", app_name + ".apk");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    try {

                        Uri apkUri = FileProvider.getUriForFile(mCtx, "com.spicysoftware.test.provider", fileApkToInstall);
                        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                        intent.setData(apkUri);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        mCtx.startActivity(intent);


                    } catch (Exception e) {

                        Log.d("download_error", String.valueOf(e));
                    }

                    String res = controlDB.update_download_version(package_name);
                } else {
                    Uri apkUri = Uri.fromFile(fileApkToInstall);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mCtx.startActivity(intent);

                    String res = controlDB.update_download_version(package_name);
                }
            }
        }
    }

}
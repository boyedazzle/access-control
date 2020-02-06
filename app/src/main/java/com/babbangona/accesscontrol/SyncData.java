package com.babbangona.accesscontrol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncData {

    static String URL = "http://apps.babbangona.com/app_control/mobile/";

    @SuppressLint("StaticFieldLeak")

    public static class DownloadApplication extends AsyncTask<String, String, String> {

        @SuppressLint("StaticFieldLeak")
        Context context;
        StringBuilder result = null;
        SessionManagement sessionManagement;
        ControlDB controlDB;
        AppVersionDatabaseController.DatabaseHelper appVersionDatabaseController;


        public DownloadApplication(Context mCtx) {
            this.context = mCtx;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn;
            URL url = null;
            String staff_id = null;


            try {
                sessionManagement = new SessionManagement(context);
                HashMap<String, String> user = sessionManagement.getUserDetails();
                staff_id = user.get(SessionManagement.KEY_STAFF_ID);
            } catch (Exception e) {
                System.out.println("Exception caught: Session Management Didnt work");
            }


            if (staff_id == null) {


            }
            controlDB = ControlDB.getInstance(context);
            controlDB.open();


            try {
                url = new URL(URL + "/download_application.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception 1";
            }

            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(20000);
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("staff_id", staff_id);

                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();


            } catch (IOException e1) {
                e1.printStackTrace();
                return "Failed! Kindly check your network connection ";
            }

            try {
                int response_code = conn.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }


                    String[] app_name, package_name, Staff_id, status, staff_role, download_link, what_new, new_version, app_version, date_updated;
                    Map<String, String> map = null;
                    ArrayList<Map<String, String>> wordList;
                    wordList = new ArrayList<Map<String, String>>();
                    Log.d("DOWNlOADED_1", String.valueOf(result));

                    try {
                        JSONArray JA = new JSONArray(result.toString());
                        JSONObject json = null;
                        app_name = new String[JA.length()];
                        package_name = new String[JA.length()];
                        Staff_id = new String[JA.length()];
                        status = new String[JA.length()];
                        staff_role = new String[JA.length()];
                        download_link = new String[JA.length()];
                        what_new = new String[JA.length()];
                        new_version = new String[JA.length()];
                        app_version = new String[JA.length()];
                        date_updated = new String[JA.length()];


                        for (int i = 0; i < JA.length(); i++) {

                            json = JA.getJSONObject(i);
                            app_name[i] = json.getString("app_name");
                            package_name[i] = json.getString("package_name");
                            Staff_id[i] = json.getString("staff_id");
                            status[i] = json.getString("status");
                            staff_role[i] = json.getString("staff_role");
                            download_link[i] = json.getString("download_link");
                            what_new[i] = json.getString("updated_features");
                            new_version[i] = json.getString("new_version");
                            app_version[i] = json.getString("app_version");
                            date_updated[i] = json.getString("date_updated");

                            map = new HashMap<String, String>();
                            map.put("app_name", app_name[i]);
                            map.put("package_name", package_name[i]);
                            map.put("staff_id", Staff_id[i]);
                            map.put("status", status[i]);
                            map.put("staff_role", staff_role[i]);
                            map.put("download_link", download_link[i]);
                            map.put("updated_features", what_new[i]);
                            map.put("new_version", new_version[i]);
                            map.put("app_version", app_version[i]);
                            map.put("date_updated", date_updated[i]);

                            wordList.add(map);

                        }

                        JSONArray jsonArray = new JSONArray(result.toString());
                        controlDB.download_application_record(jsonArray);

                        appVersionDatabaseController = new AppVersionDatabaseController.DatabaseHelper(context);
                        appVersionDatabaseController.insertOnlineVersionDownload(jsonArray);

                        return ("done");

                    } catch (Exception e) {
                        Log.e("Fail 3", e.toString());
                        return ("Failed! Kindly check your network connection or contact the admin");
                    }


                } else {
                    return ("Connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }

        }


    }

    public static class StaffList extends AsyncTask<String, String, String> {

        @SuppressLint("StaticFieldLeak")
        Context context;
        StringBuilder result = null;
        SessionManagement sessionManagement;
        ControlDB controlDB;

        public StaffList(Context mCtx) {
            this.context = mCtx;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn;
            URL url = null;
            String staff_id = null;

            try {
                sessionManagement = new SessionManagement(context);
                HashMap<String, String> user = sessionManagement.getUserDetails();
                staff_id = user.get(SessionManagement.KEY_STAFF_ID);
            } catch (Exception e) {
                System.out.println("Exception caught: Session Management Didnt work");
            }

            if (staff_id == null) {
                staff_id = "0";
            }


            try {
                url = new URL(URL + "/download_staff_list.php");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception 1";
            }

            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(20000);
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                SessionManagement sessionManagement = new SessionManagement(context);
                HashMap<String, String> user = sessionManagement.getUserDetails();
                String latestDate = user.get(SessionManagement.KEY_LAST_SYNCED);

                Log.d("JJAA", latestDate);
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("staff_id", staff_id).appendQueryParameter("latest_date", latestDate);

                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                e1.printStackTrace();
                return "Failed! Kindly check your network connection ";
            }

            try {
                int response_code = conn.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }


                    Log.d("JJAA", result.toString());
                    String[] staff_name, staff_role, Staff_id, staff_program, password, hub, phone_number, supervisor_id, lead_id;
                    Map<String, String> map = null;
                    ArrayList<Map<String, String>> wordList;
                    wordList = new ArrayList<Map<String, String>>();
                    if (result.length() == 0) return "updated";

                    try {
                        JSONArray JA = new JSONArray(result.toString());
                        JSONObject json = null;

                        Staff_id = new String[JA.length()];

                        staff_role = new String[JA.length()];
                        staff_name = new String[JA.length()];
                        password = new String[JA.length()];
                        hub = new String[JA.length()];
                        staff_program = new String[JA.length()];
                        phone_number = new String[JA.length()];
                        supervisor_id = new String[JA.length()];
                        lead_id = new String[JA.length()];

                        JSONObject jsonObject = JA.getJSONObject(0);
                        SessionManagement sessionManagement = new SessionManagement(context);
                        sessionManagement.SaveLastSynced(jsonObject.getString("last_fetched"));


                        for (int i = 0; i < JA.length(); i++) {

                            json = JA.getJSONObject(i);
                            staff_name[i] = json.getString("staff_name");
                            Staff_id[i] = json.getString("staff_id");
                            staff_role[i] = json.getString("staff_role");
                            password[i] = json.getString("password");
                            hub[i] = json.getString("hub");
                            phone_number[i] = json.getString("phone_number");
                            supervisor_id[i] = json.getString("supervisor_id");
                            lead_id[i] = json.getString("lead_id");
                            staff_program[i] = json.getString("staff_program");

                            map = new HashMap<String, String>();
                            map.put("staff_name", staff_name[i]);
                            map.put("staff_role", staff_role[i]);
                            map.put("staff_id", Staff_id[i]);
                            map.put("password", password[i]);
                            map.put("hub", hub[i]);
                            map.put("phone_number", phone_number[i]);
                            map.put("supervisor_id", phone_number[i]);
                            map.put("lead_id", phone_number[i]);
                            map.put("staff_program", staff_program[i]);

                            wordList.add(map);

                        }
                        controlDB = ControlDB.getInstance(context);
                        controlDB.open();

                        controlDB.download_staff_list(wordList);
                        Log.d("JJAA", wordList.toString());

                        return ("done");
                    } catch (Exception e) {
                        Log.e("Fail3", e.toString());
                        return ("Failed! Kindly check your network connection or contact the admin");
                    }

                } else {
                    return ("Connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }
    }

    public static class AppVersionDownload extends AsyncTask<String, String, String> {

        @SuppressLint("StaticFieldLeak")
        Context context;
        StringBuilder result = null;
        SessionManagement sessionManagement;
        ControlDB controlDB;

        public AppVersionDownload(Context mCtx) {
            this.context = mCtx;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn;
            URL url = null;
            String staff_id = null;

            try {
                sessionManagement = new SessionManagement(context);
                HashMap<String, String> user = sessionManagement.getUserDetails();
                staff_id = user.get(SessionManagement.KEY_STAFF_ID);
            } catch (Exception e) {
                System.out.println("Exception caught: Session Management Didnt work");
            }

            if (staff_id == null) {
                staff_id = "0";
            }


            try {
                url = new URL(URL + "/download_outdated_app_records.php");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception 1";
            }

            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(20000);
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("staff_id", staff_id);

                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                e1.printStackTrace();
                return "exception 2";
            }

            try {
                int response_code = conn.getResponseCode();
                Log.d("RESPONSE_m", response_code + "");
                Log.d("RESPONSE_m", HttpURLConnection.HTTP_OK + "");
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("RESPONSE_m", result.toString());
                    try {
                        JSONArray JA = new JSONArray(result.toString());
                        controlDB = ControlDB.getInstance(context);
                        controlDB.open();
                        controlDB.versionController(JA);
                        return ("done");
                    } catch (Exception e) {
                        Log.e("Fail3", e.toString());
                        return ("Failed! Kindly check your network connection or contact the admin");
                    }

                } else {

                    return ("Connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }
    }

    public static class AnalyseVersions extends AsyncTask<String, String, String> {
        ArrayList<Map<String, String>> VersionList;

        AppVersionDatabaseController.DatabaseHelper databaseHelper;
        String UserAppVersion;
        String AccessControlVersion;
        String AppName;
        List<String> app_names;

        Context context;

        public AnalyseVersions(Context mCtx) {
            this.context = mCtx;
        }


        @Override
        protected String doInBackground(String... strings) {


            ArrayList<Map<String, String>> VersionList = new ArrayList<>();
            AppVersionDatabaseController.DatabaseHelper databaseHelper = new AppVersionDatabaseController.DatabaseHelper(context);
            VersionList = databaseHelper.getAppVersionByPackageName(strings[0]);
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
                        return "corrupted";
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (app_names.size() != 0) {


                return "outdated";
            }

            return "";
        }
    }

    public static class SyncVersion extends AsyncTask<Void, Void, String> {

        Context context;
        ArrayList wordlist;
        ControlDB controlDB;

        String staff_id;


        public SyncVersion(Context mCtx) {
            this.context = mCtx;
        }

        @Override
        protected String doInBackground(Void... voids) {


            SessionManagement sessionManagement = new SessionManagement(context);
            HashMap<String, String> user = sessionManagement.getUserDetails();
            staff_id = user.get(SessionManagement.KEY_STAFF_ID);

            AppVersionDatabaseController.DatabaseHelper databaseHelper = new AppVersionDatabaseController.DatabaseHelper(context);
            wordlist = new ArrayList();
            wordlist = databaseHelper.UploadAppVersion(staff_id);


            if (wordlist.size() < 1) return "Your online database is already updated";

            Gson gson = new GsonBuilder().create();
            String WordList = gson.toJson(wordlist);

            HttpURLConnection httpURLConnection = null;

            try {
                java.net.URL url = new URL(URL + "/sync_version_status.php");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data_string = URLEncoder.encode("wordlist", "UTF-8") + "=" + URLEncoder.encode(WordList, "UTF-8") + "&" +
                        URLEncoder.encode("versionNo", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(BuildConfig.VERSION_NAME), "UTF-8");
                //+URLEncoder.encode("version=", "UTF-8") + "=" + URLEncoder.encode(WordList, "UTF-8");

                bufferedWriter.write(data_string); // writing information to Database
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                httpURLConnection.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                StringBuilder result;
                int response_code = httpURLConnection.getResponseCode();
                String output = "Poor internet connection. Please check your internet connection and try again";
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("RESPONSE_NEW", String.valueOf(result));


                    return "Status synced successfully";

                } else {
                    return ("connection error, unable to communicate with the online server");
                }


            } catch (IOException e) {
                e.printStackTrace();
                return "No internet connection. Please check your internet connection and try again";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }


        }

    }

    public static class SyncAppStatus extends AsyncTask<Void, Void, String> {

        Context context;
        ArrayList wordlist;
        ControlDB controlDB;

        String staff_id;


        public SyncAppStatus(Context mCtx) {
            this.context = mCtx;
        }

        @Override
        protected String doInBackground(Void... voids) {


            SessionManagement sessionManagement = new SessionManagement(context);
            HashMap<String, String> user = sessionManagement.getUserDetails();
            staff_id = user.get(SessionManagement.KEY_STAFF_ID);

            ControlDB controlDB = ControlDB.getInstance(context);
            controlDB.open();
            wordlist = controlDB.uploadVersionStatus(staff_id);


            if (wordlist.size() < 1) return "Your online database is already updated";

            Gson gson = new GsonBuilder().create();
            String WordList = gson.toJson(wordlist);

            HttpURLConnection httpURLConnection = null;

            try {
                java.net.URL url = new URL(URL + "/sync_app_status.php");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data_string = URLEncoder.encode("wordlist", "UTF-8") + "=" + URLEncoder.encode(WordList, "UTF-8") + "&" +
                        URLEncoder.encode("versionNo", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(BuildConfig.VERSION_NAME), "UTF-8");
                //+URLEncoder.encode("version=", "UTF-8") + "=" + URLEncoder.encode(WordList, "UTF-8");

                bufferedWriter.write(data_string); // writing information to Database
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                httpURLConnection.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                StringBuilder result;
                int response_code = httpURLConnection.getResponseCode();
                String output = "Poor internet connection. Please check your internet connection and try again";
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("RESPONSE_NEW", String.valueOf(result));

                    try {

                        JSONArray jsonArray = new JSONArray(result);
                        controlDB.deleteClosedOutEntries(jsonArray);

                        controlDB.close();


                    } catch (Exception e) {

                    }


                    return "Status synced successfully";

                } else {
                    return ("connection error, unable to communicate with the online server");
                }


            } catch (IOException e) {
                e.printStackTrace();
                return "No internet connection. Please check your internet connection and try again";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }


        }

    }


    public static class DownloadTemplate extends AsyncTask<String, String, String> {

        @SuppressLint("StaticFieldLeak")
        Context context;
        StringBuilder result = null;


        public DownloadTemplate(Context mCtx) {
            this.context = mCtx;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn;
            java.net.URL url = null;

            SessionManagement sessionManagement = new SessionManagement(context);
            HashMap<String, String> user = sessionManagement.getUserDetails();
            String staffid = user.get(SessionManagement.KEY_STAFF_ID);


            try {
                url = new URL(URL + "/download_template.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "connection failed";
            }

            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(20000);
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("staff_id", staffid);

                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();


            } catch (IOException e1) {
                e1.printStackTrace();
                return "connection failed";
            }

            try {
                int response_code = conn.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("MUFASA", result + "");
                    try {

                        JSONArray jsonArray = new JSONArray(result.toString());
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        Log.d("MUFASA", result + "");
                        SessionManagement sessionManagement1 = new SessionManagement(context);
                        sessionManagement1.SaveTemplate(jsonObject.getString("template"), staffid);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return "syncing successful";
                } else {
                    return "connection failed";
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }

        }


    }

}

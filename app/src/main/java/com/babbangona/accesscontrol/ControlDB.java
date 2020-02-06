package com.babbangona.accesscontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ControlDB {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static ControlDB instance;
    // Context cont;

    private ControlDB(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
        // this.cont = context;
    }

    public static ControlDB getInstance(Context context) {
        if (instance == null) {
            instance = new ControlDB(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public List<String> get_db_username() {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT staff_name||' '||staff_id FROM users", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public int login_controller(String staff_id, String password) {
        int count = 0;

        Cursor cursor = database.rawQuery("SELECT count(*) FROM users WHERE staff_id = \"" + staff_id + "\" and password = \"" + password + "\"", null);
        Log.d("cursor", String.valueOf(cursor));
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            count = cursor.getInt(cursor.getPosition());
            //cursor.moveToNext();
            return count;
        }
        cursor.close();

        return count;
    }


    public String staffid_login_controller(String staff_id, String password) {
        int count = 0;
        Cursor cursor = database.rawQuery("SELECT count(*) FROM users WHERE staff_id = \"" + staff_id + "\" and password = \"" + password + "\"", null);


        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            count = cursor.getInt(cursor.getPosition());
            //cursor.moveToNext();

        }
        cursor.close();

        String name = "no_user_found";
        if (count > 0) {
            Cursor cursor1 = database.rawQuery("select staff_name from users where staff_id = \"" + staff_id + "\" ", null);
            cursor1.moveToFirst();
            if (!cursor1.isAfterLast()) {
                name = cursor1.getString(cursor1.getColumnIndex("staff_name"));
                return name;
            }

        } else if (count < 0) {
            return name;
        }
        return name;
    }

    public String fpf_id(String staff_id) {


        String count = "";


        Cursor cursor = database.rawQuery("select fpf_id from users where staff_id =\"" + staff_id + "\"", null);
        cursor.moveToFirst();
        if (cursor != null) {
            if (!cursor.isAfterLast()) {
                count = cursor.getString(cursor.getPosition());
            }
        }

        return count;

    }

    public ArrayList<Map<String, String>> load_applications(String staff_id) {
        Map<String, String> map = null;
        ArrayList<Map<String, String>> wordList;
        wordList = new ArrayList<Map<String, String>>();

        Cursor cursor = database.rawQuery("SELECT a.app_name,a.package_name, a.status,a.staff_id,b.staff_name,a.download_version,a.new_version,a.what_new,b.role,b.hub,a.app_version,b.staff_program FROM es_application_details a JOIN users b on a.staff_id = b.staff_id Where a.staff_id  = \"" + staff_id + "\" order by a.app_name asc", null);


        cursor.moveToFirst();
        if (cursor.moveToFirst()) {

            do {
                map = new HashMap<String, String>();
                map.put("app_name", cursor.getString(0));
                map.put("package_name", cursor.getString(1));
                map.put("status", cursor.getString(2));
                map.put("staff_id", cursor.getString(3));
                map.put("staff_name", cursor.getString(4));
                map.put("download_version", cursor.getString(5));
                map.put("new_version", cursor.getString(6));
                map.put("what_new", cursor.getString(7));
                map.put("staff_role", cursor.getString(8));
                map.put("hub", cursor.getString(9));
                map.put("app_version", cursor.getString(10));
                map.put("staff_program", cursor.getString(11));
                wordList.add(map);

                cursor.moveToNext();
            }

            while (!cursor.isAfterLast());
        }

        cursor.close();

        return wordList;
    }

    public String download_application_record(JSONArray wordlist) {

        try {
            //ProgressDialog progressDialog = new ProgressDialog();

            String[] app_name, package_name, Staff_id, status, Staff_role, Download_link, what_new, new_version, app_version, date_updated;


            JSONObject json = null;

            app_name = new String[wordlist.length()];
            package_name = new String[wordlist.length()];
            Staff_id = new String[wordlist.length()];
            status = new String[wordlist.length()];
            Staff_role = new String[wordlist.length()];
            Download_link = new String[wordlist.length()];
            what_new = new String[wordlist.length()];

            new_version = new String[wordlist.length()];
            app_version = new String[wordlist.length()];
            date_updated = new String[wordlist.length()];

            for (int i = 0; i < wordlist.length(); i++) {

                json = wordlist.getJSONObject(i);

                app_name[i] = json.getString("app_name");
                package_name[i] = json.getString("package_name");
                Staff_id[i] = json.getString("staff_id");
                status[i] = json.getString("status");
                Staff_role[i] = json.getString("staff_role");
                Download_link[i] = json.getString("download_link");
                what_new[i] = json.getString("updated_features");
                new_version[i] = json.getString("new_version");
                app_version[i] = json.getString("app_version");
                date_updated[i] = json.getString("date_updated");


                try {
                    String Insert_Query = "INSERT INTO es_application_details(app_name,package_name,staff_id,status,download_link,what_new,new_version,app_version,date_updated) values('" + app_name[i] + "','" + package_name[i] + "'," +
                            "'" + Staff_id[i] + "','" + status[i] + "',\"" + Download_link[i] + "\",\"" + what_new[i] + "\",\"" + new_version[i] + "\",\"" + app_version[i] + "\",\"" + date_updated[i] + "\")";
                    Log.d("INSERT_APP", Insert_Query);
                    database.execSQL(Insert_Query);

                } catch (Exception e) {
                    String Update_Query = "UPDATE es_application_details set  app_name = \"" + app_name[i] + "\", staff_id= '" + Staff_id[i] + "', status= '" + status[i] + "', download_link=\"" + Download_link[i] + "\", what_new=\"" + what_new[i] + "\", new_version =\"" + new_version[i] + "\", date_updated = \"" + date_updated[i] + "\", app_version = \"" + app_version[i] + "\"  WHERE package_name = \"" + package_name[i] + "\"";
                    Log.d("INSERT_APP", Update_Query);
                    database.execSQL(Update_Query);
                }

            }
            return "done";

        } catch (Exception e) {
            Log.e("Fail 3", e.toString());
        }


        return "failed";
    }

    public String download_staff_list(ArrayList<Map<String, String>> wordlist) {

        try {
            Log.d("JJAA", wordlist.toString());
            //ProgressDialog progressDialog = new ProgressDialog();
            String[] staff_name, staff_role, staff_id, staff_program, password, hub, phone_number, supervisor_id, lead_id;
            JSONArray JA = new JSONArray(wordlist);

            JSONObject json = null;

            staff_name = new String[JA.length()];
            staff_role = new String[JA.length()];
            staff_id = new String[JA.length()];
            password = new String[JA.length()];
            hub = new String[JA.length()];
            staff_program = new String[JA.length()];
            phone_number = new String[JA.length()];
            supervisor_id = new String[JA.length()];
            lead_id = new String[JA.length()];

            for (int i = 0; i < JA.length(); i++) {

                json = JA.getJSONObject(i);
                json = JA.getJSONObject(i);

                staff_name[i] = json.getString("staff_name");
                staff_role[i] = json.getString("staff_role");
                staff_id[i] = json.getString("staff_id");
                password[i] = json.getString("password");
                hub[i] = json.getString("hub");
                staff_program[i] = json.getString("staff_program");
                phone_number[i] = json.getString("phone_number");
                supervisor_id[i] = json.getString("supervisor_id");
                lead_id[i] = json.getString("lead_id");

                try {
                    String Insert_Query = "INSERT INTO users(staff_name,role,staff_id,password,hub,phone_number,supervisor_id,lead_id,staff_program) " +
                            "values(\"" + staff_name[i] + "\",\"" + staff_role[i] + "\",\"" + staff_id[i] + "\",'" + password[i] + "',\"" + hub[i] + "\",\"" + phone_number[i] + "\",\"" + supervisor_id[i] + "\",\"" + lead_id[i] + "\",\"" + staff_program[i] + "\")";
                    Log.d("JJAA", Insert_Query);
                    database.execSQL(Insert_Query);

                } catch (Exception e) {

                    String Update_Query = "UPDATE users SET staff_name=\"" + staff_name[i] + "\", role = \"" + staff_role[i] + "\",password = \"" + password[i] + "\",hub=\"" + hub[i] +
                            "\",phone_number=\"" + phone_number[i] + "\",supervisor_id = \"" + supervisor_id[i] + "\",lead_id = \"" + lead_id[i] + "\", staff_program = \"" + staff_program[i] + "\"" +
                            " where staff_id=\"" + staff_id[i] + "\"";
                    Log.d("JJAA", Update_Query);
                    database.execSQL(Update_Query);


                }

            }
            return "done";

        } catch (Exception e) {
            Log.e("Fail 3", e.toString());
            Log.d("JJAA", e.toString());
        }


        return "failed";
    }

    public String package_name(String app_name) {


        String count = "";


        Cursor cursor = database.rawQuery("SELECT package_name from es_application_details  where app_name =\"" + app_name + "\"", null);
        cursor.moveToFirst();
        if (cursor != null) {
            if (!cursor.isAfterLast()) {
                count = cursor.getString(cursor.getPosition());
            }
        }

        return count;

    }

    public String what_new(String package_name) {


        String count = "";


        Cursor cursor = database.rawQuery("SELECT what_new from es_application_details  where package_name =\"" + package_name + "\"", null);
        cursor.moveToFirst();
        if (cursor != null) {
            if (!cursor.isAfterLast()) {
                count = cursor.getString(cursor.getPosition());
            }
        }

        return count;

    }

    public String download_link(String app_name) {

        String count = "";

        Cursor cursor = database.rawQuery("SELECT download_link from es_application_details  where app_name =\"" + app_name + "\"", null);
        cursor.moveToFirst();
        if (cursor != null) {
            if (!cursor.isAfterLast()) {
                count = cursor.getString(cursor.getPosition());
            }
        }

        return count;

    }

    public String status_by_packagename(String package_name) {

        String count = "";

        Cursor cursor = database.rawQuery("SELECT status from es_application_details  where package_name =\"" + package_name + "\"", null);
        cursor.moveToFirst();
        if (cursor != null) {
            if (!cursor.isAfterLast()) {
                count = cursor.getString(cursor.getPosition());
            }
        }

        return count;

    }

    public String update_download_version(String package_name) {
        int count = 0;

        Cursor cursor = database.rawQuery("select new_version from es_application_details where package_name = \"" + package_name + "\"", null);
        Log.d("cursor", String.valueOf(cursor));
        cursor.moveToFirst();
        if (cursor != null) {
            if (!cursor.isAfterLast()) {
                count = cursor.getInt(cursor.getPosition());
            }

        }
        int new_version = count;

        String update = "UPDATE es_application_details SET download_version = " + new_version + ", new_version = " + new_version + " where package_name = \"" + package_name + "\"";
        database.execSQL(update);

        return "done" + new_version;

    }

    /**
     * ==========================================================================================
     * VERSION 2.0.0
     * ==========================================================================================
     **/


    public void versionController(JSONArray jsonArray) {
        JSONObject jsonObject = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                int count = 0;
                jsonObject = jsonArray.getJSONObject(i);
                Cursor cursor = database.rawQuery("select count(appid_staffid) from app_version_controller where appid_staffid = \"" + jsonObject.getString("appid_staffid") + "\"", null);
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    count = cursor.getInt(cursor.getPosition());

                }

                if (count == 0) {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("appid_staffid", jsonObject.getString("appid_staffid"));
                    contentValues.put("staff_id", jsonObject.getString("staff_id"));
                    contentValues.put("staff_name", jsonObject.getString("staff_name"));
                    contentValues.put("app_id", jsonObject.getString("app_id"));
                    contentValues.put("app_name", jsonObject.getString("app_name"));
                    contentValues.put("access_control_version", jsonObject.getString("access_control_version"));
                    contentValues.put("supervisor_id", jsonObject.getString("supervisor_id"));
                    contentValues.put("lead_id", jsonObject.getString("lead_id"));
                    contentValues.put("user_app_version", jsonObject.getString("user_version"));
                    database.insert("app_version_controller", null, contentValues);


                } else {

                    ContentValues contentValues = new ContentValues();

                    contentValues.put("staff_id", jsonObject.getString("staff_id"));
                    contentValues.put("staff_name", jsonObject.getString("staff_name"));
                    contentValues.put("app_id", jsonObject.getString("app_id"));
                    contentValues.put("app_name", jsonObject.getString("app_name"));
                    contentValues.put("access_control_version", jsonObject.getString("access_control_version"));
                    contentValues.put("supervisor_id", jsonObject.getString("supervisor_id"));
                    contentValues.put("lead_id", jsonObject.getString("lead_id"));
                    contentValues.put("user_app_version", jsonObject.getString("user_version"));

                    String where = "appid_staffid =?";
                    String[] whereArgs = new String[]{String.valueOf(jsonObject.getString("appid_staffid"))};

                    database.update("app_version_controller", contentValues, where, whereArgs);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

    public ArrayList<Map<String, String>> loadVersion(String staff_id) {
        Map<String, String> map = null;
        ArrayList<Map<String, String>> wordList;
        wordList = new ArrayList<Map<String, String>>();
        Cursor cursor = database.rawQuery("SELECT appid_staffid,app_id, app_name,access_control_version,staff_name," +
                "staff_id,supervisor_id,lead_id,user_app_version FROM app_version_controller where (supervisor_id  = \"" + staff_id + "\" or lead_id = \"" + staff_id + "\") and status = '0' ", null);


        cursor.moveToFirst();
        if (cursor.moveToFirst()) {

            do {
                map = new HashMap<String, String>();
                map.put("appid_staffid", cursor.getString(0));
                map.put("app_id", cursor.getString(1));
                map.put("app_name", cursor.getString(2));
                map.put("access_control_version", cursor.getString(3));
                map.put("staff_name", cursor.getString(4));
                map.put("staff_id", cursor.getString(5));
                map.put("supervisor_id", cursor.getString(6));
                map.put("lead_id", cursor.getString(7));
                map.put("user_app_version", cursor.getString(8));

                wordList.add(map);

                cursor.moveToNext();
            }

            while (!cursor.isAfterLast());
        }

        cursor.close();

        return wordList;
    }

    public void updateIssue(String appid_staffid) {

        String update = "update app_version_controller set status = '1' where appid_staffid = \"" + appid_staffid + "\" ";
        database.execSQL(update);

    }

    public ArrayList<Map<String, String>> uploadVersionStatus(String staff_id) {
        Map<String, String> map = null;
        ArrayList<Map<String, String>> wordList;
        wordList = new ArrayList<Map<String, String>>();
        Cursor cursor = database.rawQuery("SELECT appid_staffid,status FROM app_version_controller where (supervisor_id  = \"" + staff_id + "\" or lead_id = \"" + staff_id + "\") and status = '1' ", null);


        cursor.moveToFirst();
        if (cursor.moveToFirst()) {

            do {
                map = new HashMap<String, String>();
                map.put("appid_staffid", cursor.getString(0));
                map.put("status", cursor.getString(1));

                wordList.add(map);
                cursor.moveToNext();
            }

            while (!cursor.isAfterLast());
        }

        cursor.close();

        return wordList;
    }

    public void deleteClosedOutEntries(JSONArray jsonArray) {


        JSONObject jsonObject = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {


                String where = "appid_staffid =?";
                String[] whereArgs = new String[]{String.valueOf(jsonObject.getString("appid_staffid"))};

                database.delete("app_version_controller", where, whereArgs);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

    public String getAppDate(String package_name) {
        String date = "";
        Cursor cursor = database.rawQuery("select date_updated from es_application_details where package_name = \"" + package_name + "\" ", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            date = cursor.getString(0);
            cursor.moveToNext();
        }

        return date;
    }


}

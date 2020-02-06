package com.babbangona.accesscontrol;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;


public class SessionManagement {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";


    // Email address (make variable public to access from outside)
    public static final String KEY_STAFF_NAME = "staffname";


    // User name (make variable public to access from outside)
    public static final String KEY_STAFF_ID = "staffid";
    public static final String KEY_LOGIN = "login";

    public static final String KEY_LAST_SYNCED = "last_synced";

    public static final String KEY_TEMPLATE = "template";


    // Constructor
    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public SessionManagement() {

    }

    public void CreateLoginSession(String staffname, String staffid, String login_status) {
        // Storing username in pref
        editor.putString(KEY_STAFF_NAME, staffname);
        // Storing role in pref
        editor.putString(KEY_STAFF_ID, staffid);
        editor.putString(KEY_LOGIN, login_status);

        // commit changes
        editor.commit();
    }

    public void SaveLastSynced(String date) {
        editor.putString(KEY_LAST_SYNCED, date);
        editor.commit();
    }

    public void SaveLoginStatus(String status) {
        editor.putString(KEY_LOGIN, status);
        editor.commit();
    }

    public void SaveTemplate(String template, String staff_id) {
        editor.putString(KEY_TEMPLATE, template + "___" + staff_id);
        editor.commit();
    }


    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_STAFF_NAME, pref.getString(KEY_STAFF_NAME, null));

        // user email id
        user.put(KEY_STAFF_ID, pref.getString(KEY_STAFF_ID, "TXX"));

        user.put(KEY_LOGIN, pref.getString(KEY_LOGIN, "false"));
        user.put(KEY_LAST_SYNCED, pref.getString(KEY_LAST_SYNCED, "2019-03-25 12:00:00"));
        user.put(KEY_TEMPLATE, pref.getString(KEY_TEMPLATE, "0___0"));
        // return user
        return user;
    }

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, Main2Activity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


}
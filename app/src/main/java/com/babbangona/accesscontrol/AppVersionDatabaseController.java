package com.babbangona.accesscontrol;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jason Wei
 */
public class AppVersionDatabaseController extends ContentProvider {

    /*
     * This activity contains all the methods that retrieves data from the SQlite database
     * also it has the content provider implementation that allows other application to access its database
     *
     * */

    private static final String TAG = "AppVersionDatabase";

    private static final String DATABASE_NAME = "version_controller.db";
    private static final String VERSION_BINDING_TABLE = "version_binding";

    /*
     * The string below are the column names for the version controller table
     * */
    public static final String PACKAGE_NAME = "package_name";
    public static final String ACCESS_CONTROL_VERSION = "access_control_version";
    public static final String APP_VERSION = "application_version";
    public static final String STAFF_ID = "staff_id";
    public static final String APP_NAME = "app_name";
    public static final String USER_VERSION = "user_version";
    public static final String APP_LAST_USED_DATE = "app_last_used_date";

    private static final int DATABASE_VERSION = 1;
    public static final Uri CONTENT_URI = Uri.parse("content://"
            + AppVersionDatabaseController.AUTHORITY + "/" + VERSION_BINDING_TABLE);
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.jwei512." + VERSION_BINDING_TABLE;


    public static final String AUTHORITY = "com.babbangona.accesscontrol";

    private static final UriMatcher sUriMatcher;

    // url provided to other applications
    static final String URL = "content://" + AUTHORITY + "/" + VERSION_BINDING_TABLE;

    private static final int NOTES = 1;

    private static final int NOTES_ID = 2;

    private static HashMap<String, String> notesProjectionMap;

    public static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //OnCreate method is called to create all the important databases for our android application
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + VERSION_BINDING_TABLE + " (" + PACKAGE_NAME
                    + " TEXT PRIMARY KEY," + ACCESS_CONTROL_VERSION + " TEXT," + USER_VERSION + " TEXT," + STAFF_ID + " TEXT, " + APP_NAME + " TEXT, " + APP_LAST_USED_DATE + " TEXT );");

        }

        // OnUpgrade method is called to handle database changes
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

        }

        public void insertOnlineVersionDownload(JSONArray jsonArray) {
            SQLiteDatabase database = getWritableDatabase();
            JSONObject jsonObject = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                int count = 0;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    Cursor cursor = database.rawQuery("select count(" + PACKAGE_NAME + ") from " + VERSION_BINDING_TABLE + " where " + PACKAGE_NAME + " = \"" + jsonObject.getString("package_name") + "\" ", null);
                    cursor.moveToFirst();
                    if (!cursor.isAfterLast()) {
                        count = cursor.getInt(0);
                    }

                    if (count == 0) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(PACKAGE_NAME, jsonObject.getString("package_name"));
                        contentValues.put(ACCESS_CONTROL_VERSION, jsonObject.getString("app_version"));
                        contentValues.put(USER_VERSION, jsonObject.getString("user_app_version"));
                        contentValues.put(APP_NAME, jsonObject.getString("app_name"));
                        contentValues.put(STAFF_ID, jsonObject.getString("staff_id"));


                        database.insert(VERSION_BINDING_TABLE, null, contentValues);
                    } else {

                        ContentValues contentValues = new ContentValues();

                        contentValues.put(PACKAGE_NAME, jsonObject.getString("package_name"));
                        contentValues.put(ACCESS_CONTROL_VERSION, jsonObject.getString("app_version"));
                        contentValues.put(USER_VERSION, jsonObject.getString("user_app_version"));
                        contentValues.put(APP_NAME, jsonObject.getString("app_name"));
                        contentValues.put(STAFF_ID, jsonObject.getString("staff_id"));
                        String where = PACKAGE_NAME + "=?";
                        String[] whereArgs = new String[]{String.valueOf(jsonObject.getString("package_name"))};

                        database.update(VERSION_BINDING_TABLE, contentValues, where, whereArgs);
                    }


                } catch (JSONException e) {
                    Log.d(TAG, e.toString());
                }


            }


        }

        public ArrayList<Map<String, String>> getAppVersion() {
            SQLiteDatabase database = getWritableDatabase();

            ArrayList<Map<String, String>> appVersions = new ArrayList<>();
            Map<String, String> map = null;

            Cursor cursor = database.rawQuery("select " + PACKAGE_NAME + "," + ACCESS_CONTROL_VERSION + "," + USER_VERSION + "," + APP_NAME + " from " +
                    "" + VERSION_BINDING_TABLE + " where " + ACCESS_CONTROL_VERSION + " !='' and " + USER_VERSION + " != '' ", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                map = new HashMap<>();
                map.put("package_name", cursor.getString(0));
                map.put("access_control_version", cursor.getString(1));
                map.put("app_version", cursor.getString(2));
                map.put("app_name", cursor.getString(3));
                appVersions.add(map);
                Log.d("Version_Response", appVersions + " ");

                cursor.moveToNext();
            }

            cursor.close();
            return appVersions;
        }

        public ArrayList<Map<String, String>> getAppVersionByPackageName(String package_name) {
            SQLiteDatabase database = getWritableDatabase();

            ArrayList<Map<String, String>> appVersions = new ArrayList<>();
            Map<String, String> map = null;

            Cursor cursor = database.rawQuery("select " + PACKAGE_NAME + "," + ACCESS_CONTROL_VERSION + "," + USER_VERSION + "," + APP_NAME + " from " +
                    "" + VERSION_BINDING_TABLE + " where " + PACKAGE_NAME + " = \"" + package_name + "\" ", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                map = new HashMap<>();
                map.put("package_name", cursor.getString(0));
                map.put("access_control_version", cursor.getString(1));
                map.put("app_version", cursor.getString(2));
                map.put("app_name", cursor.getString(3));
                appVersions.add(map);
                Log.d("Version_Response", appVersions + " ");

                cursor.moveToNext();
            }

            cursor.close();
            return appVersions;
        }

        public void InsertVersionRecords(String package_name, String user_version, String staff_id) {
            Log.d("HHEELLOONOW", package_name + " " + user_version + " " + staff_id);
            int count = 0;
            SQLiteDatabase database = getWritableDatabase();
            Cursor cursor = database.rawQuery("select count(" + PACKAGE_NAME + ") from " + VERSION_BINDING_TABLE + " where " + PACKAGE_NAME + " = '" + package_name + "' ", null);
            cursor.moveToFirst();

            if (!cursor.isAfterLast()) {
                count = cursor.getInt(0);
            }
            Log.d("HHEELLOONOW", count + " ");
            if (count == 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(PACKAGE_NAME, package_name);
                contentValues.put(USER_VERSION, user_version);
                contentValues.put(STAFF_ID, staff_id);


                long x = database.insert(VERSION_BINDING_TABLE, null, contentValues);
                Log.d("HHEELLOONOW", x + " ");

            } else {

                ContentValues contentValues = new ContentValues();
                contentValues.put(USER_VERSION, user_version);
                contentValues.put(STAFF_ID, staff_id);

                String where = PACKAGE_NAME + "= \"" + package_name + "\"";
                String[] whereArgs = new String[]{package_name};

                int x = database.update(VERSION_BINDING_TABLE, contentValues, where, null);
                Log.d("HHEELLOONOW", x + " ");
            }


        }

        public ArrayList<Map<String, String>> UploadAppVersion(String staff_id) {
            ArrayList<Map<String, String>> uploadVersions;
            Map<String, String> map = null;
            SQLiteDatabase database = getWritableDatabase();
            Cursor cursor = database.rawQuery("select " + PACKAGE_NAME + "," + USER_VERSION + "," + STAFF_ID + "," + APP_LAST_USED_DATE + " from " + VERSION_BINDING_TABLE + " where " + STAFF_ID + " = \"" + staff_id + "\" and " + USER_VERSION + " != '' ", null);
            cursor.moveToFirst();
            uploadVersions = new ArrayList<>();

            while (!cursor.isAfterLast()) {
                map = new HashMap<>();
                map.put("package_name", cursor.getString(cursor.getColumnIndex(PACKAGE_NAME)));
                map.put("user_version", cursor.getString(cursor.getColumnIndex(USER_VERSION)));
                map.put("staff_id", cursor.getString(cursor.getColumnIndex(STAFF_ID)));
                map.put("app_last_used_date", cursor.getString(cursor.getColumnIndex(APP_LAST_USED_DATE)));

                uploadVersions.add(map);
                Log.d("--H--E", uploadVersions + "");
                cursor.moveToNext();
            }

            return uploadVersions;
        }

        public void LastOpened(String packageName) {
            SQLiteDatabase database = getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String date_updated = dateFormat.format(date);
            ContentValues contentValues = new ContentValues();
            contentValues.put(APP_LAST_USED_DATE, date_updated);

            String where = PACKAGE_NAME + "=?";
            String[] whereArgs = new String[]{packageName};

            database.update(VERSION_BINDING_TABLE, contentValues, where, whereArgs);

        }


    }

    private DatabaseHelper dbHelper;

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case NOTES:
                break;
            case NOTES_ID:
                where = where + "_id = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int count = db.delete(VERSION_BINDING_TABLE, where, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case NOTES:
                return CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    // The insert method is called when other apps push data into the incentive app
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != NOTES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(VERSION_BINDING_TABLE, PACKAGE_NAME, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }
// This query method is used to pull records from the incentive database based on some criteria

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(VERSION_BINDING_TABLE);
        qb.setProjectionMap(notesProjectionMap);

        switch (sUriMatcher.match(uri)) {
            case NOTES:
                break;
            case NOTES_ID:
                selection = selection + "_id = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case NOTES:
                count = db.update(VERSION_BINDING_TABLE, values, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, VERSION_BINDING_TABLE, NOTES);
        sUriMatcher.addURI(AUTHORITY, VERSION_BINDING_TABLE + "/#", NOTES_ID);

/*        notesProjectionMap = new HashMap<String, String>();
        notesProjectionMap.put(Note.Notes.OPERATION_ID, Note.Notes.OPERATION_ID);
        notesProjectionMap.put(Note.Notes.ACTIVITY_NAME, Note.Notes.ACTIVITY_NAME);
        notesProjectionMap.put(Note.Notes.STAFF_ID, Note.Notes.STAFF_ID);
        notesProjectionMap.put(Note.Notes.COMPLETION_DATE, Note.Notes.COMPLETION_DATE);
        notesProjectionMap.put(Note.Notes.UPLOAD_STATUS, Note.Notes.UPLOAD_STATUS);*/

    }
}
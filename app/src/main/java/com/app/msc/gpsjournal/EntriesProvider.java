package com.app.msc.gpsjournal;

/**
 * Created by Michael on 8/14/2016.
 * Created using the ContentProvider-SQLite module sample code. Modified to work with a
 * more extensive database, though all functions remain essentially the same as the original.
 */
import java.util.HashMap;

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
import android.text.TextUtils;


/*
 *  content provider is implemented as a subclass of ContentProvider class and
 *  must implement a standard set of APIs that enable other applications to
 *  perform transactions.
 */
public class EntriesProvider extends ContentProvider {

    //specify the query string in the form of a URI
    static final String PROVIDER_NAME = "com.msc.contentprovider.EntriesProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/entries";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String DATE = "date";
    static final String NOTE = "note";
    static final String XCOORD = "xcoord";
    static final String YCOORD = "ycoord";
    static final String PHOTO = "photo";


    private static HashMap<String, String> ENTRIES_PROJECTION_MAP;

    static final int ENTRIES = 1;
    static final int ENTRY_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "entries", ENTRIES);
        uriMatcher.addURI(PROVIDER_NAME, "entries/#", ENTRY_ID);
    }

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Journal";
    static final String ENTRIES_TABLE_NAME = "entries";
    static final int DATABASE_VERSION = 0;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + ENTRIES_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " date TEXT NOT NULL, " +
                    " note TEXT NOT NULL, " +
                    " xcoord DOUBLE NOT NULL, " +
                    " ycoord DOUBLE NOT NULL, " +
                    " photo TEXT);";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  ENTRIES_TABLE_NAME);

            //This method is called when the provider is started.
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         * Add a new student record
         */
        long rowID = db.insert(	ENTRIES_TABLE_NAME, "", values);
        /**
         * If record is added successfully
         */
        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(_uri, null);
            }
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ENTRIES_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ENTRIES:
                qb.setProjectionMap(ENTRIES_PROJECTION_MAP);
                break;
            case ENTRY_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder.equals("")){
            /**
             * By default sort on student names
             */
            sortOrder = DATE;
        }
        Cursor c = qb.query(db,	projection,	selection, selectionArgs,
                null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        if (getContext() != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case ENTRIES:
                count = db.delete(ENTRIES_TABLE_NAME, selection, selectionArgs);
                break;
            case ENTRY_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( ENTRIES_TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case ENTRIES:
                count = db.update(ENTRIES_TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case ENTRY_ID:
                count = db.update(ENTRIES_TABLE_NAME, values, _ID +
                        " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all student records
             */
            case ENTRIES:
                return "vnd.android.cursor.dir/vnd.example.entries";
            /**
             * Get a particular student
             */
            case ENTRY_ID:
                return "vnd.android.cursor.item/vnd.example.entries";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}

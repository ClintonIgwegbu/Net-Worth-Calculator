package com.nile.networthcalculator;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AssetProvider extends ContentProvider {

    // Perhaps one database for food items and another for saved food items?

    // Database columns
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TOTAL_CASH = "total_cash";
    public static final String COLUMN_TOTAL_INVESTED_ASSETS = "total_invested_assets";
    public static final String COLUMN_TOTAL_USE_ASSETS = "total_use_assets";

    // Database Related Constants
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "assets";
    private static final String DATABASE_TABLE = "assets";

    // Content Provider Uri and Authority
    public static final String AUTHORITY = "com.nile.macros.AssetProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/assets");

    // MIME types used for listing items or looking up a single item
    private static final String ITEMS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.com.nile.assets.items";
    private static final String ITEM_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.com.nile.asset.item";

    // UriMatcher stuff
    private static final int LIST = 0;
    private static final int ITEM = 1;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    // The database itself
    SQLiteDatabase db;

    // Called when the app is started up
    @Override
    public boolean onCreate() {
        // Grab a connection to our database
        db = new DatabaseHelper(getContext()).getWritableDatabase();
        return true;
    }

    // Builds a UriMatcher for search suggestion and shortcut refresh queries
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "assets", LIST);
        matcher.addURI(AUTHORITY, "assets/#", ITEM);
        return matcher;
    }

    // This method is required in order to query the supported types
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case LIST:
                return ITEMS_MIME_TYPE;
            case ITEM:
                return ITEM_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    // CRUD database operations
    // Create
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Database returns id of new row when you insert sth into it -
        // Therefore it doesn't make sense to allow you to specify a key during insertion
        if (values.containsKey(COLUMN_ID))
            throw new UnsupportedOperationException();

        // Insert entry into database and return its id
        long id = db.insertOrThrow(DATABASE_TABLE, null, values);

        // The context acts as a way to pass background information to a new component
        // We have just inserted a new item into the table, so we notify any listeners of change
        // Recall that one of main responsibilities of a ContentProvider is to notify listeners of
        // changes to the data.
        getContext().getContentResolver().notifyChange(uri, null);

        // return the uri of the newly added task
        return ContentUris.withAppendedId(uri, id);
    }

    // Update
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        if (values.containsKey(COLUMN_ID))
            throw new UnsupportedOperationException();

        // Update the database and return the number of entries that were updated
        int count = db.update(
                DATABASE_TABLE,
                values,
                COLUMN_ID + "=?",
                new String[]{Long.toString(ContentUris.parseId(uri))}
        );

        // Notify listeners that the database has been changed
        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        // Return the number of entries that have been updated
        return count;
    }

    // Delete
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Delete entries from the database and return count of entries that were deleted
        int count = db.delete(
                DATABASE_TABLE,
                COLUMN_ID + "=?",
                new String[]{Long.toString(ContentUris.parseId(uri))}
        );

        // Notify any listeners of changes to the database
        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        // Return count of deleted entries
        return count;
    }

    // Read
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] ignored1, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String[] projection = new String[]{
                COLUMN_ID,
                COLUMN_TOTAL_CASH,
                COLUMN_TOTAL_INVESTED_ASSETS,
                COLUMN_TOTAL_USE_ASSETS
        };

        Cursor c;
        switch (URI_MATCHER.match(uri)) {

            case LIST:
                c = db.query(DATABASE_TABLE,
                        projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case ITEM:
                c = db.query(DATABASE_TABLE, projection,
                        COLUMN_ID + "=?",
                        new String[]{Long.toString(ContentUris.parseId(uri))},
                        null, null, null, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);

        }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    // A helper class which knows how to create and upgrade the database;
    // onCreate is called when the app is installed
    // onUpgrade is called when the database version is upgraded (with an app upgrade)
    protected static class DatabaseHelper extends SQLiteOpenHelper {

        static final String DATABASE_CREATE =
                "create table " + DATABASE_TABLE + " (" +
                        COLUMN_ID + " integer primary key autoincrement, " +
                        COLUMN_TOTAL_CASH + " double not null, " +
                        COLUMN_TOTAL_INVESTED_ASSETS + " double not null, " +
                        COLUMN_TOTAL_USE_ASSETS + " double not null)";

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);  // execute SQL command
        }

        // Upgrades the database using SQL ALTER statements
        // This is done instead of deleting the old database and making the knew one so as to
        // retain user information between database (and hence app) upgrades
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            throw new UnsupportedOperationException();
        }
    }

}

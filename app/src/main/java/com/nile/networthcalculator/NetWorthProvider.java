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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.URI;

// Content allows stored information to be shared across apps if needed. This allows other
// apps to access stored data securely. Content provider offers CRUD operations so that a
// Content resolver (in client app) can query the stored data. The stored data does not
// necessarily have to be a SQLite database. E.g. it could be in Json format.
public class NetWorthProvider extends ContentProvider {

    // Log tag
    private static final String TAG = "NetWorthProvider";

    // Common database columns
    private static final String COLUMN_ID = "_id";

    // Balance sheet columns
    private static final String COLUMN_NAME = "item_name";
    public static final String COLUMN_VALUE = "item_value";

    // History table columns
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_ASSETS = "total_assets";
    private static final String COLUMN_LIABILITIES = "total_liabilities";
    private static final String COLUMN_NETWORTH = "net_worth";

    // Database Related Constants
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "networth";
    private static final String BALANCE_SHEET_TABLE = "balancesheet";
    private static final String HISTORY_TABLE = "history";

    // Content Provider Uri and Authority
    public static final String AUTHORITY = "com.nile.networthcalculator.NetWorthProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/networth");

    // The content provider supports three types of data (balance sheet, history item and full history)
    // so three MIME types are defined
    // MIME types used for obtaining balance sheet, net worth at a specified month, and entire
    // net worth history
    private static final String BALANCE_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.com.nile.networthcalculator.networth.balancesheet";
    private static final String HISTORY_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.com.nile.networthcalculator.networth.history";
    private static final String FULL_HISTORY_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.com.nile.networthcalculator.networth.fullhistory";

    // Uri Matcher stuff
    private static final int BALANCE_SHEET = 0;
    private static final int HISTORY = 1;
    private static final int FULL_HISTORY = 2;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    // Builds up a UriMatcher for search suggestion and shortcut refresh queries
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "networth/balancesheet", BALANCE_SHEET);
        matcher.addURI(AUTHORITY, "networth/history/#", HISTORY);
        matcher.addURI(AUTHORITY, "networth/history", FULL_HISTORY);
        return matcher;
    }

    // This method is required in order to query the supported types
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case BALANCE_SHEET:
                return BALANCE_MIME_TYPE;
            case HISTORY:
                return HISTORY_MIME_TYPE;
            case FULL_HISTORY:
                return FULL_HISTORY_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    // The database itself
    SQLiteDatabase db;

    // Called when the app is started up
    @Override
    public boolean onCreate() {
        // Grab a connection to our database
        db = new DatabaseHelper(getContext()).getWritableDatabase();
        return true;
    }

    // CRUD content provider operations
    // Create
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // You don't get to choose the entry id
        if (values.containsKey(COLUMN_ID))
            throw new UnsupportedOperationException();

        long id;
        switch (URI_MATCHER.match(uri)) {
            case BALANCE_SHEET:
                throw new UnsupportedOperationException("Cannot insert balance sheet row.");
            case HISTORY:
            case FULL_HISTORY:
                id = db.insertOrThrow(HISTORY_TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

        // Notify listeners of changes to the data - one of the functions of a content provider
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the uri of the newly added task
        return ContentUris.withAppendedId(uri, id);
    }

    // Update
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int count;

        // You can't change a db entry id, column name or column date
        if (values.containsKey(COLUMN_ID) || values.containsKey(COLUMN_NAME) || values.containsKey(COLUMN_DATE))
            throw new UnsupportedOperationException();

        // Update the database and return the number of entries that were updated
        switch (URI_MATCHER.match(uri)) {
            case BALANCE_SHEET:
                count = db.update(
                        BALANCE_SHEET_TABLE,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            case HISTORY:
            case FULL_HISTORY:
                count = db.update(
                        HISTORY_TABLE,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

        // Notify listeners that the database has been changed
        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        // Return the number of entries that have been updated
        return count;
    }

    // Delete
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;

        // Delete entries from the database and return count of entries that were deleted
        switch (URI_MATCHER.match(uri)) {
            case BALANCE_SHEET:
                throw new IllegalArgumentException("Cannot delete balance sheet row.");
            case HISTORY:
            case FULL_HISTORY:
                count = db.delete(
                        HISTORY_TABLE,
                        COLUMN_DATE + "=?",
                        selectionArgs
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

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
        String[] balanceProjection = new String[]{
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_VALUE
        };

        String[] historyProjection = new String[]{
                COLUMN_ID,
                COLUMN_DATE,
                COLUMN_ASSETS,
                COLUMN_LIABILITIES,
                COLUMN_NETWORTH
        };

        Cursor c;
        switch (URI_MATCHER.match(uri)) {
            case BALANCE_SHEET:
                c = db.query(BALANCE_SHEET_TABLE,
                        balanceProjection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case FULL_HISTORY:
                c = db.query(HISTORY_TABLE,
                        historyProjection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case HISTORY:
                c = db.query(HISTORY_TABLE, historyProjection,
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
        return c;  // Return the result of the query
    }

    // This is the database
    protected static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        static final String BALANCE_TABLE_CREATE =
            "create table " + BALANCE_SHEET_TABLE + " (" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_NAME + " String not null, " +
                    COLUMN_VALUE + " double not null);";

        static final String HISTORY_TABLE_CREATE =
            "create table " + HISTORY_TABLE + " (" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_DATE + " integer not null, " +
                    COLUMN_ASSETS + " double not null, " +
                    COLUMN_LIABILITIES + " double not null, " +
                    COLUMN_NETWORTH + " double not null);";

        // Called when the app is installed
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(BALANCE_TABLE_CREATE);  // create balance sheet table
            db.execSQL(HISTORY_TABLE_CREATE); // create history table
            populateBalanceSheet(db);  // populate balance sheet with asset/liability rows
        }

        // Upgrades the database using SQL ALTER statements
        // This is done instead of deleting the old database and making the knew one so as to
        // retain user information between database (and hence app) upgrades
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            throw new UnsupportedOperationException();
        }

        private static void populateBalanceSheet(SQLiteDatabase db) {
            ContentValues[] values = new ContentValues[45];
            for (int i = 0; i < values.length; i++)
                values[i] = new ContentValues(2);
            values[0].put(COLUMN_NAME, "checking_accounts");
            values[0].put(COLUMN_VALUE, 0.0);
            values[1].put(COLUMN_NAME, "savings_accounts");
            values[1].put(COLUMN_VALUE, 0.0);
            values[2].put(COLUMN_NAME, "money_market_accounts");
            values[2].put(COLUMN_VALUE, 0.0);
            values[3].put(COLUMN_NAME, "savings_bonds");
            values[3].put(COLUMN_VALUE, 0.0);
            values[4].put(COLUMN_NAME, "cds");
            values[4].put(COLUMN_VALUE, 0.0);
            values[5].put(COLUMN_NAME, "cash_value_life_insurance");
            values[5].put(COLUMN_VALUE, 0.0);
            values[6].put(COLUMN_NAME, "brokerage");
            values[6].put(COLUMN_VALUE, 0.0);
            values[7].put(COLUMN_NAME, "other_taxable");
            values[7].put(COLUMN_VALUE, 0.0);
            values[8].put(COLUMN_NAME, "ira");
            values[8].put(COLUMN_VALUE, 0.0);
            values[9].put(COLUMN_NAME, "roth_ira");
            values[9].put(COLUMN_VALUE, 0.0);
            values[10].put(COLUMN_NAME, "401k");
            values[10].put(COLUMN_VALUE, 0.0);
            values[11].put(COLUMN_NAME, "sep_ira");
            values[11].put(COLUMN_VALUE, 0.0);
            values[12].put(COLUMN_NAME, "keogh");
            values[12].put(COLUMN_VALUE, 0.0);
            values[13].put(COLUMN_NAME, "pension");
            values[13].put(COLUMN_VALUE, 0.0);
            values[14].put(COLUMN_NAME, "annuity");
            values[14].put(COLUMN_VALUE, 0.0);
            values[15].put(COLUMN_NAME, "real_estate");
            values[15].put(COLUMN_VALUE, 0.0);
            values[16].put(COLUMN_NAME, "sole_propietorship");
            values[16].put(COLUMN_VALUE, 0.0);
            values[17].put(COLUMN_NAME, "partnership");
            values[17].put(COLUMN_VALUE, 0.0);
            values[18].put(COLUMN_NAME, "ccorporation");
            values[18].put(COLUMN_VALUE, 0.0);
            values[19].put(COLUMN_NAME, "scorporation");
            values[19].put(COLUMN_VALUE, 0.0);
            values[20].put(COLUMN_NAME, "llc");
            values[20].put(COLUMN_VALUE, 0.0);
            values[21].put(COLUMN_NAME, "other_business_interests");
            values[21].put(COLUMN_VALUE, 0.0);
            values[22].put(COLUMN_NAME, "principal_home");
            values[22].put(COLUMN_VALUE, 0.0);
            values[23].put(COLUMN_NAME, "vacation_home");
            values[23].put(COLUMN_VALUE, 0.0);
            values[24].put(COLUMN_NAME, "cars_trucks_boats");
            values[24].put(COLUMN_VALUE, 0.0);
            values[25].put(COLUMN_NAME, "home_furnishings");
            values[25].put(COLUMN_VALUE, 0.0);
            values[26].put(COLUMN_NAME, "art");
            values[26].put(COLUMN_VALUE, 0.0);
            values[27].put(COLUMN_NAME, "jewelry_furs");
            values[27].put(COLUMN_VALUE, 0.0);
            values[28].put(COLUMN_NAME, "other_use_assets");
            values[28].put(COLUMN_VALUE, 0.0);
            values[29].put(COLUMN_NAME, "total_cash");
            values[29].put(COLUMN_VALUE, 0.0);
            values[30].put(COLUMN_NAME, "total_invested_assets");
            values[30].put(COLUMN_VALUE, 0.0);
            values[31].put(COLUMN_NAME, "total_use_assets");
            values[31].put(COLUMN_VALUE, 0.0);
            values[32].put(COLUMN_NAME, "total_assets");
            values[32].put(COLUMN_VALUE, 0.0);
            values[33].put(COLUMN_NAME, "credit_card_balances");
            values[33].put(COLUMN_VALUE, 0.0);
            values[34].put(COLUMN_NAME, "income_tax_owed");
            values[34].put(COLUMN_VALUE, 0.0);
            values[35].put(COLUMN_NAME, "other_bills");
            values[35].put(COLUMN_VALUE, 0.0);
            values[36].put(COLUMN_NAME, "home_mortgage");
            values[36].put(COLUMN_VALUE, 0.0);
            values[37].put(COLUMN_NAME, "home_equity_loan");
            values[37].put(COLUMN_VALUE, 0.0);
            values[38].put(COLUMN_NAME, "mortgages_on_rentals");
            values[38].put(COLUMN_VALUE, 0.0);
            values[39].put(COLUMN_NAME, "car_loans");
            values[39].put(COLUMN_VALUE, 0.0);
            values[40].put(COLUMN_NAME, "student_loans");
            values[40].put(COLUMN_VALUE, 0.0);
            values[41].put(COLUMN_NAME, "life_insurance_policy_loans");
            values[41].put(COLUMN_VALUE, 0.0);
            values[42].put(COLUMN_NAME, "other_long_term_debt");
            values[42].put(COLUMN_VALUE, 0.0);
            values[43].put(COLUMN_NAME, "total_liabilities");
            values[43].put(COLUMN_VALUE, 0.0);
            values[44].put(COLUMN_NAME, "net_worth");
            values[44].put(COLUMN_VALUE, 0.0);
            for (ContentValues rowValue: values)
                db.insertOrThrow(BALANCE_SHEET_TABLE, null, rowValue);
        }
    }

}

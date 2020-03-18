package videodemos.example.restaurantinspector.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBAdapter {

    /////////////////////////////////////////////////////////////////////
    //	Constants & Data
    /////////////////////////////////////////////////////////////////////
    // For logging:
    private static final String TAG = "DBAdapter";

    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "RestaurantInspectorDB";
    public static final String DATABASE_TABLE_INSPECTIONS = "Inspections";
    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 1;

    /*
     * CHANGE 1:
     */
    // TODO: Setup your fields here:
    /*
        Restaurant Table
     */
    public static final String DATABASE_TABLE_RESTAURANTS = "Restaurants";

    public static final String KEY_TRACKING_NUMBER = "trackingNumber";
    public static final String KEY_RESTAURANT_NAME = "restaurantName";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_CITY = "city";
    public static final String KEY_FAC_TYPE = "type";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";


    // TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
    public static final int COL_TRACKING_NUMBER = 0;
    public static final int COL_RESTAURANT_NAME = 1;
    public static final int COL_ADDRESS = 2;
    public static final int COL_CITY = 3;
    public static final int COL_FAC_TYPE = 4;
    public static final int COL_LATITUDE = 5;
    public static final int COL_LONGITUDE = 6;


    public static final String[] ALL_RESAURANT_KEYS = new String[]{
            KEY_TRACKING_NUMBER,
            KEY_RESTAURANT_NAME,
            KEY_ADDRESS,
            KEY_CITY,
            KEY_FAC_TYPE,
            KEY_LATITUDE,
            KEY_LONGITUDE
    };

    private static final String DATABASE_CREATE_RESTAURANTS_SQL =
            "CREATE TABLE " + DATABASE_TABLE_RESTAURANTS
                    + " (" + KEY_TRACKING_NUMBER + " TEXT PRIMARY KEY, "
                    + KEY_RESTAURANT_NAME + " TEXT NOT NULL, "
                    + KEY_ADDRESS + " TEXT NOT NULL, "
                    + KEY_CITY + " TEXT NOT NULL, "
                    + KEY_FAC_TYPE + " TEXT NOT NULL, "
                    + KEY_LATITUDE + " REAL NOT NULL, "
                    + KEY_LONGITUDE + " REAL NOT NULL "
                    /*
                     * CHANGE 2:
                     */
                    // + KEY_{...} + " {type} not null"
                    //	- Key is the column name you created above.
                    //	- {type} is one of: text, integer, real, blob
                    //		(http://www.sqlite.org/datatype3.html)
                    //  - "not null" means it is a required field (must be given a value).
                    // NOTE: All must be comma separated (end of line!) Last one must have NO comma!!
                    // Rest  of creation:
                    + ");";

    // Context of application who uses us.
    private final Context context;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    /////////////////////////////////////////////////////////////////////
    //	Public methods:
    /////////////////////////////////////////////////////////////////////

    public DBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    // Add a new set of values to the database.
    public long insertRow(String trackingNumber, String name, String physicalAddress,
                          String physicalCity, String factype, double latitude, double longitude) {

        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TRACKING_NUMBER, trackingNumber);
        initialValues.put(KEY_RESTAURANT_NAME, name);
        initialValues.put(KEY_ADDRESS, physicalAddress);
        initialValues.put(KEY_CITY, physicalCity);
        initialValues.put(KEY_FAC_TYPE, factype);
        initialValues.put(KEY_LATITUDE, latitude);
        initialValues.put(KEY_LONGITUDE, longitude);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_RESTAURANTS, null, initialValues);
    }


    // Return all data in the database.
    public Cursor getAllRestaurantRows() {
        String where = null;
        Cursor c = db.query(true, DATABASE_TABLE_RESTAURANTS, ALL_RESAURANT_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }


    /////////////////////////////////////////////////////////////////////
    //	Private Helper Classes:
    /////////////////////////////////////////////////////////////////////

    /**
     * Private class which handles database creation and upgrading.
     * Used to handle low-level database access.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_RESTAURANTS_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_RESTAURANTS);

            // Recreate new database:
            onCreate(_db);
        }
    }

}
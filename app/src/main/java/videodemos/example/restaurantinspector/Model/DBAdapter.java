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
    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 15;

    /*
     * CHANGE 1:
     */

    // region Restaurant Table Data

    public static final String DATABASE_TABLE_RESTAURANTS = "Restaurants";

    public static final String KEY_TRACKING_NUMBER = "trackingNumber";
    public static final String KEY_RESTAURANT_NAME = "restaurantName";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_CITY = "city";
    public static final String KEY_FAC_TYPE = "type";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";


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

    //endregion Restaurant Table Data

    //region Inspection Table Data

    public static final String DATABASE_TABLE_INSPECTIONS = "Inspections";

    public static final String KEY_INSPECTION_ROW_ID = "_id";
    public static final String KEY_TRACKING_NUMBER_INSPECTION = "trackingNumber";
    public static final String KEY_HAZARD_RATING = "hazardRating";
    public static final String KEY_INSPECTION_DATE = "inspectionDate";
    public static final String KEY_INSP_TYPE = "inspType";
    public static final String KEY_NUM_CRITICAL = "numCritical";
    public static final String KEY_NUM_NON_CRITICAL = "numNonCritical";

    public static final int COL_INSPECTION_ROW_ID = 0;
    public static final int COL_TRACKING_NUMBER_INSPECTION = 1;
    public static final int COL_HAZARD_RATING = 2;
    public static final int COL_INSPECTION_DATE = 3;
    public static final int COL_INSP_TYPE = 4;
    public static final int COL_NUM_CRITICAL = 5;
    public static final int COL_NUM_NON_CRITICAL = 6;


    public static final String[] ALL_INSPECTION_KEYS = new String[]{
            KEY_INSPECTION_ROW_ID,
            KEY_TRACKING_NUMBER_INSPECTION,
            KEY_HAZARD_RATING,
            KEY_INSPECTION_DATE,
            KEY_INSP_TYPE,
            KEY_NUM_CRITICAL,
            KEY_NUM_NON_CRITICAL
    };

    private static final String DATABASE_CREATE_INSPECTIONS_SQL =
            "CREATE TABLE " + DATABASE_TABLE_INSPECTIONS
                    + " (" + KEY_INSPECTION_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_TRACKING_NUMBER_INSPECTION + " TEXT,"
                    + KEY_HAZARD_RATING + " TEXT NOT NULL, "
                    + KEY_INSPECTION_DATE + " TEXT, "
                    + KEY_INSP_TYPE + " TEXT NOT NULL, "
                    + KEY_NUM_CRITICAL + " INT NOT NULL, "
                    + KEY_NUM_NON_CRITICAL + " INT NOT NULL, "
                    + "FOREIGN KEY (" + KEY_TRACKING_NUMBER_INSPECTION + ") REFERENCES " + DATABASE_TABLE_RESTAURANTS + "(" + KEY_TRACKING_NUMBER + ")"
                    + ");";

    //endregion Inspection Table Data

    //region Violation Table Data
    // Violations(trackingNumber, date, violationCode)
    public static final String DATABASE_TABLE_VIOLATIONS = "Violations";

    public static final String KEY_TRACKING_NUMBER_VIOLATION = "trackingNumber";
    public static final String KEY_VIOLATION_DATE = "inspectionDate";
    public static final String KEY_VIOLATION_CODE = "violationCode";

    public static final int COL_TRACKING_NUMBER_VIOLATION = 0;
    public static final int COL_VIOLATION_DATE = 1;
    public static final int COL_VIOLATION_CODE = 2;

    public static final String[] ALL_VIOLATION_KEYS = new String[]{
            KEY_TRACKING_NUMBER_VIOLATION,
            KEY_VIOLATION_DATE,
            KEY_VIOLATION_CODE
    };

    private static final String DATABASE_CREATE_VIOLATIONS_SQL =
            "CREATE TABLE " + DATABASE_TABLE_VIOLATIONS
                    + " (" + KEY_TRACKING_NUMBER_VIOLATION + " TEXT,"
                    + KEY_VIOLATION_DATE + " TEXT, "
                    + KEY_VIOLATION_CODE + " INT, "
                    + "PRIMARY KEY (" + KEY_TRACKING_NUMBER_VIOLATION + ", " + KEY_VIOLATION_DATE + ", " + KEY_VIOLATION_CODE + ")"
                    + ");";

    //endregion Violation Table Data


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



    // Add a new set of values to the Restaurants Table.
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

    public Cursor getAllRestaurantRows() {
        String where = null;
        Cursor c = db.query(true, DATABASE_TABLE_RESTAURANTS, ALL_RESAURANT_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }



    // Add a new set of values to the Inspections Table.
    public long insertRow(String trackingNumber, String hazardRating, String inspectionDate, String inspType,
                          int numCritical, int numNonCritical) {

        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TRACKING_NUMBER_INSPECTION, trackingNumber);
        initialValues.put(KEY_HAZARD_RATING, hazardRating);
        initialValues.put(KEY_INSPECTION_DATE, inspectionDate);
        initialValues.put(KEY_INSP_TYPE, inspType);
        initialValues.put(KEY_NUM_CRITICAL, numCritical);
        initialValues.put(KEY_NUM_NON_CRITICAL, numNonCritical);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_INSPECTIONS, null, initialValues);
    }

    public Cursor getAllInspectionRows() {
        String where = null;
        Cursor c = db.query(true, DATABASE_TABLE_INSPECTIONS, ALL_INSPECTION_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Add a new set of values to the Inspections Table.
    public long insertViolationRow(String trackingNumber, String inspectionDate,
                                   int violationCode) {

        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TRACKING_NUMBER_VIOLATION, trackingNumber);
        initialValues.put(KEY_VIOLATION_DATE, inspectionDate);
        initialValues.put(KEY_VIOLATION_CODE, violationCode);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_VIOLATIONS, null, initialValues);
    }

    public Cursor getAllViolationRows() {
        String where = null;
        Cursor c = db.query(true, DATABASE_TABLE_VIOLATIONS, ALL_VIOLATION_KEYS,
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
            _db.execSQL(DATABASE_CREATE_INSPECTIONS_SQL);
            _db.execSQL(DATABASE_CREATE_VIOLATIONS_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_RESTAURANTS);
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_INSPECTIONS);
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_VIOLATIONS);

            // Recreate new database:
            onCreate(_db);
        }
    }

}
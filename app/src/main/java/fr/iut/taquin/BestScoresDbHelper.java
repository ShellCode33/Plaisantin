package fr.iut.taquin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by shellcode on 3/11/17.
 */

public class BestScoresDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + BestScoresContract.Entry.TABLE_NAME + " (" +
                    BestScoresContract.Entry._ID + " INTEGER PRIMARY KEY," +
                    BestScoresContract.Entry.PLAYER_PSEUDO + " TEXT," +
                    BestScoresContract.Entry.PLAYER_MOVES_COUNT + " INTEGER," +
                    BestScoresContract.Entry.PLAYER_TIME + " INTEGER," +
                    BestScoresContract.Entry.GRID_SIZE + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BestScoresContract.Entry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "scores.db";

    public BestScoresDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public final class BestScoresContract {
        // To prevent someone from accidentally instantiating the contract class,
        // make the constructor private.
        private BestScoresContract() {}

        /* Inner class that defines the table contents */
        public class Entry implements BaseColumns {
            public static final String TABLE_NAME = "scores";
            public static final String PLAYER_PSEUDO = "pseudo";
            public static final String PLAYER_MOVES_COUNT = "moves_count";
            public static final String PLAYER_TIME = "time";
            public static final String GRID_SIZE = "grid_size";

        }
    }

}

package fr.iut.taquin;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by shellcode on 3/9/17.
 */

public class BestScoreActivity extends AppCompatActivity {

    private LinearLayout mainLayout;
    private BestScoresDbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bestscores);
        mainLayout = (LinearLayout)findViewById(R.id.mainLayout);


        dbHelper = new BestScoresDbHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        for(int grid_size = 2; grid_size <= 6; grid_size++)
            mainLayout.addView(createLayoutScoresOfCategory(grid_size));

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableFullScreen();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) enableFullScreen();
    }

    private void enableFullScreen() {

        mainLayout.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private LinearLayout createLayoutScoresOfCategory(int grid_size) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_bestscores_entry, mainLayout, false);
        TextView title = (TextView) layout.findViewById(R.id.categoryTitle);
        LinearLayout container = (LinearLayout) layout.findViewById(R.id.entriesContainer);

        title.setText(getResources().getString(R.string.category) + " " + grid_size + "x" + grid_size);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                BestScoresDbHelper.BestScoresContract.Entry._ID,
                BestScoresDbHelper.BestScoresContract.Entry.PLAYER_PSEUDO,
                BestScoresDbHelper.BestScoresContract.Entry.PLAYER_MOVES_COUNT,
                BestScoresDbHelper.BestScoresContract.Entry.PLAYER_TIME,
                BestScoresDbHelper.BestScoresContract.Entry.GRID_SIZE
        };

        String selection = BestScoresDbHelper.BestScoresContract.Entry.GRID_SIZE + " = ?";
        String[] selectionArgs = {Integer.toString(grid_size)};

        String sortOrder = BestScoresDbHelper.BestScoresContract.Entry.PLAYER_MOVES_COUNT + " DESC, " + BestScoresDbHelper.BestScoresContract.Entry.PLAYER_TIME + " ASC";

        Cursor cursor = db.query(
                BestScoresDbHelper.BestScoresContract.Entry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        boolean hasData = false;

        while(cursor.moveToNext()) {
            hasData = true;
            String pseudo = cursor.getString(cursor.getColumnIndexOrThrow(BestScoresDbHelper.BestScoresContract.Entry.PLAYER_PSEUDO));
            Integer moves_count = cursor.getInt(cursor.getColumnIndexOrThrow(BestScoresDbHelper.BestScoresContract.Entry.PLAYER_MOVES_COUNT));
            long elaspedTime = cursor.getLong(cursor.getColumnIndexOrThrow(BestScoresDbHelper.BestScoresContract.Entry.PLAYER_TIME));

            long milliseconds = elaspedTime % 100;
            long seconds = elaspedTime / 1000 % 60;
            long minutes = elaspedTime / (60 * 1000) % 60;

            StringBuilder stringBuilder = new StringBuilder();

            if(minutes < 10)
                stringBuilder.append("0");

            stringBuilder.append(minutes);
            stringBuilder.append("m ");

            if(seconds < 10)
                stringBuilder.append("0");

            stringBuilder.append(seconds);
            stringBuilder.append("s ");

            if(milliseconds < 10)
                stringBuilder.append("0");

            stringBuilder.append(milliseconds + "ms");

            TextView row = new TextView(getApplicationContext());
            row.setText(pseudo + " : " + moves_count + " " + getResources().getString(R.string.moves) + " " + getResources().getString(R.string.in) + " " + stringBuilder.toString());
            container.addView(row);
        }

        cursor.close();

        if(!hasData) {
            TextView row = new TextView(getApplicationContext());
            row.setText(getResources().getString(R.string.empty));
            container.addView(row);
        }

        return layout;
    }
}

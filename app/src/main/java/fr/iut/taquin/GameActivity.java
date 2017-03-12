package fr.iut.taquin;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by shellcode on 3/6/17.
 */

public class GameActivity extends AppCompatActivity {

    private LinearLayout mainLayout;
    private GameCore gameCore;
    private GridView gridGameView;
    private Chrono chrono;
    private BestScoresDbHelper dbHelper;
    private SQLiteDatabase db;
    private Button giveUpButton;
    private Button hintButton;
    private Button shuffleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        dbHelper = new BestScoresDbHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        mainLayout = (LinearLayout) findViewById(R.id.main_layout);

        chrono = new Chrono(this);
        chrono.setGravity(Gravity.CENTER);
        chrono.setPadding(30, 30, 30, 30);
        mainLayout.addView(chrono, 0);
        chrono.start();

        gridGameView = (GridView) mainLayout.findViewById(R.id.gameView);
        gameCore = new GameCore(getIntent().getIntExtra("grid_size", 3));
        gridGameView.setNumColumns(gameCore.getGridSize());

        final ItemAdapter itemAdapter = new ItemAdapter(getApplicationContext(), gameCore.getItems());
        itemAdapter.setNewPositions(gameCore.getActualState()); //Le jeu a été mélangé, on le notifie à l'adapter
        gridGameView.setAdapter(itemAdapter);

        gridGameView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (id != -1) {
                if (gameCore.play(id)) {
                    itemAdapter.setNewPositions(gameCore.getActualState());
                    itemAdapter.notifyDataSetChanged();

                    if(gameCore.isGameWon()) {
                        winUpdate();

                        WinDialogFragment winDialogFragment = new WinDialogFragment();
                        winDialogFragment.show(getFragmentManager(), "win_dialog");

                        winDialogFragment.setOnWinDialogEvent(new WinDialogFragment.WinDialogListener() {
                            @Override
                            public void onDialogPositiveClick(DialogFragment dialog) {
                                WinDialogFragment winDialog = (WinDialogFragment)dialog;
                                saveNewScore(winDialog.getPseudo());
                            }

                            @Override
                            public void onDialogNegativeClick(DialogFragment dialog) {

                            }
                        });
                    }
                }

                else
                    Toast.makeText(getApplicationContext(), R.string.wrong_move, Toast.LENGTH_SHORT).show();
            }
            }
        });

        hintButton = (Button)mainLayout.findViewById(R.id.hintButton);
        shuffleButton = (Button)mainLayout.findViewById(R.id.shuffleButton);
        giveUpButton = (Button)mainLayout.findViewById(R.id.giveUpButton);

        hintButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                itemAdapter.setNewPositions(gameCore.getWinMatrix());
                itemAdapter.notifyDataSetChanged();
            }

            else if (event.getAction() == MotionEvent.ACTION_UP) {
                itemAdapter.setNewPositions(gameCore.getActualState());
                itemAdapter.notifyDataSetChanged();
            }

            return true;
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            gameCore.shuffle();
            itemAdapter.setNewPositions(gameCore.getActualState());
            itemAdapter.notifyDataSetChanged();
            }
        });

        giveUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) enableFullScreen();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableFullScreen();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
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

    private void winUpdate() {
        chrono.stop();
        gridGameView.setOnItemClickListener(null);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.win_toast).replace("%i", Integer.toString(gameCore.getMovesCount())), Toast.LENGTH_SHORT).show();

        gridGameView.removeAllViewsInLayout();

        Item fullGridItem = new Item(GameCore.getGameImage());
        gridGameView.setNumColumns(1);
        gridGameView.setAdapter(new ItemAdapter(getApplicationContext(), new Item[]{fullGridItem}));
        giveUpButton.setText(R.string.go_back);
        hintButton.setEnabled(false);
        shuffleButton.setEnabled(false);

    }

    public void saveNewScore(String pseudo) {

        ContentValues values = new ContentValues();
        values.put(BestScoresDbHelper.BestScoresContract.Entry.PLAYER_PSEUDO, pseudo);
        values.put(BestScoresDbHelper.BestScoresContract.Entry.PLAYER_MOVES_COUNT, gameCore.getMovesCount());
        values.put(BestScoresDbHelper.BestScoresContract.Entry.PLAYER_TIME, chrono.getDuration());
        values.put(BestScoresDbHelper.BestScoresContract.Entry.GRID_SIZE, gameCore.getGridSize());

        db.insert(BestScoresDbHelper.BestScoresContract.Entry.TABLE_NAME, null, values);
    }
}

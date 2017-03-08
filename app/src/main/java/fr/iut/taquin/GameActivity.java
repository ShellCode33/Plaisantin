package fr.iut.taquin;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by shellcode on 3/6/17.
 */

public class GameActivity extends AppCompatActivity {

    private LinearLayout mainLayout;
    private GameCore gameCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);

        Button resolveButton = (Button)mainLayout.findViewById(R.id.resolveButton);

        resolveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Chrono chrono = new Chrono(this);
        chrono.setGravity(Gravity.CENTER);
        chrono.setPadding(30, 30, 30, 30);
        mainLayout.addView(chrono, 0);
        //chrono.start();

        final GridView gridGameView = (GridView) mainLayout.findViewById(R.id.gameView);
        gameCore = new GameCore(getIntent().getIntExtra("grid_size", 3));
        gridGameView.setNumColumns(gameCore.getGridSize());

        final ItemAdapter itemAdapter = new ItemAdapter(getApplicationContext(), gameCore.getItems());
        itemAdapter.setNewPositions(gameCore.getActualState()); //Le jeu a été mélangé, on le notifie à l'adapter
        gridGameView.setAdapter(itemAdapter);

        gridGameView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(id != -1) {
                    if(gameCore.play(id)) {
                        itemAdapter.setNewPositions(gameCore.getActualState());
                    }

                    else
                        Toast.makeText(getApplicationContext(), R.string.wrong_move, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableFullScreen();
    }

    private void enableFullScreen() {

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null)
            actionBar.hide();

        mainLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}

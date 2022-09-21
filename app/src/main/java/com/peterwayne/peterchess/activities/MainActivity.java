package com.peterwayne.peterchess.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.peterwayne.peterchess.R;
import com.peterwayne.peterchess.gui.GameSetup;
import com.peterwayne.peterchess.gui.GameUI;


public class MainActivity extends AppCompatActivity {
    private LinearLayout mainView;
    private GameUI gameUI;
    private GameSetup gameSetup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
    }

    private void addControls() {
        mainView = findViewById(R.id.mainView);
        initGameSetUp();
        initUI();

    }

    private void initUI() {
        gameUI = new GameUI(this,gameSetup);
        mainView.addView(gameUI);
    }

    private void initGameSetUp() {
        String playerType = getIntent().getStringExtra("playerType");
        gameSetup = new GameSetup(playerType);

    }

}
package com.peterwayne.peterchess.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.peterwayne.peterchess.R;
import com.peterwayne.peterchess.gui.GameSetup;
import com.peterwayne.peterchess.gui.GameUI;


public class MainActivity extends AppCompatActivity {
    private LinearLayout mainView;
    private BottomNavigationView bottomNavigationView ;
    private GameUI gameUI;
    private GameSetup gameSetup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        initGameSetUp();
        initGameUI();
        initSpace();
        initNavigation();


    }

    private void initSpace() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,0.5f);
        LinearLayout space = new LinearLayout(this);
        space.setLayoutParams(params);
        mainView.addView(space);
        Log.d("nav", "space");
    }

    private void initNavigation() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomNavigationView = new BottomNavigationView(this);
        bottomNavigationView.inflateMenu(R.menu.bottom_nav_in_game);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_UNLABELED);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setLayoutParams(params);
        mainView.addView(bottomNavigationView);
        Log.d("nav", "here");
    }

    private void addControls() {
        mainView = findViewById(R.id.mainView);
    }

    private void initGameUI() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,1f);
        gameUI = new GameUI(this,gameSetup);
        gameUI.setLayoutParams(params);
        mainView.addView(gameUI);
    }

    private void initGameSetUp() {
        String playerType = getIntent().getStringExtra("playerType");
        gameSetup = new GameSetup(playerType);
    }

}
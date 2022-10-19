package com.peterwayne.peterchess.activities;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.peterwayne.peterchess.R;
import com.peterwayne.peterchess.adapter.MoveLogAdapter;
import com.peterwayne.peterchess.engine.board.Board;
import com.peterwayne.peterchess.gui.GameSetup;
import com.peterwayne.peterchess.gui.GameUI;


public class MainActivity extends AppCompatActivity {
    private LinearLayout mainView;
    private BottomNavigationView bottomNavigationView ;
    private GameUI gameUI, tempBoard;
    private GameSetup gameSetup;
    private RecyclerView moveHistoryUI;
    private MoveLogAdapter moveLogAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        initGameSetUp();
        initGameUI();
        initTempBoardUI();
        initMoveHistoryUI();
        initSpace();
        initNavigation();
        addEvents();
    }

    private void initTempBoardUI() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,1f);
        tempBoard = new GameUI(this,gameSetup);
        tempBoard.removeAllObservers();
        tempBoard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        tempBoard.setLayoutParams(params);
        mainView.addView(tempBoard);
        tempBoard.setVisibility(View.GONE);
    }

    private void initMoveHistoryUI() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        moveHistoryUI = new RecyclerView(this);
        moveLogAdapter = new MoveLogAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        moveHistoryUI.setLayoutManager(linearLayoutManager);
        gameUI.addObserver(moveLogAdapter);
        moveHistoryUI.setAdapter(moveLogAdapter);
        moveHistoryUI.setLayoutParams(params);
        mainView.addView(moveHistoryUI);
    }

    private void addEvents() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_previous_move:
                        backToPreviousMove();
                        break;
                    case R.id.nav_next_move:
                        forwardToNextMove();
                        break;
                    case R.id.nav_flip_board:
                        gameUI.flipBoard();
                        break;
                }
                return true;
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void forwardToNextMove() {
        int currentPosition = moveLogAdapter.getSelectedPosition();
        if(currentPosition<moveLogAdapter.getItemCount()-1)
        {
            currentPosition++;
            moveLogAdapter.setSelectedPosition(currentPosition);
            moveLogAdapter.notifyDataSetChanged();
            moveHistoryUI.scrollToPosition(currentPosition);
            if(currentPosition==moveLogAdapter.getMoveLog().size()-1)
            {
                gameUI.setVisibility(VISIBLE);
                tempBoard.setVisibility(View.GONE);
            }else
            {
                tempBoard.setChessBoard(moveLogAdapter.getMoveLog().get(currentPosition+1).getBoard());
                tempBoard.setInstantMove(moveLogAdapter.getMoveLog().get(currentPosition));
                tempBoard.invalidate();
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void backToPreviousMove() {
        int currentPosition = moveLogAdapter.getSelectedPosition();
        if(currentPosition>0)
        {
            currentPosition--;
            moveLogAdapter.setSelectedPosition(currentPosition);
            moveLogAdapter.notifyDataSetChanged();
            moveHistoryUI.scrollToPosition(currentPosition);
            tempBoard.setChessBoard(moveLogAdapter.getMoveLog().get(currentPosition+1).getBoard());
            tempBoard.setInstantMove(moveLogAdapter.getMoveLog().get(currentPosition));

        }else {
            moveLogAdapter.setSelectedPosition(-1);
            moveLogAdapter.notifyDataSetChanged();
            tempBoard.setChessBoard(Board.createStandardBoard());
            tempBoard.setInstantMove(null);
        }
        gameUI.setVisibility(View.GONE);
        tempBoard.setVisibility(VISIBLE);
        tempBoard.invalidate();
    }

    private void initSpace() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,0.5f);
        LinearLayout space = new LinearLayout(this);
        space.setLayoutParams(params);
        mainView.addView(space);
    }

    private void initNavigation() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomNavigationView = new BottomNavigationView(this);
        bottomNavigationView.inflateMenu(R.menu.bottom_nav_in_game);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_UNLABELED);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setLayoutParams(params);
        mainView.addView(bottomNavigationView);
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

    public RecyclerView getMoveHistoryUI() {
        return moveHistoryUI;
    }
}
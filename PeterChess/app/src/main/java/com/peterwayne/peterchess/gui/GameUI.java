package com.peterwayne.peterchess.gui;

import static com.peterwayne.peterchess.engine.board.BoardUtils.NUM_TILES_PER_ROW;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import com.peterwayne.peterchess.engine.board.Board;
import com.peterwayne.peterchess.engine.board.BoardUtils;
import com.peterwayne.peterchess.engine.board.Move;
import com.peterwayne.peterchess.engine.board.MoveTransition;
import com.peterwayne.peterchess.engine.pieces.Piece;
import com.peterwayne.peterchess.engine.player.ai.MoveStrategy;
import com.peterwayne.peterchess.engine.player.ai.StockAlphaBeta;
import com.peterwayne.peterchess.pattern.MyObservable;
import com.peterwayne.peterchess.pattern.MyObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GameUI extends View implements MyObservable {
    private final int TILE_SIZE = getScreenWidth() / 8;
    private Board chessBoard;
    private BoardUI boardUI;
    private Piece sourceTile;
    private Piece humanMovedPiece;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;
    private ArrayList<MyObserver> observers;

    public GameUI(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        observers = new ArrayList<>();

        chessBoard = Board.createStandardBoard();
        this.boardUI = new BoardUI(context);
        this.gameSetup = new GameSetup();
        this.moveLog = new MoveLog();
        observers.add(new TableGameAIWatcher());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boardUI.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("touch", pixelToTileCoordinate(event.getX(), event.getY()).toString());
        Log.d("touch", pixelToTileId(event.getX(), event.getY()) + "");

        int tileId = pixelToTileId(event.getX(), event.getY());
        if (sourceTile == null) {
            sourceTile = chessBoard.getPiece(tileId);
            humanMovedPiece = sourceTile;

            if (humanMovedPiece == null) {
                sourceTile = null;
            }
        } else {
            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getPiecePosition(), tileId);

            final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                chessBoard = transition.getToBoard();
                moveLog.addMove(move);
                Log.d("move", moveLog.getMoves().get(moveLog.size()-1).toString());
            }
            sourceTile = null;
            humanMovedPiece = null;
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Log.d("here", "reached");
                moveMadeUpdate(PlayerType.HUMAN);
                boardUI.invalidate();
                invalidate();
            }
        });


        return super.onTouchEvent(event);
    }

    public int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private Coordinate2D pixelToTileCoordinate(final float x, final float y) {
        int xCor = (int) Math.floor(x / TILE_SIZE) * TILE_SIZE;
        int yCor = (int) Math.floor(y / TILE_SIZE) * TILE_SIZE;
        return new Coordinate2D(xCor, yCor);
    }

    private int pixelToTileId(final float x, final float y) {
        Coordinate2D coordinate = pixelToTileCoordinate(x, y);
        return NUM_TILES_PER_ROW * (coordinate.getY() / TILE_SIZE) + coordinate.getX() / TILE_SIZE;
    }

    @Override
    public void addObserver(MyObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(MyObserver observer) {
        int i = observers.indexOf(observer);
        if(i>=0) observers.remove(i);
    }

    @Override
    public void notifyObservers() {
        for( MyObserver observer : observers)
        {
            observer.update();
        }
    }
    public void moveMadeUpdate(final PlayerType playerType)
    {
        notifyObservers();
    }
    public class BoardUI extends View {
        final List<TileUI> boardTiles;

        public BoardUI(Context context) {
            super(context);
            this.boardTiles = new ArrayList<>();
            generateBoardTiles();
        }

        private void generateBoardTiles() {
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TileUI tilePanel = new TileUI(this, i);
                this.boardTiles.add(tilePanel);
            }
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (final TileUI tilePanel : this.boardTiles) {
                tilePanel.draw(canvas);
            }
            invalidate();
        }
    }

    private class TileUI extends View {

        private final Coordinate2D coordinate;
        private final int tileId;
        private Paint tileColor;

        public TileUI(final BoardUI boardUI, final int tileID) {
            super(boardUI.getContext());
            tileColor = new Paint();
            this.tileId = tileID;
            this.coordinate = calculateTileCoordinate();
            assignTileColor();
        }

        private Coordinate2D calculateTileCoordinate() {
            int x = (this.tileId % NUM_TILES_PER_ROW) * TILE_SIZE;
            int y = (this.tileId / NUM_TILES_PER_ROW * TILE_SIZE);
            return new Coordinate2D(x, y);
        }

        private void assignTileColor() {
            if (BoardUtils.EIGHTH_RANK[this.tileId] || BoardUtils.SIXTH_RANK[this.tileId]
                    || BoardUtils.FOURTH_RANK[this.tileId] || BoardUtils.SECOND_RANK[this.tileId]) {
                this.tileColor.setColor(this.tileId % 2 == 0 ? Color.WHITE : Color.GRAY);
            } else if (BoardUtils.SEVENTH_RANK[this.tileId] || BoardUtils.FIFTH_RANK[this.tileId]
                    || BoardUtils.THIRD_RANK[this.tileId] || BoardUtils.FIRST_RANK[this.tileId]) {
                this.tileColor.setColor(this.tileId % 2 != 0 ? Color.WHITE : Color.GRAY);
            }
        }

        private void assignTilePieceIcon(final Canvas canvas) {

            if (chessBoard.getTile(tileId).isTileOccupied()) {
                final String pieceId = chessBoard.getTile(tileId).getPiece().getPieceAlliance().toString().charAt(0)
                        + chessBoard.getTile(tileId).getPiece().getPieceType().toString();
                Drawable drawable = getContext().getResources().getDrawable(BoardUtils.pieceIcons.get(pieceId));
                Log.d("draw", drawable.toString());
                Rect imageBounds = new Rect(this.coordinate.getX(),
                        this.coordinate.getY(),
                        this.coordinate.getX() + TILE_SIZE,
                        this.coordinate.getY() + TILE_SIZE);
                drawable.setBounds(imageBounds);
                drawable.draw(canvas);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            assignTileColor();
            canvas.drawRect(this.coordinate.getX(),
                    this.coordinate.getY(),
                    this.coordinate.getX() + TILE_SIZE,
                    this.coordinate.getY() + TILE_SIZE,
                    this.tileColor);
            assignTilePieceIcon(canvas);
            invalidate();
        }

        public Coordinate2D getCoordinate() {
            return coordinate;
        }

        public int getTileId() {
            return tileId;
        }

    }

    enum PlayerType {
        HUMAN,
        COMPUTER
    }
    private class TableGameAIWatcher implements MyObserver
    {
        @Override
        public void update() {
            if(gameSetup.isAIPlayer(chessBoard.getCurrentPlayer()))
            {
                AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }
        }
    }
    private class AIThinkTank extends AsyncTask<Void, Void, Move> {
        private AIThinkTank(){};
        @Override
        protected Move doInBackground(Void... voids) {
            final StockAlphaBeta strategy = new StockAlphaBeta(4);
            Move bestMove = strategy.execute(chessBoard);
            return bestMove;
        }
        @Override
        protected void onPostExecute(Move move) {
            super.onPostExecute(move);
            Log.d("done", "done");
            chessBoard = chessBoard.getCurrentPlayer().makeMove(move).getToBoard();
            moveLog.addMove(move);
            Log.d("move", moveLog.getMoves().get(moveLog.size()-1).toString());
            invalidate();
            moveMadeUpdate(PlayerType.COMPUTER);
        }
    }
    public static class MoveLog
    {
        private final List<Move> moves;
        MoveLog()
        {
            this.moves = new ArrayList<>();
        }
        public List<Move> getMoves()
        {
            return this.moves;
        }
        void addMove(final Move move)
        {
            this.moves.add(move);
        }
        public int size()
        {
            return this.moves.size();
        }
        void clear()
        {
            this.moves.clear();
        }
        Move removeMove(final int index) {
            return this.moves.remove(index);
        }

        boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }
    }
}


package com.peterwayne.peterchess.gui;

import static com.peterwayne.peterchess.engine.board.BoardUtils.NUM_TILES_PER_ROW;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.common.collect.Lists;
import com.peterwayne.peterchess.R;
import com.peterwayne.peterchess.engine.board.Board;
import com.peterwayne.peterchess.engine.board.BoardUtils;
import com.peterwayne.peterchess.engine.board.Move;
import com.peterwayne.peterchess.engine.board.MoveTransition;
import com.peterwayne.peterchess.engine.pieces.Piece;
import com.peterwayne.peterchess.engine.player.ai.StockAlphaBeta;
import com.peterwayne.peterchess.pattern.MyObservable;
import com.peterwayne.peterchess.pattern.MyObserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GameUI extends View implements MyObservable {
    private final int TILE_SIZE = getScreenWidth() / 8;
    private final int MAX_TILE_COORDINATE = 7*TILE_SIZE;
    private Board chessBoard;
    private final BoardUI boardUI;
    private Piece sourceTile;
    private Piece humanMovedPiece;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;
    private BoardDirection boardDirection;
    private final ArrayList<MyObserver> observers;
    private Move instantMove;
    public GameUI(Context context, GameSetup gameSetup) {
        super(context);
        observers = new ArrayList<>();
        chessBoard = Board.createStandardBoard();
        this.gameSetup = gameSetup;
        initBoardDirection();
        this.boardUI = new BoardUI(context);
        this.moveLog = new MoveLog();
        observers.add(new TableGameAIWatcher());
        notifyObservers(null);
    }

    public void flipBoard() {
        this.boardDirection = boardDirection.opposite();
        invalidate();
    }


    private void initBoardDirection() {
        if (gameSetup.getWhitePlayerType() == PlayerType.HUMAN) {
            boardDirection = BoardDirection.NORMAL;
        }else {
            boardDirection = BoardDirection.FLIPPED;
        }
    }
    private void updateInstantMove(final Move move)
    {
        this.instantMove = move;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boardUI.draw(canvas);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
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
            final Piece pieceAtTile = chessBoard.getPiece(tileId);
            if(pieceAtTile!=null && pieceAtTile.getPieceAlliance()==chessBoard.getCurrentPlayer().getAlliance())
            {
                sourceTile = chessBoard.getPiece(tileId);
                humanMovedPiece = sourceTile;
            }else
            {
                final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getPiecePosition(), tileId);
                final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
                if (transition.getMoveStatus().isDone()) {
                    chessBoard = transition.getToBoard();
                    moveLog.addMove(move);
                    updateInstantMove(move);
                    notifyObservers(moveLog);
                    Log.d("move", moveLog.getMoves().get(moveLog.size()-1).toString());
                }
                sourceTile = null;
                humanMovedPiece = null;
            }
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
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
        if(boardDirection==BoardDirection.FLIPPED)
        {
            xCor = MAX_TILE_COORDINATE - xCor;
            yCor = MAX_TILE_COORDINATE - yCor;
        }
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
    public void notifyObservers(Object obj) {
        for( MyObserver observer : observers)
        {
            observer.update(obj);
        }
    }
    public void moveMadeUpdate(final PlayerType playerType)
    {
        notifyObservers(playerType);
    }
    public class BoardUI extends View {
        final List<TileUI> boardTiles;
        TileUI start, end;
        Paint paint;
        public BoardUI(Context context) {
            super(context);
            this.boardTiles = new ArrayList<>();
            generateBoardTiles();
            initPaint();
        }

        private void initPaint() {
            paint = new Paint();
            paint.setStrokeWidth(15);
            paint.setColor(Color.BLUE);
            paint.setAlpha(150);
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
            drawAllTiles(canvas);
            highlightInstantMove(canvas);

        }

        private void highlightInstantMove(final Canvas canvas) {
            if(instantMove != null) {
                for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                    TileUI currentTile = boardTiles.get(i);
                    if(currentTile.getTileId() == instantMove.getCurrentCoordinate()) {
                        start = boardTiles.get(i);
                    }
                    else if(currentTile.getTileId() == instantMove.getDestinationCoordinate()) {
                        end = boardTiles.get(i);
                    }
                }
                if(start!=null && end!=null) {
                    drawArrow(paint, canvas,
                            start.getCoordinate().getX()+TILE_SIZE/2.0f,
                            start.getCoordinate().getY()+TILE_SIZE/2.0f,
                            end.getCoordinate().getX()+TILE_SIZE/2.0f,
                            end.getCoordinate().getY()+TILE_SIZE/2.0f);
                }

            }
        }
        private void drawArrow(Paint paint, Canvas canvas, float from_x, float from_y, float to_x, float to_y)
        {
            float angle,anglerad, radius, lineangle;

            //values to change for other appearance *CHANGE THESE FOR OTHER SIZE ARROWHEADS*
            radius=50f;
            angle=75f;

            //some angle calculations
            anglerad= (float) (PI*angle/180.0f);
            lineangle= (float) (atan2(to_y-from_y,to_x-from_x));

            //tha line
            canvas.drawLine(from_x,from_y,to_x,to_y,paint);

            //tha triangle
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(to_x, to_y);
            path.lineTo((float)(to_x-radius*cos(lineangle - (anglerad / 2.0))),
                    (float)(to_y-radius*sin(lineangle - (anglerad / 2.0))));
            path.lineTo((float)(to_x-radius*cos(lineangle + (anglerad / 2.0))),
                    (float)(to_y-radius*sin(lineangle + (anglerad / 2.0))));
            path.close();

            canvas.drawPath(path, paint);
        }

        private void drawAllTiles(final Canvas canvas) {
            for (final TileUI tilePanel : boardDirection.traverse(boardTiles)) {
            tilePanel.draw(canvas);
        }
        }
    }

    private class TileUI extends View {

        private Coordinate2D coordinate;
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
            if(boardDirection == BoardDirection.FLIPPED)
            {
                x = MAX_TILE_COORDINATE - x;
                y = MAX_TILE_COORDINATE - y;
            }
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
        protected void onDraw(final Canvas canvas) {
            super.onDraw(canvas);
            updateCoordinate();
            assignTileColor();
            drawTile(canvas);
            assignTilePieceIcon(canvas);
            highlightLegalMoves(canvas);
            highlightBorder(canvas);

        }
        public Coordinate2D getCoordinate() {
            return coordinate;
        }
        private void highlightBorder(final Canvas canvas) {
            if(humanMovedPiece != null &&
                    humanMovedPiece.getPieceAlliance() == chessBoard.getCurrentPlayer().getAlliance() &&
                    humanMovedPiece.getPiecePosition() == this.tileId) {
                Paint strokePaint = new Paint();
                strokePaint.setStyle(Paint.Style.STROKE);
                strokePaint.setStrokeWidth(5);
                strokePaint.setColor(Color.BLUE);

                canvas.drawRect(this.coordinate.getX(),
                        this.coordinate.getY(),
                        this.coordinate.getX() + TILE_SIZE,
                        this.coordinate.getY() + TILE_SIZE,
                        strokePaint);
            }
        }

        private void drawTile(final Canvas canvas) {
            canvas.drawRect(this.coordinate.getX(),
                this.coordinate.getY(),
                this.coordinate.getX() + TILE_SIZE,
                this.coordinate.getY() + TILE_SIZE,
                this.tileColor);
        }

        private void updateCoordinate() {
            this.coordinate = calculateTileCoordinate();
        }

        private void highlightLegalMoves(final Canvas canvas) {
            for(final Move move: pieceLegalMoves(chessBoard))
            {
                if(move.getDestinationCoordinate() == this.tileId)
                {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable = getContext().getResources().getDrawable(R.drawable.circle);
                    Rect imageBounds = new Rect(this.coordinate.getX(),
                            this.coordinate.getY(),
                            this.coordinate.getX() + TILE_SIZE,
                            this.coordinate.getY() + TILE_SIZE);
                    drawable.setBounds(imageBounds);
                    drawable.setAlpha(150);
                    drawable.draw(canvas);
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board chessBoard) {
            if(humanMovedPiece!=null &&
               humanMovedPiece.getPieceAlliance() == chessBoard.getCurrentPlayer().getAlliance()){
                return humanMovedPiece.calculateLegalMoves(chessBoard);
            }
            return Collections.emptyList();
        }


        public int getTileId() {
            return tileId;
        }

    }
    enum BoardDirection {
        NORMAL {
            @Override
            List<TileUI> traverse(final List<TileUI> boardTiles) {
                return boardTiles;
            }
            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },FLIPPED {
            @Override
            List<TileUI> traverse(final List<TileUI> boardTiles) {
                return Lists.reverse(boardTiles);
            }
            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };
        abstract List<TileUI> traverse(final List<TileUI> boardTiles);
        abstract BoardDirection opposite();
    }
    enum PlayerType {
        HUMAN,
        COMPUTER
    }
    private class TableGameAIWatcher implements MyObserver
    {
        @Override
        public void update(Object o) {
            if(o instanceof PlayerType)
            {
                    if(gameSetup.isAIPlayer(chessBoard.getCurrentPlayer()))
                    {
                        AIThinkTank thinkTank = new AIThinkTank();
                        thinkTank.execute();
                    }
            }
        }
    }
    @SuppressLint("StaticFieldLeak")
    private class AIThinkTank extends AsyncTask<Void, Void, Move> {
        private AIThinkTank(){};

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Move doInBackground(Void... voids) {
            final StockAlphaBeta strategy = new StockAlphaBeta(4);
            Move bestMove = strategy.execute(chessBoard);
            return bestMove;
        }
        @Override
        protected void onPostExecute(Move move) {
            super.onPostExecute(move);
            chessBoard = chessBoard.getCurrentPlayer().makeMove(move).getToBoard();
            moveLog.addMove(move);
            notifyObservers(moveLog);
            updateInstantMove(move);
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


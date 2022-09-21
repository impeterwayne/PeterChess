package com.peterwayne.peterchess.engine.board;

import com.google.common.collect.ImmutableMap;
import com.peterwayne.peterchess.R;
import com.peterwayne.peterchess.engine.pieces.King;
import com.peterwayne.peterchess.engine.pieces.Piece;
import com.peterwayne.peterchess.engine.player.ai.PawnStructureAnalyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum BoardUtils {
    INSTANCE;
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);
    public static final boolean[] EIGHTH_RANK = initRow(0);
    public static final boolean[] SEVENTH_RANK = initRow(8);
    public static final boolean[] SIXTH_RANK = initRow(16);
    public static final boolean[] FIFTH_RANK = initRow(24);
    public static final boolean[] FOURTH_RANK = initRow(32);
    public static final boolean[] THIRD_RANK = initRow(40);
    public static final boolean[] SECOND_RANK = initRow(48);
    public static final boolean[] FIRST_RANK = initRow(56);

    public static final Map<String, Integer> pieceIcons = importPieceIcons();
    private static final List<String> ALGEBRAIC_NOTATION = initAlgebraicNotation();
    private static final int START_TILE_INDEX = 0;
    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;
//    public final Map<String, Integer> POSITION_TO_COORDINATE = initPositionToCoordinateMap();

    private Map<String, Integer> initPositionToCoordinateMap() {
        final Map<String, Integer> positionToCoordinate = new HashMap<>();
        for(int i= START_TILE_INDEX; i <=NUM_TILES;i++)
        {
            positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i),i);
        }
        return Collections.unmodifiableMap(positionToCoordinate);
    }

    private static List<String> initAlgebraicNotation() {
        return Collections.unmodifiableList(Arrays.asList(
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"));
    }

    private static Map<String,Integer> importPieceIcons() {
        Map<String,Integer> icons = new HashMap<>();
        icons.put("BB", R.drawable.bb);
        icons.put("BK",R.drawable.bk);
        icons.put("BN",R.drawable.bn);
        icons.put("BP",R.drawable.bp);
        icons.put("BQ",R.drawable.bq);
        icons.put("BR",R.drawable.br);
        icons.put("WB", R.drawable.wb);
        icons.put("WK",R.drawable.wk);
        icons.put("WN",R.drawable.wn);
        icons.put("WP",R.drawable.wp);
        icons.put("WQ",R.drawable.wq);
        icons.put("WR",R.drawable.wr);
        return ImmutableMap.copyOf(icons);
    }

    private static boolean[] initColumn(int columnNumber) {
        final boolean[] column = new boolean[NUM_TILES];
        do {
            column[columnNumber] = true;
            columnNumber+=NUM_TILES_PER_ROW;
        }while(columnNumber<NUM_TILES);
        return column;
    }
    private static boolean[] initRow(int rowNumber) {
        final boolean[] row = new boolean[NUM_TILES];
        do
        {
            row[rowNumber] = true;
            rowNumber++;
        }while(rowNumber%NUM_TILES_PER_ROW!=0);
        return row;
    }
    public static boolean isValidTileCoordinate(final int coordinate) {
        return coordinate>=0&&coordinate<NUM_TILES;
    }

    public static boolean isKingPawnTrap(final Board board,
                                         final King king,
                                         final int frontTile) {
        final Piece piece = board.getPiece(frontTile);
        return piece!= null &&
                piece.getPieceType() == Piece.PieceType.PAWN &&
                piece.getPieceAlliance() != king.getPieceAlliance();
    }
    public static boolean isEndGame(final Board board) {
        //TODO Repetition cause draw
        return board.getCurrentPlayer().isInCheckMate() ||
                board.getCurrentPlayer().isInStaleMate();
    }
    //Most Valuable Victim - Least Valuable Aggressor
    public static int mvvlva(final Move move)
    {
        final Piece movingPiece = move.getMovedPiece();
        if(move.isAttack())
        {
            final Piece attackedPiece = move.getAttackedPiece();
            return (attackedPiece.getPieceValue() - movingPiece.getPieceValue() + Piece.PieceType.KING.getPieceValue())*100;
        }
        return Piece.PieceType.KING.getPieceValue() - movingPiece.getPieceValue();
    }

    public static boolean kingThreat(final Move move) {
        final Board board = move.getBoard();
        final MoveTransition transition = board.getCurrentPlayer().makeMove(move);
        return transition.getToBoard().getCurrentPlayer().isInCheck();
    }

    public static Collection<Move> lastNMove(final Board board, int N) {
        final List<Move> moveHistory = new ArrayList<>();
        Move currentMove = board.getTransitionMove();
        int i = 0;
        while(currentMove != Move.MoveFactory.getNullMove() && i < N) {
            moveHistory.add(currentMove);
            currentMove = currentMove.getBoard().getTransitionMove();
            i++;
        }
        return Collections.unmodifiableList(moveHistory);
    }
    public String getPositionAtCoordinate(int coordinate) {
        return ALGEBRAIC_NOTATION.get(coordinate);
    }
}

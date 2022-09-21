package com.peterwayne.peterchess.engine.pieces;

import androidx.annotation.NonNull;

import com.peterwayne.peterchess.engine.Alliance;
import com.peterwayne.peterchess.engine.board.Board;
import com.peterwayne.peterchess.engine.board.Move;

import java.util.Collection;
import java.util.Objects;

public abstract class Piece {
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    public final PieceType pieceType;
    public final boolean isFirstMove;

    public Piece(final PieceType pieceType,
                 final int piecePosition,
                 final Alliance pieceAlliance,
                 final boolean isFirstMove) {
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.pieceType = pieceType;
        this.isFirstMove = isFirstMove;
    }

    public int getPiecePosition() {
        return piecePosition;
    }

    public Alliance getPieceAlliance() {
        return pieceAlliance;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }
    public int getPieceValue() {
        return this.pieceType.getPieceValue();
    }
    public abstract Collection<Move> calculateLegalMoves(final Board board);
    public abstract Piece movePiece(final Move move);
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return piecePosition == piece.piecePosition && isFirstMove == piece.isFirstMove && pieceAlliance == piece.pieceAlliance && pieceType == piece.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piecePosition, pieceAlliance, pieceType, isFirstMove);
    }

    public abstract int locationBonus();

//    public abstract int locationBonus();

    public enum PieceType {
        PAWN("P",100),
        KNIGHT("N",300),
        BISHOP("B", 300),
        ROOK("R", 500),
        QUEEN("Q",900),
        KING("K",10000);

        private final String pieceName;
        private final int pieceValue;

        PieceType(final String pieceName,final int pieceValue) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        @NonNull
        @Override
        public String toString() {
            return this.pieceName;
        }


        public int getPieceValue() {
            return pieceValue;
        }
    }
}

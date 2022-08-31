package com.peterwayne.peterchess.engine.pieces;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.peterwayne.peterchess.engine.Alliance;
import com.peterwayne.peterchess.engine.board.Board;
import com.peterwayne.peterchess.engine.board.BoardUtils;
import com.peterwayne.peterchess.engine.board.Move;
import com.peterwayne.peterchess.engine.board.Tile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class King extends Piece{
    private final static int[] CANDIDATE_MOVE_COORDINATE = { -9, -8, -7, -1, 1, 7, 8, 9 };
    private final boolean isCastled;
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;
    public King(final Alliance pieceAlliance,
                final int piecePosition,
                final boolean isFirstMove,
                final boolean isCastled,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }
    public King(final Alliance pieceAlliance,
                final int piecePosition,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance, true);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        King king = (King) o;
        return isCastled == king.isCastled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isCastled);
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.kingBonus(this.piecePosition);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        //TODO fix legalMoves when the king is being in check
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset)
                    || isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
                continue;
            }
            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAtDestinationAlliance = pieceAtDestination.getPieceAlliance();
                    if (this.pieceAlliance != pieceAtDestinationAlliance) {
                        legalMoves.add(
                                new Move.MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                }
            }

        }
        return ImmutableList.copyOf(legalMoves);
    }
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition]
                && (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition]
                && (candidateOffset == -7 || candidateOffset == 1 || candidateOffset == 9);
    }
    @Override
    public King movePiece(final Move move) {
        return new King(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate(),
                false, move.isCastlingMove(),false,false);
    }

    public boolean isCastled() {
        return isCastled;
    }

    public boolean isKingSideCastleCapable() {
        return kingSideCastleCapable;
    }

    public boolean isQueenSideCastleCapable() {
        return queenSideCastleCapable;
    }

    @NonNull
    @Override
    public String toString() {
        return PieceType.KING.toString();
    }
}

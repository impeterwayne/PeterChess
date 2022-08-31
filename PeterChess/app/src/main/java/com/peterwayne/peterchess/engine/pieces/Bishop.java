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

public class Bishop extends Piece{
    private static final int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-9,-7,7,9};
    public Bishop(final Alliance pieceAlliance,
                  final int piecePosition,
                  final boolean isFirstMove) {
        super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
    }

    public Bishop(final Alliance pieceAlliance, final int piecePosition)
    {
        super(PieceType.BISHOP, piecePosition,pieceAlliance,true);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for(final int candidateCoordinateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES)
        {
            int candidateDestinationCoordinate = this.piecePosition;
            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
            {
                if(isFirstColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset)||
                 isEighthColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset))
                {
                    break;
                }
                candidateDestinationCoordinate +=candidateCoordinateOffset;
                if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
                {
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    if(!candidateDestinationTile.isTileOccupied())
                    {
                        legalMoves.add(new Move.MajorMove(board,this,candidateDestinationCoordinate));
                    }else
                    {
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                        if(this.pieceAlliance!=pieceAlliance)
                        {
                            legalMoves.add(new Move.MajorAttackMove(board,this,candidateDestinationCoordinate,pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    private static boolean isFirstColumnExclusion(int candidateDestinationCoordinate, int candidateCoordinateOffset) {
        return BoardUtils.FIRST_COLUMN[candidateDestinationCoordinate] && (candidateCoordinateOffset==-9||candidateCoordinateOffset==7);
    }
    private static boolean isEighthColumnExclusion(int candidateDestinationCoordinate, int candidateCoordinateOffset) {
        return BoardUtils.EIGHTH_COLUMN[candidateDestinationCoordinate] && (candidateCoordinateOffset==-7||candidateCoordinateOffset==9);
    }

    @Override
    public Bishop movePiece(final Move move) {
        return new Bishop(move.getMovedPiece().getPieceAlliance(),move.getDestinationCoordinate());
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.bishopBonus(this.piecePosition);
    }

    @NonNull
    @Override
    public String toString() {
        return Piece.PieceType.BISHOP.toString();
    }
}

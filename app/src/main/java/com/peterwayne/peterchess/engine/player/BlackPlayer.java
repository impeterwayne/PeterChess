package com.peterwayne.peterchess.engine.player;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.peterwayne.peterchess.engine.Alliance;
import com.peterwayne.peterchess.engine.board.Board;
import com.peterwayne.peterchess.engine.board.BoardUtils;
import com.peterwayne.peterchess.engine.board.Move;
import com.peterwayne.peterchess.engine.pieces.Piece;
import com.peterwayne.peterchess.engine.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class BlackPlayer extends Player{
    public BlackPlayer(final Board board,
                       final Collection<Move> whiteStandardLegals,
                       final Collection<Move> blackStandardLegals) {
        super(board, blackStandardLegals, whiteStandardLegals);
    }


    @Override
    public Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                 final Collection<Move> opponentLegals) {
        if(!hasCastleOpportunities())
        {
            return Collections.emptyList();
        }
        final List<Move> kingCastles = new ArrayList<>();
        if(this.playerKing.isFirstMove() &&this.playerKing.getPiecePosition()==4 && !this.isInCheck())
        {
            //king side
            if(this.board.getPiece(5)==null && this.board.getPiece(6)==null)
            {
                final Piece kingSideRook = this.board.getPiece(7);
                if(kingSideRook!=null && kingSideRook.isFirstMove()
                    && Player.calculateAttacksOnTile(5,opponentLegals).isEmpty()
                    && Player.calculateAttacksOnTile(6, opponentLegals).isEmpty()
                    && kingSideRook.getPieceType() == Piece.PieceType.ROOK) {
                    if(!BoardUtils.isKingPawnTrap(this.board, this.playerKing,12)) {
                        kingCastles.add(
                                new Move.KingSideCastleMove(this.board, this.playerKing, 6,
                                        (Rook) kingSideRook , kingSideRook.getPiecePosition(), 5));
                    }
                }

            }
            //queen side
            if(this.board.getPiece(1)==null && this.board.getPiece(2)==null
              && this.board.getPiece(3)==null)
            {
                final Piece queenSideRook = this.board.getPiece(0);
                if(queenSideRook != null && queenSideRook.isFirstMove() &&
                Player.calculateAttacksOnTile(2,opponentLegals).isEmpty() &&
                Player.calculateAttacksOnTile(3, opponentLegals).isEmpty() &&
                queenSideRook.getPieceType() == Piece.PieceType.ROOK)
                {
                    if(!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12))
                    {
                        kingCastles.add(
                                new Move.QueenSideCastleMove(this.board, this.playerKing, 2,
                                        (Rook) queenSideRook, queenSideRook.getPiecePosition(), 3));
                    }
                }
            }

        }
        return Collections.unmodifiableList(kingCastles);
    }
    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public WhitePlayer getOpponent() {
        return this.board.getWhitePlayer();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @NonNull
    @Override
    public String toString() {
        return Alliance.BLACK.toString();
    }
}

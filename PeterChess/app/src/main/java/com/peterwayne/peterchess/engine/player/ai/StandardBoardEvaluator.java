package com.peterwayne.peterchess.engine.player.ai;

import com.peterwayne.peterchess.engine.board.Board;
import com.peterwayne.peterchess.engine.board.Move;
import com.peterwayne.peterchess.engine.pieces.Piece;
import com.peterwayne.peterchess.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator{
    private final static int CHECK_MATE_BONUS = 100000;
    private final static int CHECK_BONUS = 45;
    private final static int CASTLE_BONUS = 25;
    private final static int MOBILITY_MULTIPLIER = 5;
    private final static int ATTACK_MULTIPLIER = 1;
    private final static int TWO_BISHOP_BONUS = 25;
    private static final StandardBoardEvaluator INSTANCE = new StandardBoardEvaluator();

    private StandardBoardEvaluator(){

    };
    public static StandardBoardEvaluator get()
    {
        return INSTANCE;
    }
    @Override
    public int evaluate(final Board board,final int depth) {
        return score(board.getWhitePlayer(),depth) - score(board.getBlackPlayer(),depth);
    }
    private static int score(final Player player,
                             final int depth) {
        return mobility(player) +
                kingThreats(player, depth) +
                attack(player) +
                castle(player) +
                pieceEvaluation(player) +
                pawnStructure(player);
    }

    private static int pawnStructure(final Player player) {
        return PawnStructureAnalyzer.get().pawnStructureScore(player);
    }

    private static int pieceEvaluation(final Player player) {
        int pieceValuationScore = 0 ;
        int numBishops = 0;
        for(final Piece piece : player.getActivePieces())
        {
            pieceValuationScore+=piece.getPieceValue();
//            + piece.locationBonus();
            if(piece.getPieceType() == Piece.PieceType.BISHOP)
            {
                numBishops++;
            }
        }
        return pieceValuationScore + (numBishops==2? TWO_BISHOP_BONUS : 0);
    }

    private static int castle(final Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    private static int attack(final Player player) {
        int attackScore = 0;
        for(final Move move : player.getLegalMoves())
        {
            if(move.isAttack())
            {
                final Piece movedPiece = move.getMovedPiece();
                final Piece attackedPiece = move.getAttackedPiece();
                if(movedPiece.getPieceValue() <= attackedPiece.getPieceValue())
                {
                    attackScore++;
                }
            }
        }
        return attackScore*ATTACK_MULTIPLIER;
    }

    private static int kingThreats(final Player player,final int depth) {
        return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS * depthBonus(depth) : check(player);
    }

    private static int check(final Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int depthBonus(final int depth) {
        return depth == 0 ? 1 : 100*depth;
    }

    private static int mobility(final Player player) {
        return MOBILITY_MULTIPLIER*mobilityRatio(player);
    }

    private static int mobilityRatio(final Player player) {
        return (int) ((player.getLegalMoves().size()*10.0f)/player.getOpponent().getLegalMoves().size());
    }
}

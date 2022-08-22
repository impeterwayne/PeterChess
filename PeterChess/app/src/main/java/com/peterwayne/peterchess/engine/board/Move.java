package com.peterwayne.peterchess.engine.board;

import androidx.annotation.NonNull;

import com.peterwayne.peterchess.engine.pieces.Pawn;
import com.peterwayne.peterchess.engine.pieces.Piece;
import com.peterwayne.peterchess.engine.pieces.Rook;

import java.util.Objects;

public abstract class Move {
    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;
    public static final Move NULL_MOVE = new NullMove();

    private Move(final Board board,final Piece movedPiece,final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }
    private Move(final Board board, final int destinationCoordinate)
    {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return destinationCoordinate == move.destinationCoordinate && isFirstMove == move.isFirstMove && movedPiece.equals(move.movedPiece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movedPiece, destinationCoordinate, isFirstMove);
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }
    public Board getBoard() {
        return board;
    }
    public int getDestinationCoordinate() {
        return destinationCoordinate;
    }
    public boolean isAttack()
    {
        return false;
    }
    public boolean isCastlingMove()
    {
        return false;
    }
    public Piece getAttackedPiece()
    {
        return null;
    }
    public int getCurrentCoordinate() {
        return this.movedPiece.getPiecePosition();
    }

    public Board execute()
    {
        Board.Builder builder = new Board.Builder();
        for(final Piece piece : this.board.getCurrentPlayer().getActivePieces())
        {
            if(!this.movedPiece.equals(piece))
            {
                builder.setPiece(piece);
            }
        }
        for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces())
        {
            builder.setPiece(piece);
        }
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
        return builder.build();
    }

    public static class MajorMove extends Move
    {
        public MajorMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @NonNull
        @Override
        public String toString() {
            return null;
        }
    }
    public static class AttackMove extends Move
    {
        final Piece attackedPiece;

        public AttackMove(final Board board,final Piece movedPiece,final int destinationCoordinate,final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            AttackMove that = (AttackMove) o;
            return attackedPiece.equals(that.attackedPiece);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), attackedPiece);
        }

        @Override
        public boolean isAttack() {
            return true;
        }
        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }

    }
    public static class MajorAttackMove extends AttackMove {

        public MajorAttackMove(final Board board,
                               final Piece movedPiece,
                               final int destinationCoordinate,
                               final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
    }
    public static final class PawnMove extends Move
    {

        public PawnMove(final Board board,
                         final Piece movedPiece,
                         final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString();
        }
    }
    public static class PawnAttackMove extends AttackMove
    {

        public PawnAttackMove(final Board board,
                              final Piece movedPiece,
                              final int destinationCoordinate,
                              final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString();
        }
    }
    public static final class PawnJump extends Move
    {
        public PawnJump(final Board board,
                        final Piece movedPiece,
                        final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for(final Piece piece: this.board.getCurrentPlayer().getActivePieces())
            {
                if(!this.movedPiece.equals(piece))
                {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces())
            {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString();
        }
    }
    public static class PawnPromotion extends Move
    {
        final Move decorateMove;
        final Pawn promotedPawn;
        public PawnPromotion(final Move decorateMove)
        {
            super(decorateMove.getBoard(),decorateMove.getMovedPiece(),decorateMove.getDestinationCoordinate());
            this.decorateMove= decorateMove;
            this.promotedPawn= (Pawn)decorateMove.getMovedPiece();
        }

        @Override
        public Board execute() {
            final Board pawnMovedBoard = this.decorateMove.execute();
            final Board.Builder builder = new Board.Builder();
            for(final Piece piece : pawnMovedBoard.getCurrentPlayer().getActivePieces())
            {
                if(!this.promotedPawn.equals(piece))
                {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : pawnMovedBoard.getCurrentPlayer().getOpponent().getActivePieces())
            {
                builder.setPiece(piece);
            }
            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.getCurrentPlayer().getAlliance());
            return builder.build();
        }
        @Override
        public boolean isAttack() {
            return this.decorateMove.isAttack();
        }
        @Override
        public Piece getAttackedPiece() {
            return this.decorateMove.getAttackedPiece();
        }
        @Override
        public String toString() {
            return "";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            PawnPromotion that = (PawnPromotion) o;
            return decorateMove.equals(that.decorateMove) && promotedPawn.equals(that.promotedPawn);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), decorateMove, promotedPawn);
        }
    }
    public static final class PawnEnPassantAttackMove extends PawnAttackMove {

        public PawnEnPassantAttackMove(final Board board,
                                       final Piece movedPiece,
                                       final int destinationCoordinate,
                                       final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for(final Piece piece: this.board.getCurrentPlayer().getActivePieces())
            {
                if(!this.movedPiece.equals(piece))
                {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces())
            {
                if(!piece.equals(this.getAttackedPiece()))
                {
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

    }
    static abstract class CastleMove extends Move
    {
        final Rook castleRook;
        final int castleRookStart;
        final int castleRookDestination;
        CastleMove(final Board board,
                   final Piece movedPiece,
                   final int destinationCoordinate,
                   final Rook castleRook,
                   final int castleRookStart,
                   final int castleRookDestination) {
            super(board,movedPiece,destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            PawnEnPassantAttackMove.CastleMove that = (PawnEnPassantAttackMove.CastleMove) o;
            return castleRookStart == that.castleRookStart && castleRookDestination == that.castleRookDestination && Objects.equals(castleRook, that.castleRook);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), castleRook, castleRookStart, castleRookDestination);
        }

        public Rook getCastleRook() {
            return castleRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for(final Piece piece : this.board.getAllPieces()) {
                if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece))
                {
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            //set a new rook
            builder.setPiece(new Rook(this.castleRook.getPieceAlliance(), this.castleRookDestination, false));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            builder.setMoveTransition(this);
            return builder.build();
        }
    }
    public static class KingSideCastleMove extends CastleMove
    {

        public KingSideCastleMove(final Board board,
                                  final Piece movedPiece,
                                  final int destinationCoordinate,
                                  final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @NonNull
        @Override
        public String toString() {
            return "O-O";
        }

    }
    public static class QueenSideCastleMove extends CastleMove{

        public QueenSideCastleMove(final Board board,
                            final Piece movedPiece,
                            final int destinationCoordinate,
                            final Rook castleRook,
                            final int castleRookStart,
                            final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @NonNull
        @Override
        public String toString() {
           return "O-O-O";
        }
    }
    public static final class NullMove extends Move {

        public NullMove() {
            super(null, -1);
        }
        @Override
        public int getCurrentCoordinate() {
            return -1;
        }
    }
    public static class MoveFactory
    {
        private MoveFactory()
        {
            throw new RuntimeException("Not instantiable!");
        }
        public static Move createMove(final Board board,
                                      final int currentCoordinate,
                                      final int destinationCoordinate) {
            for(final Move move : board.getAllLegalMoves())
            {
                if(move.getCurrentCoordinate() == currentCoordinate
                   && move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return NULL_MOVE;
        }

        public static Move getNullMove() {
            return NULL_MOVE;
        }
    }
    public enum MoveStatus {
        DONE {
            @Override
            public boolean isDone() {
                return true;
            }
        },
        ILLEGAL_MOVE {
            @Override
            public boolean isDone() {
                return false;
            }
        },
        LEAVE_PLAYER_IN_CHECK {
            @Override
            public boolean isDone() {
                return false;
            }
        };
        public abstract boolean isDone();
    }


}

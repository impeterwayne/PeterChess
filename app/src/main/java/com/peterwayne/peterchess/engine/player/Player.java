package com.peterwayne.peterchess.engine.player;

import static java.util.stream.Collectors.collectingAndThen;

import com.peterwayne.peterchess.engine.Alliance;
import com.peterwayne.peterchess.engine.board.Board;
import com.peterwayne.peterchess.engine.board.Move;
import com.peterwayne.peterchess.engine.board.Move.MoveStatus;
import com.peterwayne.peterchess.engine.board.MoveTransition;
import com.peterwayne.peterchess.engine.pieces.King;
import com.peterwayne.peterchess.engine.pieces.Piece;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;
    public Player(final Board board,
                  final Collection<Move> playerLegals,
                  final Collection<Move> opponentLegals) {
        this.board = board;
        this.playerKing = establishKing();
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(),opponentLegals).isEmpty();
        playerLegals.addAll(calculateKingCastles(playerLegals,opponentLegals));
        this.legalMoves = Collections.unmodifiableCollection(playerLegals);

    }

    public static Collection<Move> calculateAttacksOnTile(final int piecePosition, final Collection<Move> moves) {
//        final List<Move> attackMoves = new ArrayList<>();
//        for(final Move move : moves)
//        {
//            if(piecePosition == move.getDestinationCoordinate())
//            {
//                attackMoves.add(move);
//            }
//        }
//        return ImmutableList.copyOf(attackMoves);
        return moves.stream()
                .filter(move->move.getDestinationCoordinate()==piecePosition)
                .collect(collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }
    public boolean isMoveLegal(final Move move)
    {
        return this.legalMoves.contains(move);
    }
    public boolean isInCheck()
    {
        return this.isInCheck;
    }
    public boolean isInCheckMate()
    {
        return this.isInCheck && !hasEscapeMove();
    }
    public boolean isInStaleMate()
    {
        return !this.isInCheck && !hasEscapeMove();
    }
    private King establishKing(){
//        for(final Piece piece : getActivePieces())
//        {
//            if(piece.getPieceType().isKing())
//            {
//                return (King) piece;
//            }
//        }
//        throw new RuntimeException("Invalid board!");
        return (King) getActivePieces().stream()
                .filter(piece -> piece.getPieceType()== Piece.PieceType.KING)
                .findAny()
                .orElseThrow(RuntimeException::new);
    }

    private boolean hasEscapeMove() {
//        for(final Move move : this.legalMoves)
//        {
//            final MoveTransition transition = makeMove(move);
//            if(transition.getMoveStatus().isDone())
//            {
//                return true;
//            }
//        }
//        return false;
        return this.legalMoves.stream().anyMatch(move -> makeMove(move).getMoveStatus().isDone());
    }
    protected boolean hasCastleOpportunities()
    {
        return !this.isInCheck && !this.isCastled() &&
                (this.playerKing.isKingSideCastleCapable() || this.playerKing.isQueenSideCastleCapable());
    }
    public boolean isCastled()
    {
        return false;
    }

    public King getPlayerKing() {
        return playerKing;
    }

    public MoveTransition makeMove(final Move move) {
        if(!isMoveLegal(move))
        {
            return new MoveTransition(this.board,this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionedBoard = move.execute();
        return transitionedBoard.getCurrentPlayer().getOpponent().isInCheck() ?
                new MoveTransition(this.board,this.board,move, MoveStatus.LEAVE_PLAYER_IN_CHECK) :
                new MoveTransition(this.board, transitionedBoard, move, MoveStatus.DONE);
    }


    public abstract Collection<Piece> getActivePieces();

    public abstract Player getOpponent();

    public abstract Alliance getAlliance();

    public Collection<Move> getLegalMoves()
    {
        return legalMoves;
    }
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> legalMoves, Collection<Move> opponentMoves);
}

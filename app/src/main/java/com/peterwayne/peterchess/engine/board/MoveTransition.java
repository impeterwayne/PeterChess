package com.peterwayne.peterchess.engine.board;

import com.peterwayne.peterchess.engine.board.Move.MoveStatus;

public class MoveTransition {
    private final Board fromBoard;
    private final Board toBoard;
    private final Move transitionMove;
    private final MoveStatus moveStatus;

    public MoveTransition(final Board fromBoard,
                          final Board toBoard,
                          final Move transitionMove,
                          final MoveStatus moveStatus) {
        this.fromBoard = fromBoard;
        this.toBoard = toBoard;
        this.transitionMove = transitionMove;
        this.moveStatus = moveStatus;
    }

    public Board getFromBoard() {
        return fromBoard;
    }

    public Board getToBoard() {
        return toBoard;
    }

    public Move getTransitionMove() {
        return transitionMove;
    }

    public MoveStatus getMoveStatus() {
        return moveStatus;
    }
}

package com.peterwayne.peterchess.model;

import com.peterwayne.peterchess.engine.board.Move;

public class MovePair {
    private int order;
    private Move whiteMove;
    private Move blackMove;

    public MovePair(int order, Move whiteMove, Move blackMove) {
        this.order = order;
        this.whiteMove = whiteMove;
        this.blackMove = blackMove;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Move getWhiteMove() {
        return whiteMove;
    }

    public void setWhiteMove(Move whiteMove) {
        this.whiteMove = whiteMove;
    }

    public Move getBlackMove() {
        return blackMove;
    }

    public void setBlackMove(Move blackMove) {
        this.blackMove = blackMove;
    }
}

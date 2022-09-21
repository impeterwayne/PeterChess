package com.peterwayne.peterchess.engine.player.ai;

import com.peterwayne.peterchess.engine.board.Board;

public interface BoardEvaluator {
    int evaluate(Board board, int depth);
}

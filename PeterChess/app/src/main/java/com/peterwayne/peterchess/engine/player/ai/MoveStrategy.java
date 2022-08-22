package com.peterwayne.peterchess.engine.player.ai;

import com.peterwayne.peterchess.engine.board.Board;
import com.peterwayne.peterchess.engine.board.Move;

public interface MoveStrategy {
    long getNumBoardsEvaluated();
    Move execute(Board board);
}

package com.peterwayne.peterchess.engine.player.ai;

import static com.peterwayne.peterchess.engine.board.BoardUtils.mvvlva;

import android.util.Log;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.peterwayne.peterchess.engine.board.Board;
import com.peterwayne.peterchess.engine.board.BoardUtils;
import com.peterwayne.peterchess.engine.board.Move;
import com.peterwayne.peterchess.engine.board.MoveTransition;
import com.peterwayne.peterchess.engine.player.Player;

import java.util.Collection;
import java.util.Comparator;
import java.util.Observable;

public class StockAlphaBeta extends Observable implements MoveStrategy{
    private final BoardEvaluator evaluator;
    private final int searchDepth;
    private long boardsEvaluated;
    private int quiescenceCount;
    private final int MAX_QUIESCENCE = 5000*5;
    private enum MoveSorter {
        STANDARD {
            @Override
            Collection<Move> sort(Collection<Move> moves) {
                return Ordering.from((Comparator<Move>)(move1, move2) -> ComparisonChain.start()
                        .compareTrueFirst(move1.isCastlingMove(),move2.isCastlingMove())
                        .compare(mvvlva(move2), mvvlva(move1))
                        .result()).immutableSortedCopy(moves);
            }
        },
        EXPENSIVE {
            @Override
            Collection<Move> sort(Collection<Move> moves) {
                return Ordering.from((Comparator<Move>)(move1, move2) -> ComparisonChain.start()
                            .compareTrueFirst(BoardUtils.kingThreat(move1), BoardUtils.kingThreat(move2))
                            .compareTrueFirst(move1.isCastlingMove(),move2.isCastlingMove())
                            .compare(BoardUtils.mvvlva(move2),BoardUtils.mvvlva(move1))
                            .result()).immutableSortedCopy(moves);
            }
        };
        abstract Collection<Move> sort(Collection<Move> moves);
    }
    @Override
    public Move execute(final Board board) {
        final long startTime = System.currentTimeMillis();
        final Player currentPlayer = board.getCurrentPlayer();
        Move bestMove = Move.MoveFactory.getNullMove();
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        Log.d("Engine", board.getCurrentPlayer() + " thinking with depth = " + this.searchDepth);
        int moveCounter =1;
        int numMoves = board.getCurrentPlayer().getLegalMoves().size();
        for(final Move move: MoveSorter.EXPENSIVE.sort(board.getCurrentPlayer().getLegalMoves()))
        {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            this.quiescenceCount = 0;
            final String s;
            if(moveTransition.getMoveStatus().isDone())
            {
                final long candidateMoveStartTime = System.nanoTime();
                currentValue = currentPlayer.getAlliance().isWhite() ?
                        min(moveTransition.getToBoard(),this.searchDepth-1,highestSeenValue,lowestSeenValue) :
                        max(moveTransition.getToBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue);
                if(currentPlayer.getAlliance().isWhite() && currentValue>highestSeenValue)
                {
                    highestSeenValue = currentValue;
                    bestMove = move;
                    if(moveTransition.getToBoard().getBlackPlayer().isInCheckMate())
                    {
                        break;
                    }
                }else if(currentPlayer.getAlliance().isBlack() && currentValue <lowestSeenValue)
                {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                    if(moveTransition.getToBoard().getWhitePlayer().isInCheckMate())
                    {
                        break;
                    }
                }
                final String quiescenceInfo = " " + score(currentPlayer, highestSeenValue, lowestSeenValue) + " q: " +this.quiescenceCount;
                s = "\t" + toString() + "(" +this.searchDepth+ "), m: (" +moveCounter+ "/" +numMoves+ ") " + move + ", best:  " + bestMove

                        + quiescenceInfo + ", t: " +calculateTimeTaken(candidateMoveStartTime, System.nanoTime());

            }else {
                s = "\t" + toString() + "(" +this.searchDepth + ")" + ", m: (" +moveCounter+ "/" +numMoves+ ") " + move + " is illegal! best: " +bestMove;
            }
            Log.d("Engine",s);
            setChanged();
            notifyObservers();
            moveCounter++;
        }

        final long executionTime = System.currentTimeMillis() - startTime;
        final String result = board.getCurrentPlayer() + " SELECTS " +bestMove+ " [#boards evaluated = " +this.boardsEvaluated+
                " time taken = " + executionTime /1000+ " rate = " +(1000 * ((double)this.boardsEvaluated/ executionTime));
        System.out.printf("%s SELECTS %s [#boards evaluated = %d, time taken = %d ms, rate = %.1f\n", board.getCurrentPlayer(),
                bestMove, this.boardsEvaluated, executionTime, (1000 * ((double)this.boardsEvaluated/ executionTime)));
        setChanged();
        notifyObservers(result);
        return bestMove;
    }

    private String calculateTimeTaken(final long start,final long end) {
        final long timeTaken = (end - start) / 1000000;
        return timeTaken + " ms";
    }

    private static String score(final Player currentPlayer,
                                final int highestSeenValue,
                                int lowestSeenValue) {
        if(currentPlayer.getAlliance().isWhite()) {
            return "[score: " +highestSeenValue + "]";
        } else if(currentPlayer.getAlliance().isBlack()) {
            return "[score: " +lowestSeenValue+ "]";
        }
        throw new RuntimeException("Error while calculating!");
    }

    public StockAlphaBeta(final int searchDepth)
    {
        this.evaluator =StandardBoardEvaluator.get();
        this.searchDepth =searchDepth;
        this.boardsEvaluated = 0;
        this.quiescenceCount = 0;
    }


    @Override
    public long getNumBoardsEvaluated() {
        return this.boardsEvaluated;
    }

    @Override
    public String toString() {
        return "StockAlphaBeta";
    }
    private int min(final Board board,
                    final int depth,
                    final int highest,
                    final int lowest) {
        if(depth == 0 || BoardUtils.isEndGame(board))
        {
            this.boardsEvaluated++;
            return this.evaluator.evaluate(board,depth);
        }
        int currentLowest = lowest;
        for(final Move move : MoveSorter.STANDARD.sort(board.getCurrentPlayer().getLegalMoves()))
        {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone())
            {
                final Board toBoard = moveTransition.getToBoard();
                currentLowest = Math.min(currentLowest, max(toBoard,
                 calculateQuiescenceDepth(toBoard, depth), highest,currentLowest));
                if(currentLowest<=highest)
                {
                    return highest;
                }
            }
        }
        return currentLowest;
    }

    private int max(final Board board,
                    final int depth,
                    final int highest,
                    final int lowest) {
        if(depth == 0 || BoardUtils.isEndGame(board))
        {
            this.boardsEvaluated++;
            return this.evaluator.evaluate(board, depth);
        }
        int currentHighest = highest;
        for(final Move move: MoveSorter.STANDARD.sort(board.getCurrentPlayer().getLegalMoves()))
        {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone())
            {
                final Board toBoard = moveTransition.getToBoard();
                currentHighest = Math.max(currentHighest, min(toBoard,
                        calculateQuiescenceDepth(toBoard,depth),
                        currentHighest,
                        lowest));
                if(currentHighest>=lowest)
                {
                    return lowest;
                }
            }
        }
        return currentHighest;
    }

    private int calculateQuiescenceDepth(final Board toBoard,
                                         final int depth) {
        if(depth==1&& this.quiescenceCount<MAX_QUIESCENCE)
        {
            int activityMeasure = 0;
            if(toBoard.getCurrentPlayer().isInCheck())
            {
                activityMeasure++;
            }
            for(final Move move: BoardUtils.lastNMove(toBoard,2))
            {
                if(move.isAttack())
                {
                    activityMeasure++;
                }
            }
            if(activityMeasure>=2)
            {
                this.quiescenceCount++;
                return 2;
            }
        }
        return depth-1;
    }
}

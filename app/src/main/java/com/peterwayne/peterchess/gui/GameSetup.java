package com.peterwayne.peterchess.gui;

import com.peterwayne.peterchess.engine.Alliance;
import com.peterwayne.peterchess.engine.player.Player;

public class GameSetup {
    private final GameUI.PlayerType whitePlayerType;
    private final GameUI.PlayerType blackPlayerType;
    public static final String HUMAN_TEXT = "Human";
    public static final String COMPUTER_TEXT = "Computer";

    public GameSetup(final String chosenPlayerType)
    {
        if(chosenPlayerType.equals(HUMAN_TEXT))
        {
            this.whitePlayerType = GameUI.PlayerType.HUMAN;
            this.blackPlayerType = GameUI.PlayerType.COMPUTER;
        }else
        {
            this.whitePlayerType = GameUI.PlayerType.COMPUTER;
            this.blackPlayerType = GameUI.PlayerType.HUMAN;
        }

    }
    boolean isAIPlayer(final Player player) {
        if(player.getAlliance() == Alliance.WHITE) {
            return getWhitePlayerType() == GameUI.PlayerType.COMPUTER;
        }
        return getBlackPlayerType() == GameUI.PlayerType.COMPUTER;
    }
    GameUI.PlayerType getWhitePlayerType() {
        return this.whitePlayerType;
    }

    GameUI.PlayerType getBlackPlayerType() {
        return this.blackPlayerType;
    }
}

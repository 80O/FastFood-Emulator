package com.eu.habbo.fastfood.game.powerups;

import com.eu.habbo.fastfood.game.Game;
import com.eu.habbo.fastfood.game.GamePlayer;
import com.eu.habbo.fastfood.game.ICycle;

public abstract class PlayerPowerUp implements ICycle
{
    private final Game game;
    private final GamePlayer gamePlayer;
    public boolean needsRemoval;

    protected PlayerPowerUp(Game game, GamePlayer gamePlayer)
    {
        this.game         = game;
        this.gamePlayer   = gamePlayer;
        this.needsRemoval = false;
    }
}
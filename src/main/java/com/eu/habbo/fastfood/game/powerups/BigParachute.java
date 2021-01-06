package com.eu.habbo.fastfood.game.powerups;

import com.eu.habbo.fastfood.game.Game;
import com.eu.habbo.fastfood.game.GameFallingObject;
import com.eu.habbo.fastfood.game.GamePlayer;

public class BigParachute extends PlayerPowerUp
{
    private final GameFallingObject fallingObject;

    public BigParachute(Game game, GamePlayer gamePlayer, GameFallingObject fallingObject)
    {
        super(game, gamePlayer);

        this.fallingObject = fallingObject;
    }

    @Override
    public void cycle(double time, double interval)
    {

    }
}
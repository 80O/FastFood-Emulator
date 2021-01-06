package com.eu.habbo.fastfood.game.powerups;

import com.eu.habbo.fastfood.game.Game;
import com.eu.habbo.fastfood.game.GamePlayer;

public class Shield extends PlayerPowerUp
{
    public static int activationTime;

    public Shield(Game game, GamePlayer gamePlayer)
    {
        super(game, gamePlayer);
    }

    @Override
    public void cycle(double time, double interval)
    {

    }
}
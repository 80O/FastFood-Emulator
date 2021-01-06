package com.eu.habbo.fastfood.game.powerups;

import com.eu.habbo.fastfood.game.Game;
import com.eu.habbo.fastfood.game.GameFallingObject;
import com.eu.habbo.fastfood.game.GamePlayer;

public class Missile extends PlayerPowerUp
{
    public static short flyTime = 750;

    public final Game game;
    public final GameFallingObject fallingObject;
    public final long time;

    public Missile(Game game, GamePlayer gamePlayer, GameFallingObject fallingObject, long time)
    {
        super(game, gamePlayer);
        this.game = game;
        this.fallingObject = fallingObject;
        this.time = time;
    }

    @Override
    public void cycle(double time, double interval)
    {
        if (time >= this.time + flyTime)
        {
            this.needsRemoval = true;
            if (this.fallingObject.tryExplodePlate())
            {
                //TODO: Progress Missile Success achievement
            }
        }
    }
}
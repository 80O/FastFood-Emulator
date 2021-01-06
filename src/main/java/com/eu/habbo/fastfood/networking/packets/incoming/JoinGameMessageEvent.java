package com.eu.habbo.fastfood.networking.packets.incoming;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.game.Game;
import com.eu.habbo.fastfood.networking.packets.MessageHandler;

public class JoinGameMessageEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
       FastFood.gameEnvironment.gameManager().queuePlayer(this.client.gamePlayer());
    }
}
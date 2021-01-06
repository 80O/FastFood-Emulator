package com.eu.habbo.fastfood.networking.packets.incoming;

import com.eu.habbo.fastfood.game.Game;
import com.eu.habbo.fastfood.networking.packets.MessageHandler;

public class UseButtonMessageEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if (this.client.gamePlayer().game() != null)
        {
            this.client.gamePlayer().game().action(this.client.gamePlayer(), Game.GameAction.fromValue(this.packet.readInt()));
        }
    }
}
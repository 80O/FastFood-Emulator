package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.game.GamePlayer;
import com.eu.habbo.fastfood.game.PowerUp;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

import java.util.Map;

public class InitializeInventoryComposer extends MessageComposer
{
    private final GamePlayer gamePlayer;

    public InitializeInventoryComposer(GamePlayer gamePlayer)
    {
        this.gamePlayer = gamePlayer;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.InitializeInventoryComposer);
        this.response.appendInt32(this.gamePlayer.powerUps().size());
        for (Map.Entry<PowerUp, Integer> set : this.gamePlayer.powerUps().entrySet())
        {
            this.response.appendInt32(set.getKey().type());
            this.response.appendInt32(set.getValue());
        }
        return this.response;
    }
}
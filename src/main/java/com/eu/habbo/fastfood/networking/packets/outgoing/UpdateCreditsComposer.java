package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.game.GamePlayer;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

public class UpdateCreditsComposer extends MessageComposer
{
    public final GamePlayer gamePlayer;

    public UpdateCreditsComposer(GamePlayer gamePlayer)
    {
        this.gamePlayer = gamePlayer;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UpdateCreditsComposer);
        this.response.appendInt32(this.gamePlayer.credits());
        return this.response;
    }
}
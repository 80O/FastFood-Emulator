package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.game.GameFallingObject;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

public class UseBigParachuteComposer extends MessageComposer
{
    public final GameFallingObject fallingObject;

    public UseBigParachuteComposer(GameFallingObject fallingObject)
    {
        this.fallingObject = fallingObject;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UseBigParachuteComposer);
        this.response.appendInt32(this.fallingObject.plate.foodType().id);
        this.response.appendInt32(this.fallingObject.gamePlayer.id());
        return this.response;
    }
}
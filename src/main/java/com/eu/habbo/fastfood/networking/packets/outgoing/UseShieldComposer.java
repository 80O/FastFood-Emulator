package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.game.GameFallingObject;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

public class UseShieldComposer extends MessageComposer
{
    public final GameFallingObject fallingObject;
    public UseShieldComposer(GameFallingObject fallingObject)
    {
        this.fallingObject = fallingObject;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UseShieldComposer);
        this.response.appendInt32(this.fallingObject.plate.foodType().id);
        this.response.appendInt32(this.fallingObject.gamePlayer.id());
        this.response.appendBoolean(this.fallingObject.shield);
        return this.response;
    }
}
package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.game.GameFallingObject;
import com.eu.habbo.fastfood.game.GamePlayer;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

public class UpdateStateComposer extends MessageComposer
{
    public final GameFallingObject fallingObject;

    public UpdateStateComposer(GameFallingObject fallingObject)
    {
        this.fallingObject = fallingObject;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UpdateStateComposer);
        this.fallingObject.serialize(this.response);
        return this.response;
    }
}
package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.game.GameFallingObject;
import com.eu.habbo.fastfood.game.GamePlayer;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

public class FoodLandedComposer extends MessageComposer
{
    public final GamePlayer        gamePlayer;
    public final GameFallingObject fallingObject;
    public final int               unknownInt5 = 12;

    public FoodLandedComposer(GamePlayer gamePlayer, GameFallingObject fallingObject)
    {
        this.gamePlayer    = gamePlayer;
        this.fallingObject = fallingObject;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.FoodLandedComposer);
        this.response.appendInt32(this.gamePlayer.game().score(this.gamePlayer));
        this.response.appendInt32(this.gamePlayer.id());
        this.response.appendInt32(fallingObject == null ? 0 : this.fallingObject.state.value);
        this.response.appendInt32(fallingObject == null ? 0 : this.gamePlayer.nextPlate().id);
        this.response.appendInt32(fallingObject == null ? 0 : this.unknownInt5);
        return this.response;
    }
}
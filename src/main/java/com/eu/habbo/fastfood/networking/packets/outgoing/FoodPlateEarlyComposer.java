package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.game.GameFallingObject;
import com.eu.habbo.fastfood.game.GamePlayer;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

public class FoodPlateEarlyComposer extends MessageComposer
{
    public final GamePlayer gamePlayer;
    public final GameFallingObject plate;

    public FoodPlateEarlyComposer(GamePlayer gamePlayer, GameFallingObject plate)
    {
        this.gamePlayer = gamePlayer;
        this.plate = plate;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.FoodLandedComposer);
        this.response.appendInt32(-1);
        this.response.appendInt32(this.gamePlayer.id());
        this.response.appendInt32(0);
        this.response.appendInt32(plate.plate.foodType().id);
        this.response.appendInt32(-1);
        return this.response;
    }
}

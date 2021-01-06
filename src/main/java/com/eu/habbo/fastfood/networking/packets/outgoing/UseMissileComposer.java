package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.game.GamePlayer;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

public class UseMissileComposer extends MessageComposer
{
    public final int        plateId;
    public final GamePlayer sender;
    public final GamePlayer target;

    public UseMissileComposer(int plateId, GamePlayer sender, GamePlayer target)
    {
        this.plateId = plateId;
        this.sender  = sender;
        this.target  = target;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UseMissileComposer);
        this.response.appendInt32(this.plateId);
        this.response.appendInt32(this.sender.id());
        this.response.appendInt32(this.target.id());
        return this.response;
    }
}
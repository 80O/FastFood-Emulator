package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.game.PowerUp;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

public class PowerUpEarnedComposer extends MessageComposer
{
    public final PowerUp powerUp;
    public final int count;

    public PowerUpEarnedComposer(PowerUp powerUp, int count)
    {
        this.powerUp = powerUp;
        this.count   = count;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.PowerUpEarnedComposer);
        this.response.appendInt32(this.powerUp.type());
        this.response.appendInt32(this.count);
        return this.response;
    }
}
package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

public class TodaysRankComposer extends MessageComposer
{
    public final int total;
    public final int position;

    public TodaysRankComposer(int total, int position)
    {
        this.total = total;
        this.position = position;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnknownComposer_19);
        this.response.appendInt32(this.total);
        this.response.appendInt32(this.position);
        return this.response;
    }
}
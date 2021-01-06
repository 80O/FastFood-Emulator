package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

public class MaintenanceComposer extends MessageComposer
{
    public final boolean hotelMaintenance;
    public final boolean serverMaintenance;

    public MaintenanceComposer(boolean hotelMaintenance, boolean serverMaintenance)
    {
        this.hotelMaintenance = hotelMaintenance;
        this.serverMaintenance = serverMaintenance;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MaintenanceComposer);
        this.response.appendBoolean(this.hotelMaintenance);
        this.response.appendBoolean(this.serverMaintenance);
        return this.response;
    }
}
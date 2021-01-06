package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

public class AuthenticationComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.AuthenticationComposer);
        return this.response;
    }
}
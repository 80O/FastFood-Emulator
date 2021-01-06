package com.eu.habbo.fastfood.networking.packets;

public abstract class MessageComposer
{
    protected final ServerMessage response;
    
    protected MessageComposer()
    {
        this.response = new ServerMessage();
    }
    
    public abstract ServerMessage compose();
}
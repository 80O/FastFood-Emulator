package com.eu.habbo.fastfood.networking.packets;

import com.eu.habbo.fastfood.networking.GameClient;

public abstract class MessageHandler
{
    protected GameClient client;
    protected ClientMessage packet;

    public abstract void handle() throws Exception;

    public GameClient client()
    {
        return this.client;
    }

    public void client(GameClient client)
    {
        this.client = client;
    }

    public ClientMessage packet()
    {
        return this.packet;
    }

    public void packet(ClientMessage packet)
    {
        this.packet = packet;
    }
}
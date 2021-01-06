package com.eu.habbo.fastfood.networking.packets.incoming;

import com.eu.habbo.fastfood.networking.packets.MessageHandler;
import com.eu.habbo.fastfood.networking.packets.outgoing.InitializeInventoryComposer;

import java.util.HashMap;

public class RequestInventoryMessageEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new InitializeInventoryComposer(this.client.gamePlayer()));
    }
}
package com.eu.habbo.fastfood.networking.packets.incoming;

import com.eu.habbo.fastfood.networking.packets.MessageHandler;
import com.eu.habbo.fastfood.networking.packets.outgoing.UpdateCreditsComposer;

public class RequestUserCreditBalanceMessageEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new UpdateCreditsComposer(this.client.gamePlayer()));
    }
}
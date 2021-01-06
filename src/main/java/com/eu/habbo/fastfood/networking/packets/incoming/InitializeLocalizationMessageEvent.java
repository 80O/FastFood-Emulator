package com.eu.habbo.fastfood.networking.packets.incoming;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.game.TextManager;
import com.eu.habbo.fastfood.networking.packets.MessageHandler;
import com.eu.habbo.fastfood.networking.packets.outgoing.InitializeLocationComposer;

public class InitializeLocalizationMessageEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new InitializeLocationComposer(FastFood.gameEnvironment.textManager().textConfiguration().get(TextManager.Language.ENGLISH)));
    }
}
package com.eu.habbo.fastfood.networking.packets.incoming;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.networking.packets.MessageHandler;
import com.eu.habbo.fastfood.networking.packets.outgoing.ItemPriceListComposer;
import com.eu.habbo.fastfood.game.shop.ShopItem;

import java.util.ArrayList;

public class RequestItemPriceListMessageEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new ItemPriceListComposer(new ArrayList<ShopItem>(FastFood.gameEnvironment.shopManager().shopItems().values())));
    }
}
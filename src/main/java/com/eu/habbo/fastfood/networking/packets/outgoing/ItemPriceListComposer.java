package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;
import com.eu.habbo.fastfood.game.shop.ShopItem;

import java.util.List;

public class ItemPriceListComposer extends MessageComposer
{
    public final List<ShopItem> shopItems;

    public ItemPriceListComposer(List<ShopItem> shopItems)
    {
        this.shopItems = shopItems;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ItemPriceListComposer);
        this.response.appendInt32(this.shopItems.size());
        for (ShopItem item : this.shopItems)
        {
            item.serialize(this.response);
        }

        return this.response;
    }
}
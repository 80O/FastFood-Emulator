package com.eu.habbo.fastfood.networking.packets.incoming;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.game.PowerUp;
import com.eu.habbo.fastfood.game.shop.ShopItem;
import com.eu.habbo.fastfood.networking.packets.MessageHandler;
import com.eu.habbo.fastfood.networking.packets.outgoing.InitializeInventoryComposer;
import com.eu.habbo.fastfood.networking.packets.outgoing.PowerUpEarnedComposer;
import com.eu.habbo.fastfood.networking.packets.outgoing.UpdateCreditsComposer;

public class BuyCatalogItemMessageEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        String itemId = this.packet.readString();

        ShopItem shopItem = FastFood.gameEnvironment.shopManager().shopItem(PowerUp.fromType(Integer.valueOf(itemId)));

        if (shopItem != null)
        {
            if (shopItem.price() <= this.client.gamePlayer().credits())
            {
                if (this.client.gamePlayer().powerUps().get(shopItem.powerUp()) + shopItem.amount() <= 100)
                {
                    this.client.gamePlayer().addCredits(-shopItem.price());
                    this.client.sendResponse(new UpdateCreditsComposer(this.client.gamePlayer()));
                    this.client.gamePlayer().addPowerUp(shopItem.powerUp(), shopItem.amount());
                    this.client.sendResponse(new InitializeInventoryComposer(this.client.gamePlayer())); //maybe replace with PowerUpEarnedComposer
                    this.client.gamePlayer().update();
                }
            }
        }
    }
}
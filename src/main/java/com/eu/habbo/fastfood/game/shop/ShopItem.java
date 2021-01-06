package com.eu.habbo.fastfood.game.shop;

import com.eu.habbo.fastfood.game.PowerUp;
import com.eu.habbo.fastfood.networking.packets.ISerialize;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

public class ShopItem implements ISerialize
{
    private final PowerUp powerUp;
    private final int price;
    private final int amount;

    public ShopItem(PowerUp powerUp, int amount, int price)
    {
        this.powerUp = powerUp;
        this.amount = amount;
        this.price = price;
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendString(this.powerUp.type() + "");
        message.appendString(this.powerUp.itemName());
        message.appendInt32(this.amount);
        message.appendInt32(this.price);
    }

    public PowerUp powerUp()
    {
        return this.powerUp;
    }

    public int price()
    {
        return this.price;
    }

    public int amount()
    {
        return this.amount;
    }
}
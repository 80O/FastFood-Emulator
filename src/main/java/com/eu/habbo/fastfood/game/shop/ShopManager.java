package com.eu.habbo.fastfood.game.shop;

import com.eu.habbo.fastfood.IDisposable;
import com.eu.habbo.fastfood.game.PowerUp;
import gnu.trove.map.hash.THashMap;

public class ShopManager implements IDisposable
{
    private boolean loaded   = false;
    private boolean disposed = false;

    private final THashMap<PowerUp, ShopItem> shopItems = new THashMap<PowerUp, ShopItem>();

    public ShopManager()
    {
        System.out.println("[Shop Manager] Initialising...");
        this.loaded = true;
        this.reload();
        System.out.println("[Shop Manager] Loaded!");
    }

    public void reload()
    {
        if (this.loaded)
        {
            this.shopItems.put(PowerUp.BIG_PARACHUTE, new ShopItem(PowerUp.BIG_PARACHUTE, 2, 25));
            this.shopItems.put(PowerUp.MISSILE, new ShopItem(PowerUp.MISSILE, 5, 15));
            this.shopItems.put(PowerUp.SHIELD, new ShopItem(PowerUp.SHIELD, 3, 10));
            System.out.println("[Shop Manager] Loaded " + this.shopItems.size() + " items!");
        }
    }

    public ShopItem shopItem(PowerUp powerUp)
    {
        return this.shopItems.get(powerUp);
    }

    public THashMap<PowerUp, ShopItem> shopItems()
    {
        return shopItems;
    }

    @Override
    public void dispose()
    {
        this.loaded   = false;
        this.shopItems.clear();
        this.disposed = true;
        System.out.println("[Shop Manager] Disposed!");
    }

    @Override
    public boolean disposed()
    {
        return this.disposed;
    }
}
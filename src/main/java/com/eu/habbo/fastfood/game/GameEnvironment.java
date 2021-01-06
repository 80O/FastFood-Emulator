package com.eu.habbo.fastfood.game;

import com.eu.habbo.fastfood.IDisposable;
import com.eu.habbo.fastfood.game.shop.ShopManager;

public class GameEnvironment implements IDisposable
{
    private boolean loaded = false;
    private boolean disposed = false;

    private final GameManager gameManager;
    private final TextManager textManager;
    private final ShopManager shopManager;

    public GameEnvironment()
    {
        System.out.println("[Game Environment] Initialising...");
        this.shopManager = new ShopManager();
        this.textManager = new TextManager();
        this.gameManager = new GameManager();
        this.loaded = true;
        System.out.println("[Game Environment] Loaded!");
    }

    public GameManager gameManager()
    {
        return this.gameManager;
    }

    public TextManager textManager()
    {
        return this.textManager;
    }

    public ShopManager shopManager()
    {
        return this.shopManager;
    }

    @Override
    public void dispose()
    {
        System.out.println("[Game Environment] Disposing...");
        this.loaded = false;
        this.gameManager.dispose();
        this.textManager.dispose();
        this.shopManager.dispose();
        this.disposed = true;
        System.out.println("[Game Environment] Disposed!");
    }

    @Override
    public boolean disposed()
    {
        return this.disposed;
    }
}
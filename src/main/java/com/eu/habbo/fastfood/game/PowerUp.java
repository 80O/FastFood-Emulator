package com.eu.habbo.fastfood.game;

public enum PowerUp
{
    BIG_PARACHUTE(0, "bigparachute", 3),
    MISSILE(1, "missile", 5),
    SHIELD(2, "shield", 3);

    private final int type;
    private final String itemName;
    private final int maximumPerGame;

    PowerUp(final int type, final String itemName, int maximumPerGame)
    {
        this.type = type;
        this.itemName = itemName;
        this.maximumPerGame = maximumPerGame;
    }

    public int type()
    {
        return this.type;
    }

    public String itemName()
    {
        return this.itemName;
    }

    public int maximumPerGame()
    {
        return this.maximumPerGame;
    }

    public static PowerUp fromType(Integer integer)
    {
        for (PowerUp powerUp : PowerUp.values())
        {
            if (powerUp.type() == integer)
            {
                return powerUp;
            }
        }

        return null;
    }
}
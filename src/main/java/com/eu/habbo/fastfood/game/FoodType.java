package com.eu.habbo.fastfood.game;

public enum FoodType
{
    SOUP(0),
    PIZZA(1),
    BURGER(2),
    TRAY(3),
    CAKE(4),
    TRAY_2(5);

    public final int id;

    FoodType(int id)
    {
        this.id = id;
    }
}
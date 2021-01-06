package com.eu.habbo.fastfood.game;

import com.eu.habbo.fastfood.networking.packets.ISerialize;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FastFoodPlate implements ISerialize
{
    public static final Map<FoodType, FastFoodPlate> plateDefinitions = new THashMap<FoodType, FastFoodPlate>()
    {
        {
            put(FoodType.SOUP, new FastFoodPlate(FoodType.SOUP, 0.4, -2.0, 0.1, 0.2, 100));
            put(FoodType.PIZZA, new FastFoodPlate(FoodType.PIZZA, 0.5, -2.0, 0.1, 0.15, 150));
            put(FoodType.BURGER, new FastFoodPlate(FoodType.BURGER, 0.7, -1.2, 0.2, 0.2, 100));
            put(FoodType.TRAY, new FastFoodPlate(FoodType.TRAY, 0.9, -1.5, 0.2, 0.2, 200));
            put(FoodType.CAKE, new FastFoodPlate(FoodType.CAKE, 1.1, -1.5, 0.15, 0.15, 300));
            put(FoodType.TRAY_2, new FastFoodPlate(FoodType.TRAY_2, 1.5, -2.0, 0.15, 0.2, 200));
        }
    };

    private final FoodType foodType;
    private final double   fallMultiplier;
    private final double   parachuteMultiplier;
    private final double   parachuteSpeed;
    private final double   bigParachuteSpeed;
    private final int      plateTimer;

    public FastFoodPlate(FoodType foodType, double fallMultiplier, double parachuteMultiplier, double parachuteSpeed, double bigParachuteSpeed, int plateTimer)
    {
        this.foodType            = foodType;
        this.fallMultiplier      = fallMultiplier;
        this.parachuteMultiplier = parachuteMultiplier;
        this.parachuteSpeed      = parachuteSpeed;
        this.bigParachuteSpeed   = bigParachuteSpeed;
        this.plateTimer          = plateTimer;
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendInt32 (this.foodType.id);
        message.appendString(this.fallMultiplier + "");
        message.appendString(this.parachuteMultiplier + "");
        message.appendString(this.parachuteSpeed + "");
        message.appendString(this.bigParachuteSpeed + "");
        message.appendInt32 (this.plateTimer);
    }

    public FoodType foodType()
    {
        return this.foodType;
    }

    public double fallMultiplier()
    {
        return this.fallMultiplier;
    }

    public double parachuteMultiplier()
    {
        return this.parachuteMultiplier;
    }

    public double parachuteSpeed()
    {
        return this.parachuteSpeed;
    }

    public double bigParachuteSpeed()
    {
        return this.bigParachuteSpeed;
    }

    public int plateTimer()
    {
        return this.plateTimer;
    }

    @Override
    public String toString()
    {
        return this.foodType.name() + ": " +
                "fallMultiplier=" + this.fallMultiplier +
                ", parachuteMultiplier=" + this.parachuteMultiplier +
                ", parachuteSpeed=" + this.parachuteSpeed +
                ", bigParachuteSpeed=" + this.bigParachuteSpeed +
                ", plateTimer=" + this.plateTimer;
    }
}
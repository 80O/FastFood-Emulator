package com.eu.habbo.fastfood.networking.packets;

public abstract interface ISerialize
{
    public abstract void serialize(ServerMessage message);
}
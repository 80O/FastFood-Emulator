package com.eu.habbo.fastfood.game;

import com.eu.habbo.fastfood.networking.packets.ISerialize;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

public class AchievementBadge implements ISerialize
{
    public final String name;
    public final int level;
    public final String url;

    public AchievementBadge(String name, int level, String url)
    {
        this.name = name;
        this.level = level;
        this.url = url;
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendString(this.name);
        message.appendInt32(this.level);
        message.appendString(this.url);
    }
}
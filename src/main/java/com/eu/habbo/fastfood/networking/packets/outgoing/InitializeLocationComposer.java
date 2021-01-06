package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

import java.util.Map;

public class InitializeLocationComposer extends MessageComposer
{
    public final Map<String, String> unknownMap;

    public InitializeLocationComposer(Map<String, String> unknownMap)
    {
        this.unknownMap = unknownMap;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.InitializeLocationComposer);
        this.response.appendInt32(this.unknownMap.size());
        for (Map.Entry<String, String> set : this.unknownMap.entrySet())
        {
            this.response.appendString(set.getKey());
            this.response.appendString(set.getValue());
        }
        return this.response;
    }
}
package com.eu.habbo.fastfood.networking;

import com.eu.habbo.fastfood.IDisposable;
import com.eu.habbo.fastfood.game.GamePlayer;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;
import com.eu.habbo.fastfood.networking.packets.outgoing.MaintenanceComposer;
import io.netty.channel.*;
import io.netty.util.AttributeKey;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GameClientManager implements IDisposable
{
    private boolean loaded = false;
    private boolean disposed = false;

    private final ConcurrentMap<ChannelId, GameClient> clients;

    public static final AttributeKey<GameClient> CLIENT = AttributeKey.valueOf("GameClient");
    
    public GameClientManager()
    {
        this.clients = new ConcurrentHashMap<ChannelId, GameClient>();
        this.loaded = true;
    }
    
    public ConcurrentMap<ChannelId, GameClient> getSessions()
    {
        return this.clients;
    }
    
    public boolean containsClient(Channel channel)
    {
        return this.clients.containsKey(channel.id());
    }
    
    public GameClient findClient(Channel channel)
    {
        if (this.clients.containsKey(channel.id())) {
            return this.clients.get(channel.id());
        }
        return null;
    }

    public GameClient findClient(GamePlayer gamePlayer)
    {
        for(GameClient client : this.clients.values())
        {
            if (client.gamePlayer() == gamePlayer)
            {
                return client;
            }
        }

        return null;
    }
    
    public boolean addClient(ChannelHandlerContext ctx)
    {
        if (!this.loaded)
            return false;

        GameClient client = new GameClient(ctx.channel());
        ctx.channel().closeFuture().addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception
            {
                disposeClient(ctx.channel());
            }
        });

        ctx.attr(CLIENT).set(client);
        ctx.fireChannelRegistered();

        return this.clients.putIfAbsent(ctx.channel().id(), client) == null;
    }
    
    public void disposeClient(Channel channel)
    {
        GameClient client = this.findClient(channel);

        if (client != null)
        {
            client.dispose();
        }

        channel.attr(CLIENT).set(null);
        channel.deregister();
        channel.closeFuture();
        channel.close();
        this.clients.remove(channel.id());
    }

    public void sendBroadcastResponse(MessageComposer composer)
    {
        sendBroadcastResponse(composer.compose());
    }

    public void sendBroadcastResponse(ServerMessage msg)
    {
        for (GameClient client : this.clients.values()) {
            client.sendResponse(msg);
        }
    }

    public void sendBroadcastResponse(ServerMessage msg, GameClient exclude)
    {
        for (GameClient client : this.clients.values()) {
            if(client.equals(exclude))
                continue;

            client.sendResponse(msg);
        }
    }

    @Override
    public void dispose()
    {
        this.loaded = false;
        for (GameClient client : this.clients.values())
        {
            client.sendResponse(new MaintenanceComposer(true, true));
            this.disposeClient(client.channel());
        }

        this.disposed = true;
    }

    @Override
    public boolean disposed()
    {
        return this.disposed;
    }
}
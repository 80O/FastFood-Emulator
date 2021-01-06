package com.eu.habbo.fastfood.networking.packets;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.IDisposable;
import com.eu.habbo.fastfood.networking.GameClient;
import com.eu.habbo.fastfood.networking.packets.incoming.*;

import java.util.HashMap;

public class PacketManager implements IDisposable
{
    public static boolean SHOW_PACKETS = false;

    private boolean loaded   = false;
    private boolean disposed = false;
    private final HashMap<Integer, Class<? extends MessageHandler>> incoming;

    public PacketManager()
    {
        System.out.println("[Packet Manager] Registering packets...");
        this.incoming = new HashMap<Integer, Class<? extends MessageHandler>>();
        this.incoming.put(Incoming.AuthenticationMessageEvent,              AuthenticationMessageEvent.class);
        this.incoming.put(Incoming.UseButtonMessageEvent,                   UseButtonMessageEvent.class);
        this.incoming.put(Incoming.QuitGameMessageEvent,                    QuitGameMessageEvent.class);
        this.incoming.put(Incoming.JoinGameMessageEvent,                    JoinGameMessageEvent.class);
        this.incoming.put(Incoming.InitializeLocalizationMessageEvent,      InitializeLocalizationMessageEvent.class);
        this.incoming.put(Incoming.RequestUserCreditBalanceMessageEvent,    RequestUserCreditBalanceMessageEvent.class);
        this.incoming.put(Incoming.RequestItemPriceListMessageEvent,        RequestItemPriceListMessageEvent.class);
        this.incoming.put(Incoming.BuyCatalogItemMessageEvent,              BuyCatalogItemMessageEvent.class);
        this.incoming.put(Incoming.RequestInventoryMessageEvent,            RequestInventoryMessageEvent.class);
        this.incoming.put(Incoming.RequestScoresMessageEvent,               RequestScoresMessageEvent.class);
        this.incoming.put(Incoming.FriendRequestMessageEvent,               FriendRequestMessageEvent.class);
        this.incoming.put(Incoming.RequestRankMessageEvent,                 RequestRankMessageEvent.class);
        this.incoming.put(Incoming.RequestGlobalScoresMessageEvent,         RequestGlobalScoresMessageEvent.class);
        this.loaded = true;
        System.out.println("[Packet Manager] " + this.incoming.size() + " incoming packets registered!");
    }

    public void handlePacket(GameClient client, ClientMessage packet)
    {
        if(client == null || FastFood.isShuttingDown || !this.loaded || this.disposed())
            return;

        try
        {
            if(this.isRegistered(packet.header()))
            {
                if (SHOW_PACKETS)
                {
                    System.out.println("[CLIENT][" + packet.header() + "] => " + packet.messageBody());
                }

                final MessageHandler handler = this.incoming.get(packet.header()).newInstance();

                handler.client(client);
                handler.packet(packet);
                handler.handle();
            }
            else if (SHOW_PACKETS)
            {
                System.out.println("[CLIENT][UNDEFINED][" + packet.header() + "] => " + packet.messageBody());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    boolean isRegistered(int header)
    {
        return this.incoming.containsKey(header);
    }

    @Override
    public void dispose()
    {
        this.loaded   = false;
        PacketManager.SHOW_PACKETS = false;
        this.incoming.clear();
        this.disposed = true;
    }

    @Override
    public boolean disposed()
    {
        return this.disposed;
    }
}
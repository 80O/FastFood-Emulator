package com.eu.habbo.fastfood.networking;

import com.eu.habbo.fastfood.game.GamePlayer;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.PacketManager;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.util.ArrayList;

public class GameClient
{
    /**
     * The Channel this client is using.
     */
    private final Channel channel;

    /**
     * The Habbo it is linked to.
     */
    private GamePlayer gamePlayer;

    /**
     * The MAC Address of the connected client.
     */
    private String machineId = "";

    public GameClient(Channel channel)
    {
        this.channel = channel;
    }

    /**
     * Sends an composer to the client.
     * @param composer The composer to send.
     */
    public void sendResponse(MessageComposer composer)
    {
        if(this.channel.isOpen())
        {
            try
            {
                ServerMessage msg = composer.compose();
                sendResponse(msg);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends an response to the client.
     * @param response The response to send.
     */
    public void sendResponse(ServerMessage response)
    {
        if(this.channel.isOpen())
        {
            if (response == null || response.header() <= 0)
            {
                return;
            }

            if (PacketManager.SHOW_PACKETS)
            {
                System.out.println("[SERVER] => [" + response.header() + "] -> " + response.bodyString());
            }

            this.channel.write(response.get(), this.channel.voidPromise());
            this.channel.flush();
        }
    }

    /**
     * Sends multiple responses to the client.
     * @param responses The responses to send.
     */
    public void sendResponses(ArrayList<ServerMessage> responses)
    {
        ByteBuf buffer = Unpooled.buffer();

        if(this.channel.isOpen())
        {
            for(ServerMessage response : responses)
            {
                if (response == null || response.header() <= 0)
                {
                    return;
                }

                if (PacketManager.SHOW_PACKETS)
                {
                    System.out.println("[SERVER] => [" + response.header() + "] -> " + response.bodyString());
                }

                buffer.writeBytes(response.get());
            }
            this.channel.write(buffer.copy(), this.channel.voidPromise());
            this.channel.flush();
        }
        buffer.release();
    }

    /**
     * Disposes the client. Disconnection mostly.
     */
    public void dispose()
    {
        try
        {
            this.channel.close();

            if (this.gamePlayer != null)
            {
                this.gamePlayer.dispose();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Channel channel()
    {
        return this.channel;
    }

    public GamePlayer gamePlayer()
    {
        return this.gamePlayer;
    }

    public void gamePlayer(GamePlayer gamePlayer)
    {
        this.gamePlayer = gamePlayer;
    }
}
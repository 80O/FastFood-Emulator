package com.eu.habbo.fastfood.networking.packets.incoming;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.networking.packets.MessageHandler;
import com.eu.habbo.fastfood.networking.packets.outgoing.AuthenticationComposer;
import com.eu.habbo.fastfood.networking.packets.outgoing.MaintenanceComposer;

public class AuthenticationMessageEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        String sso = this.packet.readString();
        String[] data = sso.split("\t");

        if (data.length == 7)
        {
            String arcturusAccount = data[0];
            String emulatorVersion = data[1];
            int hotelUserId = Integer.valueOf(data[2]);
            String hotelUserName = data[3];
            String hotelUserLook = data[4];
            int credits = Integer.valueOf(data[5]);
            String key = data[6];

            String host = this.packet.readString();

            if (emulatorVersion.equalsIgnoreCase("version: 1.11.0") || emulatorVersion.equalsIgnoreCase("version: 1.12.0"))
            {
                if (FastFood.gameServer.validTicket(key))
                {
                    this.client.gamePlayer(FastFood.gameEnvironment.gameManager().loadGamePlayer(arcturusAccount, hotelUserId, hotelUserName, hotelUserLook, host));

                    System.out.println("[Login] Received authentication " + hotelUserName + "@" + host + ": " + this.client.gamePlayer());
                    if (this.client.gamePlayer() != null)
                    {
                        this.client.gamePlayer().client(this.client);
                        this.client.gamePlayer().look(hotelUserLook);
                        this.client.gamePlayer().credits(credits);
                        this.client.gamePlayer().lastSeen(FastFood.intUnixTimestamp());
                        this.client.gamePlayer().hotelName(host);
                        this.client.sendResponse(new AuthenticationComposer());
                        return;
                    }
                }
            }
        }

        System.out.println("Invalid SSO: " + sso);

        this.client.sendResponse(new MaintenanceComposer(false, true));
    }
}
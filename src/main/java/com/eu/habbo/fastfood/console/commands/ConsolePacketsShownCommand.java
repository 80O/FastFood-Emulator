package com.eu.habbo.fastfood.console.commands;

import com.eu.habbo.fastfood.console.ConsoleCommand;
import com.eu.habbo.fastfood.networking.packets.PacketManager;

public class ConsolePacketsShownCommand extends ConsoleCommand
{
    public ConsolePacketsShownCommand()
    {
        super("packets", "Toggles packets output on / off.");
    }

    @Override
    public void handle(String[] args) throws Exception
    {
        PacketManager.SHOW_PACKETS = !PacketManager.SHOW_PACKETS;
        System.out.println("Packets are now " + (PacketManager.SHOW_PACKETS ? "shown" : "hidden"));
    }
}

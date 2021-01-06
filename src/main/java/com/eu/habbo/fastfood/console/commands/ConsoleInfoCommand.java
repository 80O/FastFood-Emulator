package com.eu.habbo.fastfood.console.commands;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.console.ConsoleCommand;
import com.eu.habbo.fastfood.game.Statistics;

import java.util.concurrent.TimeUnit;

public class ConsoleInfoCommand extends ConsoleCommand
{
    public ConsoleInfoCommand()
    {
        super("info", "Display information about the FastFood server.");
    }

    @Override
    public void handle(String[] args) throws Exception
    {
        int seconds = FastFood.intUnixTimestamp() - FastFood.startTimeStamp;
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);

        System.out.println("FastFood Server: " + FastFood.version);
        System.out.println("Uptime: " + day + " day" + (day > 1 ? "s" : "") + ", " + hours + " hour" + (hours > 1 ? "s" : "") + ", " + minute + " minutes, " + second + " seconds!");
        System.out.println("Statistics");
        System.out.println("- Connected Clients: " + FastFood.gameClientManager.getSessions().size());
        System.out.println("- Active Games: " + FastFood.gameEnvironment.gameManager().activeGames.size());
        System.out.println("- Queued Players: " + FastFood.gameEnvironment.gameManager().queue.size());
        System.out.println("");
        System.out.println("Game Statistics: ");
        System.out.println("- Missiles Launched: " + Statistics.MISSILES_LAUNCHED);
        System.out.println("- Shields Used: " + Statistics.SHIELDS_USED);
        System.out.println("- Big Parachutes Used: " + Statistics.BIG_PARACHUTES_USED);
        System.out.println("- Shields Defended: " + Statistics.SHIELD_DEFENCE);
        System.out.println("- Plates Landed: " + Statistics.PLATES_LANDED);
        System.out.println("- Plates Crashed: " + Statistics.PLATES_CRASHED);
        System.out.println("- Games Played: " + Statistics.GAMES_PLAYED);
    }
}
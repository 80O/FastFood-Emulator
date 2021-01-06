package com.eu.habbo.fastfood.console.commands;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.console.ConsoleCommand;

public class ConsoleDebugCommand extends ConsoleCommand
{
    public ConsoleDebugCommand()
    {
        super("debug", "debug - Toggles debugging information output.");
    }

    @Override
    public void handle(String[] args) throws Exception
    {
        FastFood.DEBUGGING = !FastFood.DEBUGGING;

        System.out.println("Debugging " + (FastFood.DEBUGGING ? "enabled" : "disabled") + "!");
    }
}

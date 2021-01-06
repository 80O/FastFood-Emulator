package com.eu.habbo.fastfood.console.commands;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.console.ConsoleCommand;

public class ConsoleShutdownCommand extends ConsoleCommand
{
    public ConsoleShutdownCommand()
    {
        super("stop", "Shuts down the FastFood server!");
    }

    @Override
    public void handle(String[] args) throws Exception
    {
        FastFood.stop();
    }
}
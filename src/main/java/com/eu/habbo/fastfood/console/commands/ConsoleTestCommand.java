package com.eu.habbo.fastfood.console.commands;

import com.eu.habbo.fastfood.console.ConsoleCommand;

public class ConsoleTestCommand extends ConsoleCommand
{
    /**
     * Constructs a new ConsoleCommand.
     */
    public ConsoleTestCommand()
    {
        super("test", "Used for debugging.");
    }

    @Override
    public void handle(String[] args) throws Exception
    {

    }
}

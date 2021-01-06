package com.eu.habbo.fastfood;

import com.eu.habbo.fastfood.console.ConsoleCommand;
import com.eu.habbo.fastfood.database.Database;
import com.eu.habbo.fastfood.game.GameEnvironment;
import com.eu.habbo.fastfood.game.Statistics;
import com.eu.habbo.fastfood.networking.GameClientManager;
import com.eu.habbo.fastfood.networking.GameServer;
import com.eu.habbo.fastfood.util.ConfigurationManager;
import com.eu.habbo.fastfood.util.ThreadPooling;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FastFood
{
    public static final int FRAME_RATE = 60;

    public static int startTimeStamp = 0;
    public static final String version = "Version 1.0";

    public static ConfigurationManager configurationManager;
    public static Database             database;
    public static GameClientManager    gameClientManager;
    public static GameEnvironment      gameEnvironment;
    public static GameServer           gameServer;
    public static ThreadPooling        threadPooling;

    public static boolean DEBUGGING = false;
    public static boolean isShuttingDown = false;
    public static boolean shutDown = false;

    public static void main(String[] args) throws Exception
    {
        System.out.println("\u001B[1m" + "\u001B[32m");
        System.out.println("  _____      _            _     ");
        System.out.println(" |  __ \\    | |          (_)    ");
        System.out.println(" | |__) |__ | | __ _ _ __ _ ___ ");
        System.out.println(" |  ___/ _ \\| |/ _` | '__| / __|");
        System.out.println(" | |  | (_) | | (_| | |  | \\__ \\");
        System.out.println(" |_|   \\___/|_|\\__,_|_|  |_|___/");
        System.out.println("\u001B[34m" + " Fastfood Server -> Arcturus Emulator!");
        System.out.println("\u001B[34m" + " By The General / Wesley <3");
        System.out.println("\u001B[0m");

        System.out.println("Initializing Fastfood Server");
        configurationManager    = new ConfigurationManager("config.ini");
        database                = new Database(configurationManager);
        threadPooling           = new ThreadPooling(2);
        configurationManager.loadFromDatabase();
        gameClientManager       = new GameClientManager();
        gameEnvironment         = new GameEnvironment();
        gameServer              = new GameServer(configurationManager.value("fastfood.host"),
                                                 configurationManager.integer("fastfood.port"));
        gameServer.initialise();
        gameServer.connect();
        ConsoleCommand.load();
        Statistics.load();
        startTimeStamp = intUnixTimestamp();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("-> Ready for commands");

        while (!shutDown)
        {
            try
            {
                String line = reader.readLine();

                if (line != null)
                {
                    ConsoleCommand.handle(line);
                    System.out.print("->");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    public static void stop()
    {
        Statistics.save();
        isShuttingDown = true;
        gameClientManager.dispose();
        gameEnvironment.dispose();
        gameServer.dispose();
        threadPooling.shutDown();
        database.dispose();
        shutDown = true;
    }
    public static int intUnixTimestamp()
    {
        return (int) (System.currentTimeMillis() / 1000);
    }
}
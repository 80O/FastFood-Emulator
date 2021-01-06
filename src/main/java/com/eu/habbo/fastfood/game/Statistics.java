package com.eu.habbo.fastfood.game;

import com.eu.habbo.fastfood.FastFood;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Statistics
{
    public static final int SAVE_INTERVAL = 60;

    public static volatile int MISSILES_LAUNCHED = 0;
    public static volatile int SHIELDS_USED = 0;
    public static volatile int BIG_PARACHUTES_USED = 0;
    public static volatile int SHIELD_DEFENCE = 0;
    public static volatile int PLATES_LANDED = 0;
    public static volatile int PLATES_CRASHED = 0;
    public static volatile int GAMES_PLAYED = 0;

    public static void load()
    {
        try (Connection connection = FastFood.database.dataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM ff_statistics"))
        {
            try (ResultSet set = statement.executeQuery())
            {
                if (set.next())
                {
                    MISSILES_LAUNCHED   = set.getInt("missiles_launched");
                    SHIELDS_USED        = set.getInt("shields_used");
                    BIG_PARACHUTES_USED = set.getInt("big_parachutes_used");
                    SHIELD_DEFENCE      = set.getInt("shield_defence");
                    PLATES_LANDED       = set.getInt("plates_landed");
                    PLATES_CRASHED      = set.getInt("plates_crashed");
                    GAMES_PLAYED        = set.getInt("games_played");
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        FastFood.threadPooling.run(new Runnable()
        {
            @Override
            public void run()
            {
                save();
                FastFood.threadPooling.run(this, SAVE_INTERVAL * 1000);
            }
        });
    }

    public static void save()
    {
        try (Connection connection = FastFood.database.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE ff_statistics SET " +
                     "missiles_launched   = ?, " +
                     "shields_used        = ?, " +
                     "big_parachutes_used = ?, " +
                     "shield_defence      = ?, " +
                     "plates_landed       = ?, " +
                     "plates_crashed      = ?, " +
                     "games_played        = ?"))
        {
            statement.setInt(1, MISSILES_LAUNCHED);
            statement.setInt(2, SHIELDS_USED);
            statement.setInt(3, BIG_PARACHUTES_USED);
            statement.setInt(4, SHIELD_DEFENCE);
            statement.setInt(5, PLATES_LANDED);
            statement.setInt(6, PLATES_CRASHED);
            statement.setInt(7, GAMES_PLAYED);
            statement.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}

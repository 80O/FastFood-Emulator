package com.eu.habbo.fastfood.game;

import com.eu.habbo.fastfood.FastFood;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GamePlayerStats
{
    public final GamePlayer player;
    public final Game game;

    public int score = 0;
    public int missiles = 0;
    public int shields = 0;
    public int parachutes = 0;
    public int emptyPlates = 0;
    public int crashedPlates = 0;

    public GamePlayerStats(GamePlayer player, Game game)
    {
        this.player = player;
        this.game = game;
    }

    public void save()
    {
        try (Connection connection = FastFood.database.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO ff_game_scores (user_id, game_id, score, missiles, shields, parachutes, empty_plates, crashed_plates) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"))
        {
            statement.setInt(1, this.player.id());
            statement.setLong(2, this.game.startTime);
            statement.setInt(3, this.score);
            statement.setInt(4, this.missiles);
            statement.setInt(5, this.shields);
            statement.setInt(6, this.parachutes);
            statement.setInt(7, this.emptyPlates);
            statement.setInt(8, this.crashedPlates);
            statement.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}

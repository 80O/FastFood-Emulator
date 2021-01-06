package com.eu.habbo.fastfood.game;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.IDisposable;

import java.sql.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class GameManager implements Runnable, IDisposable
{
    private boolean disposed = false;
    private boolean loaded   = false;

    public static final int MAXIMUM_ACTIVE_GAMES   = 100;
    public static final int MAXIMUM_BIG_PARACHUTES = PowerUp.BIG_PARACHUTE.maximumPerGame();
    public static final int MAXIMUM_MISSILES       = PowerUp.MISSILE.maximumPerGame();
    public static final int MAXIMUM_SHIELDS        = PowerUp.SHIELD.maximumPerGame();

    public final ConcurrentLinkedDeque<GamePlayer> queue = new ConcurrentLinkedDeque<>();
    public final ConcurrentLinkedDeque<Game> activeGames = new ConcurrentLinkedDeque<>();

    public GameManager()
    {
        System.out.println("[Game Manager] Initialising...");
        this.loaded = true;
        FastFood.threadPooling.run(this, 1000);
        System.out.println("[Game Manager] Loaded!");
    }

    public GamePlayer loadGamePlayer(String arcturusAccount, int hotelUserId, String hotelUserName, String hotelUserLook, String hotelName)
    {
        if (!this.loaded)
            return null;

        try (Connection connection = FastFood.database.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE arcturus_account = ? AND hotel_user_id = ? LIMIT 1"))
        {
            statement.setString(1, arcturusAccount);
            statement.setInt(2, hotelUserId);

            try (ResultSet set = statement.executeQuery())
            {
                if (set.next())
                {
                    return new GamePlayer(set);
                }
            }

            try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO users (arcturus_account, hotel_user_id, hotel_user_name, hotel_user_look, hotel_name) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS))
            {
                insertStatement.setString(1, arcturusAccount);
                insertStatement.setInt(2, hotelUserId);
                insertStatement.setString(3, hotelUserName);
                insertStatement.setString(4, hotelUserLook);
                insertStatement.setString(5, hotelName);
                insertStatement.execute();

                try (ResultSet set = insertStatement.getGeneratedKeys())
                {
                    if (set.next())
                    {
                        try (ResultSet userSet = statement.executeQuery())
                        {
                            if (userSet.next())
                            {
                                return new GamePlayer(userSet);
                            }
                        }
                    }
                }
            }
        }
        catch (SQLException e)
        {
            if (FastFood.DEBUGGING)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void queuePlayer(GamePlayer gamePlayer)
    {
        if (!this.loaded)
            return;

        if (gamePlayer.game() != null)
        {
            if (FastFood.DEBUGGING)
            {
                System.out.println("[Game Manager] Error: Cannot add player to queue while existing game exists for player: " + gamePlayer.toString());
            }
            return;
        }

        if (gamePlayer.state().equals(GamePlayerState.QUEUED) || gamePlayer.state().equals(GamePlayerState.PLAYING))
        {
            if (FastFood.DEBUGGING)
            {
                System.out.println("[Game Manager] Error: Cannot add player to queue. Current player (" + gamePlayer.toString() + "), state " + gamePlayer.state().name());
            }
        }

        if (FastFood.DEBUGGING)
        {
            System.out.println("Queued: " + gamePlayer.toString());
        }
        this.queue.push(gamePlayer);
        gamePlayer.state(GamePlayerState.QUEUED);
    }

    public void removePlayer(GamePlayer gamePlayer)
    {
        if (!this.loaded)
            return;

        if (gamePlayer.state().equals(GamePlayerState.NONE))
        {
            return;
        }

        if (gamePlayer.state().equals(GamePlayerState.QUEUED))
        {
            this.queue.remove(gamePlayer);
        }

        if (gamePlayer.state().equals(GamePlayerState.PLAYING))
        {
            gamePlayer.game().removePlayer(gamePlayer);
        }
    }

    public void removeGame(Game game)
    {
        this.activeGames.remove(game);
    }

    public void run()
    {
        try
        {
            if (this.loaded)
            {
                if (this.queue.size() >= 3 && this.activeGames.size() < MAXIMUM_ACTIVE_GAMES)
                {
                    Game game = new Game();
                    boolean success = true;
                    for (int count = 0; count < 3; count++)
                    {
                        GamePlayer player = this.queue.pop();
                        if (player != null)
                        {
                            game.addPlayer(player);
                        } else
                        {
                            success = false;
                        }
                    }

                    if (!success)
                    {
                        for (GamePlayer p : game.players().values())
                        {
                            this.removePlayer(p);
                            this.queuePlayer(p);
                        }
                    } else
                    {
                        this.activeGames.push(game);
                    }
                }

                FastFood.threadPooling.run(this, 25);
            }
        }
        catch (Exception e)
        {
            if (FastFood.DEBUGGING)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dispose()
    {
        System.out.println("[Game Manager] Disposing...");
        this.loaded = false;
        while(this.activeGames.size() > 0)
        {
            Game game = null;
            try
            {
                game = this.activeGames.pop();
            }
            catch (Exception e)
            {
                break;
            }

            if (game != null)
            {
                game.dispose();
            }
        }
        System.out.println("[Game Manager] Disposed!");
    }

    @Override
    public boolean disposed()
    {
        return false;
    }
}
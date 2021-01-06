package com.eu.habbo.fastfood.game;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.IDisposable;
import com.eu.habbo.fastfood.game.powerups.Missile;
import com.eu.habbo.fastfood.game.powerups.PlayerPowerUp;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;
import com.eu.habbo.fastfood.networking.packets.outgoing.*;
import gnu.trove.map.hash.THashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Game implements Runnable, IDisposable
{
    public static final int PLATE_TASK_COUNT = 6;

    public       long startTime = System.currentTimeMillis();
    public       GameState state;
    public       FoodType firstPlate;
    public final ConcurrentHashMap<Integer, GamePlayer> players;
    public       GamePlayer winner = null;
    public final List<FastFoodPlate> plateTasks;

    public final List<PlayerPowerUp>             playerPowerUps;
    public final Map<Integer, GamePlayerStats>   playerScores;
    public final Map<Integer, GameFallingObject> fallingObjects;
    public double countDown = 3.0;

    private Random random;
    private long lastCycle = System.currentTimeMillis();

    public Game()
    {
        this.state          = GameState.WAITING;
        this.players        = new ConcurrentHashMap<>();
        this.plateTasks     = new ArrayList<FastFoodPlate>();
        this.playerPowerUps = new ArrayList<PlayerPowerUp>();

        this.fallingObjects = new THashMap<Integer, GameFallingObject>();
        this.playerScores   = new THashMap<Integer, GamePlayerStats>();

        this.random = new Random(FastFood.intUnixTimestamp());

        System.out.println("Created Game at " + this.startTime);
    }

    public ConcurrentHashMap<Integer, GamePlayer> players()
    {
        return this.players;
    }

    public boolean addPlayer(GamePlayer gamePlayer)
    {
        if (this.state == GameState.WAITING)
        {
            if (this.players.size() < 3)
            {
                if (FastFood.DEBUGGING)
                {
                    System.out.println("Added " + gamePlayer.toString() + " to game " + this.startTime);
                }
                gamePlayer.game(this);
                gamePlayer.state(GamePlayerState.PLAYING);
                this.players.put(gamePlayer.id(), gamePlayer);
                this.playerScores.put(gamePlayer.id(), new GamePlayerStats(gamePlayer, this));
                if (this.players.size() == 3)
                {
                    this.start();
                }

                return true;
            }
        }

        return false;
    }

    public void removePlayer(GamePlayer player)
    {
        player.game(null);
        player.state(GamePlayerState.NONE);
        this.players.remove(player.id());
        this.playerScores.remove(player.id());

        if (this.players().size() <= 1)
        {
            this.dispose();
        }
    }

    private void start()
    {
        if (this.state == GameState.WAITING)
        {
            this.state = GameState.STARTING;

            for (int i = 0; i < Game.PLATE_TASK_COUNT; i++)
            {
                this.plateTasks.add(FastFoodPlate.plateDefinitions.get(FoodType.values()[this.random.nextInt(FoodType.values().length)]));
            }

            this.firstPlate = this.plateTasks.get(0).foodType();

            for (Map.Entry<Integer, GamePlayer> set : this.players.entrySet())
            {
                this.nextPlate(set.getValue(), true);
                set.getValue().client().sendResponse(new GameStartingComposer(set.getValue(), this));
            }

            this.lastCycle = System.currentTimeMillis();
            FastFood.threadPooling.run(this);
        }
    }

    public synchronized GameState state()
    {
        return this.state;
    }

    public synchronized void sendComposer(ServerMessage message)
    {
        for (Map.Entry<Integer, GamePlayer> set : this.players.entrySet())
        {
            set.getValue().client().sendResponse(message);
        }
    }

    @Override
    public void run()
    {
        long currentTime = System.currentTimeMillis();
        long cycleDifference = currentTime - lastCycle;

        if (this.state == GameState.STARTING)
        {
            this.state = GameState.INITIALISE_PLATES;
            if (FastFood.DEBUGGING)
            {
                for (FastFoodPlate type : this.plateTasks)
                {
                    System.out.println(type.toString());
                }
            }

            FastFood.threadPooling.run(this, 3000);
        }
        else if (this.state == GameState.INITIALISE_PLATES)
        {
            this.state = GameState.PLAYING;
            for (Map.Entry<Integer, GamePlayer> entry : this.players.entrySet())
            {
                GameFallingObject object = this.nextPlate(entry.getValue(), true, GameFallingObject.State.WAITING);
                object.plateTime = 0;
            }
        }

        if (this.state == GameState.PLAYING)
        {
            for (Map.Entry<Integer, GameFallingObject> set : this.fallingObjects.entrySet())
            {
                if (set.getValue().earlyDrop)
                {
                    if (!set.getValue().packetDrop)
                    {
                        set.getValue().packetDrop = true;
                        //this.sendComposer(new FoodLandedComposer(set.getValue().gamePlayer, null).compose());
                    }
                }

                set.getValue().cycle(currentTime, cycleDifference);
            }

            List<PlayerPowerUp> toRemove = new ArrayList<PlayerPowerUp>();
            for (PlayerPowerUp playerPowerUp : playerPowerUps)
            {
                playerPowerUp.cycle(currentTime, cycleDifference);

                if (playerPowerUp.needsRemoval)
                {
                    toRemove.add(playerPowerUp);
                }
            }

            this.playerPowerUps.removeAll(toRemove);

            this.lastCycle = System.currentTimeMillis();
            long time = (1000 / FastFood.FRAME_RATE) - (lastCycle - currentTime);
            FastFood.threadPooling.run(this, time);
        }

        if (this.state == GameState.FINISHED)
        {
            this.dispose();
        }
    }
    public GameFallingObject nextPlate(GamePlayer gamePlayer, boolean repeat)
    {
        return nextPlate(gamePlayer, repeat, GameFallingObject.State.WAITING);
    }

    public GameFallingObject nextPlate(GamePlayer gamePlayer, boolean repeat, GameFallingObject.State state)
    {
        int index = this.score(gamePlayer);

        if (!repeat)
        {
            index++;
            this.playerScores.get(gamePlayer.id()).score = index;
        }

        if (index >= this.plateTasks.size())
            return null;

        FastFoodPlate plate = this.plateTasks.get(index);
        GameFallingObject fallingObject = new GameFallingObject(plate, gamePlayer, state);
        this.fallingObjects.put(gamePlayer.id(), fallingObject);
        gamePlayer.nextPlate(plate.foodType());

        if (FastFood.DEBUGGING)
        {
            System.out.println("Getting next plate for: " + gamePlayer.username() + ", Type: " + plate.foodType().name());
        }
        return fallingObject;
    }

    public int score(GamePlayer gamePlayer)
    {
        return this.playerScores.get(gamePlayer.id()).score;
    }

    public void action(GamePlayer gamePlayer, GameAction action)
    {
        if (this.state == GameState.PLAYING)
        {
            synchronized (this.fallingObjects)
            {
                long currentTime = System.currentTimeMillis();
                GameFallingObject fallingObject = this.fallingObjects.get(gamePlayer.id());

                boolean succes = false;
                PowerUp powerUp = null;
                switch (action)
                {
                    case UNKNOWN:
                        succes = false;
                        break;
                    case DROP:
                        succes = true;
                        if (fallingObject == null)
                        {
                            if (FastFood.DEBUGGING)
                            {
                                System.out.println("Game No plate found for Player: " + gamePlayer.toString());
                            }
                            break;
                        }


                        if (FastFood.DEBUGGING)
                        {
                            System.out.println("Dropping. " + gamePlayer.username() + ": " + fallingObject.plate.foodType() + ", Plate State: " + fallingObject.state.name() + ", Current Time: " + currentTime + ", Plate Time: " + fallingObject.plateTime + ", Difference: " + (currentTime - fallingObject.plateTime) + ", Plate Definition Time: " + ((FastFoodPlate.plateDefinitions.get(fallingObject.plate.foodType()).plateTimer() / 60) * 1000));
                        }
                        if (fallingObject.state == GameFallingObject.State.WAITING && (currentTime < fallingObject.plateTime + ((FastFoodPlate.plateDefinitions.get(fallingObject.plate.foodType()).plateTimer() / 60) * 1000)))
                        {
                            long diff = currentTime - fallingObject.plateTime + fallingObject.dropDifference - fallingObject.dropDifference + ((FastFoodPlate.plateDefinitions.get(fallingObject.plate.foodType()).plateTimer() / 60) * 1000);

                            if (FastFood.DEBUGGING)
                            {
                                System.out.println("Dropping To Early! CurrentTime: " + currentTime + ", Plate Time: " + fallingObject.plateTime + ", Timer Total: " + ((FastFoodPlate.plateDefinitions.get(fallingObject.plate.foodType()).plateTimer() / 60) * 1000) + ", Timer Remaining: " + +diff + " MS");
                            }

                            fallingObject.earlyDrop = true;
                            fallingObject.dropTime = System.currentTimeMillis();
                            fallingObject.dropDifference = ((FastFoodPlate.plateDefinitions.get(fallingObject.plate.foodType()).plateTimer() / 60) * 1000) - (currentTime - fallingObject.plateTime);
                            //fallingObject.earlyDrop = true;
                            //fallingObject.drop();
                            //this.sendComposer(new UpdateStateComposer(fallingObject).compose());
                            //this.sendComposer(new FoodPlateEarlyComposer(gamePlayer, fallingObject).compose());
                            //fallingObject.earlyDrop = true;
                            //return;

                            //GameFallingObject newPlate = this.nextPlate(gamePlayer, true);
                            //newPlate.plateTime = fallingObject.plateTime;
                            //break;
                        }

                        fallingObject.drop();

                        break;
                    case PARACHUTE:
                        if (fallingObject.state == GameFallingObject.State.PARACHUTE || fallingObject.earlyDrop)
                            return;

                        succes = fallingObject.openParachute();
                        break;
                    case BIG_PARACHUTE:
                        powerUp = PowerUp.BIG_PARACHUTE;
                        if (this.playerScores.get(gamePlayer.id()).parachutes < GameManager.MAXIMUM_BIG_PARACHUTES)
                        {
                            succes = fallingObject.openBigParachute();
                            break;
                        }

                        succes = true;
                        break;
                    case SHIELD:
                        powerUp = PowerUp.SHIELD;
                        if (this.playerScores.get(gamePlayer.id()).shields < GameManager.MAXIMUM_SHIELDS)
                        {
                            succes = fallingObject.activateShield();
                            break;
                        }

                        succes = true;
                        break;
                    case MISSILE:
                        synchronized (this.playerPowerUps)
                        {
                            powerUp = PowerUp.MISSILE;
                            if (this.playerScores.get(gamePlayer.id()).missiles < GameManager.MAXIMUM_MISSILES)
                            {
                                ArrayList<GameFallingObject> objects = new ArrayList<GameFallingObject>(this.fallingObjects.values());
                                objects.remove(fallingObject);
                                for (GameFallingObject object : this.fallingObjects.values())
                                {
                                    if (!object.state.canBeTargeted)
                                    {
                                        objects.remove(object);
                                    }
                                }

                                GameFallingObject target = null;
                                if (!objects.isEmpty())
                                {
                                    Collections.shuffle(objects);
                                    target = objects.get(0);
                                }

                                if (target != null)
                                {
                                    succes = true;
                                    this.playerPowerUps.add(new Missile(this, gamePlayer, target, System.currentTimeMillis()));
                                    this.sendComposer(new UseMissileComposer(target.plate.foodType().id, gamePlayer, target.gamePlayer).compose());
                                    Statistics.MISSILES_LAUNCHED++;
                                }
                            }
                        }
                        break;
                }

                if (succes && powerUp != null)
                {
                    switch (powerUp)
                    {
                        case MISSILE:
                            this.playerScores.get(gamePlayer.id()).missiles++;
                            break;
                        case BIG_PARACHUTE:
                            this.playerScores.get(gamePlayer.id()).parachutes++;
                            break;
                        case SHIELD:
                            this.playerScores.get(gamePlayer.id()).shields++;
                            break;
                    }

                    gamePlayer.addPowerUp(powerUp, -1);
                }

                if (!succes && powerUp == null)
                {
                    if (FastFood.DEBUGGING)
                    {
                        System.out.println("Game: Failed to " + action.name() + " plate@" + (fallingObject == null ? "null" : fallingObject.state.name()) + " for: " + gamePlayer.toString());
                    }

                    nextPlate(gamePlayer, true);
                }
            }
        }
    }

    public void finish(GamePlayer winner)
    {
        if (this.state.equals(GameState.FINISHED))
            return;

        this.save();

        this.state = GameState.FINISHED;
        this.winner = winner;
        this.sendComposer(new GameEndedComposer(this.players.values()).compose());

        for (GamePlayer gamePlayer : this.players().values())
        {
            gamePlayer.client().sendResponse(new InitializeInventoryComposer(gamePlayer));
        }
        Statistics.GAMES_PLAYED++;
    }

    public void dispose()
    {
        for (GamePlayer gamePlayer : this.players().values())
        {
            this.removePlayer(gamePlayer);
        }

        this.players().clear();
        this.plateTasks.clear();
        this.playerPowerUps.clear();
        this.playerScores.clear();
        this.winner = null;
        FastFood.gameEnvironment.gameManager().removeGame(this);
        this.state = GameState.ENDED;
    }

    @Override
    public boolean disposed()
    {
        return this.state.equals(GameState.ENDED);
    }

    public enum GameState
    {
        WAITING,
        STARTING,
        INITIALISE_PLATES,
        PLAYING,
        FINISHED,
        ENDED;
    }

    public enum GameAction
    {
        UNKNOWN(-1),
        DROP(0),
        PARACHUTE(1),
        BIG_PARACHUTE(2),
        MISSILE(3),
        SHIELD(4);

        private final int value;
        GameAction(int value)
        {
            this.value = value;
        }

        public static GameAction fromValue(Integer integer)
        {
            for (GameAction action : GameAction.values())
            {
                if (action.value == integer)
                {
                    return action;
                }
            }

            return UNKNOWN;
        }
    }

    public void save()
    {
        for (GamePlayerStats stats : this.playerScores.values())
        {
            stats.save();
        }

        try (Connection connection = FastFood.database.dataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO ff_games (start_time, end_time) VALUES (?, ?)"))
        {
            statement.setLong(1, this.startTime);
            statement.setLong(2, System.currentTimeMillis());
            statement.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return "Game (" + this.startTime + "), Players: " + this.players().size() + "";
    }

    public GamePlayerStats getPlayerStats(GamePlayer player)
    {
        return this.playerScores.get(player.id());
    }
}
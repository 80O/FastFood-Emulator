package com.eu.habbo.fastfood.game;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.networking.GameClient;
import com.eu.habbo.fastfood.networking.packets.ISerialize;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;
import gnu.trove.map.hash.THashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GamePlayer implements ISerialize, Comparable<GamePlayer>
{
    private GameClient client;

    private final int id;
    private final String arcturusAccount;
    private final int hotelUserId;
    private String hotelUserName;
    private String look = "";
    private String index = "0"; //?
    private String hotelName = "";
    private int lastSeen;
    private int credits = 0;
    private final ArrayList<AchievementBadge> achievementBadges;
    private GamePlayerState state = GamePlayerState.NONE;

    private Game game = null;
    private GameFallingObject fallingObject;
    private FoodType nextPlate = null;
    private final THashMap<PowerUp, Integer> powerUps = new THashMap<PowerUp, Integer>();
    public GamePlayer(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.arcturusAccount = set.getString("arcturus_account");
        this.hotelUserId = set.getInt("hotel_user_id");
        this.hotelUserName = set.getString("hotel_user_name");
        this.powerUps.put(PowerUp.BIG_PARACHUTE, set.getInt("bigparachutes"));
        this.powerUps.put(PowerUp.MISSILE, set.getInt("missiles"));
        this.powerUps.put(PowerUp.SHIELD, set.getInt("shields"));
        this.lastSeen = set.getInt("last_seen");
        this.achievementBadges = new ArrayList<AchievementBadge>();
        this.fallingObject = null;
    }

    public void update()
    {
        try (Connection connection = FastFood.database.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE ff_users SET hotel_user_name = ?, hotel_user_look = ?, hotel_name = ?, missiles = ?, bigparachutes = ?, shields = ?, last_seen = ? WHERE id = ?"))
        {
            statement.setString(1, this.hotelUserName);
            statement.setString(2, this.look);
            statement.setString(3, this.hotelName);
            statement.setInt(4, this.powerUps.get(PowerUp.MISSILE));
            statement.setInt(5, this.powerUps.get(PowerUp.BIG_PARACHUTE));
            statement.setInt(6, this.powerUps.get(PowerUp.SHIELD));
            statement.setInt(7, this.lastSeen);
            statement.setInt(8, this.id);
            statement.execute();
        }
        catch (SQLException e)
        {
            if (FastFood.DEBUGGING)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendInt32(this.id);
        message.appendString(this.hotelUserName);
        message.appendString("https://avatar-retro.com/habbo-imaging/avatarimage?figure=" + this.look);
        message.appendString(this.index);
        message.appendString(this.hotelName);
        message.appendInt32(this.achievementBadges.size());
        for (AchievementBadge i : this.achievementBadges)
        {
            i.serialize(message);
        }
    }

    public GameClient client()
    {
        return this.client;
    }

    public void client(GameClient client)
    {
        this.client = client;
    }

    public void addCredits(int credits)
    {
        this.credits += credits;

        if (this.credits < 0)
        {
            this.credits = 0;
        }
    }

    public void credits(int credits)
    {
        this.credits = credits;
    }

    public int credits()
    {
        return this.credits;
    }

    public String username()
    {
        return this.hotelUserName;
    }

    public String index()
    {
        return this.index;
    }

    public void index(String index)
    {
        this.index = index;
    }

    public Game game()
    {
        return this.game;
    }

    public void game(Game game)
    {
        this.game = game;
    }

    public FoodType nextPlate()
    {
        return this.nextPlate;
    }

    public void nextPlate(FoodType nextPlate)
    {
        this.nextPlate = nextPlate;
    }

    public void dispose()
    {
        FastFood.gameEnvironment.gameManager().removePlayer(this);
        this.lastSeen = FastFood.intUnixTimestamp();
        this.update();
        System.out.println("[Game Player] " + this.toString() + " disconnected!");
    }

    public void addPowerUp(PowerUp powerUp, int amount)
    {
        this.powerUps.put(powerUp, Math.max(this.powerUps.get(powerUp) + amount, 0));
    }

    public THashMap<PowerUp, Integer> powerUps()
    {
        return this.powerUps;
    }

    public String look()
    {
        return this.look;
    }

    public void look(String look)
    {
        this.look = look;
    }

    public int lastSeen()
    {
        return this.lastSeen;
    }

    public void lastSeen(int lastSeen)
    {
        this.lastSeen = lastSeen;
    }

    public Integer id()
    {
        return this.id;
    }

    public GamePlayerState state()
    {
        return this.state;
    }

    public void state(GamePlayerState state)
    {
        this.state = state;
    }

    public String hotelName()
    {
        return this.hotelName;
    }

    public void hotelName(String hotelName)
    {
        this.hotelName = hotelName;
    }

    @Override
    public String toString()
    {
        return "User (" + this.id + "): " + this.hotelUserName + "@" + this.hotelName;
    }

    @Override
    public int compareTo(GamePlayer o)
    {
        return o.game().score(o) - this.game().score(this);
    }
}

package com.eu.habbo.fastfood.game;

import com.eu.habbo.fastfood.networking.packets.ISerialize;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;
import com.eu.habbo.fastfood.networking.packets.outgoing.FoodLandedComposer;
import com.eu.habbo.fastfood.networking.packets.outgoing.UpdateStateComposer;
import com.eu.habbo.fastfood.networking.packets.outgoing.UseBigParachuteComposer;
import com.eu.habbo.fastfood.networking.packets.outgoing.UseShieldComposer;

public class GameFallingObject implements ISerialize, ICycle
{
    public final GamePlayer    gamePlayer;
    public final FastFoodPlate plate;
    public       State         state = State.WAITING;

    public double  speed              = 0;
    public double  distance           = 1;
    public boolean bigParachuteActive = false;
    public boolean failed             = false;
    public boolean shield             = false;
    public boolean earlyDrop          = false;
    public boolean packetDrop         = false;
    public long     dropDifference = 0;
    public long     dropTime = 0;

    public long plateTime = System.currentTimeMillis();

    public GameFallingObject(FastFoodPlate plate, GamePlayer gamePlayer, State state)
    {
        this.plate      = plate;
        this.gamePlayer = gamePlayer;
        this.state      = state;
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendInt32(this.plate.foodType().id);
        message.appendInt32(this.gamePlayer.id());
        message.appendString(this.distance());
        message.appendString(this.speed());
        message.appendInt32(this.state.value);
        message.appendBoolean(!this.earlyDrop ? this.state.success : true);
    }

    public double speed()
    {
        return this.speed;
    }

    public double distance()
    {
        return this.distance;
    }

    @Override
    public void cycle(double time, double interval)
    {
        if (this.state == State.FALLING || this.state == State.PARACHUTE)
        {
            this.speed += ((interval / 1000d) * this.speedMultiplier());
            if (this.state == State.PARACHUTE)
            {
                this.speed = Math.max(this.speed, this.parachuteSpeed());
            }

            this.distance -= ((interval / 1000d) * this.speed);
            if (this.distance < 0)
            {
                this.hitTable();
            }
        }
    }

    public void hitTable()
    {
        if (!this.earlyDrop && !this.failed)
        {
            this.plateTime = System.currentTimeMillis();
        }

        if (!this.failed && (this.shield || this.speed <= 0.5))
        {
            this.gamePlayer.game().nextPlate(this.gamePlayer, false);

            if (this.gamePlayer.game().score(this.gamePlayer) <= Game.PLATE_TASK_COUNT)
            {
                this.updateState(State.SUCCESS);
                Statistics.PLATES_LANDED++;

                if (this.gamePlayer.game().score(this.gamePlayer) == Game.PLATE_TASK_COUNT)
                {
                    this.gamePlayer.game().finish(this.gamePlayer);
                }
            }
        }
        else
        {
            if (!this.earlyDrop)
            {
                this.gamePlayer.game().nextPlate(this.gamePlayer, true);
                this.gamePlayer.game().getPlayerStats(this.gamePlayer).crashedPlates++;
            }

            long diff = (System.currentTimeMillis() - this.dropTime);

            if (this.earlyDrop)
            {
                this.gamePlayer.game().getPlayerStats(this.gamePlayer).emptyPlates++;
                this.plateTime += diff;// - this.dropDifference;
            }
            this.updateState(State.FAIL);
            this.distance = 1;
            this.speed = 0;
            this.state = State.WAITING;
            this.earlyDrop = false;
            Statistics.PLATES_CRASHED++;
        }
    }

    public double speedMultiplier()
    {
        switch (this.state)
        {
            case FALLING:
                return this.plate.fallMultiplier();
            case PARACHUTE:
                return this.plate.parachuteMultiplier() * (this.bigParachuteActive ? 10 : 1);
            default:
                return 0;
        }
    }

    public double parachuteSpeed()
    {
        return this.bigParachuteActive ? this.plate.bigParachuteSpeed() : this.plate.parachuteSpeed();
    }

    public void updateState(State state)
    {
        this.state = state;

        if (this.state == State.FALLING || this.state == State.PARACHUTE)
        {
            this.gamePlayer.game().sendComposer(new UpdateStateComposer(this).compose());
        }
        else
        {
            this.gamePlayer.game().sendComposer(new FoodLandedComposer(this.gamePlayer, this).compose());
        }
    }

    public boolean openParachute()
    {
        if (!this.failed && this.state == State.FALLING)
        {
            this.updateState(State.PARACHUTE);

            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean openBigParachute()
    {
        if (this.openParachute())
        {
            this.bigParachuteActive = true;
            this.gamePlayer.game().sendComposer(new UseBigParachuteComposer(this).compose());
            Statistics.BIG_PARACHUTES_USED++;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean activateShield()
    {
        if (!this.failed & !this.shield)
        {
            this.updateShield(true);
            Statistics.SHIELDS_USED++;
            return true;
        }
        else
        {
            return false;
        }
    }

    public void updateShield(boolean activated)
    {
        this.shield = activated;
        this.gamePlayer.game().sendComposer(new UseShieldComposer(this).compose());
    }

    public boolean tryExplodePlate()
    {
        if (this.shield)
        {
            this.updateShield(false);
            Statistics.SHIELD_DEFENCE++;
            return false;
        }
        else
        {
            this.gamePlayer.game().nextPlate(this.gamePlayer, true);
            this.updateState(State.EXPLODED);
            this.plateTime = System.currentTimeMillis();
            return true;
        }
    }

    public void drop()
    {
        if (this.state == State.WAITING)
        {
            if (!this.earlyDrop)
            {
                this.plateTime = System.currentTimeMillis();
            }
            this.updateState(State.FALLING);
        }
        else if (this.state == State.FALLING)
        {
            this.plateTime = System.currentTimeMillis();
            this.updateState(State.FAIL);
        }
    }

    public enum State
    {
        WAITING(0, false, false),
        FALLING(1, false, true),
        PARACHUTE(2, false, true),
        SUCCESS(3, true, false),
        FAIL(4, false, false),
        EXPLODED(5, false, false);

        public final int value;
        public final boolean success;
        public final boolean canBeTargeted;

        State(int value, boolean success, boolean canBeTargeted)
        {
            this.value         = value;
            this.success       = success;
            this.canBeTargeted = canBeTargeted;
        }
    }
}
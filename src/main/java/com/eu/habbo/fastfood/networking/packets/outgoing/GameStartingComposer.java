package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.game.*;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameStartingComposer extends MessageComposer
{
    public final GamePlayer gamePlayer;
    public final Game game;

    public GameStartingComposer(GamePlayer gamePlayer, Game game)
    {
        this.gamePlayer = gamePlayer;
        this.game       = game;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GameStartingComposer);
        this.response.appendInt32(this.gamePlayer.id());
        this.response.appendBoolean(true);
        this.response.appendBoolean(true);
        this.response.appendBoolean(true);
        this.response.appendInt32(this.game.firstPlate.id);

        this.response.appendInt32(this.game.plateTasks.size());
        for (FastFoodPlate object : this.game.plateTasks)
        {
            object.serialize(this.response);
        }

        this.response.appendInt32(this.gamePlayer.powerUps().size());
        for (Map.Entry<PowerUp, Integer> powerUp :this.gamePlayer.powerUps().entrySet())
        {
            this.response.appendInt32(powerUp.getKey().type());
            this.response.appendInt32(Math.min(powerUp.getValue(), powerUp.getKey().maximumPerGame()));
        }

        List<GamePlayer> gamePlayerList = new ArrayList<>(this.game.players.values());
        gamePlayerList.remove(this.gamePlayer);

        this.response.appendInt32(this.game.players.size());
        this.gamePlayer.serialize(this.response);
        for (GamePlayer player : gamePlayerList)
        {
            player.serialize(this.response);
        }
        return this.response;
    }
}
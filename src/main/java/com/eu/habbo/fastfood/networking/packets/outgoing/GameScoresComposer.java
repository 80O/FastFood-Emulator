package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.game.GamePlayer;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

import java.util.List;
import java.util.Map;

public class GameScoresComposer extends MessageComposer
{
    public final List<GamePlayer> gamePlayers;

    public GameScoresComposer(List<GamePlayer> gamePlayers)
    {
        this.gamePlayers = gamePlayers;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GameScoresComposer);
        this.response.appendInt32(this.gamePlayers.size());
        for (GamePlayer gamePlayer : this.gamePlayers)
        {
            this.response.appendString(gamePlayer.username()); //TODO idk
            this.response.appendInt32(gamePlayer.credits()); //TODO score
        }
        return this.response;
    }
}
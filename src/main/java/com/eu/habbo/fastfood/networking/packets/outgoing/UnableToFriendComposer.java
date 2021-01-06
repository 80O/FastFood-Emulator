package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.game.GamePlayer;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

import java.util.List;

public class UnableToFriendComposer extends MessageComposer
{
    public final List<GamePlayer> gamePlayers;

    public UnableToFriendComposer(List<GamePlayer> gamePlayers)
    {
        this.gamePlayers = gamePlayers;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnableToFriendComposer);
        this.response.appendInt32(this.gamePlayers.size());
        for (GamePlayer gamePlayer : this.gamePlayers)
        {
            this.response.appendInt32(gamePlayer.id());
        }
        return this.response;
    }
}
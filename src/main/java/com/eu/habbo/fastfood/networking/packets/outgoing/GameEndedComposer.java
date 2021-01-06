package com.eu.habbo.fastfood.networking.packets.outgoing;

import com.eu.habbo.fastfood.game.GamePlayer;
import com.eu.habbo.fastfood.game.PowerUp;
import com.eu.habbo.fastfood.networking.packets.MessageComposer;
import com.eu.habbo.fastfood.networking.packets.Outgoing;
import com.eu.habbo.fastfood.networking.packets.ServerMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class GameEndedComposer extends MessageComposer
{
    public final ArrayList<GamePlayer> gamePlayers;

    public GameEndedComposer(Collection<GamePlayer> gamePlayers)
    {
        this.gamePlayers = new ArrayList<>(gamePlayers);
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GameEndedComposer);
        this.response.appendInt32(this.gamePlayers.size()); //Count
        Collections.sort(gamePlayers);
        for (GamePlayer gamePlayer : this.gamePlayers)
        {
            this.response.appendInt32(gamePlayer.id());
            this.response.appendInt32(gamePlayer.game().score(gamePlayer));
        }
        return this.response;
    }
}
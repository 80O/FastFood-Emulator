package com.eu.habbo.fastfood.networking;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.IDisposable;
import com.eu.habbo.fastfood.networking.packets.PacketManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer implements IDisposable
{
    private boolean loaded   = false;
    private boolean disposed = false;

    private final PacketManager packetManager;
    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final ConcurrentHashMap<String, Integer> authenticationTickets = new ConcurrentHashMap<>();

    private final String host;
    private final int port;

    public GameServer(String host, int port) throws Exception
    {
        this.packetManager = new PacketManager();
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(3);
        this.serverBootstrap = new ServerBootstrap();

        this.host = host;
        this.port = port;

        this.loaded = true;
    }

    public void initialise()
    {
        this.serverBootstrap.group(this.bossGroup, this.workerGroup);
        this.serverBootstrap.channel(NioServerSocketChannel.class);
        final GameMessageHandler gameMessageHandler = new GameMessageHandler();
        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>()
        {
            @Override
            public void initChannel(SocketChannel ch) throws Exception
            {
                ch.pipeline().addLast("bytesDecoder", new GameByteDecoder());
                ch.pipeline().addLast(gameMessageHandler);
            }
        });
        this.serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        this.serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);
        this.serverBootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        this.serverBootstrap.childOption(ChannelOption.SO_RCVBUF, 5120);
        this.serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(5120));
        this.serverBootstrap.childOption(ChannelOption.ALLOCATOR, new UnpooledByteBufAllocator(false));
    }

    public void connect()
    {
        ChannelFuture channelFuture = this.serverBootstrap.bind(this.host, this.port);

        while (!channelFuture.isDone())
        {}

        if (!channelFuture.isSuccess())
        {
            System.out.println("[Game Server] Failed to connect to the host (" + this.host + ":" + this.port + ").");
            System.out.println("[Game Server] Port already in use?");
            System.exit(0);
        }
        else
        {
            System.out.println("[Game Server] Started on " + this.host + ":" + this.port);
        }
    }

    public PacketManager packetManager()
    {
        return packetManager;
    }

    public ServerBootstrap serverBootstrap()
    {
        return serverBootstrap;
    }

    public EventLoopGroup bossGroup()
    {
        return bossGroup;
    }

    public EventLoopGroup workerGroup()
    {
        return workerGroup;
    }

    public String host()
    {
        return host;
    }

    public int port()
    {
        return port;
    }

    public boolean validTicket(String ticket)
    {
        if (this.authenticationTickets.containsKey(ticket))
        {
            return true;
        }

        boolean found = false;
        try (Connection connection = FastFood.database.dataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM login_tickets WHERE ticket LIKE ? LIMIT 1"))
        {
            statement.setString(1, ticket);
            try (ResultSet set  = statement.executeQuery())
            {
                if (set.next())
                {
                    found = true;
                    this.authenticationTickets.put(ticket, set.getInt("user_id"));
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return found;
    }

    @Override
    public void dispose()
    {
        System.out.println("[Game Server] Disposing...");
        this.loaded = false;
        this.workerGroup.shutdownGracefully();
        this.bossGroup.shutdownGracefully();
        this.packetManager.dispose();
        this.disposed = true;
        System.out.println("[Game Server] Disposed!");
    }

    @Override
    public boolean disposed()
    {
        return this.disposed;
    }
}

package com.eu.habbo.fastfood.networking;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.networking.packets.ClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class GameMessageHandler extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRegistered(ChannelHandlerContext ctx)
    {
        FastFood.gameClientManager.addClient(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx)
    {
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        try
        {
            ByteBuf m = (ByteBuf) msg;
            int length = m.readInt();
            short header = m.readShort();
            ByteBuf body = Unpooled.wrappedBuffer(m.readBytes(m.readableBytes()));
            FastFood.gameServer.packetManager().handlePacket(ctx.attr(GameClientManager.CLIENT).get(), new ClientMessage(header, body));
            body.release();
            m.release();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        System.out.println("Connection closed!");
        FastFood.gameClientManager.disposeClient(ctx.channel());
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        ctx.close();
    }
}
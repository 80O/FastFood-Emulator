package com.eu.habbo.fastfood.networking.packets;

public class Incoming
{
    public final static int AuthenticationMessageEvent = 1;
    public final static int UseButtonMessageEvent = 3;
    public final static int QuitGameMessageEvent = 5;
    public final static int JoinGameMessageEvent = 6;
    public final static int InitializeLocalizationMessageEvent = 7;
    public final static int RequestUserCreditBalanceMessageEvent = 14;//11/9/14 are the same packets
    public final static int RequestItemPriceListMessageEvent = 9;
    public final static int BuyCatalogItemMessageEvent = 10;
    public final static int RequestInventoryMessageEvent = 8;
    public final static int RequestScoresMessageEvent = 12;
    public final static int FriendRequestMessageEvent = 13;
    public final static int RequestRankMessageEvent = 11;
    public final static int RequestGlobalScoresMessageEvent = 15;
}
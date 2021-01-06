package com.eu.habbo.fastfood.game;

import com.eu.habbo.fastfood.FastFood;
import com.eu.habbo.fastfood.IDisposable;
import gnu.trove.map.hash.THashMap;

import java.sql.*;

public class TextManager implements IDisposable
{
    private boolean loaded   = false;
    private boolean disposed = false;

    private final THashMap<Language, THashMap<String, String>> textConfiguration = new THashMap<Language, THashMap<String, String>>();

    public TextManager()
    {
        System.out.println("[Text Manager] Initializing...");
        this.loaded = true;
        this.reload();
        System.out.println("[Text Manager] Loaded!");
    }

    public void reload()
    {
        if (this.loaded)
        {
            this.textConfiguration.clear();

            for (Language language : Language.values())
            {
                this.textConfiguration.put(language, new THashMap<String, String>());
            }
            try (Connection connection = FastFood.database.dataSource().getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet set = statement.executeQuery("SELECT * FROM localization"))
            {
                while (set.next())
                {
                    this.textConfiguration.get(Language.fromCountryCode(set.getString("language"))).put(set.getString("locale_id"), set.getString("locale_value"));
                }
            } catch (SQLException e)
            {
                e.printStackTrace();
            }

            System.out.println("[Text Manager] Reloaded!");
        }
    }

    public THashMap<Language, THashMap<String, String>> textConfiguration()
    {
        return this.textConfiguration;
    }

    @Override
    public void dispose()
    {
        this.loaded = false;
        this.textConfiguration.clear();
        this.disposed = true;
        System.out.println("[Text Manager] Disposed!");
    }

    @Override
    public boolean disposed()
    {
        return this.disposed;
    }

    public enum Language
    {
        ENGLISH("EN"),
        DUTCH("NL"),
        GERMAN("DE"),
        DANISH("DK"),
        SWEDISH("SE");

        private final String countryCode;

        Language(String countryCode)
        {
            this.countryCode = countryCode;
        }

        public static Language fromCountryCode(String countryCode)
        {
            for (Language language : Language.values())
            {
                if (language.countryCode.equalsIgnoreCase(countryCode))
                {
                    return language;
                }
            }

            return ENGLISH;
        }

        public String countryCode()
        {
            return this.countryCode;
        }
    }
}
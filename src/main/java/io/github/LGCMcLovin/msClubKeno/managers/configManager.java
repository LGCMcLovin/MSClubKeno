package io.github.LGCMcLovin.msClubKeno.managers;

import io.github.LGCMcLovin.msClubKeno.MSClubKeno;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;


@SuppressWarnings("CanBeFinal")
public class configManager
{
     MSClubKeno instance;

     ConfigurationLoader<CommentedConfigurationNode> loader;
     CommentedConfigurationNode config;

    static int lastTicket;
    static int currentDraw;
    static int defaultInterval;


    public configManager(MSClubKeno plugin) throws IOException {
        this.instance = plugin;
        this.loader = HoconConfigurationLoader.builder().setPath(plugin.defaultConf).build();
        this.config = loader.load();

    }

    public CommentedConfigurationNode getConfig(){return config;}

    public void enable(File configFile, ConfigurationLoader<CommentedConfigurationNode> configLoader)
    {

        if(!configFile.exists())
        {
            try
            {
                config = configLoader.load();

                config.getNode("last-ticket").setValue(0).setComment("ID of the last ticket purchased");
                config.getNode("current-draw").setValue(0).setComment("ID of the current drawing");
                config.getNode("default-interval").setValue(300).setComment("Default drawing interval, 300 by default");
                //set default config values here
                reloadConfig();



            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            load();
        }
    }

    public void save()
    {
        try
        {
            loader.save(instance.getConfig().getConfig());

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void load()
    {
        try
        {
            config = loader.load();

            lastTicket = config.getNode("last-ticket").getInt();
            currentDraw = config.getNode("current-draw").getInt();
            defaultInterval = config.getNode("default-interval").getInt();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        save();
        load();
    }

}

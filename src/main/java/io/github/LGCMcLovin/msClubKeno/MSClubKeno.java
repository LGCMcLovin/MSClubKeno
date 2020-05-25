package io.github.LGCMcLovin.msClubKeno;


import com.google.inject.Inject;
import io.github.LGCMcLovin.msClubKeno.handlers.Drawing;
import io.github.LGCMcLovin.msClubKeno.managers.*;
import io.github.LGCMcLovin.msClubKeno.threads.DrawingThread;
import io.github.LGCMcLovin.msClubKeno.threads.LiveUpdateThread;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;


@SuppressWarnings("unused")
@Plugin(id="msclubkeno", name = "MSClubKeno_Sponge", version = "0.1", description = "A fun gambling game for your server.")
public class MSClubKeno
{

    private static MSClubKeno instance = null;
    public static MSClubKeno getInstance()
    {
        return instance;
    }

    public Commands commandMgr;

    @Inject
    Game game;
  @Inject
    private Logger log;

    @Inject
    @DefaultConfig(sharedRoot = true)
    public File configFile;

    @Inject
    @DefaultConfig(sharedRoot = true)
    public Path defaultConf;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File configDir;

    @Inject
    @DefaultConfig(sharedRoot = true)
    ConfigurationLoader<CommentedConfigurationNode> configMgr;

    configManager config;
    CommentedConfigurationNode cfg;


    @Listener
    public void onStarting(GameStartedServerEvent event) throws IOException {
        instance = this;
        this.config = new configManager(instance);

        config.enable(configFile, configMgr);
        log.info("[MSCLUBKENO] $$$$$$$ config enabled!");

        cfg = instance.getConfig().getConfig();


        config.enable(configFile, configMgr);

        EconManager.setEconomyService();
        registerCommands();
        initDrawTimer();

    }

    public Logger getLogger()
    {
        return log;
    }


    public configManager getConfig()
    {
        return config;
    }
    public void registerCommands()
    {
        this.commandMgr = new Commands(instance);
        commandMgr.init();
    }

    public void initDrawTimer()
    {


        cfg = config.getConfig();

        int newID = cfg.getNode("current-draw").getInt();
        newID++;

        cfg.getNode("current-draw").setValue(newID);

        config.save();
        config.load();
        int newTime = cfg.getNode("default-interval").getInt();
        log.info("[MSCLUBKENO] $$$$$$$ new interval set");
        int multiplier =  new Random().nextInt(5) + 1;
        ArrayList<Integer> results;
        results =  DrawingManager.getNewResults();

        log.info("[MSCLUBKENO] $$$$$$$ RESULTS GENERATED FOR NEW DRAWING THREAD");

        Drawing newDrawing = new Drawing(newID, results);

        DrawingThread newDrawCycle = new DrawingThread(newID, newTime, newDrawing, multiplier);

        Thread drawingThread = new Thread(newDrawCycle);
        drawingThread.start();

        LiveUpdateThread liveUpdateThread = new LiveUpdateThread();

        Task.Builder taskBuilder =  Task.builder();
        taskBuilder.execute(liveUpdateThread);
        taskBuilder.submit(instance);


        log.info("[MSCLUBKENO] $$$$$$$ DRAWING THREAD SHOULD BE RUNNING");
    }

    @Listener
    public void onCancelClickEvent(ClickInventoryEvent.Primary event)
    {
        if(event.getTargetInventory().getName().get().contains("Drawing") || event.getTargetInventory().getName().get().contains("Ticket"))
        {
            ItemStack item = event.getCursorTransaction().getFinal().createStack();
            if (event.getSource() instanceof Player) {
                if (item.getType() == ItemTypes.BARRIER) {

                    Player player = (Player) event.getSource();
                    player.closeInventory();
                }
                event.setCancelled(true);
            }
        }
    }
    @Listener
    public void onCancelClickSecondaryEvent(ClickInventoryEvent.Secondary event)
    {
        if(event.getTargetInventory().getName().get().contains("Drawing") || event.getTargetInventory().getName().get().contains("Ticket"))
        {
            ItemStack item = event.getCursorTransaction().getFinal().createStack();
            if (event.getSource() instanceof Player) {
                if (item.getType() == ItemTypes.BARRIER) {

                    Player player = (Player) event.getSource();
                    player.closeInventory();
                }
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void onPlayerClose(InteractInventoryEvent.Close event)
    {
        if(event.getSource() instanceof Player) {
            Player player = (Player) event.getSource();
            if (!MenuManager.getPlayersLiveViewing().contains(player))
            {
                return;
            }

            MenuManager.removePlayerLiveView(player);
        }
    }

    @Listener
    public void onClickEvent(InteractBlockEvent.Primary event)
    {


        if(event.getTargetBlock().getState().getType().getName().contains("screen"))
        {
            event.setCancelled(true);
        }

    }
    @Listener
    public void onSecondaryClickEvent(InteractBlockEvent.Secondary event)
    {


        if(event.getTargetBlock().getState().getType().getName().contains("screen"))
        {
            event.setCancelled(true);
        }

    }


}

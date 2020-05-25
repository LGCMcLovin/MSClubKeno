package io.github.LGCMcLovin.msClubKeno.threads;

import com.sun.org.slf4j.internal.Logger;
import io.github.LGCMcLovin.msClubKeno.MSClubKeno;
import io.github.LGCMcLovin.msClubKeno.handlers.Drawing;
import io.github.LGCMcLovin.msClubKeno.handlers.Ticket;
import io.github.LGCMcLovin.msClubKeno.managers.*;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DrawingThread implements Runnable
{
    MSClubKeno instance = MSClubKeno.getInstance();

    static Logger log;
    CommentedConfigurationNode  cfg = instance.getConfig().getConfig();


    private  int timeToNextDraw;
    private static int staticTime;
    private  int ID;
    private int multiplier;

    private  Drawing drawing;
    private static Drawing staticDrawing;
    private static ArrayList<Ticket> tickets = new ArrayList<>();

    private static Inventory resultInv;
    private static int resultID;
    private boolean drawingStarted = false;

    private static int currentMultiplier;

    private static final ArrayList<DrawingThread> recentDraws = new ArrayList<>();


    public DrawingThread(MSClubKeno instance){this.instance = instance;}

    public DrawingThread(int ID, int timeToNextDraw, Drawing drawing, int multiplier)
    {
        this.ID = ID;
        this.timeToNextDraw = timeToNextDraw;
        this.drawing = drawing;
        this.multiplier = multiplier;
        recentDraws.add(this);



    }

    public void run()
    {
       if(!drawingStarted)
        {
            startNewDrawing();
            ChatManager.chatBroadcast("New Drawing Started");
        }

       while(timeToNextDraw < cfg.getNode("default-interval").getInt() * 2)
       {

           ID = getCurrentID();
           timeToNextDraw = getTimeToNextDraw();
           staticTime = timeToNextDraw;
           drawing = getCurrentDraw();
           tickets = getDrawTickets();

 //          getTimeToNextDraw() >= cfg.getNode("default-interval").getInt() / 2 &&

           if (getTimeToNextDraw() % 10 == 0 && resultID <= 14)
           {

               ItemStack resultItem = MenuManager.getResultItem(drawing, resultID);


               int slotNumber = drawing.getDrawingResults().get(resultID);
               Slot slot = resultInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(MenuManager.getColumn(slotNumber), MenuManager.getRow(slotNumber))));

               slot.set(resultItem);
/*
               for (Player viewingPlayer : Sponge.getServer().getOnlinePlayers())
               {
                   if (MenuManager.getPlayersLiveViewing().contains(viewingPlayer))
                   {
                       viewingPlayer.openInventory(resultInv);
                   }
               }
*/

 //              MenuManager.updateLiveViewers(drawing, resultID);

               resultID++;
           }
           if (getTimeToNextDraw() == cfg.getNode("default-interval").getInt() / 2) {
               if (Ticket.getAllTickets() == null) {
                   log.error("NO TICKETS EXIST");
               }
               if (Ticket.getAllTickets() != null) {
                   for (Ticket newTicket : Ticket.getAllTickets()) {
                       if (!getDrawTickets().contains(newTicket)) {
                           if (newTicket.getDrawIDs().contains(getCurrentID())) {
                               getDrawTickets().add(newTicket);
                           }
                       }
                   }
               }
           }

           if (timeToNextDraw == 0) {
 /*              for (Ticket ticket : getDrawTickets()) {
                   if (ticket.getDrawIDs().get(ticket.getDrawIDs().toArray().length - 1) + 4 < getCurrentID()) {
                       Objects.requireNonNull(Ticket.getAllTickets()).remove(ticket);
                   }
                   if (ticket.getDrawIDs().contains(getCurrentID())) {
                       double payout = ResultManager.calculatePayout(ticket, drawing);
                       if (payout == 0.0) {
                           ChatManager.sendPlayerMSG(ticket.getTicketOwner(), "Sorry ticket " + ticket.getTicketID() + "was not a winner this drawing.");
                           ChatManager.sendMSGnoPrefix(ticket.getTicketOwner(), "Your matched numbers: " + ResultManager.getMatchedNumbers(ticket, drawing).toString());
                       }
                       if (!(payout == 0.0)) {
                           if (!ticket.getMultiplierActive()) {
                               if (EconManager.deposit(ticket.getTicketOwner(), payout).equals(ResultType.SUCCESS)) {
                                   ChatManager.sendPlayerMSG(ticket.getTicketOwner(), "You have won " + payout + "on drawing " + getCurrentID());
                                   ChatManager.sendMSGnoPrefix(ticket.getTicketOwner(), "Your matched numbers were " + ResultManager.getMatchedNumbers(ticket, drawing));
                               } else {
                                   ticket.getTicketOwner().sendMessage(Text.of("An error occurred while issuing payout"));
                               }
                           }

                           if (ticket.getMultiplierActive()) {
                               if (EconManager.deposit(ticket.getTicketOwner(), payout * getMultiplier()).equals(ResultType.SUCCESS)) {
                                   ChatManager.sendPlayerMSG(ticket.getTicketOwner(), "You have won " + payout * getMultiplier());
                                   ChatManager.sendMSGnoPrefix(ticket.getTicketOwner(), "Your matched numbers were " + ResultManager.getMatchedNumbers(ticket, drawing));
                               } else {
                                   ticket.getTicketOwner().sendMessage(Text.of("An error occured while issuing payout"));
                               }
                           }
                       }
                   }
               }
*/
               ChatManager.chatBroadcast("Drawing " + (getCurrentID() + 1) + " has started!");


               startNewDrawing();
           }
           MSClubKeno.getInstance().getLogger().info("tick run");
           try {
               TimeUnit.SECONDS.sleep(5);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           runTick();
       }
    }

    public static int getStaticID(){return getStaticDrawing().getDrawingID() ;}
    public static int getResultID(){return resultID;}
    public  int getCurrentID() {return ID;}
    public  int getTimeToNextDraw(){return timeToNextDraw;}
    public static int getStaticTime(){return staticTime;}
    public  Drawing getCurrentDraw(){return drawing;}
    public static Drawing getStaticDrawing(){return staticDrawing;}
    public static ArrayList<Ticket> getDrawTickets(){return tickets;}
    public int getMultiplier() {return multiplier;}
    public static int getCurrentMultiplier(){return currentMultiplier;}
    public static ArrayList<DrawingThread> getRecentDraws(){return recentDraws;}
    public static Inventory getLiveResultInv(){return resultInv;}
    public void runTick()
    {
        timeToNextDraw -= 5;
    }

    public void startNewDrawing()
    {
        LiveUpdateThread liveUpdateThread = new LiveUpdateThread();

        Task.Builder taskBuilder =  Task.builder();
        taskBuilder.execute(liveUpdateThread);
        taskBuilder.submit(instance);

        LiveUpdateThread.refreshDrawing();
        drawingStarted = true;

        resultID = 0;

        int newID = cfg.getNode("current-draw").getInt();
        newID++;
        cfg.getNode("current-draw").setValue(newID);

        instance.getConfig().save();
        int newTime = cfg.getNode("default-interval").getInt();
         multiplier = new Random().nextInt(5);

        if(multiplier == 0)
        {
            multiplier = 1;
        }



        ArrayList<Integer> results = DrawingManager.getNewResults();

        drawing = new Drawing(newID, results);
        staticDrawing = drawing;
       resultInv = (MenuManager.defaultResultsMenu(drawing));



        ID = newID;

        currentMultiplier = multiplier;
        timeToNextDraw = newTime;

/*
        for(Player viewingPlayer : Sponge.getServer().getOnlinePlayers())
        {
            if(MenuManager.getPlayersLiveViewing().contains(viewingPlayer))
            {
                viewingPlayer.closeInventory();
                MenuManager.getPlayersLiveViewing().add(viewingPlayer);
            }
        }
*/
        instance.getLogger().info("Completed startup of new drawing");
    }
}

package io.github.LGCMcLovin.msClubKeno.threads;

import io.github.LGCMcLovin.msClubKeno.MSClubKeno;
import io.github.LGCMcLovin.msClubKeno.handlers.Drawing;
import io.github.LGCMcLovin.msClubKeno.handlers.Ticket;
import io.github.LGCMcLovin.msClubKeno.managers.*;
import jdk.nashorn.internal.ir.WhileNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;


import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static io.github.LGCMcLovin.msClubKeno.threads.DrawingThread.*;

public class LiveUpdateThread implements Runnable
{
    MSClubKeno instance = MSClubKeno.getInstance();
    Drawing drawing;
    public int resultID = -1;
    Inventory resultInv;
    int newResultID ;
    static boolean winsPaid = false;

    public void run()
    {




        instance.getLogger().info("Sync thread Started");

        drawing = DrawingThread.getStaticDrawing();
        newResultID = DrawingThread.getResultID();

        instance.getLogger().info("newRestultID " + newResultID);

                resultID = newResultID;
                resultInv = DrawingThread.getLiveResultInv();

                for (Player viewingPlayer : Sponge.getServer().getOnlinePlayers()) {
                    if (MenuManager.getPlayersLiveViewing().contains(viewingPlayer)) {
                        viewingPlayer.closeInventory();
                        viewingPlayer.openInventory(resultInv);
                        MenuManager.getPlayersLiveViewing().add(viewingPlayer);
                    }
                }




                for (Ticket ticket : DrawingThread.getDrawTickets()) {
                    if (ticket.getDrawIDs().get(ticket.getDrawIDs().toArray().length - 1) + 4 < getStaticID()) {
                        Objects.requireNonNull(Ticket.getAllTickets()).remove(ticket);
                    }
                    if (ticket.getDrawIDs().contains(getStaticID())) {
                        double payout = ResultManager.calculatePayout(ticket, drawing);
                        if (payout == 0.0) {
                            ChatManager.sendPlayerMSG(ticket.getTicketOwner(), "&6Sorry ticket &f" + ticket.getTicketID() + " &6was not a winner this drawing.");
                            ChatManager.sendMSGnoPrefix(ticket.getTicketOwner(), "&3Your matched numbers were &f" + ResultManager.getMatchedNumbers(ticket, drawing).toString());
                        }
                        if (!(payout == 0.0)) {
                            if (!ticket.getMultiplierActive()) {
                                if (EconManager.deposit(ticket.getTicketOwner(), payout).equals(ResultType.SUCCESS)) {
                                    ChatManager.sendPlayerMSG(ticket.getTicketOwner(), "&k1&6You have won&k1 &f" + payout + "&6on drawing " + getStaticID());
                                    ChatManager.sendMSGnoPrefix(ticket.getTicketOwner(), "&3Your matched numbers were &f" + ResultManager.getMatchedNumbers(ticket, drawing));
                                    winsPaid = true;
                                } else {
                                    ticket.getTicketOwner().sendMessage(Text.of("An error occurred while issuing payout"));
                                }
                            }

                            if (ticket.getMultiplierActive()) {
                                if (EconManager.deposit(ticket.getTicketOwner(), payout * DrawingThread.getCurrentMultiplier()).equals(ResultType.SUCCESS)) {
                                    ChatManager.sendPlayerMSG(ticket.getTicketOwner(), "&k1&6You have won&k1 " + (DrawingThread.getCurrentMultiplier() * payout) + "&6on drawing " + getStaticID());
                                    ChatManager.sendMSGnoPrefix(ticket.getTicketOwner(), "&3Your matched numbers were &f" + ResultManager.getMatchedNumbers(ticket, drawing));
                                    winsPaid = true;
                                } else {
                                    ticket.getTicketOwner().sendMessage(Text.of("An error occured while issuing payout"));
                                }
                            }
                        }
                    }
                }

            instance.getLogger().info("sync tick run");
    }

    public static void refreshDrawing()
    {
        winsPaid = false;
    }

    /*


    public void startNewDrawing()
    {


        resultInv = MenuManager.defaultResultsMenu(drawing);

        for(Player viewingPlayer : Sponge.getServer().getOnlinePlayers())
        {
            if(MenuManager.getPlayersLiveViewing().contains(viewingPlayer))
            {
                viewingPlayer.openInventory(resultInv);
                MenuManager.getPlayersLiveViewing().add(viewingPlayer);
            }
        }
        instance.getLogger().info("Completed startup of update thread");

    }
 */
}

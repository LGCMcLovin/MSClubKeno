package io.github.LGCMcLovin.msClubKeno.managers;

import io.github.LGCMcLovin.msClubKeno.MSClubKeno;
import io.github.LGCMcLovin.msClubKeno.handlers.Drawing;
import io.github.LGCMcLovin.msClubKeno.handlers.Ticket;
import io.github.LGCMcLovin.msClubKeno.threads.DrawingThread;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Random;

public class TicketManager
{

     static final MSClubKeno plugin = MSClubKeno.getInstance();
     static CommentedConfigurationNode cfg;


    public static ArrayList<Integer> getDrawIDs(int drawAmount)
    {

        int newDrawID;
        ArrayList<Integer> drawIDs = new ArrayList<>();

        cfg = plugin.getConfig().getConfig();

        for(Drawing draw : Drawing.getAllDrawings()) {
            if (draw.getDrawingID() == cfg.getNode("current-draw").getInt()) {

                newDrawID = cfg.getNode("current-draw").getInt();

                if(DrawingThread.getStaticTime() <= cfg.getNode("default-interval").getInt() / 2) {
                    newDrawID++;
                }

                int i = drawAmount;
                while (i > 0) {
                    drawIDs.add(newDrawID);
                    newDrawID++;
                    i--;
                }
            }
        }
        return drawIDs;
    }


    public static ArrayList<Integer> getNewNumbers(int drawQuantity)
    {
        ArrayList<Integer> numbers = new ArrayList<>();

        while(numbers.toArray().length < drawQuantity)
        {
            int number =  new Random().nextInt(54) + 1;
            if(numbers.contains(number))
            {
                continue;
            }
            if(!numbers.contains(number))
            {
                numbers.add(number);

            }
        }
        return numbers;
    }



    public static ArrayList<Ticket> getPlayerTickets(Player player)
    {
        ArrayList<Ticket> playerTickets = new ArrayList<>();
        for(Ticket ticket : Ticket.getAllTickets())
        {
            if(ticket.getTicketOwner().equals(player))
            {
                playerTickets.add(ticket);
            }
        }
        return playerTickets;
    }

    public static ArrayList<ItemStack> getPlayerMenuTickets(Player player)
    {
        ItemStack item;
        ArrayList<ItemStack> menuTickets = new ArrayList<>();

        for(Ticket ticket : TicketManager.getPlayerTickets(player))
        {
            item = ItemStack.of(ItemTypes.PAPER,  1);

            ArrayList<Text> lore = new ArrayList<>();

            lore.add(Text.of("Draws: " + ticket.getDrawIDs()));
            lore.add(Text.of("Spots: " + ticket.getPlayedNumbers()));
            lore.add(Text.of("Bet: " + ticket.getBetAmt()));
            lore.add(Text.of("Multiplier: " + ticket.getMultiplierActive()));


            item.offer(Keys.ITEM_LORE, lore);
            item.offer(Keys.DISPLAY_NAME, Text.of("Ticket Number: " + ticket.getTicketID()));

            menuTickets.add(item);
        }
        return menuTickets;
    }

    public  ArrayList<ItemStack> getResultMenuItems(Ticket ticket, Drawing drawing) {
        ArrayList<Integer> matchedNumbers = ResultManager.getMatchedNumbers(ticket, drawing);
        ArrayList<Integer> playedNumbers = ticket.getPlayedNumbers();
        ArrayList<Integer> drawResults = drawing.getDrawingResults();

        ArrayList<ItemStack> resultMenuItems = new ArrayList<>();

        ItemStack item;

        int i = 1;
        while (i <= 54)
        {
            if (!drawResults.contains(i)) {
                item = ItemStack.of(ItemTypes.FIREWORK_CHARGE, i);
                ArrayList<Text> lore = new ArrayList<>();

                item.offer(Keys.DISPLAY_NAME, Text.of("Ticket: " + ticket.getTicketID()));
                lore.add(Text.of("Draws: " + ticket.getDrawIDs()));
                lore.add(Text.of("Spots: " + ticket.getPlayedNumbers()));
                lore.add(Text.of("Bet: " + ticket.getBetAmt()));
                lore.add(Text.of("Multiplier: " + ticket.getMultiplierActive()));

                item.offer(Keys.ITEM_LORE, lore);

                resultMenuItems.add(item);
            }


            for (Integer drawNumber : drawResults) {
                if (!playedNumbers.contains(drawNumber)) {
                    item = ItemStack.of(ItemTypes.SLIME_BALL, drawNumber);
                    ArrayList<Text> lore = new ArrayList<>();

                    item.offer(Keys.DISPLAY_NAME, Text.of("Ticket: " + ticket.getTicketID()));
                    lore.add(Text.of("Draws: " + ticket.getDrawIDs()));
                    lore.add(Text.of("Spots: " + ticket.getPlayedNumbers()));
                    lore.add(Text.of("Bet: " + ticket.getBetAmt()));
                    lore.add(Text.of("Multiplier: " + ticket.getMultiplierActive()));
                    item.offer(Keys.ITEM_LORE, lore);

                    resultMenuItems.add(item);
                }
            }


            for (Integer number : playedNumbers) {
                if (matchedNumbers.contains(number)) {
                    item = ItemStack.of(ItemTypes.DIAMOND, number);

                    ArrayList<Text> lore = new ArrayList<>();

                    item.offer(Keys.DISPLAY_NAME, Text.of("Ticket: " + ticket.getTicketID()));
                    lore.add(Text.of("Draws: " + ticket.getDrawIDs()));
                    lore.add(Text.of("Spots: " + ticket.getPlayedNumbers()));
                    lore.add(Text.of("Bet: " + ticket.getBetAmt()));
                    lore.add(Text.of("Multiplier: " + ticket.getMultiplierActive()));
                    item.offer(Keys.ITEM_LORE, lore);

                    resultMenuItems.add(item);
                }

                if (!matchedNumbers.contains(number)) {
                    item = ItemStack.of(ItemTypes.EMERALD, number);
                    ArrayList<Text> lore = new ArrayList<>();

                    item.offer(Keys.DISPLAY_NAME, Text.of("Ticket: " + ticket.getTicketID()));
                    lore.add(Text.of("Draws: " + ticket.getDrawIDs()));
                    lore.add(Text.of("Spots: " + ticket.getPlayedNumbers()));
                    lore.add(Text.of("Bet: " + ticket.getBetAmt()));
                    lore.add(Text.of("Multiplier: " + ticket.getMultiplierActive()));
                    item.offer(Keys.ITEM_LORE, lore);



                    resultMenuItems.add(item);
                }
            }

            i++;

        }
        return resultMenuItems;
    }
}

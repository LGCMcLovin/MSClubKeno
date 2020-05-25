package io.github.LGCMcLovin.msClubKeno.managers;

import io.github.LGCMcLovin.msClubKeno.MSClubKeno;
import io.github.LGCMcLovin.msClubKeno.handlers.Drawing;
import io.github.LGCMcLovin.msClubKeno.handlers.Ticket;
import io.github.LGCMcLovin.msClubKeno.threads.DrawingThread;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("CanBeFinal")
public class MenuManager
{
    static final MSClubKeno plugin = MSClubKeno.getInstance();
    static int menuSize;
    static Player ticketPlayer;

    static ArrayList<Player> playersLiveViewing = new ArrayList<>();


    public static Inventory buildTicketResultsMenu(Ticket ticket, Drawing drawing)
    {

        for(Player player : playersLiveViewing)
        {
            if(Objects.requireNonNull(Ticket.getTicketsByPlayer(player)).contains(ticket))
            {
                ticketPlayer = player;
            }

        }

        Inventory inv = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of("Ticket: " + ticket.getTicketID() + "Drawing: " + drawing.getDrawingID())))
                .property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 6))
                .build(plugin);


        List<Integer> matchedNumbers = ResultManager.getMatchedNumbers(ticket, drawing);

        ItemStack item;

        int i = 1;

        while(i <= 54)
        {
            item = ItemStack.of(ItemTypes.REDSTONE, i);

            if(drawing.getDrawingResults().contains(i) && !matchedNumbers.contains(i))
            {
                item = ItemStack.of(ItemTypes.EMERALD, i);
            }

            if(drawing.getDrawingResults().contains(i) && matchedNumbers.contains(i))
            {
                item = ItemStack.of(ItemTypes.DIAMOND, i);
            }



           ArrayList<Text> lore = new ArrayList<>();

            lore.add(Text.of("Numbers: " + ticket.getPlayedNumbers()));
            lore.add(Text.of("Multiplier: " + ticket.getMultiplierActive()));


            item.offer(Keys.ITEM_LORE, lore);
            item.offer(Keys.DISPLAY_NAME, Text.of("Ticket: " + ticket.getTicketID()));

            Slot slot = inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(getColumn(i), getRow(i))));

            slot.set(item);
            i++;
        }

        return inv;
    }

    public static Inventory BuildPlayerTicketsMenu(Player player, ArrayList<ItemStack> menuItems)
    {

        Inventory inv = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(player.getName() + "'s Tickets: ")))
                .property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 6))
                .build(plugin);

        int i = 1;
        while(i <= menuItems.toArray().length)
        {
            Slot slot = inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(getColumn(i), getRow(i))));


            slot.set(menuItems.get(i-1));

            i++;
        }

        return inv;
    }



    public static void openPlayerTicketMenu(Player player, Inventory inv)
    {
        player.openInventory(inv);
    }



/*    private static Integer getMenuSize(ArrayList<ItemStack> menuItems)
    {
        if(menuItems.toArray().length <= 9)
        {
            menuSize = 9;
        }
        if(menuItems.toArray().length > 9 && menuItems.toArray().length <= 18)
        {
            menuSize = 18;
        }

        if(menuItems.toArray().length > 18 && menuItems.toArray().length <= 27)
        {
            menuSize = 27;
        }

        if(menuItems.toArray().length > 27 && menuItems.toArray().length <= 36)
        {
            menuSize = 36;
        }

        if(menuItems.toArray().length > 36 && menuItems.toArray().length <= 45)
        {
            menuSize = 45;
        }

        if(menuItems.toArray().length > 45 && menuItems.toArray().length <= 54)
        {
            menuSize = 54;
        }
        return menuSize;
    }
*/
    public static Inventory defaultResultsMenu(Drawing drawing)
    {

        ItemStack item;
        Inventory inv = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of("Drawing: " + drawing.getDrawingID())))
                .property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 6))
                .build(plugin);


        int i = 1;
        while(i <= 54)
        {

            item = ItemStack.of(ItemTypes.REDSTONE, i);

            ArrayList<Text> lore = new ArrayList<>();

            item.offer(Keys.DISPLAY_NAME, Text.of("Drawing : " + drawing.getDrawingID()));

            lore.add(Text.of("Numbers: " + drawing.getDrawingResults()));
            lore.add(Text.of("Multiplier: " + DrawingThread.getCurrentMultiplier()));

            item.offer(Keys.ITEM_LORE, lore);

            Slot slot = inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(getColumn(i), getRow(i))));



            slot.set(item);
            i++;
        }
        return inv;
    }

    public static void updateLiveViewers(Drawing drawing, int resultID )
    {
        Inventory resultInv = MenuManager.defaultResultsMenu(drawing);



        int slotNumber = drawing.getDrawingResults().get(resultID);
        Slot slot = resultInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(MenuManager.getColumn(slotNumber), MenuManager.getRow(slotNumber))));


        ItemStack resultItem = MenuManager.getResultItem(drawing, resultID);


            slot.clear();
            slot.set(resultItem);
            for (Player viewingPlayer : Sponge.getServer().getOnlinePlayers())
            {
                if (MenuManager.getPlayersLiveViewing().contains(viewingPlayer))
                {
                    viewingPlayer.openInventory(resultInv);
                }
            }
    }



    public static ItemStack getResultItem(Drawing drawing, int resultID)
    {


        ItemStack resultItem =  ItemStack.of(ItemTypes.EMERALD, drawing.getDrawingResults().get(resultID));

        ArrayList<Text> lore = new ArrayList<>();

        lore.add(Text.of("Lucky Number"));
        lore.add(Text.of(resultID));


        resultItem.offer(Keys.ITEM_LORE, lore);
        resultItem.offer(Keys.DISPLAY_NAME, Text.of("Matched number"));

        return resultItem;

    }



    public static int getColumn(int slot)
    {
        int column;
        int slotCount;

        slotCount = 1;
        column = 0;
        while(slotCount <= slot)
        {

            if(column == 9)
            {
                column = 0;
            }
            column++;
            slotCount++;
        }
        return column - 1;
    }
    public static int getRow(int slot)
    {

        int column;
        int row;
        int slotCount;

        slotCount = 1;

        column = 0;
        row = 0;
        while(slotCount <= slot)
        {

            if(column == 9)
            {
                column = 0;
                row++;
            }
            column++;
            slotCount++;
        }
        return row;
    }



    public static ArrayList<Player> getPlayersLiveViewing()
    {
        return playersLiveViewing;
    }

    public static void addPlayerLiveView(Player player)
    {
        playersLiveViewing.add(player);
    }

    public static void removePlayerLiveView(Player player)
    {
        playersLiveViewing.remove(player);
    }

}

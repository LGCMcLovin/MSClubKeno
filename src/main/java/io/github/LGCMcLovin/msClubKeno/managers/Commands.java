package io.github.LGCMcLovin.msClubKeno.managers;

import io.github.LGCMcLovin.msClubKeno.MSClubKeno;
import io.github.LGCMcLovin.msClubKeno.handlers.Drawing;
import io.github.LGCMcLovin.msClubKeno.handlers.Ticket;
import io.github.LGCMcLovin.msClubKeno.threads.DrawingThread;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static io.github.LGCMcLovin.msClubKeno.threads.DrawingThread.*;

@SuppressWarnings("IfStatementWithIdenticalBranches")
public class Commands
{


    MSClubKeno plugin;

    MSClubKeno instance = MSClubKeno.getInstance();
    CommentedConfigurationNode cfg = instance.getConfig().getConfig();
    Ticket newTicket;

    Integer drawAmount;
    Integer numAmount;
    ArrayList<Integer> drawIDs = new ArrayList<>();
    ArrayList<Integer> numbers = new ArrayList<>();
    Double bet;
    boolean multiplier;

    int ticketID;

    private final CommandCallable addMyTicketsCommand;
    private final CommandCallable addTicketResultsCommand;
    private final CommandCallable addLiveResultsCommand;
    private final CommandCallable addBuyCommand;


    public Commands(MSClubKeno plugin) throws NoSuchElementException
    {


        this.plugin = plugin;



        this.addMyTicketsCommand = CommandSpec.builder()
        .description(Text.of("View your currently active tickets."))
        .arguments(GenericArguments.none())
        .executor((CommandSource src, CommandContext args) ->
                {
                    if(!(src instanceof Player))
                    {
                        src.sendMessage(Text.of("You must be a player to use this command"));
                        return CommandResult.success();
                    }

                    Player player = (Player) src;

                    ArrayList<ItemStack> menuTickets = TicketManager.getPlayerMenuTickets(player);
                    Inventory inv = MenuManager.BuildPlayerTicketsMenu(player, menuTickets);

                    MenuManager.openPlayerTicketMenu(player, inv);

                    return CommandResult.success();
                    //return CommandResult's here
                }).build();

        this.addTicketResultsCommand= CommandSpec.builder()
                .description(Text.of("View your currently active tickets."))
                .arguments
                        (
                                GenericArguments.onlyOne(GenericArguments.integer(Text.of("ticketID"))),
                                GenericArguments.onlyOne(GenericArguments.integer(Text.of("drawingID")))
                        )
                .executor((CommandSource src, CommandContext args) ->
                {
                    if(!(src instanceof Player))
                    {
                        src.sendMessage(Text.of("You must be a player to use this command"));
                        return CommandResult.success();
                    }

                    Player player = (Player) src;
                    if(args.<Integer>getOne(Text.of("ticketID")).isPresent() && args.<Integer>getOne(Text.of("drawingID")).isPresent())
                    {
                        int tickID = args.<Integer>getOne(Text.of("ticketID")).get();


                        int drawID = args.<Integer>getOne(Text.of("drawingID")).get();
                        Ticket ticket = Ticket.getTicketByID(tickID);
                        Drawing drawing = Drawing.getDrawingByID(drawID);

                        if (!Drawing.getAllDrawings().contains(drawing) | !Ticket.getAllTickets().contains(ticket)) {
                            player.sendMessage(Text.of("Ticket or Drawing does not exist, please make sure drawing has begun."));
                            return CommandResult.success();
                        }

                        if (Drawing.getAllDrawings().contains(drawing) && Ticket.getAllTickets().contains(ticket)) {
                            Inventory inv = MenuManager.buildTicketResultsMenu(ticket, drawing);
                            MenuManager.openPlayerTicketMenu(player, inv);
                            return CommandResult.success();
                        }

                    }

                    return CommandResult.success();
                }).build();

        this.addLiveResultsCommand= CommandSpec.builder()
                .description(Text.of("View your currently active tickets."))
                .arguments(GenericArguments.none())
                .executor((CommandSource src, CommandContext args) ->
                {
                    if(!(src instanceof Player))
                    {
                        src.sendMessage(Text.of("You must be a player to use this command"));
                        return CommandResult.success();
                    }
                    Player player = (Player) src;
                    Inventory inv = getLiveResultInv();
                    player.openInventory(inv);
                    player.closeInventory();
                    player.openInventory(inv);
                    MenuManager.addPlayerLiveView(player);
                    return CommandResult.success();
                    //return CommandResult's here
                }).build();

        this.addBuyCommand= CommandSpec.builder()
                .description(Text.of(" purchase ticket    /keno buy drawAmt numAmt Bet Multiplier"))
                .arguments
                (
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("drawAmt"))),
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("numAmt"))),
                        GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("bet"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("multiplier")))
                )
                .executor((CommandSource src, CommandContext args) ->
                {



                    if(!(src instanceof Player))
                    {
                        src.sendMessage(Text.of("You must be a player to use this command"));

                        return CommandResult.success();
                    }
                    else
                    {
                        Player player = (Player) src;
                        ticketID = cfg.getNode("last-ticket").getInt();
                        ticketID++;

                        if(TicketManager.getPlayerTickets(player).toArray().length >= 54){
                            player.sendMessage(Text.of("Too many active tickets currently"));
                            return CommandResult.success();
                        }

                        if(args.getOne(Text.of("drawAmt")).isPresent() && args.getOne(Text.of("numAmt")).isPresent() && args.getOne(Text.of("bet")).isPresent() && args.getOne(Text.of("multiplier")).isPresent())
                        {

                                drawAmount = (Integer) args.getOne(Text.of("drawAmt")).get();


                                plugin.getLogger().info("inside Optional check");

                                if (drawAmount > 10)
                                {
                                    ChatManager.sendPlayerMSG(player, "Sorry you can only play a maximum of 10 Drawings/Ticket.");
                                    return CommandResult.success();
                                }


                                for (Drawing draw : Drawing.getAllDrawings())
                                {
                                    if (draw.getDrawingID() == cfg.getNode("current-draw").getInt()) {
                                        drawIDs = TicketManager.getDrawIDs(drawAmount);

                                        numAmount = (Integer) args.getOne(Text.of("numAmt")).get();
                                        bet = (Double) args.getOne(Text.of("bet")).get();
                                        multiplier = (Boolean) args.getOne(Text.of("multiplier")).get();
                                         if (numAmount <= 10)
                                            {

                                                numbers = TicketManager.getNewNumbers(numAmount);
                                                plugin.getLogger().info("new numbers registered");
                                            }
                                        plugin.getLogger().info("new ticket created");

                                        if (!multiplier)
                                        {
                                                if (EconManager.withdraw(player, bet * drawAmount).equals(ResultType.SUCCESS)) {
                                                    newTicket = new Ticket(ticketID, numAmount, drawIDs, numbers, bet, player, multiplier);
                                                    ChatManager.sendPlayerMSG(player,  "&6Ticket purchased!");
                                                    ChatManager.sendMSGnoPrefix(player,  "&bTicket ID: &f" + ticketID);
                                                    ChatManager.sendMSGnoPrefix(player,  "&bDrawings: &f" + newTicket.getDrawIDs().toString());
                                                    ChatManager.sendMSGnoPrefix(player,  "&bSpots: &f" + numbers.toString());
                                                    ChatManager.sendMSGnoPrefix(player, "&bBet played: &f" + bet);
                                                    ChatManager.sendMSGnoPrefix(player,  "&bMultiplier: &f" + multiplier);

                                                    cfg.getNode("last-ticket").setValue(ticketID);
                                                    instance.getConfig().save();
                                                    return CommandResult.success();

                                                }
                                                else {
                                                    player.sendMessage(Text.of("&4You do not have enough money."));
                                                    return CommandResult.success();
                                                }

                                        }
                                        else {
                                            if (EconManager.withdraw(player, (bet * drawAmount) + (bet * drawAmount)).equals(ResultType.SUCCESS)) {
                                                newTicket = new Ticket(ticketID, numAmount, drawIDs, numbers, bet, player, multiplier);
                                                ChatManager.sendPlayerMSG(player,  "&6Ticket purchased!");
                                                ChatManager.sendMSGnoPrefix(player,  "&bTicket ID: &f" + ticketID);
                                                ChatManager.sendMSGnoPrefix(player,  "&bDrawings: &f" + newTicket.getDrawIDs().toString());
                                                ChatManager.sendMSGnoPrefix(player,  "&bSpots: &f" + numbers.toString());
                                                ChatManager.sendMSGnoPrefix(player, "&bBet played: &f" + bet);
                                                ChatManager.sendMSGnoPrefix(player,  "&bMultiplier: &f" + multiplier);

                                                cfg.getNode("last-ticket").setValue(ticketID);
                                                instance.getConfig().reloadConfig();
                                                return CommandResult.success();
                                            } else {
                                                player.sendMessage(Text.of("&4You do not have enough money"));
                                                return CommandResult.success();
                                            }
                                        }
                                    }
                                }
                        }

                        return CommandResult.success();
                    }

                    //return CommandResult's here
                }).build();
            //keno 0buy 1drawAmt 2numAmt 3Bet 4boolean
    }


    public void init()
    {
        CommandManager commandMgr = Sponge.getCommandManager();
        commandMgr.register(this.plugin, this.get(), "keno");
    }

    public CommandCallable get()
    {
        return CommandSpec.builder()
                .child(this.addMyTicketsCommand, "MyTickets")
                .child(this.addTicketResultsCommand, "TicketResults")
                .child(this.addLiveResultsCommand, "LiveResults")
                .child(this.addBuyCommand, "buy")
                .build();
    }


}
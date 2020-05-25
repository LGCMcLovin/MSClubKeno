package io.github.LGCMcLovin.msClubKeno.handlers;

import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;

public class Ticket
{
    private final int ID;
    private final int numQuantity;
    private final ArrayList<Integer> drawIDs;
    private final ArrayList<Integer> numbers;
    private static final ArrayList<Ticket> allTickets = new ArrayList<>();
    private final Double bet;
    private final Player player;
    private final boolean multiplier;

    public Ticket(int ID, int numQuantity, ArrayList<Integer> drawIDs, ArrayList<Integer> numbers, Double bet, Player player, boolean multiplier)
    {
        this.ID = ID;
        this.numQuantity = numQuantity;
        this.drawIDs = drawIDs;
        this.numbers = numbers;
        this.multiplier = multiplier;
        this.bet = bet;
        this.player = player;
        allTickets.add(this);
    }

    public int getTicketID()
    {
        return ID;
    }

    public int getNumQuantity()
    {
        return numQuantity;
    }

    public ArrayList<Integer> getDrawIDs()
    {
        return drawIDs;
    }

    public ArrayList<Integer> getPlayedNumbers()
    {
        return numbers;
    }

    public static ArrayList<Ticket> getAllTickets()
    {
        return allTickets;
    }

    public Double getBetAmt(){return bet; }

    public Player getTicketOwner() {
        return player;
    }

    public boolean getMultiplierActive()
    {
        return multiplier;
    }

    public static Ticket getTicketByID(Integer ticketID)
    {
        for(Ticket ticket : getAllTickets())
        {
            if(ticket.getTicketID() == ticketID)
            {
                return ticket;
            }
        }
        return null;
    }

    public static ArrayList<Ticket> getTicketsByPlayer(Player player)
    {
        ArrayList<Ticket> playerTickets = new ArrayList<>();
        for(Ticket ticket : getAllTickets())
        {
            if(ticket.getTicketOwner() == player)
            {

                playerTickets.add(ticket);
            }
            return playerTickets;
        }
        return null;
    }
}

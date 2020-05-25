package io.github.LGCMcLovin.msClubKeno.managers;

import io.github.LGCMcLovin.msClubKeno.handlers.Drawing;
import io.github.LGCMcLovin.msClubKeno.handlers.Ticket;

import java.util.ArrayList;

public class ResultManager
{
    static double payout;

    public static ArrayList<Integer> getMatchedNumbers(Ticket ticket, Drawing drawing)
    {
        ArrayList<Integer> matchedNumbers = new ArrayList<>();
        ArrayList<Integer> playedNumbers = ticket.getPlayedNumbers();
        for(Integer matched :playedNumbers)
        {
            if(drawing.getDrawingResults().contains(matched))
            {
                matchedNumbers.add(matched);
            }
        }
        return matchedNumbers;
    }

    public static double calculatePayout(Ticket ticket, Drawing drawing)
    {
        int matchTotal = getMatchedNumbers(ticket, drawing).toArray().length;
        int numQuantity = ticket.getNumQuantity();

        if(matchTotal == 0)
        {
            if(numQuantity == 10)
            {
                return ticket.getBetAmt().intValue() * 5;
            }
            return 0.0;
        }

        switch(numQuantity)
        {
            case 1:
                if(matchTotal == 1)
                {
                    payout = ticket.getBetAmt().intValue() * 2;
                }
                break;
            case 2:
                if(matchTotal == 2)
                {
                    payout = ticket.getBetAmt().intValue() * 11;
                }
                else if (matchTotal == 1)
                {
                    payout = 0;
                }
                break;
            case 3:
                if(matchTotal == 3)
                {
                    payout = ticket.getBetAmt().intValue() * 27;
                }
                else if(matchTotal == 2)
                {
                    payout = ticket.getBetAmt().intValue() * 2;
                }
                else if(matchTotal == 1)
                {
                    payout = 0.0;
                }
                break;
            case  4:
                if(matchTotal == 4)
                {
                    payout = ticket.getBetAmt().intValue() * 72;
                }
                else if(matchTotal == 3)
                {
                    payout = ticket.getBetAmt().intValue() * 5;
                }
                else if(matchTotal <= 2)
                {
                    payout = 0.0;
                }
                break;
            case 5:
                if(matchTotal == 5)
                {
                    payout = ticket.getBetAmt().intValue() * 410;
                }
                else if(matchTotal == 4)
                {
                    payout = ticket.getBetAmt().intValue() * 18;
                }
                else if(matchTotal == 3)
                {
                    payout = ticket.getBetAmt().intValue() * 2;
                }
                else if(matchTotal <= 2)
                {
                    payout = 0.0;
                }
                break;
            case 6:
                if(matchTotal == 6)
                {
                    payout = ticket.getBetAmt().intValue() * 1100;
                }
                else if(matchTotal == 5)
                {
                    payout = ticket.getBetAmt().intValue() * 57;
                }
                else if(matchTotal == 4)
                {
                    payout = ticket.getBetAmt().intValue() * 7;
                }
                else if(matchTotal == 3)
                {
                    payout = ticket.getBetAmt().intValue();
                }
                else if(matchTotal <= 2)
                {
                    payout = 0.0;
                }
                break;
            case 7:
                if(matchTotal == 7)
                {
                    payout = ticket.getBetAmt().intValue() * 2000;
                }
                else if(matchTotal == 6)
                {
                    payout = ticket.getBetAmt().intValue() * 100;
                }
                else if(matchTotal == 5)
                {
                    payout = ticket.getBetAmt().intValue() * 11;
                }
                else if(matchTotal == 4)
                {
                    payout = ticket.getBetAmt().intValue() * 5;
                }
                else if(matchTotal == 3)
                {
                    payout = ticket.getBetAmt().intValue();
                }
                else if(matchTotal <= 2 )
                {
                    payout = 0.0;
                }
                break;
            case 8:
                if(matchTotal == 8)
                {
                    payout = ticket.getBetAmt().intValue() * 7000;
                }
                else if(matchTotal == 7)
                {
                    payout = ticket.getBetAmt().intValue() * 300;
                }
                else if(matchTotal == 6)
                {
                    payout = ticket.getBetAmt().intValue() * 50;
                }
                else if(matchTotal == 5)
                {
                    payout = ticket.getBetAmt().intValue() * 15;
                }
                else if(matchTotal == 4)
                {
                    payout = ticket.getBetAmt().intValue() * 2;
                }
                else if(matchTotal <= 3)
                {
                    payout = 0.0;
                }
                break;
            case 9:
                if(matchTotal == 9)
                {
                    payout = ticket.getBetAmt().intValue() * 7500;
                }
                else if(matchTotal == 8)
                {
                    payout = ticket.getBetAmt().intValue() * 2000;
                }
                else if(matchTotal == 7)
                {
                    payout = ticket.getBetAmt().intValue() * 100;
                }
                else if(matchTotal == 6)
                {
                    payout = ticket.getBetAmt().intValue() * 20;
                }
                else if(matchTotal == 5)
                {
                    payout = ticket.getBetAmt().intValue() * 5;
                }
                else if(matchTotal == 4)
                {
                    payout = ticket.getBetAmt().intValue() * 2;
                }
                else if(matchTotal <= 3)
                {
                    payout = 0.0;
                }
                break;
            case 10:
                if(matchTotal == 10)
                {
                    payout = ticket.getBetAmt() * 10000;
                }
                else if(matchTotal == 9)
                {
                    payout = ticket.getBetAmt() * 5000;
                }
                else if(matchTotal == 8)
                {
                    payout = ticket.getBetAmt() * 500;
                }
                else if(matchTotal == 7)
                {
                    payout = ticket.getBetAmt() * 50;
                }
                else if(matchTotal == 6)
                {
                    payout = ticket.getBetAmt() * 5;
                }
                else if(matchTotal == 5)
                {
                    payout = ticket.getBetAmt() * 2;
                }
                else if(matchTotal <= 4)
                {
                    payout = 0.0;
                }
                break;
        }
        return payout;
    }
}

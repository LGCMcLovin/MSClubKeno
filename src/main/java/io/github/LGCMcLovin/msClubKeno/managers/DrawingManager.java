package io.github.LGCMcLovin.msClubKeno.managers;

import io.github.LGCMcLovin.msClubKeno.MSClubKeno;

import java.util.ArrayList;
import java.util.Random;

public class DrawingManager
{
    final MSClubKeno instance;

    static ArrayList<Integer> results;

    public DrawingManager(MSClubKeno instance){this.instance = instance;}

    public static ArrayList<Integer> getNewResults()
    {
        int i = 1;
        results = new ArrayList<>();
        while(i <= 15)
        {
            int result = new Random().nextInt(54);
            if(results.contains(result) || result == 0)
            {
                continue;
            }
            if(!results.contains(result) && result != 0)
            {
                results.add(result);
                i++;
            }
        }
        return results;
    }

}

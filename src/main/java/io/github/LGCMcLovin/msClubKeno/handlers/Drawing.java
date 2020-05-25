package io.github.LGCMcLovin.msClubKeno.handlers;

import java.util.ArrayList;

public class Drawing
{

   int ID;
   ArrayList<Integer> results;
   static ArrayList<Drawing> allDrawings = new ArrayList<>();



    public Drawing(int ID, ArrayList<Integer> results)
    {
        this.ID = ID;
        this.results = results;
        allDrawings.add(this);
    }



    public int getDrawingID(){return ID;}

    public  ArrayList<Integer> getDrawingResults(){return results;}

    public static ArrayList<Drawing> getAllDrawings(){return allDrawings;}

    public static Drawing getDrawingByID(Integer drawID)
    {
        for(Drawing drawing : getAllDrawings())
        {
            if(drawing.getDrawingID() == drawID)
            {
                return drawing;
            }
        }
        return null;
    }

    public boolean drawExists(int drawID)
    {
        Drawing drawing = getDrawingByID(drawID);
        return drawing != null;
    }


}

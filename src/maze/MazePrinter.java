/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;

import java.awt.print.*;
import java.awt.*;

/**
 *
 * @author Russ
 */
public class MazePrinter implements Printable{
    private MazeGenerator mazeGen;
    
    MazePrinter(MazeGenerator mazeGen) {
        this.mazeGen = mazeGen;
    }
    
    static public void PrintMaze(MazeGenerator mazeGen) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new MazePrinter(mazeGen));
        
        boolean doPrint = job.printDialog();
        
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException e) {
                // Do something about the failure
            }
        }
        
    }
    
    @Override
    public int print(Graphics g, PageFormat pf, int page) 
            throws PrinterException {
        if (page > 0)
            return NO_SUCH_PAGE;
        
        Graphics2D g2d = (Graphics2D)(g);
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        
        double width = pf.getImageableWidth();
        double height = pf.getImageableHeight();
        
        PaintPanel.DrawMaze((int)width, (int)height, g2d);
        
        return PAGE_EXISTS;
    }
    
}

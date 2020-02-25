/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;
import javax.swing.*;

import java.awt.*;
import java.awt.image.*;


/**
 *
 * @author Russ
 */
public class PaintPanel extends JPanel {
    
    private BufferedImage buffer = null;
    private static final Color colorWall = Color.black;
    private static final Color colorCorridor = Color.white;
    private static final Color colorDeadEndMin = Color.red;
    private static final Color colorDeadEndMax = Color.orange;
    private static final Color colorTestingMin = Color.green;
    private static final Color colorTestingMax = Color.cyan;
    private static final Color colorForward = Color.blue;
    private static final Color colorBackward = Color.yellow;
    private static final Color colorLoop = Color.magenta;
    
    /**
     * Creates new form PaintPanel
     */
    public PaintPanel() {
        initComponents();
    }
     
    private static float interpolate(float fMin, float fMax, int num, int den) {
        assert (num >= 0);
        assert (den > 0);
        assert (num <= den);
        
        float fVal = fMin + (fMax-fMin)*num/den;
        
        if (fMin < fMax) {
            if (fVal < fMin)
                fVal = fMin;
            if (fVal > fMax)
                fVal = fMax;
        }
        else {
            if (fVal < fMax)
                fVal = fMax;
            if (fVal > fMin)
                fVal = fMin;
        }
        
        return fVal;
    }
    
    public static Color colorInterpolated(Color colorMin, Color colorMax, int num, int den) {
        float[] minComponents = new float[3];
        colorMin.getColorComponents(minComponents);
        float[] maxComponents = new float[3];
        colorMax.getColorComponents(maxComponents);
        
        num = num % den;
        
        float red = interpolate(minComponents[0], maxComponents[0], num, den);
        float green = interpolate(minComponents[1], maxComponents[1], num, den);
        float blue = interpolate(minComponents[2], maxComponents[2], num, den);
        
        assert(0.0 <= red && red <= 1.0);
        assert(0.0 <= green && green <= 1.0);
        assert(0.0 <= blue && blue <= 1.0);
        
        Color colorInt = new Color(red, green, blue);
        
        return colorInt;
    }
    
    public static void DrawMaze(int width, int height,
            Graphics2D graph) {
        int marginWidth = 10;
        int marginHeight = 10;
        MazeGenerator maze = Maze.getMazeGenerator();
        int mazeColumns = maze.endCol()- maze.beginCol();
        int mazeRows = maze.endRow() - maze.beginRow();

        int blockWidth = (width - marginWidth)/mazeColumns;
        int blockHeight = (height - marginHeight)/mazeRows;

        // Let's get square blocks
        if (blockWidth < blockHeight)
            blockHeight = blockWidth;
        else
            blockWidth = blockHeight;

        int mazeWidth = blockWidth*mazeColumns;
        int mazeHeight = blockHeight*mazeRows;

        int marginLeft = (width-mazeWidth)/2; 
        int marginTop = (height-mazeHeight)/2;
        
        final int colorChangeRate = 10;

        if (graph != null) {
            graph.setColor(Color.gray);
            graph.fillRect(0, 0, width, height);

            int countOfCells = maze.getSize();

            for (int row=maze.beginRow(); row<maze.endRow(); row++) {
                for (int col = maze.beginCol(); col<maze.endCol(); col++) {
                    int serial = maze.getSerial(row, col);

                    Color colorBlock = colorCorridor;
                    switch (maze.getCell(row, col)) {
                        case wall:
                            colorBlock = colorWall;
                            break;

                        case testing:
                            colorBlock = colorInterpolated(colorTestingMin,
                                    colorTestingMax,
                                    serial*colorChangeRate,
                                    countOfCells);
                            break;

                        case deadend:
                            colorBlock = colorInterpolated(colorDeadEndMin,
                                    colorDeadEndMax,
                                    serial*colorChangeRate,
                                    countOfCells);
                            break;

                        case forward:
                            colorBlock = colorForward;
                            break;

                        case backward:
                            colorBlock = colorCorridor;
                            break;

                        case loop:
                        case queued:
                            colorBlock = colorLoop;
                            break;
                    }
                    graph.setColor(colorBlock);
                    int leftBlock = marginLeft + col*blockWidth;
                    int topBlock = marginTop + row*blockHeight;
                    graph.fillRect(leftBlock, topBlock, blockWidth, blockHeight);
                }
            }
        }
    }
    
    
    @Override
    protected void paintComponent(Graphics g) {
        if (g != null) {
            Graphics2D g2 = (Graphics2D)g;

            if (buffer == null) {
                int width = this.getWidth();
                int height = this.getHeight();
                buffer = (BufferedImage)this.createImage(width, height);
                Graphics2D gb = buffer.createGraphics();
                DrawMaze(width, height, gb);

            }
            g2.drawImage(buffer, null, 0, 0);
            buffer = null;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

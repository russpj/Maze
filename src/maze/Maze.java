/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author Russ
 */
public class Maze implements ActionListener {
    private static MazeGenerator mazeGen;
    private MainFrame mainFrame;
    
    Maze(MainFrame frame) {
        mainFrame = frame;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        switch (source.getMnemonic()) {
            default:
                break;
                
            case KeyEvent.VK_Z:
                // set size of maze
                mainFrame.setMazeSize(mazeGen);
                break;
                
            case KeyEvent.VK_M:
                // draw new maze with animation
                mainFrame.generateMaze(mazeGen);
                break;
                
            case KeyEvent.VK_A:
                // Solve the maze with animation
                mainFrame.solveMazeAnimate(mazeGen);
                break;
                
            case KeyEvent.VK_S:
                // Solve the maze
                mainFrame.solveMazeAll(mazeGen);
                break;
                
            case KeyEvent.VK_C:
                // Clear the solution in the maze
                mainFrame.solveMazeClear(mazeGen);
                break;
                
            case KeyEvent.VK_P:
                MazePrinter.PrintMaze(mazeGen);
                break;
                
            case KeyEvent.VK_X:
                System.exit(0);
                break;
        }
    }
    
       
    private static void setupGUI() {
        MainFrame frame = new MainFrame();
        frame.setTitle("Maze");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Maze.getMazeGenerator().generateDFMaze(12, 16, 0);
        
        Maze maze = new Maze(frame);
        
        // Menu
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setBackground(new Color(154, 165, 127));
        menuBar.setPreferredSize(new Dimension(200, 20));
        frame.setJMenuBar(menuBar);
        
        JMenu menu = new JMenu("Maze");
        menu.setMnemonic(KeyEvent.VK_M);
        menu.getAccessibleContext().setAccessibleDescription("Menu control items");
        menuBar.add(menu);
        
        JMenuItem menuItem = new JMenuItem("Print");
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.getAccessibleContext().setAccessibleDescription("Print the Maze");
        menuItem.addActionListener(maze);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuItem.getAccessibleContext().setAccessibleDescription("Exit the Maze Program");
        menuItem.addActionListener(maze);
        menu.add(menuItem);
        
        menu = new JMenu("Generate");
        menu.setMnemonic(KeyEvent.VK_G);
        menu.getAccessibleContext().setAccessibleDescription("Generate a maze");
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Set Size ...");
        menuItem.setMnemonic(KeyEvent.VK_Z);
        menuItem.getAccessibleContext().setAccessibleDescription("Set the size of the maze");
        menuItem.addActionListener(maze);
        menu.add(menuItem);
           
        menuItem = new JMenuItem("Same Size");
        menuItem.setMnemonic(KeyEvent.VK_M);
        menuItem.getAccessibleContext().setAccessibleDescription("Draw a new maze with the same size");
        menuItem.addActionListener(maze);
        menu.add(menuItem);
           
        menu = new JMenu("Solve");
        menu.setMnemonic(KeyEvent.VK_S);
        menu.getAccessibleContext().setAccessibleDescription("Solve the maze");
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Animate Solution");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.getAccessibleContext().setAccessibleDescription("Solve the Maze with animation");
        menuItem.addActionListener(maze);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Solve");
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.getAccessibleContext().setAccessibleDescription("Solve the Maze");
        menuItem.addActionListener(maze);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Clear Solution");
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.getAccessibleContext().setAccessibleDescription("Clear the solution in the Maze");
        menuItem.addActionListener(maze);
        menu.add(menuItem);
        
        // Work with the canvas
        PaintPanel canvas = new PaintPanel();
        
       
        frame.setContentPane(canvas);
        
         // Display the window
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     *  getMaze
     * 
     * @return the static Maze that we are playing with
     */
    public static MazeGenerator getMazeGenerator() {
        if (mazeGen == null)
            mazeGen = new MazeGenerator();
        
        return mazeGen;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            setupGUI();
        });
        
    }
    
}

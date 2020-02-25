/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author Russ
 */
public class MainFrame extends javax.swing.JFrame implements ActionListener {
    private Timer solveTimer = null;
    private static String dfMazeType = "Depth First";
    private static String bfMazeType = "Breadth First";
    private String lastMazeGenerator = dfMazeType;
    private static final String solveRHWActionString = "Solve RHW Timer"; // Right Hand Wall Solver
    private Timer generateTimer = null;
    private static final String generateDFMActionString = "Generate DFM Timer"; // Depth-first Maze
    private static final String generateBFMActionString = "Generate BFM Timer"; // Breadth-first Maze
    private MazeGenerator mazeGenTimer;
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        solveTimer = new Timer(100, this);
        solveTimer.setActionCommand(solveRHWActionString);
        generateTimer = new Timer(100, this);
        generateTimer.setActionCommand(generateDFMActionString);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) { 
        String command = e.getActionCommand();
        if (command.equals(solveRHWActionString)) {
            if (mazeGenTimer == null || !mazeGenTimer.solveMazeNextStep()) {
                // the maze is all solved. Clean up time
                solveTimer.stop();
                mazeGenTimer = null;
            }
        }
        if (command.equals(generateDFMActionString)) {
            if (mazeGenTimer == null || !mazeGenTimer.generateDFMazeNextStep()) {
                // The maze is all generated. Clean up time
                generateTimer.stop();
                mazeGenTimer.generateDFMazeEnd();
                mazeGenTimer = null;
            }
        }
        if (command.equals(generateBFMActionString)) {
            if (mazeGenTimer == null || !mazeGenTimer.generateBFMazeNextStep()) {
                // The maze is all generated. Clean up time
                generateTimer.stop();
                mazeGenTimer.generateBFMazeEnd();
                mazeGenTimer = null;
            }
        }
        repaint();
    }   
    
    public void setMazeSize(MazeGenerator mazeGen) {
        solveTimer.stop();
        generateTimer.stop();
        
        boolean validInput = false;
        
        do {
            try {
                String[] mazeTypes = {dfMazeType, bfMazeType};
                Object mazeTypeReturn = JOptionPane.showInputDialog(this, "What maze generator?", "input",
                        JOptionPane.INFORMATION_MESSAGE, null, mazeTypes, mazeTypes[0]);
                String mazeType = mazeTypeReturn.toString();
                String rowsInput = JOptionPane.showInputDialog(this, "How many rows?", "input",
                        JOptionPane.INFORMATION_MESSAGE);
                String columnsInput = JOptionPane.showInputDialog(this, "How many columns?", "input",
                        JOptionPane.INFORMATION_MESSAGE);
                String holesInput = JOptionPane.showInputDialog(this, "How many holes? ", "input",
                        JOptionPane.INFORMATION_MESSAGE);
                int userOption = JOptionPane.showConfirmDialog(this,
                        "Animate the maze generation?", 
                        "Set Size",
                        JOptionPane.YES_NO_OPTION);

                int rows = Integer.parseInt(rowsInput);
                int columns = Integer.parseInt(columnsInput);
                int holes = Integer.parseInt(holesInput);
                lastMazeGenerator = mazeType;

                boolean animate = (userOption == JOptionPane.YES_OPTION);

                validInput = true;

                if (animate) {
                    String action = generateDFMActionString;
                    if (mazeType.equals(dfMazeType)) {
                        mazeGen.generateDFMazeInit(rows, columns, holes);
                        // action = generateDFMActionString;
                    } else if (mazeType.equals(bfMazeType)) {
                        mazeGen.generateBFMazeInit(rows, columns, holes);
                        action = generateBFMActionString;
                    }
                    mazeGenTimer = mazeGen;
                    int timerFreq = 4000/mazeGen.endCol();
                    generateTimer = new Timer(timerFreq, this);
                    generateTimer.setActionCommand(action);
                    generateTimer.start();
                    repaint();
                } else {
                    if(mazeType.equals(dfMazeType))
                        mazeGen.generateDFMaze(rows, columns, holes);
                    else if (mazeType.equals(bfMazeType))
                        mazeGen.generateBFMaze(rows, columns, holes);
                    this.repaint();
                }
            }
            catch (java.lang.NumberFormatException e) {
                // try again
            }
        }
        while (!validInput);
    }
    
    public void generateMaze(MazeGenerator mazeGen) {
        solveTimer.stop();
        generateTimer.stop();

        if (lastMazeGenerator.equals(dfMazeType))
            mazeGen.generateDFMaze();
        if (lastMazeGenerator.equals(bfMazeType))
            mazeGen.generateBFMaze();
        repaint();
    }
    
    public void solveMazeAll(MazeGenerator mazeGen) {
        solveTimer.stop();
        generateTimer.stop();
        
        mazeGen.solveMazeAll();
        repaint();
    }
    
    
    public void solveMazeAnimate(MazeGenerator mazeGen) {
        solveTimer.stop();
        generateTimer.stop();
        
        mazeGen.solveMazeInit();
    
        mazeGenTimer = mazeGen;
        int timerFreq = 4000/mazeGen.endCol();
        solveTimer = new Timer(timerFreq, this);
        solveTimer.setActionCommand(solveRHWActionString);
        solveTimer.start();
    }

    public void solveMazeClear(MazeGenerator mazeGen) {
        solveTimer.stop();
        generateTimer.stop();
        
        mazeGen.solveMazeInit();
        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

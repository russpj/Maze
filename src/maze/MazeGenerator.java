/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;
import java.util.Random;
import java.util.PriorityQueue;

/**
 *
 * @author Russ
 */
public final class MazeGenerator {
    private int rowMax, colMax;
    private int rowViewMax, colViewMax;
    private int rowEntry, colEntry;
    private int rowExit, colExit;
    private int maxHoles;
    
    public enum Cell {
        notfound,
        wall,
        corridor,
        testing,
        deadend,
        forward,
        backward,
        loop,
        queued
    }
    
    public class MazeCell {
        public Cell cell;
        public int serial;  
    }
    
    private MazeCell[][] view;
    
    private static Random rand;
    
    private static final Direction[] rightHandDirs = {
        Direction.right, 
        Direction.up, 
        Direction.left, 
        Direction.down};
    
    // State for the iterative solver
    private int rowViewSolve = 0;
    private int colViewSolve = 0;
    private int rowViewExit = 0;
    private int colViewExit = 0;
    private int idirSolve = 0;
    
    // State for the iterative generator
    private int rowViewGenerate = 0;
    private int colViewGenerate = 0;
    private int holesGenerate = 0;
    private boolean backtrackingDone;
    
    // Queue for breadth-first generator
    private class CellInfo implements Comparable {
        public int row;
        public int col;
        private final int priority;
        Random rand;
        private static final int priorityMax = 1000000000;
        private static final int priorityMin = 3;
        
        public CellInfo(int rowInit, int colInit, Random randInit) {
            row = rowInit;
            col = colInit;
            
            if (rand == null) {
                if (randInit == null)
                    rand = new Random();
                else
                    rand = randInit;
            }
            priority = rand.nextInt(priorityMax);
        }
        
        public CellInfo(int rowInit, int colInit, CellInfo lastCell) {
            row = rowInit;
            col = colInit;
            rand = lastCell.rand;
            
            int priorityMinTest = lastCell.priority/2;
            int priorityMaxTest = lastCell.priority+1;
            
            int priorityTest = rand.nextInt((priorityMaxTest-priorityMinTest))+priorityMinTest;
            if (priorityTest <= priorityMin)
                priorityTest = rand.nextInt(this.priorityMax);
            priority = priorityTest;
        }
        
        @Override
        public int compareTo(Object info) {
            return this.priority - ((CellInfo)info).priority;
        }
    }
    
    private PriorityQueue<CellInfo> cellQueue;
    
    // Color management
    private int serialMax = 0;
    
    MazeGenerator() {
    }
    
    
    public void generateDFMaze() {
        generateDFMaze(rowMax, colMax, maxHoles);
    }
    
    public void generateDFMaze(int row, int col, int holes) {
        generateDFMazeInit(row, col, holes);
        while (generateDFMazeNextStep())
            ;
        generateDFMazeEnd();
    }
    
    public void generateDFMazeInit(int row, int col, int holes) {
        setSize(row, col, holes);
        initializeMaze();
        
        rowViewGenerate = calcRowView(row/2);
        colViewGenerate = calcColView(col/2);
        holesGenerate = holes;
        backtrackingDone = false;
    }
    
    public boolean generateDFMazeNextStep() {
        if (!backtrackingDone) {
            Direction localDirs[] = rightHandDirs.clone();

            Cell cell = getCell(rowViewGenerate, colViewGenerate);
            assert(cell == Cell.forward || cell == Cell.notfound || cell == Cell.backward);

            shuffleDirections(localDirs);

            // Look for another not-found cell to add
            for (Direction dir : localDirs) {
                int rowViewNextWall = walkRowView(rowViewGenerate, dir);
                int colViewNextWall = walkColView(colViewGenerate, dir);
                int rowViewNextCorridor = walkRowView(rowViewNextWall, dir);
                int colViewNextCorridor = walkColView(colViewNextWall, dir);
                if (isLegalView(rowViewNextCorridor, colViewNextCorridor) &&
                        getCell(rowViewNextCorridor, colViewNextCorridor) == Cell.notfound) {
                    setCell(rowViewGenerate, colViewGenerate, Cell.forward);
                    setCell(rowViewNextWall, colViewNextWall, Cell.forward);
                    rowViewGenerate = rowViewNextCorridor;
                    colViewGenerate = colViewNextCorridor;
                    return true;
                }
            }

            // Look for a testing cell to backtrack
            for (Direction dir : localDirs) {
                int rowViewNextWall = walkRowView(rowViewGenerate, dir);
                int colViewNextWall = walkColView(colViewGenerate, dir);
                if (isLegalView(rowViewNextWall, colViewNextWall) &&
                        getCell(rowViewNextWall, colViewNextWall) == Cell.forward) {
                    int rowViewNextCorridor = walkRowView(rowViewNextWall, dir);
                    int colViewNextCorridor = walkColView(colViewNextWall, dir);
                    if (isLegalView(rowViewNextCorridor, colViewNextCorridor) &&
                            getCell(rowViewNextCorridor, colViewNextCorridor) == Cell.forward) {
                        setCell(rowViewGenerate, colViewGenerate, Cell.backward);
                        setCell(rowViewNextWall, colViewNextWall, Cell.backward);
                        rowViewGenerate = rowViewNextCorridor;
                        colViewGenerate = colViewNextCorridor;
                        return true;
                    }
                }
            }
            setCell(rowViewGenerate, colViewGenerate, Cell.backward);
            backtrackingDone = true;
            return true;
        }
        
        if (holesGenerate > 0) {
            makeHole();
            return true;
        }
        return false;
    }
    
    public void generateDFMazeEnd() {
        makeHoles();
        makeEntranceAndExit();
        conformCorridors();
  }
    
    public void generateBFMaze() {
        generateBFMaze(rowMax, colMax, maxHoles);
    }
    
    public void generateBFMaze(int row, int col, int holes) {
        generateBFMazeInit(row, col, holes);
        while (generateBFMazeNextStep())
            ;
        generateBFMazeEnd();
    }
    
    public void generateBFMazeInit(int row, int col, int holes) { 
        setSize(row, col, holes);
        initializeMaze();
        holesGenerate = holes;
        
        int rowView = calcRowView(row/2);
        int colView = calcColView(col/2);
        
        cellQueue = new PriorityQueue<>();
        CellInfo cellInfo = new CellInfo (rowView, colView, rand);
        cellQueue.add(cellInfo);
        setCell(rowView, colView, Cell.queued);
    }
    
    public boolean generateBFMazeNextStep() { //NYI
        if (!cellQueue.isEmpty()) {
            CellInfo cellInfo = cellQueue.remove();
            int rowView = cellInfo.row;
            int colView = cellInfo.col;
            assert(getCell(rowView, colView) == Cell.queued);

            setCell(rowView, colView, Cell.corridor);

            Direction localDirs[] = rightHandDirs.clone();
            shuffleDirections(localDirs);

            // Look for another not-found cell to add
            for (Direction dir : localDirs) {
                int rowViewNextWall = walkRowView(rowView, dir);
                int colViewNextWall = walkColView(colView, dir);
                int rowViewNextCorridor = walkRowView(rowViewNextWall, dir);
                int colViewNextCorridor = walkColView(colViewNextWall, dir);
                if (isLegalView(rowViewNextCorridor, colViewNextCorridor) &&
                        getCell(rowViewNextCorridor, colViewNextCorridor) == Cell.notfound) {
                    setCell(rowViewNextWall, colViewNextWall, Cell.corridor);
                    setCell(rowViewNextCorridor, colViewNextCorridor, Cell.queued);
                    cellQueue.add(new CellInfo(rowViewNextCorridor, colViewNextCorridor, cellInfo));
                }
            }
            return true;
        }

        if (holesGenerate > 0) {
            makeHole();
            return true;
        }
            
        return false;
    }

    public void generateBFMazeEnd() { 
        makeHoles();
        makeEntranceAndExit();
        conformCorridors();
    }
    
    public void solveMazeAll() {
        solveMazeInit();
        while (solveMazeNextStep())
            ;
    }
    
    public void solveMazeInit() {
        // Clear the corridors
        for (int rowView = 0; rowView < rowViewMax; rowView++) {
            for (int colView = 0; colView < colViewMax; colView++) {
                if (getCell(rowView, colView) != Cell.wall) {
                    setCell(rowView, colView, Cell.corridor);
                }
            }
        }
        
        rowViewSolve = calcRowViewAndWalk(rowEntry, Direction.up);
        colViewSolve = calcColViewAndWalk(colEntry, Direction.up);
        rowViewExit = calcRowViewAndWalk(rowExit, Direction.down);
        colViewExit = calcColViewAndWalk(colExit, Direction.down);
        idirSolve = 3; 
        assert(rightHandDirs[3] == Direction.down);
        serialMax = 0;
    }
    
    private void conformCorridors() {
        // Cleanup generation litter in the corridors
        for (int rowView = 0; rowView < rowViewMax; rowView++) {
            for (int colView = 0; colView < colViewMax; colView++) {
                if (getCell(rowView, colView) != Cell.wall)
                    setCell(rowView, colView, Cell.corridor);
            }
        }
    }
    
    private void makeEntranceAndExit() {
    // Make an entrance and exit
        int rowView = calcRowViewAndWalk(rowEntry, Direction.up);
        int colView = calcColViewAndWalk(colEntry, Direction.up);
        setCell(rowView, colView, Cell.corridor);
        
        rowView = calcRowViewAndWalk(rowExit, Direction.down);
        colView = calcColViewAndWalk(colExit, Direction.down);
        setCell(rowView, colView, Cell.corridor);
    }
    
    private void makeHole() {
        if (holesGenerate > 0) {
            if (rand == null)
                rand = new Random();
            int rowView = rand.nextInt(rowViewMax-2) + 1;
            int colView = rand.nextInt(colViewMax-2) + 1;
            if ((rowView % 2 != 0 || colView %2 != 0) && 
                    getCell(rowView, colView) == Cell.wall) {
                setCell(rowView, colView, Cell.forward);
                holesGenerate--;
            }
        }
    }

    private void makeHoles() {
        while (holesGenerate > 0) {
            makeHole();
        }
    }
    
    public boolean solveMazeNextStep() {
        if (rowViewSolve == rowViewExit && colViewSolve == colViewExit) {
            setCell(rowViewSolve, colViewSolve, Cell.testing);
            
            // NYI: turn all of the testing cells to a solution value
            return false;
        }
        
        int idirTest = (idirSolve+3)%4;
        
        for (;;) {
            Direction dirTest = rightHandDirs[idirTest];
            int rowTest = walkRowView(rowViewSolve, dirTest);
            int colTest = walkColView(colViewSolve, dirTest);
            Cell cellNext = getCell(rowTest, colTest); 
            if (cellNext != Cell.wall) {
                Cell cellNew = (cellNext == Cell.corridor ? Cell.testing : Cell.deadend);
                setCell(rowViewSolve, colViewSolve, cellNew);
                rowViewSolve = rowTest;
                colViewSolve = colTest;
                idirSolve = idirTest;
                break;
            }
            idirTest = (idirTest+1)%4;
        }
        return true;
    }
    
    private int calcRowViewAndWalk(int row, Direction dir) {
        int rowView = calcRowView(row);
        return walkRowView(rowView, dir);
    }
    
    private int calcColViewAndWalk(int col, Direction dir) {
        int colView = calcColView(col);
        return walkColView(colView, dir);
    }
    
    enum Direction {up, right, down, left};
    
    private void traverseMazeIter(int rowView, int colView) {
    }
    
    private void traverseMaze(int rowView, int colView) {
        assert(getCell(rowView, colView) == Cell.notfound);
        setCell(rowView, colView, Cell.corridor);
        
        Direction localDirs[] = rightHandDirs.clone();
        shuffleDirections(localDirs);
        
        for (int i = 0; i < localDirs.length; i++) {
            Direction dir = localDirs[i];
            int rowViewNextWall = walkRowView(rowView, dir);
            int colViewNextWall = walkColView(colView, dir);
            int rowViewNextCorridor = walkRowView(rowViewNextWall, dir);
            int colViewNextCorridor = walkColView(colViewNextWall, dir);
            
            if (isLegalView(rowViewNextCorridor, colViewNextCorridor)) {
                if (getCell(rowViewNextCorridor, colViewNextCorridor) == Cell.notfound) {
                    setCell(rowViewNextWall, colViewNextWall, Cell.corridor);
                    traverseMaze(rowViewNextCorridor, colViewNextCorridor);
                }
            }
        }
    }
    
    private boolean isLegalView(int rowView, int colView) {
        return (rowView >= 0 && rowView < rowViewMax &&
                colView >= 0 && colView < colViewMax);
    }
    
    private int walkRowView(int rowView, Direction dir) {
        switch (dir) {
            default:
            case left:
            case right:
                return rowView;
                
            case up:
                return rowView - 1;
                
            case down:
                return rowView + 1;
        }
    }
    
     int walkColView(int colView, Direction dir) {
        switch (dir) {
            default:
            case up:
            case down:
                return colView;
                
            case left:
                return colView - 1;
                
            case right:
                return colView + 1;
        }
    }
    
   private void shuffleDirections(Direction[] dirs) {
        if (rand == null) {
            rand = new Random();
        }
        
        for (int i = 0; i < dirs.length-1; i++) {
            int iSwap = i + rand.nextInt(dirs.length - i);
            Direction t = dirs[i];
            dirs[i]=dirs[iSwap];
            dirs[iSwap] = t;
        }       
    }
    
    private int calcRowView(int row) {
        return row*2 + 1;
    }
    
    private int calcColView(int col) {
        return col*2 + 1;
    }
    
    private void initializeMaze() {
        for (int row = 0; row < rowViewMax; row++) {
            setCell(row, 0, Cell.wall);// left wall
            setCell(row, colViewMax-1, Cell.wall);// right wall
        }
        
        for (int col = 0; col < colViewMax; col++) {
            setCell(0, col, Cell.wall); // top wall
            setCell(rowViewMax-1, col, Cell.wall); // bottom wall
        }
        
        // fill in the inside as walls
        for (int row = 1; row < rowViewMax-1; row++) {
            for (int col = 1; col < colViewMax-1; col++){
                setCell(row, col, Cell.wall);
            }
        }
        
        // punch some empty cells in the grid
        for (int row = 1; row < rowViewMax-1; row+=2) {
            for (int col = 1; col < colViewMax-1; col+=2) {
                setCell(row, col, Cell.notfound);
            }
        }
    }
    
    public int beginRow() {
        return 0;
    }
    
    public int endRow() {
        return rowViewMax;
    }
    
    public int beginCol() {
        return 0;
    }
    
    public int endCol() {
        return colViewMax;
    }
    
    public Cell getCell(int row, int col) {
        return view[row][col].cell;
    }
    
    private void setCell(int row, int col, Cell cell) {
        MazeCell mcell = new MazeCell();
        mcell.cell = cell;
        if (cell == Cell.testing || cell == Cell.deadend) {
           mcell.serial = getSerial(row, col);
           if (getCell(row, col) == Cell.corridor) {
                if (serialMax < getSize())
                    mcell.serial = serialMax++;
                else
                    mcell.serial = getSize();
            }
        }
        view[row][col] = mcell;
    }
    
    public int getSerial(int row, int col) {
        return view[row][col].serial;
    }
    
    public int getSize() {
        return rowViewMax*colViewMax/2;
    }
    
    public void setSize(int row, int col, int holes) {
        rowMax = row;
        colMax = col;
        rowViewMax = 2*row + 1;
        colViewMax = 2*col + 1;
        view = new MazeCell[rowViewMax][colViewMax];
        
        rowEntry = 0;
        colEntry = 0;
        rowExit = rowMax-1;
        colExit = colMax - 1;
        
        maxHoles = holes;
        
        serialMax = 0;
    }
}

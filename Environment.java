import java.awt.Color;
import java.io.*;
// import java.security.SecureRandom;
// import java.util.*;


/**
 * Ambiente para pianola hecha con celulas.
 * 
 * @author Jose Javier Alvarez Pacheco
 * @version 2023.06.07
 */
public class Environment
{
    // Default size for the environment.
    private static final int ROWS = 86;
    private static final int COLS = 88;
    public static final Color PINK = new Color(190, 105, 95);
    public static final Color LIGHT_BLUE = new Color(0, 174, 255);
    // The grid of cells.
    private Cell[][] cells;
    // Visualization of the environment.
    private final EnvironmentView view;
    private int[] values;
    private int currentStep;


    /**
     * Create an environment with the default size.
     */
    public Environment()
    {
        this(ROWS, COLS);
    }

    /**
     * Create an environment with the given size.
     * @param numRows The number of rows.
     * @param numCols The number of cols;
     */
    public Environment(int numRows, int numCols)
    {
        setup(numRows, numCols);
        view = new EnvironmentView(this, numRows, numCols);
        view.showCells();
        currentStep = 0;
        values = readCsvToArray("Rimsky Korsakov - Flight of the bumblebee (arr. Rachmaninoff).csv");
    }
    
    /**
     * Run the automaton for one step.
     */
    public void step()
    {
        int numRows = cells.length;
        int numCols = cells[0].length;
        // Build a record of the next volume of each cell.
        int[][] nextVolumes = new int[numRows][numCols];
        // Make the volume of each new cell the volume the cell below had before
        for(int row = 0; row <= numRows-2; row++) {
            for(int col = 0; col < numCols; col++) {
                nextVolumes[row][col] = cells[row+1][col].getVolume();
            }
        //for the last row, randomize their volumes
        try {
            for(int col = 0; col < numCols; col++) {
                nextVolumes[numRows-1][col] = values[88*currentStep+col];
            }
        }
        catch (ArrayIndexOutOfBoundsException e1) {
            try {
                Thread.sleep(5000);
                view.setVisible(false);
                view.dispose();
                System.exit(0);
            }
            catch (InterruptedException e2) {
                view.setVisible(false);
                view.dispose();
                System.exit(0);
            }
        }

        }
        // Update the cells' volumes and colors.
        for(int row = 0; row < numRows; row++) {
            for(int col = 0; col < numCols; col++) {
                setCellVolume(row, col, nextVolumes[row][col]);
            }
        }
        currentStep++;
    }
    
    /**
     * Reset the state of the automaton to all DEAD.
     */
    public void reset()
    {
        int numRows = cells.length;
        int numCols = cells[0].length;
        for(int row = 0; row < numRows; row++) {
            for(int col = 0; col < numCols; col++) {
                setCellVolume(row, col, 0);
            }
        }
        currentStep=0;
    }
    
    /**
     * Set the volume (and therefore color intensity) of one cell.
     * @param row The cell's row.
     * @param col The cell's col.
     * @param volume The cell's volume.
     */
    public void setCellVolume(int row, int col, int volume)
    {
        cells[row][col].setVolume(volume);
    }
    
    /**
     * Return the grid of cells.
     * @return The grid of cells.
     */
    public Cell[][] getCells()
    {
        return cells;
    }
    
    /**
     * Setup a new environment of the given size.
     * @param numRows The number of rows.
     * @param numCols The number of cols;
     */
    private void setup(int numRows, int numCols)
    {
        cells = new Cell[numRows][numCols];
        for(int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (col % 12 == 1 || col % 12 == 4 || col % 12 == 6 || col % 12 == 9 || col % 12 == 11){
                    cells[row][col] = new Cell(PINK);
                }
                else {
                    cells[row][col] = new Cell(LIGHT_BLUE);
                }
            }
        }
    }
    
    public int[] readCsvToArray(String fileName){
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            if (line != null) {
                String[] stringValues = line.split(",", -1);
                int[] valueArr = new int[stringValues.length];
                for (int i = 0; i< valueArr.length; i++) {
                    if (stringValues[i].isEmpty()) {
                        valueArr[i] = 0;
                    }
                    else {
                        valueArr[i] = Integer.parseInt(stringValues[i]);
                    }
                }               
                bufferedReader.close();
                fileReader.close();
                return valueArr;
            }
            bufferedReader.close();
            fileReader.close();
            return null;
        }
        catch (IOException e) {
            System.out.println("Unable to open file.");
            return null;
        }
    }
}
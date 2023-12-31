import java.awt.Color;
import java.io.*;



/**
 * Ambiente para pianola hecha con celulas.
 * 
 * @author Jose Javier Alvarez Pacheco
 * @version 2023.06.07
 */
public class Environment
{
    // Default size for the environment.
    public static final int ROWS = 43;
    private static final int COLS = 88;
    public static final Color PINK = new Color(190, 105, 95);
    public static final Color LIGHT_BLUE = new Color(0, 174, 255);
    // The grid of cells.
    private Cell[][] cells;
    // Visualization of the environment.
    private final EnvironmentView view;
    private Matriz cancion;
    private MusicPlayer player;
    private int currentRow;
    private int currentChord;
    private int lastChord;
    private EncuentraAcordes encAco;
    private String mp3Filename;
    // private int newChordChangeRow;
    // private int currentChordinNumRows;
    // private int lastChordinNumRows;
    // private int deleteChordChangeRow;
    
    /**
     * Create an environment with the default size.
     */
    public Environment(Track track, EncuentraAcordes encuentraAcordes)
    {
        this(ROWS, COLS, track.csvFile, track.mp3File, encuentraAcordes);
    }
    
    /**
     * Create an environment with the default size.
     */
    public Environment(String csvFilename, EncuentraAcordes encuentraAcordes)
    {
        this(ROWS, COLS, csvFilename, null, encuentraAcordes);
    }

    /**
     * Create an environment with the default size.
     */
    public Environment(String csvFilename, String mp3Filename, EncuentraAcordes encuentraAcordes)
    {
        this(ROWS, COLS, csvFilename, mp3Filename, encuentraAcordes);
    }

    /**
     * Create an environment with the given size.
     * @param numRows The number of rows.
     * @param numCols The number of cols;
     */
    public Environment(int numRows, int numCols, String csvFilename, String mp3Filename, EncuentraAcordes encuentraAcordes)
    {
        setup(numRows, numCols);
        view = new EnvironmentView(this, numRows, numCols);
        view.showCells();
        currentRow = 0;
        cancion = Matriz.matrizDeCancionCSV(numCols, csvFilename);
        player = new MusicPlayer();
        currentChord = -1; //-1 cuando no hay ningun acorde
        lastChord = -1;
        // currentChordinNumRows = -1;
        // lastChordinNumRows = -1;
        this.encAco = encuentraAcordes;
        this.mp3Filename = mp3Filename;
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
        boolean[][] nextLines = new boolean[numRows][numCols];
        // Make the volume of each new cell the volume the cell below had before
        for(int row = 0; row <= numRows-2; row++) {
            for(int col = 0; col < numCols; col++) {
                nextVolumes[row][col] = cells[row+1][col].getVolume();
                nextLines[row][col] = cells[row+1][col].hasLine();
            }
        //for the last row, get from cancion and if the chord changes put line
        try {
            for(int col = 0; col < numCols; col++) {
                nextVolumes[numRows-1][col] = cancion.get(currentRow, col);
                nextLines[numRows-1][col] = siAcordeCambia();
            }
        }
        catch (ArrayIndexOutOfBoundsException e1) {

        }

        }
        // Update the cells' volumes and colors.
        for(int row = 0; row < numRows; row++) {
            for(int col = 0; col < numCols; col++) {
                setCellVolume(row, col, nextVolumes[row][col]);
                cells[row][col].setHasLine(nextLines[row][col]);
            }
        }

        if (siAcordeCambia()) {

            Thread thread = new Thread() {
                int cc = currentChord;
                int lc = lastChord;
                public void run() {
                    try {
                        sleep((long)EnvironmentView.DELAY*numRows);
                        if (cc == -1) {
                            cc = lc+1;
                        }
                        else{
                            lc = cc;
                            cc = -1;
                        }
                        view.updateChords(encAco.execute(cc));
                    }
                    catch (InterruptedException ex) {

                    }

                }
            };
            thread.start();
            if (currentChord == -1) {
                currentChord = lastChord +1;
            }
            else {
                lastChord = currentChord;
                currentChord = -1;
            }
        }
        currentRow++;

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
        currentRow=0;
        currentChord = -1;
        lastChord = -1;
        view.updateChords(encAco.execute(currentChord));
    }

    public void playCurrentSong() {
        player.startPlaying(mp3Filename);
    }

    public void stopMusic() {
        player.stop();
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

    public int getCurrentRow() {
        return currentRow;
    }

    public int getCurrentChord() {
        return currentChord;
    }

    public int getLastChord() {
        return lastChord;
    }

    public boolean siAcordeCambia() {
        try {
            if (((currentChord == -1) && (currentRow+1 == encAco.getFilasAcordes()[lastChord+1][0])) || ((currentChord != -1) && (currentRow+1 == encAco.getFilasAcordes()[currentChord][1]))) {
                return true;
            }
            return false;
        } 
        catch(ArrayIndexOutOfBoundsException e) {
            return false;
        }
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
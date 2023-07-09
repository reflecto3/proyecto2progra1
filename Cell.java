import java.awt.Color;
// import java.util.*;

/**
 * Celula para pianola de piano
 * 
 * @author Jose Javier Alvarez Pacheco
 * @version 2023.06.07
 */
public class Cell
{
    // The possible states.
    // public static final int ALIVE = 0, DEAD = 1, DYING = 2;
    // The number of possible states.
    public static final int MAX_VOLUME = 255;
    
    
    // The cell's state.
    private final Color BASE_COLOR;
    private int volume;
    private Color displayColor;
    // The cell's neighbors.
    // private Cell[] neighbors;

    /**
     * Set the cell with initial volume 0 and color color
     * @param BASE_COLOR the array of rgb values of the color the cell will be *permanently* set to.
     * 
     */
    public Cell(Color BASE_COLOR)
    {
        this(0, BASE_COLOR);
    }
    
    /**
     * Set the initial volume and the color of the cell.
     * @param initialVolume The initial volume
     * @param BASE_COLOR the permanent base color of the cell. Its real color will be a shade of the base color.
     */
    public Cell(int initialVolume, Color BASE_COLOR)
    {
        volume = initialVolume;
        this.BASE_COLOR = BASE_COLOR;
        displayColor = shadeOf(BASE_COLOR, (double)initialVolume/MAX_VOLUME);
    }

    /**
     * Get the volume of this cell.
     * @return The state.
     */
    public int getVolume()
    {
        return volume;
    }
    
    public Color getColor(){
        return displayColor;
    }

    /**
     * Set the volume (and therefore color) of this cell.
     * @param volume The state.
     */
    public void setVolume(int volume)
    {
        this.volume = volume;
        displayColor = shadeOf(BASE_COLOR, ((double)volume)/MAX_VOLUME);
    }   
    
    /**
     * Mueve el color a RGB lineal antes de hacer calculos para sacar otro con una luminosidad reducida al factor puesto.
     * @param colorBase color base de entrada
     * @param factor factor de luminosidad original deseado (0-1)
     * @return un color que es el color de entrada con una luminosidad reducida al factor escrito
     */
    private static Color shadeOf(Color colorBase, double factor) {
        if (factor > 1 || factor < 0) {
            System.err.print("Argumento invalido: El factor debe de estar entre 0 y 1");
            return colorBase;
        }
        float newRed = (float)((colorBase.getRed()/255.0)*Math.pow(factor, 1/2.2)), 
              newGreen = (float)((colorBase.getGreen()/255.0)*Math.pow(factor, 1/2.2)), 
              newBlue = (float)((colorBase.getBlue()/255.0)*Math.pow(factor, 1/2.2));
        return new Color(newRed, newGreen, newBlue);
        
    }
    
}

package src;
import java.util.ArrayList;

/**
 * Reglas del problema y de la estrategia de Solución
 */

public class PuzzleRules {
    /**
     * Tamaño del tablero
     */
    public static int boardSize = 3;

    /**
     * Valor máximo de la suma de las distancias de Manhattan
     */
    public static int maxManhattan = 24;

    /**
     * Valor máximo de la suma de piezas mal colocadas
     */
    public static int maxMisplaced = 8;
    
    /**
     * Valor máximo del total de inversos de permutaciones
     */
    public static int maxInversions;

    /**
     * Valor máximo de la suma de fichas fuera de su fila/columna
     * correcta
     */
    public static int maxCommutes;

    public static final char START = 'S';
    public static final char RIGHT = 'R';
    public static final char UP = 'U';
    public static final char LEFT = 'L';
    public static final char DOWN = 'D';

    /**
     * Estado resuelto representado análogamente al tablero como una matriz.
     */
    public static int[][] solvedState = {
        {0,1,2},
        {3,4,5},
        {6,7,8}
    };

    /**
     * Estado resuelto visto como un vector de coordenadas. Cada índice representa
     * la ficha con ese número, y su valor es un vector de coordenadas (x,y) de su
     * posición en el tablero meta.
     */
    public static int[][] solvedStateCoordinates = {
        {0,0},
        {0,1},
        {0,2},
        {1,0},
        {1,1},
        {1,2},
        {2,0},
        {2,1},
        {2,2}
    };

    public static void prepare() {
        PuzzleRules.maxMisplaced = (int) Math.pow(boardSize, 2) - 1;
        setMaxManhattan();
        setMaxInversions();
        setMaxCommutes();
    }

    private static void setMaxManhattan() {
        int[] blankTile = new int[2];
        // Ubicación de la pieza vacía en el estado resuelto
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (solvedState[i][j] == 0) {
                    blankTile[0] = i;
                    blankTile[1] = j;
                }
            }
        }

        // Máximo de Manhattan para un tablero resuelto con la ficha vacía
        // en el centro
        maxManhattan = (int) Math.pow(boardSize, 3) - boardSize;
        if (boardSize % 2 == 0) maxManhattan += 2;
        
        // Obtiene el total real de Manhattan según la posición de la
        // ficha vacía en el tablero meta
        for (int i = 0, k= 0; i < boardSize; i++) {
            ArrayList<int[]> coordinates = new ArrayList<>();
            if ( i >= boardSize/2) k++;
            for (int j = 0 + k; j < i + 1 - k; j++) {
                coordinates.add(new int[]{j, i - j});
                coordinates.add(new int[]{boardSize - 1 - j, i - j});
                coordinates.add(new int[]{j, boardSize - 1 - (i - j)});
                coordinates.add(new int[]{boardSize - 1 - j, boardSize - 1 - (i - j)});
            }

            for (int[] coord : coordinates) {
                if (coord[0] == blankTile[0] && coord[1] == blankTile[1]) {
                    maxManhattan -= boardSize % 2 == 0 ? boardSize - i : boardSize - 1 - i;
                    return;
                }
            }
        }
    }

    private static void setMaxInversions() {
        int n = (int) Math.pow(boardSize, 2);
        maxInversions = (n - 1) * (n - 2) / 2;
    }

    private static void setMaxCommutes() {
        maxCommutes = 0;
        for (int i = 0; i < Math.ceil((double) boardSize / 2); i++) {
            int aux = boardSize % 2 == 0 ? 2 * i + 1 : 2 * i;
            maxCommutes += aux;
        }

        maxCommutes *= 2;
        maxCommutes *= boardSize;
    }
}

import java.util.ArrayList;

/**
 * Representación de un estado del tablero en el árbol de búsqueda.
 * Almaaena el estado como una matriz, representando análogamente el tablero, y
 * la posición de la ficha vacía como coordenadas (x,y).
 * Almacena además el movimiento previo del que se generó el estado actual
 * a partir del estado anterior.
 * Calcula su propio valor de evaluación con las siguientes heurísticas:
 *  -Suma de las distancias de Manhattan de las fichas desde su posición
 *  actual hasta su posición en el estado resuelto (sin contar la ficha vacía).
 *  -Total de las fichas que no están en su posición correcta del estado resuelto.
 *  -Suma total de los inversos de permutaciones, viendo el tablero como un vector.
 */

public class PuzzleStateNode {
    /**
     * Estado del tablero
     */
    private int[][] state;

    /**
     * Posición de la ficha vacía.
     */
    private int[] blankTile;

    /**
     * Evaluación del tablero según la función de evaluación.
     */
    private int evaluation;

    /**
     * Movimiento a partir del cual se generó desde el estado anterior.
     */
    private char previousMovement;

    /**
     * Constructor inicial. La posición de la ficha vacía se busca al construir el nodo.
     * Se evalúa el nodo al construirlo.
     * El movmiento generador se establece como ninguno por defecto.
     * @param state El estado del tablero como matriz
     */
    public PuzzleStateNode(int[][] state) {
        this.state = state;
        this.blankTile = new int[2];

        for (int i = 0; i < PuzzleRules.boardSize; i++)
            for (int j = 0; j < PuzzleRules.boardSize; j++)
                if (this.state[i][j] == 0) {
                    this.blankTile[0] = i;
                    this.blankTile[1] = j;
                }

        this.evaluation = evaluate();
        this.previousMovement = PuzzleRules.START;
    }

    /**
     * Construye el nodo con el constructor inicial y sobreescribe el movimiento
     * generador con el movimiento en el argumento.
     * @param state Estado del tablero como matriz
     * @param movement Movimiento por el cual se generó el estado actual (ver PuzzleRules).
     */
    private PuzzleStateNode(int[][] state, char movement) {
        this(state);
        this.previousMovement = movement;
    }

    public PuzzleStateNode genChild(char dir) {
        int[] newBlankTile = this.blankTile.clone();

        switch (dir) {
            case PuzzleRules.RIGHT:
                newBlankTile[1]++;
                break;
            case PuzzleRules.UP:
                newBlankTile[0]--;
                break;
            case PuzzleRules.LEFT:
                newBlankTile[1]--;
                break;
            case PuzzleRules.DOWN:
                newBlankTile[0]++;
                break;
        }

        int[][] newState = new int[PuzzleRules.boardSize][];
        for (int i = 0; i < PuzzleRules.boardSize; i++) {
            newState[i] = this.state[i].clone();
        }

        if (newBlankTile[0] >= 0 && newBlankTile[0] < PuzzleRules.boardSize &&
            newBlankTile[1] >= 0 && newBlankTile[1] < PuzzleRules.boardSize) {
            newState[this.blankTile[0]][this.blankTile[1]] = newState[newBlankTile[0]][newBlankTile[1]];
            newState[newBlankTile[0]][newBlankTile[1]] = 0;
            return new PuzzleStateNode(newState, dir);
        }
        else return null;
    }

    private int manhattan() {
        int total = 0;
        for (int i = 0; i < PuzzleRules.boardSize; i++) {
            for (int j = 0; j < PuzzleRules.boardSize; j++) {
                if (state[i][j] != 0) {
                    total += Math.abs((i - PuzzleRules.solvedStateCoordinates[state[i][j]][0])) + Math.abs((j - PuzzleRules.solvedStateCoordinates[state[i][j]][1]));
                }
            }
        }

        return total;
    }

    private int misplacedTiles() {
        int total = 0;
        for (int i = 0; i < PuzzleRules.boardSize; i++) {
            for (int j = 0; j < PuzzleRules.boardSize; j++) {
                if (state[i][j] != 0) {
                    total += i == PuzzleRules.solvedStateCoordinates[state[i][j]][0] && j == PuzzleRules.solvedStateCoordinates[state[i][j]][1] ? 0 : 1;
                }
            }
        }

        return total;
    }

    private int permutationInversions() {
        int total = 0;

        // Obtiene la permutación (vector) correspondiente al estado resuelto
        int[] solvedPermutation = new int[(int) Math.pow(PuzzleRules.boardSize, 2)];
        for (int i = 0, j = 0; i < solvedPermutation.length; i++, j = i/3) {
            solvedPermutation[i] = PuzzleRules.solvedState[j][i % 3];
        }

        // Obtiene la permutación (vector) correspondiente al estado actual
        ArrayList<Integer> statePermutation = new ArrayList<>();
        for (int i = 0; i < PuzzleRules.boardSize; i++) {
            for (int j = 0; j < PuzzleRules.boardSize; j++) {
                statePermutation.add(this.state[i][j]);
            }
        }

        // Mapea la permutación actual a una permutación normalizada
        // La permutación normalizada tiene como estado resuelto:
        // 0,1,2
        // 3,4,5
        // 6,7,8
        // Cada posición del estado resuelto en la entrada se corresponde con
        // una posición del estado resuelto normal, y el estado actual se mapea
        // a una correspondencia con este.
        int[] normalizedStatePermutation = new int[(int) Math.pow(PuzzleRules.boardSize, 2)];
        for (int i = 0; i < normalizedStatePermutation.length; i++) {
            normalizedStatePermutation[i] = statePermutation.indexOf(solvedPermutation[i]);
        }

        // Obtiene el total de Inversiones de Permutaciones
        for (int i = 0; i < normalizedStatePermutation.length - 1; i++) {
            // Recorre el arreglo desde el inicio hasta el penúltimo elemento
            for (int j = i + 1; j < normalizedStatePermutation.length; j++) {
                // Recorre el arreglo desde el elemento i hasta el último elemento
                // Obtiene la cantidad de valores menores que el elemento i a su derecha
                if (normalizedStatePermutation[j] < normalizedStatePermutation[i]) total++;
            }
        }

        return total;
    }

    private int evaluate() {
        return 1 * this.manhattan() + 1 * this.misplacedTiles() + 1 * this.permutationInversions();
    }

    public int getEvaluation() {
        return evaluation;
    }

    public char getPreviousMovement() {
        return previousMovement;
    }

    @Override
    public String toString() {
        String out = "";
        for (int[] line : this.state) {
            out += '\n';
            for (int tile : line) {
                out += (tile == 0 ? "_" : tile) + "\t";
            }
        }

        return out;
    }
}
package src;
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
     * Nivel del árbol en el que se encuentra este nodo
     */
    private int level;

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
    private double evaluation;

    /**
     * Movimiento a partir del cual se generó desde el estado anterior.
     */
    private char previousMovement;

    /**
     * Padre de este nodo en el árbol de búsqueda.
     * El padre no es estático, puede variar dependiendo si el
     * algoritmo a* encuentra un mejor camino hacia este estado.
     */
    private PuzzleStateNode father;

    /**
     * Hijos de este nodo en el árbol de búsqueda.
     * Los hijos almacenados no son todos los que este nodo puede generar,
     * sino los que se encuentran asociados a este nodo en un
     * momento determinado del algoritmo. Esto evitando que se repitan
     * estados.
     */
    private ArrayList<PuzzleStateNode> currentChildren;

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
        this.father = null;
        this.level = 0;
        this.currentChildren = new ArrayList<>();
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

    private PuzzleStateNode(int[][] state, char movement, PuzzleStateNode father, int level) {
        this(state);
        this.previousMovement = movement;
        this.father = father;
        this.level = level;

        this.evaluation = evaluate();
    }

    /**
     * Generador de nodos hijo. Genera el nodo para el movimiento solicitado en
     * el parámetro.
     * El movimiento es de tipo caracter, especificado en PuzzleRules.
     * @param dir Dirección del movimiento para generar el nodo hijo (PuzzleRules).
     * @return El nodo generado del movimiento, si es que el movimiento fue válido
     * (es decir, la pieza blanca no sale de los límites del tablero), o null en caso
     * contrario.
     */
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
            return new PuzzleStateNode(newState, dir, this, this.level + 1);
        }
        else return null;
    }

    /**
     * Genera todos los hijos posibles de este nodo y los retorna en un arreglo simple.
     * No analiza por estados repetidos.
     * @return El arreglo simple con los hijos generados.
     */
    public PuzzleStateNode[] genAllChildren() {
        ArrayList<PuzzleStateNode> aux = new ArrayList<>();

        for (char dir : "RULD".toCharArray()) {
            PuzzleStateNode auxNode = this.genChild(dir);
            if (auxNode != null) aux.add(auxNode);
        }

        return aux.toArray(new PuzzleStateNode[aux.size()]);
    }

    @Override
    public boolean equals(Object object) {
        PuzzleStateNode node = (PuzzleStateNode) object;

        // Si el hueco está en un lugar distinto el tablero es automáticamente distinto
        if (node.getTile(this.blankTile[0], this.blankTile[1]) != 0) return false;

        // Si el hueco está en el mismo lugar, se comprueba cada una de las casillas
        for (int i = 0; i < PuzzleRules.boardSize; i++) {
            for (int j = 0; j < PuzzleRules.boardSize; j++) {
                if (this.state[i][j] != node.getTile(i, j)) return false;
            }
        }
        return true;
    }

    /**
     * Calcula la suma de las distancias de Manhattan de cada ficha desde
     * su posición en el estado actual hacia su posición en el estado resuelto.
     * Nota: Normalizar
     * @return La suma de las distancias de Manhattan (normalizada)
     */
    private double manhattan() {
        double total = 0;
        for (int i = 0; i < PuzzleRules.boardSize; i++) {
            for (int j = 0; j < PuzzleRules.boardSize; j++) {
                if (state[i][j] != 0) {
                    total += Math.abs((i - PuzzleRules.solvedStateCoordinates[state[i][j]][0])) + Math.abs((j - PuzzleRules.solvedStateCoordinates[state[i][j]][1]));
                }
            }
        }

        return total / PuzzleRules.maxManhattan;
    }

    /**
     * Calcula el número total de fichas fuera de su posición en el estado resuelto.
     * Nota: Normalizar
     * @return El total de fichas colocadas incorrectamente (normalizada)
     */
    private double misplacedTiles() {
        double total = 0;
        for (int i = 0; i < PuzzleRules.boardSize; i++) {
            for (int j = 0; j < PuzzleRules.boardSize; j++) {
                if (state[i][j] != 0) {
                    total += i == PuzzleRules.solvedStateCoordinates[state[i][j]][0] && j == PuzzleRules.solvedStateCoordinates[state[i][j]][1] ? 0 : 1;
                }
            }
        }

        return total / PuzzleRules.maxMisplaced;
    }

    /**
     * Calcula el total de Inversos de Permutaciones en el tablero visto como
     * un vector. El vector se forma de unir secuencialmente las filas de la
     * matriz del tablero una detrás de otra.
     * Un inverso de una permutación es una pareja de números x,y dentro de la
     * permutación, x > y, tales que x se encuentra en alguna posición a la izquierda
     * de y.
     * @return El total de inversos de permutaciones para cada ficha del tablero.
     */
    private double permutationInversions() {
        double total = 0;

        // Obtiene la permutación (vector) correspondiente al estado resuelto
        int[] solvedPermutation = new int[(int) Math.pow(PuzzleRules.boardSize, 2)];
        for (int i = 0, j = 0; i < solvedPermutation.length; i++, j = i/PuzzleRules.boardSize) {
            
            solvedPermutation[i] = PuzzleRules.solvedState[j][i % PuzzleRules.boardSize];
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
                if (normalizedStatePermutation[j] != 0 && normalizedStatePermutation[j] < normalizedStatePermutation[i]) total++;
            }
        }

        return total / PuzzleRules.maxInversions;
    }

    private double commutedColumns() {
        double total = 0;

        for (int i = 0; i < PuzzleRules.boardSize; i++) {
            for (int j = 0; j < PuzzleRules.boardSize; j++) {
                if (state[i][j] != 0) {
                    total += Math.abs((j - PuzzleRules.solvedStateCoordinates[state[i][j]][1]));
                }
            }
        }

        return total / PuzzleRules.maxCommutes;
    }

    private double commutedRows() {
        double total = 0;

        for (int i = 0; i < PuzzleRules.boardSize; i++) {
            for (int j = 0; j < PuzzleRules.boardSize; j++) {
                if (state[i][j] != 0) {
                    total += Math.abs((i - PuzzleRules.solvedStateCoordinates[state[i][j]][0]));
                }
            }
        }

        return total / PuzzleRules.maxCommutes;
    }

    /**
     * Evalua el estado actual según las funciones heurísticas.
     * @return El valor final de la evaluación
     */
    public double h() {
        return
            0.8 * this.manhattan() +
            0.2 * this.misplacedTiles() +
            0.0 * this.permutationInversions() +
            0.0 * this.commutedColumns() +
            0.0 * this.commutedRows()
        ;
    }

    public double g() {
        return (double) level / PuzzleRules.maxMov[PuzzleRules.boardSize - 3];
    }

    private double evaluate() {
        if (h() > 0.2) return 4 * g() + 2 * Math.pow(h(), 2);
        else return (0.8 * g() + 0.2 * h());
    }


    // Utilities

    public void setLevel(int level) {
        this.level = level;
    }

    public void setFather(PuzzleStateNode father) {
        this.father = father;
    }

    public void setCurrentChildren(ArrayList<PuzzleStateNode> children) {
        currentChildren = children;
    }

    public void addChild(PuzzleStateNode child) {
        currentChildren.add(child);
    }
    
    public double getEvaluation() {
        return evaluation;
    }

    public char getPreviousMovement() {
        return previousMovement;
    }

    public int getLevel() {
        return level;
    }

    public PuzzleStateNode getFather() {
        return father;
    }

    public ArrayList<PuzzleStateNode> getCurrentChildren() {
        return currentChildren;
    }

    public int getTile(int y, int x) {
        return state[y][x];
    }

    public PuzzleStateNode asRoot() {
        return new PuzzleStateNode(this.state);
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

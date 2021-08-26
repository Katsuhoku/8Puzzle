import java.util.ArrayList;
import java.util.Random;

/**
 * Nodo del "Árbol" de estados. Almacena el estado del tablero en una matriz
 * y la posición de la ficha vacía en un arreglo (x,y).
 * 
 * La clase almacena el tamaño del tablero SIZE y el estado resuleto SOLVED.
 * El estado resuelto es un vector donde cada índice representa el número de
 * una ficha (menos 1) del tablero -> [0]: ficha 1, [1]: ficha 2, ...
 * El contenido de cada índice es un arreglo (x,y) indicando la posición que
 * dicha ficha debe tener en el estado resuelto.
 */

public class StateNode {
    /**
     * Tamaño del tablero
     */
    private static final int SIZE = 3;

    /**
     * Estado resuelto. Cada índice representa el número de una ficha (menos 1)
     * del tablero.
     * El contenido de cada índice es un arreglo (x,y) indicando la posición
     * que dicha ficha debe tener en el estado resuelto.
     */
    private static final int[][] SOLVED = {
        {0,1},
        {0,2},
        {1,0},
        {1,1},
        {1,2},
        {2,0},
        {2,1},
        {2,2}
    };

    /**
     * Estado del tablero almacenado.
     */
    private int[][] state;

    /**
     * Posición de la ficha vacía.
     */
    private int[] pos;

    /**
     * Constructor con posición de la ficha vacía.
     * @param state Estado a almacenar.
     * @param pos Posición de la ficha vacía.
     */
    public StateNode(int[][] state, int[] pos) {
        this.state = state;
        this.pos = pos;
    }

    /**
     * Constructor sin posición de la ficha vacía. La posición
     * se busca al momento de construir el objeto.
     * @param state Estado a almacenar.
     */
    public StateNode(int[][] state) {
        this.state = state;
        this.pos = new int[2];

        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (state[i][j] == 0) {
                    pos[0] = i;
                    pos[1] = j;
                }
    }

    /**
     * Obtiene el estado del tablero para un movimiento dado el estado
     * actual del tablero.
     * @param direction Dirección del movimiento en caracter. 'r': derecha,
     * 'u': arriba, 'l': izquierda, 'd': abajo.
     * @return Un nuevo tablero (StateNode) con el nuevo estado del tablero,
     * si es que el movimiento fue válido (dentro de los límites del tablero),
     * o null en caso contrario.
     */
    public StateNode genChild(char direction) {
        int[] newpos = new int[2];
        newpos[0] = this.pos[0];
        newpos[1] = this.pos[1];
        switch(direction) {
            case 'r':
                newpos[1]++;
                break;
            case 'u':
                newpos[0]--;
                break;
            case 'l':
                newpos[1]--;
                break;
            case 'd':
                newpos[0]++;
                break;
        } // Calcula la nueva posición según la dirección solicitada

        int[][] newState = new int[SIZE][];
        for (int i = 0; i < SIZE; i++) {
            newState[i] = new int[SIZE];
            for (int j = 0; j < SIZE; j++) {
                newState[i][j] = this.state[i][j];
            }
        }
        if (newpos[0] >= 0 && newpos[0] < SIZE && newpos[1] >= 0 && newpos[1] < SIZE) {
            newState[this.pos[0]][this.pos[1]] = newState[newpos[0]][newpos[1]];
            newState[newpos[0]][newpos[1]] = 0; // Intercambia posiciones entre la ficha en la dirección deseada y el hueco.
            return new StateNode(newState, newpos);
        }
        else return null;
    }

    /**
     * Calcula la distancia de Manhattan total, para cada ficha en el tablero.
     * @return La suma de todas las distancias de Manhattan de las fichas en
     * el tablero.
     */
    private int manhattan() {
        int total = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (this.state[i][j] != 0) {
                    // |x.actual - x.solución| + |y.actual - y.solución| de cada ficha
                    total += Math.abs((i - SOLVED[state[i][j] - 1][0])) + Math.abs((j - SOLVED[state[i][j] - 1][1]));
                }
            }
        }

        return total;
    }

    private int misplacedTiles() {
        int total = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (this.state[i][j] != 0) {
                    total += i == SOLVED[state[i][j] - 1][0] && j == SOLVED[state[i][j] - 1][1] ? 0 : 1;
                }
            }
        }

        return total;
    }

    /**
     * Genera todos los nodos hijos posibles para este nodo y busca aquel que
     * tenga la menor distancia de Manhattan.
     * El nodo actual tendrá siempre la menor distancia obtenida hasta ahora
     * por el algoritmo.
     * @return El nodo con la menor distancia de Manhattan entre los hijos y el
     * nodo actual, o null en caso contrario.
     */
    public StateNode getBestChild() {
        ArrayList<StateNode> best = new ArrayList<>();
        char[] dir = {'r', 'u', 'l', 'd'};

        for (char d : dir) {
            StateNode aux = this.genChild(d);
            if (aux != null) {
                if (aux.evaluate() < this.evaluate()) {
                    best.add(aux);
                }
                else if (aux.evaluate() == this.evaluate()) {
                    best.add(aux);
                }
            }
        }

        Random rand = new Random();
        return best.size() == 0 ? null : best.get(rand.nextInt(best.size()));
    }

    private int evaluate() {
        return (int)(0.7 * this.manhattan() + 0.3 * this.misplacedTiles());
    }

    //  UTILITES

    public int getEvaluation() {
        return this.evaluate();
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

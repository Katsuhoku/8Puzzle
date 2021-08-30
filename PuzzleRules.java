import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Reglas del problema y de la estrategia de Solución
 */

public class PuzzleRules {
    public static int boardSize = 3;

    public static int maxManhattan = 24;

    public static int maxMisplaced = 8;
    
    public static int maxInversions;

    public static int maxCommutes;

    public static final char START = 's';
    public static final char RIGHT = 'r';
    public static final char UP = 'u';
    public static final char LEFT = 'l';
    public static final char DOWN = 'd';

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

    /**
     * Genera el mejor hijo para el nodo dado en el parámetro.
     * El mejor hijo es aquel que tiene un valor mínimo de evaluación,
     * sea menor o igual que la evaluación del nodo en el parámetro.
     * Si hay más de un hijo que cumple la condición, se regresa uno
     * de manera aleatoria.
     * @param node El nodo padre a partir del cual se generará el mejor nodo.
     * @return El mejor candidato, o null en caso de que no sea posible minimizar
     * el valor de evaluación.
     */
    public static PuzzleStateNode getBestChild(PuzzleStateNode node) {
        PuzzleStateNode[] childNodes = {
            node.genChild(RIGHT),
            node.genChild(UP),
            node.genChild(LEFT),
            node.genChild(DOWN)
        };

        double minEvaluation = node.getEvaluation();
        for (PuzzleStateNode childNode : childNodes) {
            if (childNode != null && childNode.getEvaluation() < minEvaluation)
                minEvaluation = childNode.getEvaluation();
        }

        ArrayList<PuzzleStateNode> bestChildNodes = new ArrayList<>();
        for (PuzzleStateNode childNode : childNodes) {
            if (childNode != null && childNode.getEvaluation() == minEvaluation)
                bestChildNodes.add(childNode);
        }

        Random rand = new Random();
        return bestChildNodes.size() == 0 ? null : bestChildNodes.get(rand.nextInt(bestChildNodes.size()));
    }

    /**
     * Genera la secuencia de nodos hijos que llegan al mejor resultado posible
     * dada la estrategia de solución.
     * @param root Raíz del problema (estado inicial)
     * @param maxIterations Número máximo de iteraciones (hijos generados)
     * @return La secuencia de nodos generados que llegan al mejor resultado
     * posible en el máximo número de iteraciones dado. La secuencia tendrá un
     * tamaño menor o igual al máximo de iteraciones.
     */
    public static Queue<PuzzleStateNode> findSequence(PuzzleStateNode root, int maxIterations) {
        Queue<PuzzleStateNode> nodeSequence = new LinkedList<>();
        nodeSequence.offer(root);

        for (int i = 0; i < maxIterations; i++) {
            root = getBestChild(root);
            if (root == null) break;
            nodeSequence.offer(root);

            System.out.println("Tablero:");
            System.out.println(root);
            System.out.println("Evaluación");
            System.out.println(root.getEvaluation());
        }

        return nodeSequence;
    }

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

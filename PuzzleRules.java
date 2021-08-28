import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Reglas del problema y de la estrategia de Solución
 */

public class PuzzleRules {
    public static int boardSize = 3;

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

        int minEvaluation = node.getEvaluation();
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
        }

        return nodeSequence;
    }
}

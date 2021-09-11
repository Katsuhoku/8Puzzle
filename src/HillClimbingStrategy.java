package src;
import java.util.Random;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

public class HillClimbingStrategy {
    public static double bestEvaluation = 1;
    
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
            node.genChild(PuzzleRules.RIGHT),
            node.genChild(PuzzleRules.UP),
            node.genChild(PuzzleRules.LEFT),
            node.genChild(PuzzleRules.DOWN)
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
            bestEvaluation = root.getEvaluation();
            root = getBestChild(root);
            if (root == null) break;
            nodeSequence.offer(root);
        }

        return nodeSequence;
    }
}

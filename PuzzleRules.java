import java.util.ArrayList;
import java.util.Random;

public class PuzzleRules {
    public static int boardSize = 3;

    public static final char START = 's';
    public static final char RIGHT = 'r';
    public static final char UP = 'u';
    public static final char LEFT = 'l';
    public static final char DOWN = 'd';

    public static int[][] solvedState = {
        {0,1,2},
        {3,4,5},
        {6,7,8}
    };

    // Posición de cada número en el tablero solución
    // El índice representa el número de la ficha
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

    public static PuzzleStateNode getBestChild(PuzzleStateNode node) {
        PuzzleStateNode[] childNodes = {
            node.genChild(RIGHT),
            node.genChild(UP),
            node.genChild(LEFT),
            node.genChild(DOWN)
        };

        int minEvaluation = node.getEvaluation();
        for (PuzzleStateNode childNode : childNodes) {
            if (childNode.getEvaluation() < minEvaluation) minEvaluation = childNode.getEvaluation();
        }

        ArrayList<PuzzleStateNode> bestChildNodes = new ArrayList<>();
        for (PuzzleStateNode childNode : childNodes) {
            if (childNode.getEvaluation() == minEvaluation) bestChildNodes.add(childNode);
        }

        Random rand = new Random();
        return bestChildNodes.size() == 0 ? null : bestChildNodes.get(rand.nextInt(bestChildNodes.size()));
    }
}

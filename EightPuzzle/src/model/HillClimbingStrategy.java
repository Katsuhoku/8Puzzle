package EightPuzzle.src.model;
import java.util.ArrayList;

public class HillClimbingStrategy implements EightPuzzleStrategy {
    private ArrayList<EightPuzzleNode> route;
    private final EightPuzzleNode initialNode;
    private final EightPuzzleNode goalNode;
    private final int size;
    
    
    public HillClimbingStrategy(EightPuzzleNode initialNode, EightPuzzleNode goalNode, int size) {
        this.initialNode = initialNode;
        this.goalNode = goalNode;
        this.size = size;
    }
    
    @Override
    public void run() {
        route = new ArrayList<>();
        boolean band;
        EightPuzzleNode currentNode;

        //Paso 1: Seleccionar un nodo como nodo actual
        currentNode = initialNode;
        currentNode.evaluation = evaluate(currentNode);
        route.add(currentNode);

        band = true;
        while (band) {
            
            //Paso 2: Generar sucesores y seleccionar el mejor sucesor
            var bestChild = bestChild(generateChildren(currentNode));
            
            //Paso 3: Si el mejor hijo es mejor al padre, ir al paso 2
            if (currentNode.evaluation > bestChild.evaluation) {
                route.add(bestChild);
                currentNode = bestChild;
            } else { //En cualquier otro caso terminar
                band = false;
            }
        } 
    }

    public EightPuzzleNode[] getRoute() {
        return route.toArray(new EightPuzzleNode[route.size()]);
    }

    private EightPuzzleNode[] generateChildren(EightPuzzleNode node) {
        //Possible children
        var children = new ArrayList<EightPuzzleNode>(4);

        //Children generation
        for (var movement : EightPuzzleMovement.values()) {
            var child = move(node, movement);
            if (child != null) children.add(child);
        }

        return children.toArray(new EightPuzzleNode[children.size()]);
    }

    private EightPuzzleNode move(EightPuzzleNode node, EightPuzzleMovement movement) {
        int[] bpp = node.getPositionOf(0); //Blank Piece Position

        switch (movement) {
            case UP: 
                if (bpp[0] > 0) 
                    return new EightPuzzleNode(node.change(bpp, new int[] { bpp[0] - 1, bpp[1] }), movement);
                break;
            case DOWN: 
                if (bpp[0] < size - 1) 
                    return new EightPuzzleNode(node.change(bpp, new int[] { bpp[0] + 1, bpp[1] }), movement);
                break;
            case LEFT: 
                if (bpp[1] > 0) 
                    return new EightPuzzleNode(node.change(bpp, new int[] { bpp[0], bpp[1] - 1 }), movement);
                break;
            case RIGHT: 
                if (bpp[1] < size - 1) 
                    return new EightPuzzleNode(node.change(bpp, new int[] { bpp[0], bpp[1] + 1 }), movement);
                break;
            default: return null;
        }
        return null;
    }

    private EightPuzzleNode bestChild(EightPuzzleNode[] children) {
        EightPuzzleNode bestChild = null;
        for (var child : children) {
            child.evaluation = evaluate(child);
            if (bestChild == null || bestChild.evaluation >= child.evaluation)
                bestChild = child;
        }
        return bestChild;
    }

    private int evaluate(EightPuzzleNode node) {
        return manhattan(node);
    }

    private int manhattan(EightPuzzleNode node) {
        int total = 0;
        int[] blankPiece = node.getPositionOf(0);

        for (int i = 0 ; i < size ; i++)
            for (int j = 0 ; j < size ; j++)
                if (i != blankPiece[0] || j != blankPiece[1]) {
                    int piece = node.getPieceOf(i, j);
                    int[] gpp = goalNode.getPositionOf(piece);

                    total += Math.abs(i - gpp[0]) + Math.abs(j - gpp[1]);
                }
                    
        return total;
    } 
}

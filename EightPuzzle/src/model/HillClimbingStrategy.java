package EightPuzzle.src.model;
import java.util.ArrayList;
import java.util.Random;

public class HillClimbingStrategy implements EightPuzzleStrategy {
    private final double H1_MAX = 24.0;
    private final double H1_WEIGHT = 0.4;

    private final double H2_MAX = 12 * Math.sqrt(2);
    private final double H2_WEIGHT = 0.2;

    private final double H3_MAX = 8.0;
    private final double H3_WEIGHT = 0.1;

    private final double H4_MAX = 16.0;
    private final double H4_WEIGHT = 0.3;

    private final int MAX_MOVEMENTS = 200;

    private ArrayList<EightPuzzleNode> route;
    private final EightPuzzleNode initialNode;
    private final EightPuzzleNode goalNode;
    private final int size;

    private NodeEvaluable evaluation = new NodeEvaluable() {
        @Override
        public double evaluate(EightPuzzleNode node) {
            return HillClimbingStrategy.this.evaluate(node);
        }
    };
    
    public HillClimbingStrategy(EightPuzzleNode initialNode, EightPuzzleNode goalNode, int size) {
        this.initialNode = initialNode;
        this.goalNode = goalNode;
        this.size = size;

        init(); 
    }

    private void init() {
        this.initialNode.evaluate(evaluation);
        this.goalNode.evaluate(evaluation);
    }
    
    @Override
    public void run() {
        route = new ArrayList<>();
        EightPuzzleNode currentNode;

        //Paso 1: Seleccionar un nodo como nodo actual
        currentNode = initialNode;
        route.add(currentNode);

        for (int i = MAX_MOVEMENTS ; i > 0 ; i--) {
            if (!currentNode.equals(goalNode)) {
                //Paso 2: Generar sucesores y seleccionar el mejor sucesor
                var bestChild = bestChild(generateChildren(currentNode));
                
                //Paso 3: Si el mejor hijo es mejor al padre, ir al paso 2
                if (bestChild.getEvaluation() <= currentNode.getEvaluation()) {
                    route.add(bestChild);
                    currentNode = bestChild;
                } else break;//En cualquier otro caso terminar
            } else break;
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
            var child =  EightPuzzleNode.move(node, movement);
            if (child != null) children.add(child);
        }

        return children.toArray(new EightPuzzleNode[children.size()]);
    }

    private EightPuzzleNode bestChild(EightPuzzleNode[] children) {
        ArrayList<EightPuzzleNode> bestChildren = new ArrayList<>();

        for (var child : children) {
            child.evaluate(evaluation);
            if (bestChildren.isEmpty()) bestChildren.add(child);
            else {
                if (bestChildren.get(0).getEvaluation() >= child.getEvaluation()) {
                    if (bestChildren.get(0).getEvaluation() == child.getEvaluation()) {
                        bestChildren.add(child);
                    } else {
                        bestChildren.clear();
                        bestChildren.add(child);
                    }
                }
            }
        }
        
        if (bestChildren.size() > 1) 
            return bestChildren.get(new Random().nextInt(bestChildren.size()));

        return bestChildren.get(0);
    }

    public double evaluate(EightPuzzleNode node) {
        double h1Total = 0;
        double h2Total = 0;
        int h3Total = 0;
        int h4Total = 0;

        int[] blankPiece = node.getPositionOf(0);
        for (int i = 0 ; i < size ; i++)
            for (int j = 0 ; j < size ; j++)
                if (i != blankPiece[0] || j != blankPiece[1]) {
                    int piece = node.getPieceOf(i, j); //Piece of current position
                    int[] gpp = goalNode.getPositionOf(piece); //Goal piece position
                    
                    //Manhattan distance
                    h1Total += new MinkowskiDistance(1).d(
                        new int[] {i, j}, // Current piece position
                        gpp //Goal piece position
                    );

                    //Euclidean distance
                    h2Total += new MinkowskiDistance(2).d(
                        new int[] {i, j}, // Current piece position
                        gpp //Goal piece position
                    );

                    //Incorrect pieces and Pieces out of row + pieces out of column
                    if (!(i == gpp[0] && j == gpp[1])) {
                        h3Total++;
                        if (i != gpp[0]) h4Total++; //Out of row
                        if (j != gpp[1]) h4Total++; //Out of column
                    } 
                }
        return (H1_WEIGHT * h1Total / H1_MAX) + 
                (H2_WEIGHT * h2Total / H2_MAX ) + 
                (H3_WEIGHT * h3Total / H3_MAX) + 
                (H4_WEIGHT * h4Total / H4_MAX);
    }
}

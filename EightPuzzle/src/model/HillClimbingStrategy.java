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

        this.initialNode.evaluation = evaluate(initialNode);
        this.goalNode.evaluation = evaluate(goalNode);
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
            if (!currentNode.equals(goalNode)) {
                //Paso 2: Generar sucesores y seleccionar el mejor sucesor
                var bestChild = bestChild(generateChildren(currentNode));
                
                //Paso 3: Si el mejor hijo es mejor al padre, ir al paso 2
                if (bestChild.evaluation <= currentNode.evaluation ) {
                    route.add(bestChild);
                    currentNode = bestChild;
                } else band = false;//En cualquier otro caso terminar
            } else band = false;
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
            var child = node.move(movement);
            if (child != null) children.add(child);
        }

        return children.toArray(new EightPuzzleNode[children.size()]);
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

    public double evaluate(EightPuzzleNode node) {
        double h1Total = 0;
        double h2Total = 0;
        int h3Total = 0;
        int h4Total = 0;
        //int h4_1Total = 0;
        //int h4_2Total = 0;

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

                    /*//Incorrect pieces
                    if (piece != goalNode.getPieceOf(i, j))  h3Total++; //First way
                    //if (!(i == gpp[0] && j == gpp[1])) h3Total++; //Second way

                    //Pieces out of row + pieces out of column
                    if (i != gpp[0]) h4_1Total++; //Out of row
                    if (j != gpp[1]) h4_2Total++; //Out of column*/

                    //Incorrect pieces and Pieces out of row + pieces out of column
                    if (!(i == gpp[0] && j == gpp[1])) {
                        h3Total++;
                        if (i != gpp[0]) h4Total++; //Out of row
                        if (j != gpp[1]) h4Total++; //Out of column
                    } 
                }
        /*System.out.println();
        System.out.println("h3 total: " + h3Total);
        System.out.println("Out of row total: " + h4_1Total);
        System.out.println("Out of column total: "  + h4_2Total);
        h4Total = h4_1Total + h4_2Total;
        System.out.println("h4 total: " + h4Total);*/
        
         /*System.out.println("Manhattan: " + mTotal + " Norm: " + mTotal / 24 + " Weight: " + 0.4 * mTotal / 24.0);
         System.out.println("Euclides: " + eTotal + " Norm: " + eTotal / 8.4 + " Weight: " + 0.3 * eTotal / 8.4);
         System.out.println("Incorrectas: " + wTotal + " Norm: " + wTotal / 8.0 + " Weight: " + 0.2 * wTotal / 8.0);
         System.out.println("Correctas: " + cTotal + " Norm: " + cTotal / 8.0 + " Weight: " + 0.1 * cTotal / 8.0);
         System.out.println();*/

        return (0.4 * h1Total / 24.0) + (0.2 * h2Total / 8.4 ) + (0.1 * h3Total / 8.0) + (0.2 * h4Total / 16.0);
        //return (0.4 * mTotal / 24.0) + (0.3 * eTotal / 8.4 ) + (0.2 * wTotal / 8.0);
        //return (mTotal / 24.0) + (eTotal / 8.4 ) + (wTotal / 8.0);
    }
}

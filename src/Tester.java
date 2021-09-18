package src;

public class Tester {
    public static void printNodeInfo(PuzzleStateNode node) {
        System.out.println(node);
        System.out.println("Node Level: " + node.getLevel());
        System.out.println("Node Evaluation: " + node.getEvaluation());
    }
}

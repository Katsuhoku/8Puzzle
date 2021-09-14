package src;

import java.util.ArrayList;

public class PuzzleStateInstrumentation {
    public static void printObjectSize(Object object) {
        System.out.println("Object type: " + object.getClass() + ", size: " + InstrumentationAgent.getObjectSize(object) + " B");
    }

    public static void test(PuzzleStateNode root) {
        PuzzleStateNode down = root.genChild(PuzzleRules.DOWN);
        PuzzleStateNode left = root.genChild(PuzzleRules.LEFT);
        PuzzleStateNode right = root.genChild(PuzzleRules.RIGHT);

        ArrayList<PuzzleStateNode> rootChildren = new ArrayList<>();
        rootChildren.add(down);
        rootChildren.add(left);
        rootChildren.add(right);

        root.setCurrentChildren(rootChildren);

        PuzzleStateNode down2 = down.genChild(PuzzleRules.DOWN);
        PuzzleStateNode left2 = down.genChild(PuzzleRules.LEFT);
        PuzzleStateNode right2 = down.genChild(PuzzleRules.RIGHT);

        ArrayList<PuzzleStateNode> downChildren = new ArrayList<>();
        downChildren.add(down2);
        downChildren.add(left2);
        downChildren.add(right2);

        int x = 0;

        System.out.println("| Node Size |");
        printObjectSize(down);
        System.out.println("| State Matrix Size |");
        System.out.println((InstrumentationAgent.getObjectSize(x) * Math.pow(PuzzleRules.boardSize, 2)) + " B");
        System.out.println("| Children Array Size |");
        printObjectSize(downChildren);
    }
}

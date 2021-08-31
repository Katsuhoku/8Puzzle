package EightPuzzle.src.model;

import java.util.Arrays;
import java.util.Random;

public class EightPuzzleNodeGenerator {
    private final EightPuzzleMovement[] movements;
    private final EightPuzzleNode goalNode;
    private final int movementsCount;

    public EightPuzzleNodeGenerator(EightPuzzleNode goalNode, int movementsCount) throws IllegalArgumentException {
        if (movementsCount < 1) throw new IllegalArgumentException("movementsCount cannot be lower than 1");
        
        this.goalNode = goalNode;
        this.movementsCount = movementsCount;
        movements = Arrays.copyOfRange(EightPuzzleMovement.values(), 1, EightPuzzleMovement.values().length);
    }

    public EightPuzzleNode generate() {
        var random = new Random();
        EightPuzzleNode finalNode;
        
        while ((finalNode = goalNode.move(movements[random.nextInt(movements.length)])) == null);
        
        int i = 1;
        while (i < movementsCount) {
            EightPuzzleNode auxNode;
            if ((auxNode = finalNode.move(movements[random.nextInt(movements.length)])) != null) {
                finalNode = auxNode;
                i++;
            }
        }
        return finalNode;
    }
}

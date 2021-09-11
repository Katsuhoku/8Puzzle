package EightPuzzle.src.model;

import java.util.Arrays;
import java.util.Random;

public class EightPuzzleNodeGenerator {
    private final EightPuzzleMovement[] movements;
    private final EightPuzzleNode goalNode;
    private final int maxMovementsCount;

    public EightPuzzleNodeGenerator(EightPuzzleNode goalNode, int maxMovementsCount) throws IllegalArgumentException {
        if (maxMovementsCount < 1) throw new IllegalArgumentException("maxMovementsCount cannot be less than 1");
        
        this.goalNode = goalNode;
        this.maxMovementsCount = maxMovementsCount;
        movements = Arrays.copyOfRange(EightPuzzleMovement.values(), 1, EightPuzzleMovement.values().length);
    }

    public EightPuzzleNode generate() {
        var random = new Random();
        EightPuzzleNode finalNode;
        
        while ((finalNode = goalNode.move(movements[random.nextInt(movements.length)])) == null);
        
        int i = 1;
        while (i < maxMovementsCount) {
            EightPuzzleNode auxNode;
            if ((auxNode = finalNode.move(movements[random.nextInt(movements.length)])) != null) {
                finalNode = auxNode;
                i++;
            }
        }
        return finalNode;
    }

    public int getMaxMovementsCount() { return maxMovementsCount; }
}

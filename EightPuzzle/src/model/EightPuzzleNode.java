package EightPuzzle.src.model;

import java.util.Objects;

public class EightPuzzleNode{
    //Node information
    private final int[][] gameBoard;
    private final EightPuzzleMovement movement;
    public double evaluation;

    public EightPuzzleNode(int[][] gameBoard, EightPuzzleMovement movement) {
        this.gameBoard = gameBoard;
        this.movement = movement;
    }
    
    public EightPuzzleNode(int[][] gameBoard) {
        this(gameBoard, EightPuzzleMovement.NONE);
    }

    public int getPieceOf(int x, int y) { return gameBoard[x][y]; }
    public EightPuzzleMovement getMovement() { return movement ;}
    public int[] getPositionOf(int piece) {
        int[] positions = new int[2];
        outerLoop:
        for (int i = 0 ; i < gameBoard.length ; i++)
            for (int j = 0 ; j < gameBoard[i].length ; j++)
                if (gameBoard[i][j] == piece) {
                    positions[0] = i; positions[1] = j;
                    break outerLoop;
                }
        return positions;
    }

    public EightPuzzleNode move(EightPuzzleMovement movement) {
        int[] bpp = getPositionOf(0); //Blank Piece Position

        switch (movement) {
            case UP: 
                if (bpp[0] > 0) 
                    return new EightPuzzleNode(change(bpp, new int[] { bpp[0]--, bpp[1] }), movement);
                break;
            case DOWN: 
                if (bpp[0] < gameBoard.length - 1) 
                    return new EightPuzzleNode(change(bpp, new int[] { bpp[0]++, bpp[1] }), movement);
                break;
            case LEFT: 
                if (bpp[1] > 0) 
                    return new EightPuzzleNode(change(bpp, new int[] { bpp[0], bpp[1]-- }), movement);
                break;
            case RIGHT: 
                if (bpp[1] < gameBoard.length - 1) 
                    return new EightPuzzleNode(change(bpp, new int[] { bpp[0], bpp[1]++ }), movement);
                break;
            default: return null;
        }
        return null;
    }

    private int[][] change(int[] pos1, int[] pos2) {
        var gameBoard = copyGameBoard();
        int aux = gameBoard[pos1[0]][pos1[1]];
        gameBoard[pos1[0]][pos1[1]] = gameBoard[pos2[0]][pos2[1]];
        gameBoard[pos2[0]][pos2[1]] = aux;
        return gameBoard;
    }

    private int[][] copyGameBoard() {
        int size = gameBoard.length;
        int[][] copy = new int[size][];
        for (int i = 0 ; i < size ; i++)
            copy[i] = gameBoard[i].clone(); 
        return copy;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof EightPuzzleNode)) return false;

        EightPuzzleNode eightPuzzleNode = (EightPuzzleNode) o;
        for (int i = 0 ; i < gameBoard.length ; i++)
            for (int j = 0 ; j < gameBoard[i].length ; j++)
                if (getPieceOf(i, j) != eightPuzzleNode.getPieceOf(i, j))
                    return false;
        return evaluation == eightPuzzleNode.evaluation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameBoard, evaluation);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("Evaluation: " + this.evaluation + '\n');
        out.append("Movement: " + this.movement);
        for (int[] row : gameBoard) {
            out.append('\n');
            for (int piece : row)
                out.append(piece == 0 ? "-" : piece).append('\t');
        }
        return out.toString();
    }
}

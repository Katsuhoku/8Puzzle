package EightPuzzle.src.model;


public class EightPuzzleNode{
    //Node information
    private final int[][] gameBoard;
    private final EightPuzzleMovement movement;
    public int evaluation;

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

    public int[][] change(int[] pos1, int[] pos2) {
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

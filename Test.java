import java.io.*;
import java.util.Queue;

public class Test {
    public static final String filename = "./input.txt";
    public static final String outfile = "./output.txt";
    public static void main(String[] args) throws Exception {
        PuzzleStateNode root = readFile();

        Queue<PuzzleStateNode> solutionSequence = PuzzleRules.findSequence(root, 10);

        PuzzleStateNode aux = null;
        String sequence = "";
        while ((aux = solutionSequence.poll()) != null) {
            sequence += aux.getPreviousMovement() + ",";
        }
        writeFile(sequence);
    }

    private static PuzzleStateNode readFile() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(filename)));

        String st;
        PuzzleRules.boardSize = Integer.parseInt(br.readLine());

        // Lee el tablero inicial
        int[][] initialState = new int[PuzzleRules.boardSize][];
        for (int i = 0; i < PuzzleRules.boardSize; i++) {
            st = br.readLine();
            initialState[i] = new int[PuzzleRules.boardSize];
            for (int j = 0; j < PuzzleRules.boardSize; j++) {
                initialState[i][j] = Integer.parseInt(st.split(",")[j]);
            }
        }

        // Lee el estado resuelto
        // El estado resuelto se guarda de dos formas:
        // 1. Como matriz, representando análogamente el tablero
        // 2. Como vector de coordenadas. Cada índice representa una ficha del tablero
        //    y su valor es una pareja (x,y) representando la coordenada donde se
        //    encuentra en el tablero resuelto.
        int[][] solvedState = new int[PuzzleRules.boardSize][];
        int[][] solvedStateCoordinates = new int[(int) Math.pow(PuzzleRules.boardSize, 2)][];
        for (int i = 0; i < solvedStateCoordinates.length; i++) solvedStateCoordinates[i] = new int[2];

        for (int i = 0; i < PuzzleRules.boardSize; i++) {
            st = br.readLine();
            solvedState[i] = new int[PuzzleRules.boardSize];
            for (int j = 0; j < PuzzleRules.boardSize; j++) {
                int value = Integer.parseInt(st.split(",")[j]);
                solvedState[i][j] = value;
                solvedStateCoordinates[value][0] = i;
                solvedStateCoordinates[value][1] = j;
            }
        }
        PuzzleRules.solvedState = solvedState;
        PuzzleRules.solvedStateCoordinates = solvedStateCoordinates;

        br.close();

        return new PuzzleStateNode(initialState);
    }

    public static void writeFile(String output) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outfile)));
        bw.write(output.substring(0, output.length() - 1));
        bw.close();
    }
}

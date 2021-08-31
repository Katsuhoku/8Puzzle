import java.io.*;
import java.util.Queue;

public class Test {
    public static String filename = "./input.txt";
    public static String outfile = "./output.txt";
    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            filename = args[0];
            outfile = args[1];
        }

        PuzzleStateNode root = readFile();

        Queue<PuzzleStateNode> solutionSequence = PuzzleRules.findSequence(root, 200);

        PuzzleStateNode aux = null;
        String sequence = "";
        while ((aux = solutionSequence.poll()) != null) {
            sequence += aux.getPreviousMovement() + ",";
        }
        sequence = sequence.substring(0, sequence.length() - 1);
        sequence += "\n" + PuzzleRules.bestEvaluation;
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
        PuzzleRules.prepare();

        br.close();

        return new PuzzleStateNode(initialState);
    }

    public static void writeFile(String output) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outfile)));
        bw.write(output);
        bw.close();
    }
}

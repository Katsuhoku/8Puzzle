package src;
import java.io.*;
import java.util.Queue;

public class Main {
    public static String filename = "./input.txt";
    public static String outfile = "./output.txt";
    public static String strategy = "a*";
    public static void main(String[] args) throws Exception {
        if (args.length >= 3) {
            filename = args[0];
            outfile = args[1];
            strategy = args[2];
        }

        PuzzleStateNode root = readFile();

        // Test para el tamaño de los objetos en memoria
        if (strategy.equals("size_test")) {
            PuzzleStateInstrumentation.test(root);
            System.exit(0);
        }

        if (strategy.equals("expansor")) {
            long startTime = System.nanoTime();
            System.out.println("Factor de ramificación promedio: " + PuzzleStateInstrumentation.expandTree(root, Integer.parseInt(args[3])));
            long endTime = System.nanoTime();

            long totalTime = (endTime - startTime) / 1000000;

            System.out.println("Tiempo de finalización: " + totalTime + "ms");
            System.exit(0);
        }

        //System.out.println("Tablero: " + filename);

        Queue<PuzzleStateNode> solutionSequence;
        long startTime = System.nanoTime();
        if (strategy.equals("hill")) solutionSequence = HillClimbingStrategy.findSequence(root, 200);
        else solutionSequence = AStarStrategy.findSequence(root);
        long endTime = System.nanoTime();

        long totalTime = (endTime - startTime) / 1000000;
        //System.out.println("Tiempo de finalización: " + totalTime + "ms");
        //System.out.println("Movimientos requeridos: " + solutionSequence.size());

        if (solutionSequence.size() > 0 && solutionSequence.peek().getPreviousMovement() == PuzzleRules.START) {
            writeFile("-\n" + root.getEvaluation());
        }
        else {
            PuzzleStateNode aux = null;
            String sequence = "";
            while ((aux = solutionSequence.poll()) != null) {
                sequence += aux.getPreviousMovement();
                if (solutionSequence.peek() != null) sequence += ",";
                //System.out.println(aux);
            }

            System.out.println(sequence);
            
            if (strategy.equals("hill")) sequence += "\n" + HillClimbingStrategy.bestEvaluation;
            writeFile(sequence);
        }
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

package EightPuzzle.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import EightPuzzle.src.model.EightPuzzleNode;
import EightPuzzle.src.model.EightPuzzleStrategy;
import EightPuzzle.src.model.HillClimbingStrategy;

public class App {
    private static int size;
    private static EightPuzzleNode initialNode;
    private static EightPuzzleNode goalNode;

    public static void main(String[] args) throws Exception {
        //var r = new EightPuzzleReader(args[0]);
        read(args[0]);
        EightPuzzleStrategy algorithm = new HillClimbingStrategy(initialNode, goalNode, size);
        
        long startTime = System.currentTimeMillis(); 
        algorithm.run();
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime));
        write(args[1], algorithm.getRoute());  
    }


    private static void read(String pathName) throws FileNotFoundException {
        Scanner scanner;
        //scanner = new Scanner(new File(pathName));
        scanner = new Scanner(new File(System.getProperty("user.dir") + pathName));
        size = scanner.nextInt();
        initialNode = new EightPuzzleNode(readGameBoard(scanner));
        goalNode = new EightPuzzleNode(readGameBoard(scanner));
        scanner.close();
    }

    private static int[][] readGameBoard(Scanner scanner) {
        int[][] gameBoard = new int[size][size];

        for (int i = 0, j ; i < size ; i++){
            j = 0;
            while (j < size) {
                if (scanner.hasNextInt()) {
                    gameBoard[i][j] = scanner.nextInt();
                    j++;
                } else
                    scanner.next();
            }
        }
        return gameBoard;
    }

    private static void write(String pathName, EightPuzzleNode[] route) throws IOException{
        //PrintWriter pw = new PrintWriter(new FileWriter(pathName));
        PrintWriter pw = new PrintWriter(new FileWriter(System.getProperty("user.dir") + pathName));
        int i = 0;
        for (EightPuzzleNode node : route){
            pw.println("#: " + i++);
            pw.println(node.toString());
            pw.println();
        }
        pw.close();
    }
}

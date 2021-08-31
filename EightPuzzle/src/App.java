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
    private static String ext = ".txt"; 

    public static void main(String[] args) throws Exception {
        //var r = new EightPuzzleReader(args[0]);
        /*read(args[0]);
        EightPuzzleStrategy algorithm = new HillClimbingStrategy(initialNode, goalNode, size);
        
        long startTime = System.currentTimeMillis(); 
        algorithm.run();
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime));
        write(args[1], algorithm.getRoute()); 
        write(args[2], algorithm.getRoute(), true);*/

        for (int i = 1 ; i <= 20 ; i++) {
            read(args[0] + i + ext);
            EightPuzzleStrategy algorithm = new HillClimbingStrategy(initialNode, goalNode, size);
            long startTime = System.currentTimeMillis(); 
            algorithm.run();
            long endTime = System.currentTimeMillis();
            System.out.println("Execution time: " + (endTime - startTime));
            write(args[1] + i + ext, algorithm.getRoute()); 
            write(args[2] + i + ext, algorithm.getRoute(), true);
        }   
    }


    private static void read(String pathName) throws FileNotFoundException {
        Scanner scanner;
        //scanner = new Scanner(new File(pathName));
        scanner = new Scanner(new File(System.getProperty("user.dir") + pathName));
        size = scanner.nextInt(); scanner.nextLine();
        initialNode = new EightPuzzleNode(readGameBoard(scanner));
        goalNode = new EightPuzzleNode(readGameBoard(scanner));
        scanner.close();
    }

    private static int[][] readGameBoard(Scanner scanner) {
        int[][] gameBoard = new int[size][size];

        for (int i = 0 ; i < size ; i++) {
            var s = scanner.nextLine().split(",");
            for (int j = 0 ; j < size ; j++)
                gameBoard[i][j] = Integer.parseInt(s[j]);
        }
        return gameBoard;
    }

    private static void write(String pathName, EightPuzzleNode[] route) throws IOException{
        write(pathName, route, false);
    }

    private static void write(String pathName, EightPuzzleNode[] route, boolean verbose) throws IOException{
        //PrintWriter pw = new PrintWriter(new FileWriter(pathName));
        PrintWriter pw = new PrintWriter(new FileWriter(System.getProperty("user.dir") + pathName));
        for (int i = 0 ; i < route.length ; i++) {
            if (verbose) { 
                if ( i != 0) pw.println();
                pw.println("#: " + i);
                pw.println(route[i].toString());
            } else {
                if (i == 0) continue;
                pw.print(route[i].getMovement().toChar());
                if (i < route.length - 1)
                    pw.print(", ");
            }
        }
        pw.close();
    }
}

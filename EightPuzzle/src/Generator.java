package EightPuzzle.src;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import EightPuzzle.src.model.EightPuzzleNode;
import EightPuzzle.src.model.EightPuzzleNodeGenerator;

public class Generator {
    private static final String pathName = "/EightPuzzle/resources/";
    private static final String filename = "EightPuzzle_In_";
    private static final String ext = ".txt";


    private static final int size = 3;
    private static final EightPuzzleNode goalNode = new EightPuzzleNode(new int[][] {
        {1, 2 ,3},
        {4, 5, 6},
        {7, 8, 0}
    });

    public static void main(String[] args) throws IOException {
        ArrayList<EightPuzzleNode> nodes = new ArrayList<>(15);

        EightPuzzleNodeGenerator epng5 = new EightPuzzleNodeGenerator(goalNode, 5);
        EightPuzzleNodeGenerator epng10 = new EightPuzzleNodeGenerator(goalNode, 10);
        EightPuzzleNodeGenerator epng15 = new EightPuzzleNodeGenerator(goalNode, 15);
        EightPuzzleNodeGenerator epng20 = new EightPuzzleNodeGenerator(goalNode, 20);
        for (int i = 0 ; i < 5 ; i++) nodes.add(epng5.generate());
        for (int i = 0 ; i < 5 ; i++) nodes.add(epng10.generate());
        for (int i = 0 ; i < 5 ; i++) nodes.add(epng15.generate());
        for (int i = 0 ; i < 5 ; i++) nodes.add(epng20.generate());
        
        for (int i = 1 ; i <= nodes.size() ; i++) write(nodes.get(i - 1), i);
    }

    public static void write(EightPuzzleNode node, int number) throws IOException{
        PrintWriter pw = new PrintWriter(new FileWriter(System.getProperty("user.dir") + pathName + filename + number + ext));
        pw.println(size);
        
        for (int x = 0 ; x < size ; x++) {
            for (int y = 0 ; y < size ; y++) {
                pw.print(node.getPieceOf(x, y));
                if (y < size - 1) pw.write(',');
            }
            pw.println();
        }

        //Goal
        for (int x = 0 ; x < size ; x++) {
            for (int y = 0 ; y < size ; y++) {
                pw.print(goalNode.getPieceOf(x, y));
                if (y < size - 1) pw.write(',');
            }
            pw.println();
        }
        pw.close();

    }
    
}

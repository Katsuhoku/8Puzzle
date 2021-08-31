package EightPuzzle.src;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import EightPuzzle.src.model.EightPuzzleNode;
import EightPuzzle.src.model.EightPuzzleNodeGenerator;

public class Generator {
    public static final String pathName = "/EightPuzzle/resources/";
    public static final String filename = "EightPuzzle_In_";
    public static final String ext = ".txt";


    //Asignación hardcodeada
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
        
        generate(nodes, epng5);
        generate(nodes, epng10);
        generate(nodes, epng15);
        generate(nodes, epng20);
        
        for (int i = 1 ; i <= nodes.size() ; i++) write(nodes.get(i - 1), i);
    }

    private static void generate(ArrayList<EightPuzzleNode> nodes, EightPuzzleNodeGenerator generator) {
        for (int i = 0, j = 5 ; i < generator.getMaxMovementsCount() && j > 0; ) {
            EightPuzzleNode newNode = generator.generate();
            if (!nodes.contains(newNode)){
                nodes.add(newNode);
                i++;
            } else j--;
        }
    }

    private static void write(EightPuzzleNode node, int number) throws IOException{
        PrintWriter pw = new PrintWriter(new FileWriter(System.getProperty("user.dir") + pathName + filename + number + ext));
        pw.println(size);
        writeGameboard(node, pw);
        writeGameboard(goalNode, pw); //Goal gameboard
        pw.close();
    }

    private static void writeGameboard(EightPuzzleNode node, PrintWriter pw) {
        for (int x = 0 ; x < size ; x++) {
            for (int y = 0 ; y < size ; y++) {
                pw.print(node.getPieceOf(x, y));
                if (y < size - 1) pw.write(',');
            }
            pw.println();
        }
    }  
}

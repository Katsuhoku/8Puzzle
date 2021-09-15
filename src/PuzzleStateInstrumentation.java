package src;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PuzzleStateInstrumentation {
    
    // Lista de nodos hijos que no se repiten y pueden ser agregados
    private static class Result {
        private final ArrayList<PuzzleStateNode> toAdd;
        public Result(ArrayList<PuzzleStateNode> toAdd) {
            this.toAdd = toAdd;
        }
    }

    public static void printObjectSize(Object object) {
        System.out.println("Object type: " + object.getClass() + ", size: " + InstrumentationAgent.getObjectSize(object) + " B");
    }

    public static void test(PuzzleStateNode root) {
        PuzzleStateNode down = root.genChild(PuzzleRules.DOWN);
        PuzzleStateNode left = root.genChild(PuzzleRules.LEFT);
        PuzzleStateNode right = root.genChild(PuzzleRules.RIGHT);

        ArrayList<PuzzleStateNode> rootChildren = new ArrayList<>();
        rootChildren.add(down);
        rootChildren.add(left);
        rootChildren.add(right);

        root.setCurrentChildren(rootChildren);

        PuzzleStateNode down2 = down.genChild(PuzzleRules.DOWN);
        PuzzleStateNode left2 = down.genChild(PuzzleRules.LEFT);
        PuzzleStateNode right2 = down.genChild(PuzzleRules.RIGHT);

        ArrayList<PuzzleStateNode> downChildren = new ArrayList<>();
        downChildren.add(down2);
        downChildren.add(left2);
        downChildren.add(right2);

        int x = 0;

        System.out.println("| Node Size |");
        printObjectSize(down);
        System.out.println("| State Matrix Size |");
        System.out.println((InstrumentationAgent.getObjectSize(x) * Math.pow(PuzzleRules.boardSize, 2)) + " B");
        System.out.println("| Children Array Size |");
        printObjectSize(downChildren);
    }

    public static Result expand(PuzzleStateNode node, ArrayList<PuzzleStateNode> currentLevel, ArrayList<PuzzleStateNode> closed) throws InterruptedException {
        ArrayList<PuzzleStateNode> toAdd = new ArrayList<>();

        PuzzleStateNode[] children = node.genAllChildren();
        for (PuzzleStateNode child : children) {
            boolean band = false; // Se asume que el nodo es un estado no explorado

            for (PuzzleStateNode explored : closed) {
                if (child.equals(explored)) {
                    band = true;
                    break;
                }
            }

            if (!band) for (PuzzleStateNode parent : currentLevel) {
                if(child.equals(parent)) {
                    band = true;
                    break;
                }
            }

            if (!band) toAdd.add(child);
        }

        return new Result(toAdd); // Hijos expandidos que sí deben ser añadidos (no se repiten)
    }

    public static float expandTree(PuzzleStateNode root) throws InterruptedException, ExecutionException {
        int depth = 14;
        float meanChildren = 0;
        ArrayList<PuzzleStateNode> closed = new ArrayList<>();
        ArrayList<PuzzleStateNode> currentLevel = new ArrayList<>();
        
        currentLevel.add(root);
        for (int i = 0; i < depth; i++) {
            ArrayList<PuzzleStateNode> nextLevel = new ArrayList<>();

            // Crea las tareas a paralelizar
            // Cada tarea se encarga de expandir un nodo del nivel actual,
            // verificar que sus hijos no se repitan en los niveles anteriores
            // y guardar aquellos que cumplen la condición en un ArrayList (Result)
            List<Callable<Result>> tasks = new ArrayList<>();
            for (PuzzleStateNode node : currentLevel) {
                Callable<Result> c = new Callable<PuzzleStateInstrumentation.Result>(){
                    @Override
                    public Result call() throws Exception {
                        return expand(node, currentLevel, closed);
                    }
                };
                tasks.add(c);
            }

            ExecutorService exec = Executors.newCachedThreadPool();
            try {
                List<Future<Result>> results = exec.invokeAll(tasks);
                for (Future<Result> fr : results) {
                    nextLevel.addAll(fr.get().toAdd);
                }
            } finally {
                exec.shutdown();
            }

            System.out.println(closed.size());

            meanChildren += nextLevel.size() / currentLevel.size();

            closed.addAll(currentLevel);
            currentLevel.clear();
            currentLevel.addAll(nextLevel);
            nextLevel.clear();
            System.gc();
        }

        return meanChildren / depth;
    }
}

package src;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class AStarStrategy {
    public int currentSubtree = 1;

    private PuzzleStateNode expand(ArrayList<PuzzleStateNode> open, ArrayList<PuzzleStateNode> closed, ArrayList<PuzzleStateNode> limit) {
        int restriction = (int) (Math.pow(PuzzleRules.branch[PuzzleRules.boardSize - 3], PuzzleRules.maxDepth - PuzzleRules.treesNeeded[PuzzleRules.boardSize - 3] * open.get(0).h()));
        System.out.println("Subroot evaluation: " + open.get(0).h());
        System.out.println("Restricción: " + restriction);
        //Tester.printNodeInfo(open.get(0));
        while (open.size() > 0) {
            PuzzleStateNode X = open.remove(0);

            if (X.h() == 0) return X; // El nodo es la solución
            else if (X.getLevel() == PuzzleRules.maxDepth * currentSubtree) {
                // Nodo en el límite, no se puede expandir más
                for (int i = 0; i < limit.size(); i++) {
                    if (X.getEvaluation() <= limit.get(i).getEvaluation()) {
                        limit.add(i, X);
                        break;
                    }
                }
                if (!limit.contains(X)) {
                    limit.add(X);
                    System.out.println("[Update] Limit size: " + limit.size());
                }

                if (limit.size() == restriction) break;
            }
            else {
                PuzzleStateNode[] children = X.genAllChildren();
                for (PuzzleStateNode child : children) {
                    boolean found = false;
                    PuzzleStateNode replace = null;

                    // Verifica la existencia del nodo en open
                    for (PuzzleStateNode openNode : open) {
                        if (child.equals(openNode)) {
                            found = true;
                            if (child.getEvaluation() < openNode.getEvaluation()) replace = openNode;
                            break;
                        }
                    }

                    if (!found) {
                        // Verifica la existencia del nodo en closed
                        for (PuzzleStateNode closedNode : closed) {
                            if (child.equals(closedNode)) {
                                found = true;
                                if (child.getEvaluation() < closedNode.getEvaluation()) replace = closedNode;
                                break;
                            }
                        }

                        if (!found) {
                            // Verifica la existencia del nodo en limit
                            for (PuzzleStateNode limitNode : limit) {
                                if (child.equals(limitNode)) {
                                    found = true;
                                    if (child.getEvaluation() < limitNode.getEvaluation()) replace = limitNode;
                                    break;
                                }
                            }
                            if (replace != null) {
                                // El nodo es un duplicado de otro en el límite, pero tiene mejor evaluación
                                limit.remove(replace);
                                if (replace.getFather() != null) replace.getFather().getCurrentChildren().remove(replace);
                            }
                        } else if (replace != null) {
                            // El nodo es un duplicado de otro en closed, pero tiene mejor evaluación
                            if (replace.getFather() != null) replace.getFather().getCurrentChildren().remove(replace);

                            // Se elimina todo el subárbol generado por este nodo
                            ArrayList<PuzzleStateNode> toRemove = deleteChildren(replace);
                            closed.removeAll(toRemove);
                            open.removeAll(toRemove);
                            limit.removeAll(toRemove);

                            toRemove.clear();
                        }
                    } else if (replace != null) {
                        // El nodo es un duplicado de otro en open, pero tiene mejor evaluación
                        open.remove(replace);
                        if (replace.getFather() != null) replace.getFather().getCurrentChildren().remove(replace);
                    }

                    // Se agrega el nodo hijo a open si no fue encontrado o si tomó el lugar de otro nodo
                    if (!found || replace != null) {
                        for (int i = 0; i < open.size(); i++) {
                            if (child.getEvaluation() <= open.get(i).getEvaluation()) {
                                open.add(i, child);
                                break;
                            }
                        }
                        if (!open.contains(child)) open.add(child);
                        X.addChild(child);
                    }
                }
                open.remove(X);
                closed.add(X);
            }
        }

        // Si open se queda vacío significa que no encontró la solución
        // en el subárbol. Retorna null
        return null;
    }

    public PuzzleStateNode exploreSubtree(PuzzleStateNode root) {
        PuzzleStateNode solution = null;

        ArrayList<PuzzleStateNode> open = new ArrayList<>();
        ArrayList<PuzzleStateNode> closed = new ArrayList<>();
        ArrayList<PuzzleStateNode> limit = new ArrayList<>();

        open.add(root);

        // Expande subárboles de maxDepth de profundiad tanto como sea necesario
        // La expansión finalizará si se encuentra la solución o si ya no existen
        // nodos en el límite y no es posible expandir más
        while (true) {
            
            solution = expand(open, closed, limit);
            
            System.out.println("Subárbol: " + currentSubtree);
            System.out.println("Open\tClosed\tLimit");
            System.out.println(open.size() + "\t" + closed.size() + "\t" + limit.size());

            if (solution != null || limit.size() == 0) break;
            
            open.clear();
            closed.clear();

            open.add(limit.get(0));
            limit.clear();
            currentSubtree++;
        }

        return solution;
    }

    public static Queue<PuzzleStateNode> findSequence(PuzzleStateNode root) {
        Queue<PuzzleStateNode> nodeSequence = new LinkedList<>();

        ArrayList<PuzzleStateNode> toExpand = new ArrayList<>();
        PuzzleStateNode[] children = root.genAllChildren();
        for (PuzzleStateNode child : children) {
            PuzzleStateNode[] grandchildren = child.genAllChildren();
            for (PuzzleStateNode grandchild : grandchildren) if (!grandchild.equals(root)) toExpand.add(grandchild);
        }

        ArrayList<AStarAgent> searchThreads = new ArrayList<>();
        SolutionResource solutionResource = new SolutionResource(searchThreads);
        for (PuzzleStateNode subroot : toExpand) {
            //Tester.printNodeInfo(subroot);
            searchThreads.add(new AStarAgent(subroot, solutionResource));
        }

        for (AStarAgent thread : searchThreads) thread.start();
        for (AStarAgent thread : searchThreads) {
            try {
                thread.join();
            } catch (InterruptedException ie) {
                // El hilo fue interrumpido. El hilo interruptor ha encontrado
                // la solución y no se requiere seguir explorando
                System.out.println("Thread #" +thread.getID() + ": Searching but interrupted");
                return nodeSequence;
            }
        }

        Stack<PuzzleStateNode> stackSequence = new Stack<>();
        PuzzleStateNode aux = solutionResource.getSolution();
        stackSequence.push(aux);
        while ((aux = aux.getFather()) != null) {
            if (aux.getFather() != null) stackSequence.push(aux);
        }

        try {
            while (true) nodeSequence.offer(stackSequence.pop());
        } catch (EmptyStackException ese) {

        }

        return nodeSequence;
    }

    private static ArrayList<PuzzleStateNode> deleteChildren(PuzzleStateNode root) {
        ArrayList<PuzzleStateNode> pool = new ArrayList<>();

        deleteChildren(root, pool);

        return pool;
    }

    private static void deleteChildren (PuzzleStateNode root, ArrayList<PuzzleStateNode> pool) {
        if (root.getCurrentChildren() == null || root.getCurrentChildren().size() == 0) return;

        for (PuzzleStateNode child : root.getCurrentChildren()) {
            deleteChildren(child);
            child.setFather(null);
            pool.add(child);
        }
        root.getCurrentChildren().clear();
    }
}

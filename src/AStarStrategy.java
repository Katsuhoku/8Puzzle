package src;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class AStarStrategy {

    public static int currentSubtree = 1;

    private static PuzzleStateNode expand(ArrayList<PuzzleStateNode> open, ArrayList<PuzzleStateNode> closed) {
        while (open.size() > 0) {
            PuzzleStateNode X = open.remove(0);

            if (X.h() == 0 || X.getLevel() == PuzzleRules.maxDepth * currentSubtree) return X;
            else if (X.getLevel() <= PuzzleRules.maxMov[PuzzleRules.boardSize - 3]) {
                PuzzleStateNode[] children = X.genAllChildren();
                for (PuzzleStateNode child : children) {
                    boolean found = false;
                    PuzzleStateNode replace = null;

                    PuzzleStateNode parentAux = child;
                    while ((parentAux = parentAux.getFather()) != null) {
                        if (child.equals(parentAux)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        // check existence in open
                        for (PuzzleStateNode openNode : open) {
                            if (child.equals(openNode)) {
                                found = true;
                                if (child.getEvaluation() < openNode.getEvaluation()) replace = openNode;
                                break;
                            }
                        }

                        if (!found) {
                            // node not found in open, check existence in closed
                            for (PuzzleStateNode closedNode : closed) {
                                if (child.equals(closedNode)) {
                                    found = true;
                                    if (child.getEvaluation() < closedNode.getEvaluation()) replace = closedNode;
                                    break;
                                }
                            }

                            if (replace != null) {
                                // Node exists in closed, but has better evaluation
                                // Remove node from closed and from father's children
                                if (replace.getFather() != null) replace.getFather().getCurrentChildren().remove(replace);

                                // Delete children
                                ArrayList<PuzzleStateNode> toRemove = deleteChildren(replace);
                                closed.removeAll(toRemove);
                                open.removeAll(toRemove);

                                toRemove.clear();
                            }
                        } else if (replace != null) {
                            // Node exists already in open, but has better evaluation
                            // Remove node from open and from father's children
                            open.remove(replace);
                            if (replace.getFather() != null) replace.getFather().getCurrentChildren().remove(replace);
                        }

                        // append to open
                        // Insertion sort (priority to new nodes)
                        // Insertion only if state wasn't found, or was found but has better evaluation
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
                }
                open.remove(X);
                closed.add(X);
            }
        }

        // Si open se queda vacío significa que no encontró la solución
        // en el subárbol. Retorna null
        return null;
    }

    public static Queue<PuzzleStateNode> findSequence(PuzzleStateNode root) {
        Queue<PuzzleStateNode> nodeSequence = new LinkedList<>();
        PuzzleStateNode solution = null;

        ArrayList<PuzzleStateNode> open = new ArrayList<>();
        ArrayList<PuzzleStateNode> closed = new ArrayList<>();

        open.add(root);

        // Expande subárboles de maxDepth de profundiad tanto como sea necesario
        // La expansión finalizará si se encuentra la solución o si ya no existen
        // nodos en el límite y no es posible expandir más.
        while (true) {
            System.out.println("Subárbol: " + currentSubtree);
            System.out.println("Raíz: " + open.get(0).h());

            solution = expand(open, closed);

            System.out.println("Open\tClosed");
            System.out.println(open.size() + "\t" + closed.size());

            if (solution.h() == 0.0) break;
            
            open.clear();
            closed.clear();
            open.add(solution);

            currentSubtree++;
        }

        if (solution != null) {
            Stack<PuzzleStateNode> tempSequence = new Stack<>();

            PuzzleStateNode aux = solution;
            tempSequence.push(aux);
            while ((aux = aux.getFather()) != null) {
                if (aux.getFather() != null) tempSequence.push(aux);
            }
            try {
                while (true) nodeSequence.offer(tempSequence.pop());
            } catch (Exception e) {
                
            }
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

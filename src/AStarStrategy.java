package src;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class AStarStrategy {
    private static int maxDepth = 20;

    private static PuzzleStateNode expand(ArrayList<PuzzleStateNode> open, ArrayList<PuzzleStateNode> closed, ArrayList<PuzzleStateNode> limit) {
        while (open.size() > 0) {
            PuzzleStateNode X = open.remove(0);

            if (X.getEvaluation() - X.getLevel() == 0) return X;
            else if (X.getLevel() == maxDepth) {
                // Insertion Sort
                for (int i = 0; i < limit.size(); i++) {
                    if (X.getEvaluation() <= limit.get(i).getEvaluation()) {
                        limit.add(i, X);
                    }
                }
            }
            else {
                PuzzleStateNode[] children = X.genAllChildren();
                for (PuzzleStateNode child : children) {
                    boolean found = false;
                    PuzzleStateNode replace = null;

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

                        if (!found) {
                            // node not found in open nor closed, check existence in limit
                            for (PuzzleStateNode limitNode : limit) {
                                if (child.equals(limitNode)) {
                                    found = true;
                                    if (child.getEvaluation() < limitNode.getEvaluation()) replace = limitNode;
                                    break;
                                }
                            }
                            if (replace != null) {
                                // Node exists in limit, but has better evaluation
                                limit.remove(replace);
                                replace.getFather().getCurrentChildren().remove(replace);
                            }
                        } else if (replace != null) {
                            // Node exists in closed, but has better evaluation
                            // Remove node from closed and from father's children
                            replace.getFather().getCurrentChildren().remove(replace);

                            // Delete children
                            ArrayList<PuzzleStateNode> toRemove = deleteChildren(replace);
                            closed.removeAll(toRemove);
                            open.removeAll(toRemove);
                            limit.removeAll(toRemove);

                            toRemove.clear();
                        }
                    } else if (replace != null) {
                        // Node exists already in open, but has better evaluation
                        // Remove node from open and from father's children
                        open.remove(replace);
                        replace.getFather().getCurrentChildren().remove(replace);
                    }

                    // append to open
                    // Insertion sort (priority to new nodes)
                    // Insertion only if state wasn't found, or was found but has better evaluation
                    if (!found || replace != null) for (int i = 0; i < open.size(); i++) {
                        // Note: Child is automatically evaluated when created
                        if (child.getEvaluation() <= open.get(i).getEvaluation()) {
                            open.add(i, child);
                        }
                        X.addChild(child);
                    }
                }
                closed.add(X);
            }
        }

        // Si open se queda vacío significa que no encontró la solución
        // en el subárbol. Retorna null
        return null;
    }

    public static Queue<PuzzleStateNode> findSequence(PuzzleStateNode root) {
        Deque<PuzzleStateNode> nodeSequence = new LinkedList<>();
        PuzzleStateNode solution = null;

        switch (PuzzleRules.boardSize) {
            case 4:
                maxDepth = 20;
                break;
            case 5:
                maxDepth = 17;
                break;
            case 6:
                maxDepth = 16;
                break;
        }

        ArrayList<PuzzleStateNode> open = new ArrayList<>();
        ArrayList<PuzzleStateNode> closed = new ArrayList<>();
        ArrayList<PuzzleStateNode> limit = new ArrayList<>();

        open.add(root);

        // Expande subárboles de maxDepth de profundiad tanto como sea necesario
        // La expansión finalizará si se encuentra la solución o si ya no existen
        // nodos en el límite y no es posible expandir más.
        while (true) {
            solution = expand(open, closed, limit);

            if (solution != null || limit.size() == 0) break;
            
            open.clear();
            closed.clear();

            open.add(limit.get(0));
            limit.clear();
        }

        if (solution != null) {
            PuzzleStateNode auxFather;

            nodeSequence.offerFirst(solution);
            auxFather = solution;
            while ((auxFather = auxFather.getFather()).getFather() != null) {
                nodeSequence.offerFirst(auxFather);
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

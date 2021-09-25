package src;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Estrategia de solución por el algoritmo A* para el problema del N-puzzle.
 * La estrategia está diseñada para tableros de tamaño 4x4, 5x5 y 6x6.
 */

public class AStarStrategy {

    /**
     * Subárbol que se encuentra explorando el algoritmo A*
     */
    public static int currentSubtree = 1;

    /**
     * Algoritmo A* para exploran un solo árbol. El algoritmo recibe como entrada los arreglos
     * open y closed, donde open contiene únicamente la raíz del árbol y closed se encuentra vacío.
     * El algoritmo corresponde al ciclo interno descrito en pseudocódigo, que realiza la expansión
     * de los nodos obtenidos de open, hasta que llegue a la solución. El algoritmo excluye
     * de la expansión aquellos nodos que se repiten en open, en closed o en la secuencia
     * de movimientos (path) generada hasta el momento.
     * @param open Arreglo open, incluyendo únicamente la raíz del (sub)árbol
     * @param closed Arreglo closed, esencialmente una referencia a un arreglo vacío
     * @return Un nodo PuzzleStateNode que es el estado meta si lo encuentra, o el mejor nodo en el
     * horizonte limitado, o null en caso de vaciarse open y no llegar a ninguno de los casos anteriores.
     */
    private static PuzzleStateNode expand(ArrayList<PuzzleStateNode> open, ArrayList<PuzzleStateNode> closed) {
        while (open.size() > 0) {
            PuzzleStateNode X = open.remove(0);

            if (X.h() == 0 || X.getLevel() == PuzzleRules.maxDepth * currentSubtree) return X;
            else if (X.getLevel() <= PuzzleRules.maxMov[PuzzleRules.boardSize - 3]) {
                PuzzleStateNode[] children = X.genAllChildren();
                for (PuzzleStateNode child : children) {
                    boolean found = false;
                    PuzzleStateNode replace = null;

                    // Verifica la existencia del nodo en la secuencia de movimientos hasta este nodo
                    PuzzleStateNode parentAux = child;
                    while ((parentAux = parentAux.getFather()) != null) {
                        if (child.equals(parentAux)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        // Verifica la existencia del nodo en open
                        for (PuzzleStateNode openNode : open) {
                            if (child.equals(openNode)) {
                                found = true;
                                if (child.getEvaluation() < openNode.getEvaluation()) replace = openNode;
                                break;
                            }
                        }

                        if (!found) {
                            // El nodo no fue encontrado en open, se busca en closed
                            for (PuzzleStateNode closedNode : closed) {
                                if (child.equals(closedNode)) {
                                    found = true;
                                    if (child.getEvaluation() < closedNode.getEvaluation()) replace = closedNode;
                                    break;
                                }
                            }

                            if (replace != null) {
                                // El nodo ya existía en closed, pero tiene una mejor evaluación
                                // Se elimina el nodo de closed y de la lista de hijos del padre
                                if (replace.getFather() != null) replace.getFather().getCurrentChildren().remove(replace);
                                closed.remove(replace);

                                // Se eliminan los nodos hijos del nodo reemplazado
                                ArrayList<PuzzleStateNode> toRemove = deleteChildren(replace);
                                closed.removeAll(toRemove);
                                open.removeAll(toRemove);

                                toRemove.clear();
                            }
                        } else if (replace != null) {
                            // El nodo ya existía en open, pero tiene una mejor evaluación
                            // Se elimina el nodo de open y de la lista de hijos del padre
                            open.remove(replace);
                            if (replace.getFather() != null) replace.getFather().getCurrentChildren().remove(replace);
                        }

                        // El nodo no se encontró o se encontró pero tiene mejor evaluación
                        // Se agrega el nodo a open por insersión (Insertion sort)
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
            else break;
        }

        // Si open se queda vacío significa que no encontró la solución
        // en el subárbol. Retorna null
        return null;
    }

    /**
     * Coordinador de las llamadas al algoritmo A* para cada subárbol que sea
     * necesario para llegar a la solución. Esta función es llamada por el
     * programa principal, y retorna una cola de nodos lista para extraer
     * de ella la secuencia de movimientos.
     * Realiza las llamadas al algoritmo de A*, y reestablece los arreglos
     * open y closed según sea la respuesta de éste.
     * @param root El estado inicial del problema, que será la raíz del primer subárbol
     * @return Una cola de nodos PuzzleStateNode. La lista estará vacía si el
     * algoritmo A* no logra llegar a la solución.
     */
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
            //System.out.println("Subárbol: " + currentSubtree);
            //System.out.println("Raíz: " + open.get(0).h());

            solution = expand(open, closed);

            //System.out.println("Open\tClosed");
            //System.out.println(open.size() + "\t" + closed.size());

            if (solution == null || solution.h() == 0.0) break;
            
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

    /**
     * Genera el conjunto de nodos derivados de una raíz que deberán ser eliminados
     * de los arreglos open y closed. La búsqueda se hace de manera recursiva. Se
     * excluye la raíz del conjunto resultante.
     * @param root La raíz a partir de la cual se derivan los nodos a eliminar
     * @return Un Arraylist de nodos PuzzleStateNode, con todos los nodos derivados
     * de la raíz dada como argumento.
     */
    private static ArrayList<PuzzleStateNode> deleteChildren(PuzzleStateNode root) {
        ArrayList<PuzzleStateNode> pool = new ArrayList<>();

        deleteChildren(root, pool);

        return pool;
    }

    /**
     * Función recursiva para generar el arreglo de la función deleteChildren().
     * @param root Raíz de la cual se derivan los nodos a eliminar
     * @param pool El conjunto de nodos derivados de la raíz
     */
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

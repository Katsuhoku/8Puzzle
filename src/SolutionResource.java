package src;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Objeto que contiene la solución. El acceso es sincronizado por el resto
 * de los nodos.
 */
public class SolutionResource {
    private PuzzleStateNode solution = null;
    private final Lock lock;

    public SolutionResource() {
        lock = new ReentrantLock();
    }

    /**
     * Proclama haber encontrado la solución del problema y detiene la ejecución del
     * resto de nodos compitiendo. El método es sincronizado con un candado (Lock)
     * que se puede interrumpir; dos o más hilos pueden haber encontrado la solución
     * a la vez, pero solo se conservará la del primero que entre, volviendo
     * necesario interrumpir también aquellos hilos esperando por el recurso.
     * @param solution Nodo a establecer como solución (con un path específico)
     * @param threads Conjunto de hilos a detener
     * @throws InterruptedException Si el hilo fue el primero en proclamar su solución
     * será interrumpido en la espera por el recurso.
     */
    public void claimSolution(PuzzleStateNode solution, ArrayList<Thread> threads) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            this.solution = solution;
            for (Thread thread : threads) thread.interrupt();
        } finally {
            lock.unlock();
        }
    }

    public PuzzleStateNode getSolution() {
        return solution;
    }
}

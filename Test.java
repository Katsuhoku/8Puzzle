public class Test {
    public static void main(String[] args) {
        // Estado de prueba
        int[][] state = {
            {1, 4, 0},
            {3, 7, 2},
            {6, 8, 5}
        };

        StateNode node = new StateNode(state);
        System.out.println(node);
        System.out.println("Manhattan: " + node.getDistance());

        // Genera los mejores nuevos hijos hasta que se encuentre
        // la solución o hasta llegar a un mínimo local
        while (node.getDistance() > 0) {
            node = node.getBestChild();
            if (node != null) {
                System.out.println(node);
                System.out.println("Manhattan: " + node.getDistance());
            }
            else {
                System.out.println("Finnished");
                break;
            }
        }
    }
}

public class Test {
    public static void main(String[] args) {
        // Estado de prueba
        int[][] state = {
            {5, 1, 3},
            {8, 4, 2},
            {0, 7, 6}
        };

        StateNode node = new StateNode(state);
        System.out.println(node);
        System.out.println("Evaluation: " + node.getEvaluation());

        // Genera los mejores nuevos hijos hasta que se encuentre
        // la solución o hasta llegar a un mínimo local
        for (int i = 0; i < 20 && node.getEvaluation() > 0; i++) {
            node = node.getBestChild();
            if (node != null) {
                System.out.println(node);
                System.out.println("Evaluation: " + node.getEvaluation());
            }
            else {
                System.out.println("Finnished");
                break;
            }
        }
    }
}

package EightPuzzle.src.model;

public enum EightPuzzleMovement{
    NONE{
        @Override
        public String toString() {
            return "None";
        }
    }, UP {
        @Override
        public String toString() {
            return "Up";
        }
    }, DOWN {
        @Override
        public String toString() {
            return "Down";
        }
    }, LEFT {
        @Override
        public String toString() {
            return "Left";
        }
    }, RIGHT {
        @Override
        public String toString() {
            return "Right";
        }
    }  
}

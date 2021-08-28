package EightPuzzle.src.model;

public enum EightPuzzleMovement {
    NONE {
        @Override
        public String toString() {
            return "None";
        }

        @Override
        public char toChar() {
            return 'N';
        }
    }, UP {
        @Override
        public String toString() {
            return "Up";
        }

        @Override
        public char toChar() {
            return 'U';
        }
    }, DOWN {
        @Override
        public String toString() {
            return "Down";
        }

        @Override
        public char toChar() {
            return 'D';
        }
    }, LEFT {
        @Override
        public String toString() {
            return "Left";
        }

        @Override
        public char toChar() {
            return 'L';
        }
    }, RIGHT {
        @Override
        public String toString() {
            return "Right";
        }

        @Override
        public char toChar() {
            return 'R';
        }
    };
    
    public abstract char toChar();
}

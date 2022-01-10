package Chessgame.player;

public enum MoveStatus {
    DONE{
        @Override
        public boolean isDone() {
            return true;
        }
    },
    ILLEGAL {
        @Override
        public boolean isDone() {
            return false;
        }
    },
    LEAVES_IN_CHECK {
        @Override
        public boolean isDone() {
            return false;
        }
    };

    public abstract boolean isDone();
}

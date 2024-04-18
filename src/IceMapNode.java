public class IceMapNode {
    int row;
    int column;
    boolean isRock;
    IceMapNode topNeighbor;
    IceMapNode rightNeighbor;
    IceMapNode bottomNeighbor;
    IceMapNode leftNeighbor;
    Direction direction;

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        // Method to get the opposite direction
        public Direction opposite() {
            return switch (this) {
                case UP -> DOWN;
                case DOWN -> UP;
                case LEFT -> RIGHT;
                case RIGHT -> LEFT;
            };
        }
    }

    public IceMapNode(int row, int column, boolean isRock){
        this.row = row;
        this.column = column;
        this.isRock = isRock;
    }
}

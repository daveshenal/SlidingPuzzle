public class IceMapNode {
    private final int row;
    private final int column;
    private final boolean isRock;
    private IceMapNode topNeighbor,rightNeighbor,bottomNeighbor,leftNeighbor;
    private int distance = 0;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public boolean isRock() {
        return !this.isRock;
    }

    public void setTopNeighbor(IceMapNode topNeighbor) {
        this.topNeighbor = topNeighbor;
    }

    public void setRightNeighbor(IceMapNode rightNeighbor) {
        this.rightNeighbor = rightNeighbor;
    }

    public void setBottomNeighbor(IceMapNode bottomNeighbor) {
        this.bottomNeighbor = bottomNeighbor;
    }

    public void setLeftNeighbor(IceMapNode leftNeighbor) {
        this.leftNeighbor = leftNeighbor;
    }

    public IceMapNode(int row, int column, boolean isRock){
        this.row = row;
        this.column = column;
        this.isRock = isRock;
    }

    // Method to reach the end node in a specific direction
    // finishNode use to end the searching if we reach the F node in the middle of sliding
    // otherwise remove this condition
    public IceMapNode getEndNodeInDirection(Direction direction, IceMapNode finishNode) {
        IceMapNode currentNode = this;
        IceMapNode prevNode = null;
        while (currentNode != null && !currentNode.isRock) {

            // Stopping when we reach the F node in the middle of sliding
            if(currentNode == finishNode) return currentNode;

            prevNode = currentNode;
            currentNode = switch (direction) {
                case UP -> currentNode.topNeighbor;
                case DOWN -> currentNode.bottomNeighbor;
                case LEFT -> currentNode.leftNeighbor;
                case RIGHT -> currentNode.rightNeighbor;
            };
        }
        return prevNode;
    }


    // Method to get the distance between two nodes
    public int getDistanceToNode(IceMapNode endNode) {
        int distance = this.distance;
        // Nodes are in the same row, calculate horizontal distance
        if (this.row == endNode.row) distance += Math.abs(this.column - endNode.column);
        else // Nodes are in the same column, calculate vertical distance
            if (this.column == endNode.column) distance += Math.abs(this.row - endNode.row);
            else distance = -1;
        endNode.distance = distance;
        return distance;
    }

    // Class to represent a path between two nodes
    public static class Path implements Comparable<Path> {
        private final IceMapNode startNode;
        private final IceMapNode endNode;
        private final int distance;

        public Path(IceMapNode startNode, IceMapNode endNode) {
            this.startNode = startNode;
            this.endNode = endNode;
            this.distance = startNode.getDistanceToNode(endNode);
        }

        public IceMapNode getStartNode() {
            return startNode;
        }

        public IceMapNode getEndNode() {
            return endNode;
        }

        @Override
        public int compareTo(Path other) {
            return Integer.compare(this.distance, other.distance);
        }
    }
}


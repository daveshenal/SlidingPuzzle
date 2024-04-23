public class IceMapNode {
    private final int row;
    private final int column;
    private final boolean isRock;
    private IceMapNode topNeighbor,rightNeighbor,bottomNeighbor,leftNeighbor;
    private int distanceFromStart = 0;
    IceMapNode pathParent;

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


    // Class to represent a path between two nodes
    public static class Path implements Comparable<Path> {
        private final IceMapNode startNode;
        private final IceMapNode endNode;
        private final double f;
        private final double distance;
        private final double h;

        public Path(IceMapNode startNode, IceMapNode endNode, IceMapNode finishNode) {
            this.startNode = startNode;
            this.endNode = endNode;
            this.distance = getDistanceToNode();
            this.h = calculateEuclideanDistance(finishNode);
            this.f = this.distance + this.h;
        }

        // Method to get the distance between two nodes
        public int getDistanceToNode() {
            int distance = startNode.distanceFromStart;
            // Nodes are in the same row, calculate horizontal distance
            if (startNode.row == endNode.row) distance += Math.abs(startNode.column - endNode.column);
            else // Nodes are in the same column, calculate vertical distance
                if (startNode.column == endNode.column) distance += Math.abs(startNode.row - endNode.row);
                else distance = -1;
            endNode.distanceFromStart = distance;
            return distance;
        }

        public IceMapNode getStartNode() {
            return startNode;
        }

        public IceMapNode getEndNode() {
            return endNode;
        }

        public double calculateEuclideanDistance(IceMapNode finishNode) {
            int dx = endNode.column - finishNode.column;
            int dy = endNode.row - finishNode.row;
            double distance = Math.sqrt(dx * dx + dy * dy);
            return Math.round(distance * 100.0) / 100.0; // rounding to two decimal places
        }

        public void printPath() {
            System.out.println("Start Node: (" + (startNode.getColumn()+1) + ", " + (startNode.getRow()+1) + ")");
            System.out.println("End Node: (" + (endNode.getColumn()+1) + ", " + (endNode.getRow()+1) + ")");
            System.out.println("Distance: " + distance);
            System.out.println("H Value: " + h);
            System.out.println("F Value: " + f);
        }

        @Override
        public int compareTo(Path other) {
            return Double.compare(this.f, other.f);
        }
    }
}


public class IceMapNode implements Comparable<IceMapNode> {
    private final int row;
    private final int column;
    private final boolean isRock;
    private IceMapNode topNeighbor,rightNeighbor,bottomNeighbor,leftNeighbor;
    private int distanceFromStart = 0;
    private double fScore;
    private double heuristic;
    private IceMapNode pathParent;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public IceMapNode(int row, int column, boolean isRock){
        this.row = row;
        this.column = column;
        this.isRock = isRock;
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


   public void setPathAttributes(IceMapNode pathParent, IceMapNode finishNode){
        this.pathParent = pathParent;

        int distanceFromStart = getDistanceToNode(pathParent);
        this.distanceFromStart = distanceFromStart;

        double heuristic = calculateEuclideanDistance(finishNode);
        this.heuristic = heuristic;

        this.fScore = distanceFromStart+heuristic;
   }

    public IceMapNode getPathParent() {
        return pathParent;
    }

    public int getDistanceFromStart() {
        return distanceFromStart;
    }

    public int getDistanceToNode(IceMapNode startNode) {
        int distance = startNode.distanceFromStart;
        // Nodes are in the same row, calculate horizontal distance
        if (startNode.row == this.row) distance += Math.abs(startNode.column - this.column);
        else // Nodes are in the same column, calculate vertical distance
            if (startNode.column == this.column) distance += Math.abs(startNode.row - this.row);
            else distance = -1;
        return distance;
    }

    public double calculateEuclideanDistance(IceMapNode finishNode) {
        int dx = this.column - finishNode.column;
        int dy = this.row - finishNode.row;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return Math.round(distance * 100.0) / 100.0; // rounding to two decimal places
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

    @Override
    public int compareTo(IceMapNode other) {
        return Double.compare(this.fScore, other.fScore);
    }

    public void printNodeInfo() {
        System.out.println("Start Node: (" + (pathParent.getColumn()+1) + ", " + (pathParent.getRow()+1) + ")");
        System.out.println("End Node: (" + (this.getColumn()+1) + ", " + (this.getRow()+1) + ")");
        System.out.println("Distance: " + this.distanceFromStart);
        System.out.println("H Value: " + heuristic);
        System.out.println("F Value: " + fScore);
    }

}


public class IceMap {
    private IceMapNode startNode;
    private IceMapNode endNode;

    public IceMapNode getStartNode() {
        return startNode;
    }

    public IceMapNode getEndNode() {
        return endNode;
    }

    public IceMap(char[][] map) {
        int rows = map.length;
        int cols = map[0].length;
        IceMapNode[][] iceMap = new IceMapNode[rows][cols];

        // Initialize grid nodes
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boolean isObstacle = map[i][j] == '0';
                iceMap[i][j] = new IceMapNode(i, j, isObstacle);
                if(map[i][j] == 'S')
                    this.startNode = iceMap[i][j];
                else if (map[i][j] == 'F') {
                    this.endNode = iceMap[i][j];
                    this.endNode.setDistanceFromStart(Integer.MAX_VALUE);
                }
            }
        }

        // Connect neighboring nodes
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (iceMap[i][j].isRock()) {
                    if (i > 0 && iceMap[i - 1][j].isRock()) {
                        iceMap[i][j].setTopNeighbor(iceMap[i - 1][j]); // Top
                    }
                    if (i < rows - 1 && iceMap[i + 1][j].isRock()) {
                        iceMap[i][j].setBottomNeighbor(iceMap[i + 1][j]);// Bottom
                    }
                    if (j > 0 && iceMap[i][j - 1].isRock()) {
                        iceMap[i][j].setLeftNeighbor(iceMap[i][j - 1]); // Left
                    }
                    if (j < cols - 1 && iceMap[i][j + 1].isRock()) {
                        iceMap[i][j].setRightNeighbor(iceMap[i][j + 1]);// Right
                    }
                }
            }
        }
    }

}

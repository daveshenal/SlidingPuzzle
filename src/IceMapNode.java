public class IceMapNode {
    int row;
    int column;
    boolean isRock;
    IceMapNode topNeighbor;
    IceMapNode rightNeighbor;
    IceMapNode bottomNeighbor;
    IceMapNode leftNeighbor;

    public IceMapNode(int row, int column, boolean isRock){
        this.row = row;
        this.column = column;
        this.isRock = isRock;
    }
}

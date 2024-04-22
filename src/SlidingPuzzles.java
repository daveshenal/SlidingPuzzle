import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

public class SlidingPuzzles {
    public static void main(String[] args) {
        try {
            long startTime = System.nanoTime(); // start time

            writeMapToFile();
            char[][] map = parseMap("MapData.txt");
            System.out.println("Map successfully parsed\n");
            printMap(map);
            IceMap iceMap = new IceMap(map);
            Stack<IceMapNode.Path> shortestPath = findPath(iceMap.getStartNode(), iceMap.getEndNode());

            if(shortestPath != null){
                printPathSteps(iceMap, shortestPath);
            }
            else System.out.println("\nPath not found!");

            long endTime = System.nanoTime(); // end time
            long timeTaken = endTime - startTime;
            System.out.println();
            System.out.println("Time taken: " + timeTaken + " nanoseconds");

        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }

    private static void printPathSteps(IceMap iceMap, Stack<IceMapNode.Path> shortestPath) {
        System.out.println("\nPath to 'S' to 'F'\n------------------");
        System.out.println("Start Node: (" + (iceMap.getStartNode().getColumn() +1) + ", " +
                (iceMap.getStartNode().getRow() +1) + ")");
        while (!shortestPath.isEmpty()) {
            IceMapNode.Path path = shortestPath.pop();
            int startNodeColumn,startNodeRow,endNodeColumn,endNodeRow;
            startNodeColumn = path.getStartNode().getColumn();
            startNodeRow = path.getStartNode().getRow();
            endNodeColumn = path.getEndNode().getColumn();
            endNodeRow = path.getEndNode().getRow();

            if(startNodeColumn == endNodeColumn){ // vertical move
                if(endNodeRow > startNodeRow) System.out.print("Move down to "); // down
                else System.out.print("Move up to "); // up
            }
            else if (endNodeColumn > startNodeColumn) { // horizontal move
                System.out.print("Move right to "); // right
            } else System.out.print("Move left to "); // left

            System.out.println("(" + (path.getEndNode().getColumn() +1) + ", " + (path.getEndNode().getRow() +1) + ")");
        }
        System.out.println("Done :)");
    }

    public static Stack<IceMapNode.Path> findPath(IceMapNode startNode, IceMapNode finishNode) {
        PriorityQueue<IceMapNode.Path> queue = new PriorityQueue<>();
        Set<IceMapNode.Path>  pathMap = new HashSet<>();
        Set<IceMapNode>  visited = new HashSet<>();

        visited.add(startNode);
        exploreAndEnqueuePaths(startNode, finishNode, visited, queue, pathMap);

        int count = 0;

        while (!queue.isEmpty()) {
            count++;
            IceMapNode.Path currentPath = queue.peek();

            currentPath.printPath();
            System.out.println();

            if (currentPath.getEndNode() == finishNode) {
                System.out.println("Iteration count : " + count);
                return reconstructPath(startNode, currentPath, pathMap);
            }

            exploreAndEnqueuePaths(currentPath.getEndNode(), finishNode, visited, queue, pathMap);

            queue.remove();
        }
        return null; // No path found
    }

    public static Stack<IceMapNode.Path> reconstructPath(IceMapNode startNode, IceMapNode.Path finishedPath, Set<IceMapNode.Path>  pathMap) {
        IceMapNode.Path currentPath = finishedPath;
        Stack<IceMapNode.Path> shortestPath = new Stack<>();
        shortestPath.push(finishedPath);

        while(currentPath.getStartNode() != startNode){
            for (IceMapNode.Path path : pathMap) {
                if(path.getEndNode() == currentPath.getStartNode()) {
                    currentPath = path;
                    shortestPath.push(path);
                    break;
                }
            }
        }
        return shortestPath;
    }

    public static void exploreAndEnqueuePaths(IceMapNode startNode, IceMapNode finishNode, Set<IceMapNode> visited,
                                              PriorityQueue<IceMapNode.Path> queue, Set<IceMapNode.Path>  pathMap) {
        // Explore and enqueue paths in all directions
        for (IceMapNode.Direction direction : IceMapNode.Direction.values()) {
            IceMapNode endNode = startNode.getEndNodeInDirection(direction, finishNode);
            if (endNode != null && !visited.contains(endNode)) {

                visited.add(endNode);
                IceMapNode.Path newpath = new IceMapNode.Path(startNode, endNode, finishNode);
                queue.offer(newpath);
                pathMap.add(newpath);
            }
        }
    }


    // write map data to a file (for test purpose)
    public static void writeMapToFile() throws IOException {
        char[][] map = {
                {'.', '.', '.', '.','.', '0','.', '.', '.', 'S'},
                {'.', '.', '.', '.','0', '.','.', '.', '.', '.'},
                {'0', '.', '.', '.','.', '.','0', '.', '.', '0'},
                {'.', '.', '.', '0','.', '.','.', '.', '0', '.'},
                {'.', 'F', '.', '.','.', '.','.', '.', '0', '.'},
                {'.', '0', '.', '.','.', '.','.', '.', '.', '.'},
                {'.', '.', '.', '.','.', '.','.', '0', '.', '.'},
                {'.', '0', '.', '0','.', '.','0', '.', '.', '0'},
                {'0', '.', '.', '.','.', '.','.', '.', '.', '.'},
                {'.', '0', '0', '.','.', '.','.', '.', '0', '.'},
        };

        BufferedWriter writer = new BufferedWriter(new FileWriter("MapData.txt"));

        for (char[] row : map) {
            for (char cell : row) {
                writer.write(cell);
            }
            writer.newLine();
        }

        writer.close();
        System.out.println("Map successfully written to file.");
    }

    public static char[][] parseMap(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        // Determine width and height of the map
        String line;
        int height = 0;
        int width = -1; // will store the width of first line when start reading
        while ((line = reader.readLine()) != null) {
            height++;
            if (width == -1) { // when reading first line
                width = line.length();
            } else if (line.length() != width) { // if the width of any line is different from previous line(s)
                throw new IOException("Map is not rectangular");
            }
        }

        // Initialize map array
        char[][] map = new char[height][width];

        // Reset reader to beginning of file
        reader.close();
        reader = new BufferedReader(new FileReader(filename));

        // Read map data and populate the map array
        int row = 0;
        while ((line = reader.readLine()) != null) {
            for (int col = 0; col < width; col++) {
                map[row][col] = line.charAt(col);
            }
            row++;
        }

        reader.close();
        return map;
    }

    public static void printMap(char[][] map) {
        for (char[] row : map) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }
}


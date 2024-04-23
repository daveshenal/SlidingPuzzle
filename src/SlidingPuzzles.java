import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SlidingPuzzles {
    public static void main(String[] args) {
        try {

            char[][] map = selectMap();
            //printMap(map);
            IceMap iceMap = new IceMap(map);
            long startTime = System.nanoTime(); // start time
            List<IceMapNode> shortestPath = findPath(iceMap.getStartNode(), iceMap.getEndNode());

            if(shortestPath != null){
                printPathSteps(iceMap.getStartNode(), shortestPath);
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

    private static char[][] selectMap() throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Print menu options
        System.out.println("Select a category:");
        System.out.println("1. Benchmark Series");
        System.out.println("2. Example puzzles");
        System.out.println("3. Write a custom map to file (for developer testings)");

        // Get user input for category selection
        int categoryChoice = scanner.nextInt();

        switch (categoryChoice) {
            case 1 -> {
                // Benchmark Series
                System.out.println("Select a puzzle from Benchmark Series:");
                System.out.println("1. Puzzle 10");
                System.out.println("2. Puzzle 20");
                System.out.println("3. Puzzle 40");
                System.out.println("4. Puzzle 80");
                System.out.println("5. Puzzle 160");
                System.out.println("6. Puzzle 320");
                System.out.println("7. Puzzle 640");
                System.out.println("8. Puzzle 1280");
                System.out.println("9. Puzzle 2560");
                int benchmarkChoice = scanner.nextInt();
                System.out.println("You selected Benchmark Puzzle " + benchmarkChoice);
                return parseMap("benchmark_series/puzzle_"+ getBenchPuzzleNum(benchmarkChoice)+".txt");
            }
            case 2 -> {
                // Example puzzles
                System.out.println("Select an example puzzle:");
                for (int i = 1; i <= 25; i++) {
                    System.out.println(i + ". Puzzle " + getExPuzzleNum(i) + " - " + getExPuzzleIndex(i));
                }
                int exampleChoice = scanner.nextInt();
                System.out.println("You selected Example Puzzle " + exampleChoice);

                return parseMap("examples/maze"+ getExPuzzleNum(exampleChoice)+"_"+ getExPuzzleIndex(exampleChoice)+".txt");
            }
            case 3 -> {
                writeMapToFile();
                return parseMap("MapData.txt");

            }
            default -> throw new IOException("Map is not rectangular");

        }
    }

    public static int getBenchPuzzleNum(int n) {
        // Calculate puzzle value using the formula: 10 * 2^(n-1)
        return 10 * (int)Math.pow(2, n - 1);
    }

    // Method to determine puzzle number based on index
    private static int getExPuzzleNum(int index) {
        if (index <= 5) {
            return 10;
        } else if (index <= 10) {
            return 15;
        } else if (index <= 15) {
            return 20;
        } else if (index <= 20) {
            return 25;
        } else {
            return 30;
        }
    }

    // Method to determine puzzle index based on index
    private static int getExPuzzleIndex(int index) {
        if (index <= 5) {
            return index;
        } else {
            if(index % 5 == 0)
                return 5;
            return index % 5;
        }
    }

    private static void printPathSteps(IceMapNode startNode, List<IceMapNode> shortestPath) {
        System.out.println("\nPath to 'S' to 'F'\n------------------");
        System.out.println("1. Start Node: (" + (startNode.getColumn() +1) + ", " +
                (startNode.getRow() +1) + ")");
        int count = 2;
        shortestPath.remove(0);
        for(IceMapNode node: shortestPath){
            System.out.print(count+ ". ");
            count++;
            int startNodeColumn,startNodeRow,endNodeColumn,endNodeRow;
            startNodeColumn = node.pathParent.getColumn();
            startNodeRow = node.pathParent.getRow();
            endNodeColumn = node.getColumn();
            endNodeRow = node.getRow();

            if(startNodeColumn == endNodeColumn){ // vertical move
                if(endNodeRow > startNodeRow) System.out.print("Move down to "); // down
                else System.out.print("Move up to "); // up
            }
            else if (endNodeColumn > startNodeColumn) { // horizontal move
                System.out.print("Move right to "); // right
            } else System.out.print("Move left to "); // left

            System.out.println("(" + (node.getColumn() +1) + ", " + (node.getRow() +1) + ")");
        }
        System.out.println("Done :)");
    }

    public static List<IceMapNode> findPath(IceMapNode startNode, IceMapNode finishNode) {
        PriorityQueue<IceMapNode.Path> queue = new PriorityQueue<>();
        Set<IceMapNode.Path>  pathMap = new HashSet<>();
        Set<IceMapNode>  visited = new HashSet<>();

        visited.add(startNode);
        exploreAndEnqueuePaths(startNode, finishNode, visited, queue, pathMap);

        int count = 0;

        while (!queue.isEmpty()) {
            count++;
            IceMapNode.Path currentPath = queue.peek();

            if (currentPath.getEndNode() == finishNode) {
                System.out.println("Iteration count : " + count);
                return reconstructPath(finishNode);
            }

            exploreAndEnqueuePaths(currentPath.getEndNode(), finishNode, visited, queue, pathMap);

            queue.remove();
        }
        return null; // No path found
    }

    public static Stack<IceMapNode> reconstructPath(IceMapNode finishedNode) {
        Stack<IceMapNode> path = new Stack<>();
        IceMapNode currentNode = finishedNode;

        while (currentNode != null) {
            path.add(currentNode);
            currentNode = currentNode.pathParent;
        }
        Collections.reverse(path);
        return path;
    }

    public static void exploreAndEnqueuePaths(IceMapNode startNode, IceMapNode finishNode, Set<IceMapNode> visited,
                                              PriorityQueue<IceMapNode.Path> queue, Set<IceMapNode.Path>  pathMap) {

        // Explore and enqueue paths in all directions
        for (IceMapNode.Direction direction : IceMapNode.Direction.values()) {
            IceMapNode endNode = startNode.getEndNodeInDirection(direction, finishNode);
            if (endNode != null && !visited.contains(endNode)) {
                endNode.pathParent = startNode;
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


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class SlidingPuzzles {
    public static void main(String[] args) {
        try {

            char[][] map = selectMap();
//            printMap(map);
            IceMap iceMap = new IceMap(map);
            long startTime = System.nanoTime(); // start time
            List<IceMapNode> shortestPath = findPath(iceMap.getStartNode(), iceMap.getEndNode());

            printPathSteps(iceMap.getStartNode(), shortestPath);

            long endTime = System.nanoTime(); // end time
            long timeTaken = endTime - startTime;
            System.out.println();
            System.out.println("Time taken: " + timeTaken/1_000_000_000.0 + " seconds");

        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }



    private static char[][] selectMap() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select a category:");
        System.out.println("1. Benchmark Series");
        System.out.println("2. Example puzzles");
        System.out.println("3. Write a custom map to file (for developer testings)");

        int categoryChoice = scanner.nextInt();

        switch (categoryChoice) {
            case 1 -> {
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
                return selectBenchmarkMap(benchmarkChoice);
            }
            case 2 -> {
                System.out.println("Select an example puzzle:");
                for (int i = 1; i <= 25; i++) {
                    System.out.println(i + ". Puzzle " + getExPuzzleNum(i) + " - " + getExPuzzleIndex(i));
                }
                int exampleChoice = scanner.nextInt();
                System.out.println("You selected Example Puzzle " + exampleChoice);
                return selectExampleMap(exampleChoice);
            }
            case 3 -> {
                return selectCustomMap();

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

    public static char[][] selectBenchmarkMap(int benchmarkChoice) throws IOException {
        String filePath = "benchmark_series/puzzle_"+ getBenchPuzzleNum(benchmarkChoice)+".txt";
        return parseMap(filePath);
    }

    public static char[][] selectExampleMap(int exampleChoice) throws IOException {
        String filePath = "examples/maze"+ getExPuzzleNum(exampleChoice)+"_"+ getExPuzzleIndex(exampleChoice)+".txt";
        return parseMap(filePath);
    }
    public static char[][] selectCustomMap() throws IOException {
        writeMapToFile();
        String filePath = "MapData.txt";
        return parseMap(filePath);
    }



    private static void printPathSteps(IceMapNode startNode, List<IceMapNode> shortestPath) {
        if(shortestPath.size() == 1){
            System.out.println("No Path found!");
            return;
        }

        System.out.println("\nPath From 'S' to 'F'\n---------------------");
        System.out.println("1. Start Node: (" + (startNode.getColumn() +1) + ", " +
                (startNode.getRow() +1) + ")");
        int count = 2;
        shortestPath.remove(0);
        for(IceMapNode node: shortestPath){
            System.out.print(count+ ". ");
            count++;
            int startNodeColumn,startNodeRow,endNodeColumn,endNodeRow;
            startNodeColumn = node.getPathParent().getColumn();
            startNodeRow = node.getPathParent().getRow();
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
        PriorityQueue<IceMapNode> queue = new PriorityQueue<>();
        Set<IceMapNode>  visited = new HashSet<>();

        visited.add(startNode);
        exploreAndEnqueuePaths(startNode, finishNode, visited, queue);

        int count = 0;

        while (!queue.isEmpty()) {
            count++;
            IceMapNode currentNode = queue.peek();

            if(currentNode == finishNode) break;
//            currentNode.printNodeInfo();
//            System.out.println();
            exploreAndEnqueuePaths(currentNode, finishNode, visited, queue);
            queue.remove();
        }
        System.out.println("Iteration count : " + count);
        return reconstructPath(finishNode);
    }

    public static Stack<IceMapNode> reconstructPath(IceMapNode finishedNode) {
        Stack<IceMapNode> path = new Stack<>();
        IceMapNode currentNode = finishedNode;

        while (currentNode != null) {
            path.add(currentNode);
            currentNode = currentNode.getPathParent();
        }
        Collections.reverse(path);
        return path;
    }

    public static void exploreAndEnqueuePaths(IceMapNode currentNode, IceMapNode finishNode, Set<IceMapNode> visited,
                                              PriorityQueue<IceMapNode> queue) {

        // Explore and enqueue paths in all directions
        for (IceMapNode.Direction direction : IceMapNode.Direction.values()) {
            IceMapNode endNode = currentNode.getEndNodeInDirection(direction, finishNode);
            if(endNode == finishNode){
                if(finishNode.getDistanceFromStart() > endNode.getDistanceToNode(currentNode)) {
                    endNode.setPathAttributes(currentNode,finishNode);
                    return;
                }
            }
            if (endNode != null && !visited.contains(endNode)) {
                endNode.setPathAttributes(currentNode,finishNode);
                visited.add(endNode);
                queue.offer(endNode);
            }
        }
    }

    // write map data to a file (for test purpose)
    public static void writeMapToFile() throws IOException {
        char[][] map = {
                {'.', '.', '.', '.','0', '.','.', '.', '.', '.'},
                {'.', '.', '.', '.','.', '.','.', '.', '.', '.'},
                {'0', '.', '.', '.','.', '.','.', '.', '.', '.'},
                {'.', '.', '.', '.','.', 'S','0', '.', '.', '.'},
                {'.', '.', '.', '.','.', '0','.', '.', '.', '.'},
                {'.', '.', '.', '.','.', '.','.', '.', '.', '.'},
                {'.', '.', '0', '0','.', '.','.', '.', '.', '.'},
                {'.', '0', '.', '.','0', '0','.', '.', '.', '.'},
                {'.', '.', '.', 'F','.', '.','.', '.', '.', '.'},
                {'.', '0', '.', '.','0', '.','0', '.', '.', '.'},
                {'.', '0', '.', '.','.', '.','.', '0', '.', '.'},
                {'.', '.', '.', '.','.', '.','.', '0', '.', '.'},
                {'0', '.', '.', '.','.', '.','0', '.', '.', '.'},
                {'.', '.', '0', '.','.', '.','.', '.', '.', '.'},
                {'.', '.', '0', '.','.', '.','.', '.', '.', '.'},
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


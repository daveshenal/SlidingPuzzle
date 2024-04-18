import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collections;

public class SlidingPuzzles {
    public static void main(String[] args) {
        try {
            writeMapToFile();
            char[][] map = parseMap("MapData.txt");
            System.out.println("Map successfully parsed:");
            printMap(map);
            IceMap iceMap = new IceMap(map);
            List<IceMapNode> path = findPath(iceMap.startNode, iceMap.endNode);
            for (IceMapNode iceMapNode : path){
                System.out.println((iceMapNode.column+1)+", "+(iceMapNode.row+1 ));
            }

        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }


    public static List<IceMapNode> findPath(IceMapNode startNode, IceMapNode finishNode) {
        Queue<IceMapNode> queue = new LinkedList<>();
        Set<IceMapNode> visited = new HashSet<>();
        Map<IceMapNode, IceMapNode> parentMap = new HashMap<>();

        queue.offer(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            IceMapNode currentNode = queue.peek();
            if (currentNode == finishNode) {
                return reconstructPath(parentMap, finishNode);
            }
            if(currentNode.direction != null) {
                switch (currentNode.direction) {
                    case UP -> moveUp(currentNode, queue, visited, parentMap);
                    case DOWN -> moveDown(currentNode, queue, visited, parentMap);
                    case LEFT -> moveLeft(currentNode, queue, visited, parentMap);
                    case RIGHT -> moveRight(currentNode, queue, visited, parentMap);
                }
            }
            else addNeighborsToQueue(currentNode, visited, queue, parentMap);
            queue.remove();
        }
        return null; // No path found
    }

    public static void addNeighborsToQueue(IceMapNode currentNode, Set<IceMapNode> visited, Queue<IceMapNode> queue, Map<IceMapNode,IceMapNode> parentMap) {
        if(currentNode.topNeighbor != null && !currentNode.topNeighbor.isRock && !visited.contains(currentNode.topNeighbor)){
            currentNode.topNeighbor.direction = IceMapNode.Direction.UP;
            parentMap.put(currentNode.topNeighbor, currentNode);
            visited.add(currentNode.topNeighbor);
            queue.offer(currentNode.topNeighbor);
        }
        if (currentNode.rightNeighbor != null && !currentNode.rightNeighbor.isRock && !visited.contains(currentNode.rightNeighbor)){
            currentNode.rightNeighbor.direction = IceMapNode.Direction.RIGHT;
            parentMap.put(currentNode.rightNeighbor, currentNode);
            visited.add(currentNode.rightNeighbor);
            queue.offer(currentNode.rightNeighbor);
        }
        if (currentNode.bottomNeighbor != null && !currentNode.bottomNeighbor.isRock && !visited.contains(currentNode.bottomNeighbor)){
            currentNode.bottomNeighbor.direction = IceMapNode.Direction.DOWN;
            parentMap.put(currentNode.bottomNeighbor, currentNode);
            visited.add(currentNode.bottomNeighbor);
            queue.offer(currentNode.bottomNeighbor);
        }
        if (currentNode.leftNeighbor != null && !currentNode.leftNeighbor.isRock && !visited.contains(currentNode.leftNeighbor)){
            currentNode.leftNeighbor.direction = IceMapNode.Direction.LEFT;
            parentMap.put(currentNode.leftNeighbor, currentNode);
            visited.add(currentNode.leftNeighbor);
            queue.offer(currentNode.leftNeighbor);
        }
    }

    public static void moveUp(IceMapNode currentNode, Queue<IceMapNode> queue, Set<IceMapNode> visited, Map<IceMapNode,IceMapNode> parentMap) {
        if (currentNode.topNeighbor != null && !currentNode.topNeighbor.isRock ) {
            currentNode.topNeighbor.direction = IceMapNode.Direction.UP;
            parentMap.put(currentNode.topNeighbor, currentNode);
            visited.add(currentNode.topNeighbor);
            queue.offer(currentNode.topNeighbor);
        } else {
            addNeighborsToQueue(currentNode, visited, queue, parentMap);
        }
    }

    public static void moveRight(IceMapNode currentNode, Queue<IceMapNode> queue, Set<IceMapNode> visited, Map<IceMapNode,IceMapNode> parentMap) {
        if (currentNode.rightNeighbor != null && !currentNode.rightNeighbor.isRock ) {
            currentNode.rightNeighbor.direction = IceMapNode.Direction.RIGHT;
            parentMap.put(currentNode.rightNeighbor, currentNode);
            visited.add(currentNode.rightNeighbor);
            queue.offer(currentNode.rightNeighbor);
        } else {
            addNeighborsToQueue(currentNode, visited, queue, parentMap);
        }
    }

    public static void moveDown(IceMapNode currentNode, Queue<IceMapNode> queue, Set<IceMapNode> visited, Map<IceMapNode,IceMapNode> parentMap) {
        if (currentNode.bottomNeighbor != null && !currentNode.bottomNeighbor.isRock) {
            currentNode.bottomNeighbor.direction = IceMapNode.Direction.DOWN;
            parentMap.put(currentNode.bottomNeighbor, currentNode);
            visited.add(currentNode.bottomNeighbor);
            queue.offer(currentNode.bottomNeighbor);
        } else {
            addNeighborsToQueue(currentNode, visited, queue, parentMap);
        }
    }

    public static void moveLeft(IceMapNode currentNode, Queue<IceMapNode> queue, Set<IceMapNode> visited, Map<IceMapNode,IceMapNode> parentMap) {
        if (currentNode.leftNeighbor != null && !currentNode.leftNeighbor.isRock) {
            currentNode.leftNeighbor.direction = IceMapNode.Direction.LEFT;
            parentMap.put(currentNode.leftNeighbor, currentNode);
            visited.add(currentNode.leftNeighbor);
            queue.offer(currentNode.leftNeighbor);
        } else {
            addNeighborsToQueue(currentNode, visited, queue, parentMap);
        }
    }

    public static List<IceMapNode> reconstructPath(Map<IceMapNode, IceMapNode> parentMap, IceMapNode finishNode) {
        List<IceMapNode> path = new ArrayList<>();
        IceMapNode currentNode = finishNode;

        while (currentNode != null) {
            path.add(currentNode);
            currentNode = parentMap.get(currentNode);
        }

        Collections.reverse(path);
        return path;
    }

    // write map data to a file (for test purpose)
    public static void writeMapToFile() throws IOException {
        char[][] map = {
                {'.', '.', '0', '.', 'S'},
                {'F', '.', '.', '.', '.'},
                {'.', '0', '.', '.', '.'},
                {'.', '.', '.', '0', '.'},
                {'0', '.', '.', '.', '.'},
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


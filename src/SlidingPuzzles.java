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
            IceMapNode currentNode = queue.poll(); // poll() instead of peek() to remove the node from the queue

            if (currentNode == finishNode) {
                return reconstructPath(parentMap, finishNode);
            }

            if (currentNode.topNeighbor != null && !currentNode.topNeighbor.isRock && !visited.contains(currentNode.topNeighbor)) {
                IceMapNode turnNode = moveUp(currentNode, queue, visited, finishNode);
                parentMap.put(turnNode, currentNode);
            }
            if (currentNode.rightNeighbor != null && !currentNode.rightNeighbor.isRock && !visited.contains(currentNode.rightNeighbor)) {
                IceMapNode turnNode = moveRight(currentNode, queue, visited, finishNode);
                parentMap.put(turnNode, currentNode);
            }
            if (currentNode.bottomNeighbor != null && !currentNode.bottomNeighbor.isRock && !visited.contains(currentNode.bottomNeighbor)) {
                IceMapNode turnNode = moveDown(currentNode, queue, visited, finishNode);
                parentMap.put(turnNode, currentNode);
            }
            if (currentNode.leftNeighbor != null && !currentNode.leftNeighbor.isRock && !visited.contains(currentNode.leftNeighbor)) {
                IceMapNode turnNode = moveLeft(currentNode, queue, visited, finishNode);
                parentMap.put(turnNode, currentNode);
            }
        }

        return null; // No path found
    }

    public static IceMapNode moveUp(IceMapNode currentNode, Queue<IceMapNode> queue, Set<IceMapNode> visited, IceMapNode finishNode) {
        if (currentNode == finishNode)
            return currentNode;
        else if (currentNode.topNeighbor != null && !currentNode.topNeighbor.isRock && !visited.contains(currentNode.topNeighbor)) {
            visited.add(currentNode.topNeighbor);
            queue.offer(currentNode.topNeighbor);
            return currentNode.topNeighbor;
        } else {
            return currentNode;
        }
    }

    public static IceMapNode moveRight(IceMapNode currentNode, Queue<IceMapNode> queue, Set<IceMapNode> visited, IceMapNode finishNode) {
        if (currentNode == finishNode)
            return currentNode;
        else if (currentNode.rightNeighbor != null && !currentNode.rightNeighbor.isRock && !visited.contains(currentNode.rightNeighbor)) {
            visited.add(currentNode.rightNeighbor);
            queue.offer(currentNode.rightNeighbor);
            return currentNode.rightNeighbor;
        } else {
            queue.offer(currentNode);
            return currentNode;
        }
    }


    public static IceMapNode moveDown(IceMapNode currentNode, Queue<IceMapNode> queue, Set<IceMapNode> visited, IceMapNode finishNode) {
        if (currentNode == finishNode)
            return currentNode;
        else if (currentNode.bottomNeighbor != null && !currentNode.bottomNeighbor.isRock && !visited.contains(currentNode.bottomNeighbor)) {
            visited.add(currentNode.bottomNeighbor);
            queue.offer(currentNode.bottomNeighbor);
            return currentNode.bottomNeighbor;
        } else {
            queue.offer(currentNode);
            return currentNode;
        }
    }

    public static IceMapNode moveLeft(IceMapNode currentNode, Queue<IceMapNode> queue, Set<IceMapNode> visited, IceMapNode finishNode) {
        if (currentNode == finishNode)
            return currentNode;
        else if (currentNode.leftNeighbor != null && !currentNode.leftNeighbor.isRock && !visited.contains(currentNode.leftNeighbor)) {
            visited.add(currentNode.leftNeighbor);
            queue.offer(currentNode.leftNeighbor);
            return currentNode.leftNeighbor;
        } else {
            queue.offer(currentNode);
            return currentNode;
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


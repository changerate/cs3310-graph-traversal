/****************************************************************************/
/* Carlos Vargas                                                            */
/* Login ID: 017807647                                                      */
/* CS 3310, Fall 2025                                                       */
/* Programming Assignment 1                                                 */
/* Graph traveler class: traverses an undirected, unweighted graph.         */
/* Purpose: finds the 'clusters' – the parts of the graph that go together  */
/****************************************************************************/



import java.util.*;
import java.nio.file.*;

public class GraphTraveler {
    /** The total number of nodes/vertices in the current graph being processed */
    private int numNodes; 
    
    /** Adjacency list representation mapping each node ID to its list of connected neighbors */
    private Map<Integer, List<Integer>> adjacencySearchList = new HashMap<>();
    
    /** Collection of all clusters (connected components) found in the graph, where each inner ArrayList represents one cluster */
    private ArrayList<ArrayList<Integer>> clusters = new ArrayList<>();

    //------------------------------------------------------------
    // SETTER FUNCTIONS 
    //------------------------------------------------------------
    
    /**
     * Sets the number of nodes in the current graph.
     * @param numNodes the total number of nodes/vertices in the graph
     */
    private void setNumNodes(int numNodes) {
        this.numNodes = numNodes;
    }
    

    /**
     * Creates empty lists for each node (key) numbered from 1 to numNodes.
     * @param numNodes the total number of nodes in the graph
     */
    private void buildEmptyAdjacencyList(int numNodes) {
        for (int i = 1; i <= numNodes; i++) {
            adjacencySearchList.put(i, new ArrayList<>());
        }
    }


    /**
     * Adds a pair of connected nodes (aka verteces) to the adjacency list.
     * Since the graph is undirected, both nodes are added to each other's 
     * adjacency lists.
     * Prevents duplicate entries by checking if the connection already exists.
     * @param pairArray an array of size 2 containing the two connected nodes
     */
    private void addPairsToAdjacencySearchList(int[] pairArray) {
        // ADD THE RIGHT NUMBER TO THE ADJACENCY LIST OF THE LEFT NUMBER
        // first check if it's already there
        if (!adjacencySearchList.get(pairArray[0]).contains(pairArray[1])) {
            adjacencySearchList.get(pairArray[0]).add(pairArray[1]);
        }
        
        // ADD THE LEFT NUMBER TO THE ADJACENCY LIST OF THE RIGHT NUMBER
        // first check if it's already there
        if (!adjacencySearchList.get(pairArray[1]).contains(pairArray[0])) {
            adjacencySearchList.get(pairArray[1]).add(pairArray[0]);
        }
    }

    



    //------------------------------------------------------------
    // GET FUNCTIONS 
    //------------------------------------------------------------

    /**
     * Gets the number of nodes in the current graph.
     * @return the total number of nodes/vertices in the graph
     */
    public int getNumNodes() {
        return numNodes;
    }
    
    /**
     * Gets the clusters found in the current graph.
     * Each cluster represents a connected component of the graph.
     * @return a 2D ArrayList where each inner list contains the nodes in one cluster
     */
    public ArrayList<ArrayList<Integer>> getClusters() {
        return clusters;
    }
    
    
    /**
     * Gets the adjacency list representation of the current graph.
     * Maps each node to a list of its adjacent (connected) nodes.
     * @return a HashMap where keys are nodes and values are lists of connected nodes
     */
    public Map<Integer, List<Integer>> getAdjacencySearchList() {
        return adjacencySearchList;
    }
    
    
    

    
    //------------------------------------------------------------
    // UTILITY FUNCTIONS 
    //------------------------------------------------------------

    /**
     * Parses a string representation of a graph (a g-string) and 
     * builds the adjacency list representation.
     * The graph string should start with the number of nodes, 
     * followed by pairs like "(1,2)".
     * @param graphString a string containing graph data in 
     * the format: "numNodes (node1,node2) ..."
     */
    private void makeAdjacencyList(String graphString) {
        /** Scanner to parse tokens from the graph string input */
        Scanner scanner = new Scanner(graphString);
        
        // get the number of nodes by getting the first integer of the line
        /** Number of nodes read from the first token in the graph string */
        int fileNumNodes = scanner.nextInt();
        setNumNodes(fileNumNodes);
        
        // now set the number of nodes in the map 
        buildEmptyAdjacencyList(numNodes);
        
        // now fill out each vertex with it's adjacent nodes
        /** Current string pair being processed, e.g., "(1,2)" */
        String stringPair = "";
        /** Flag to control the parsing loop - true while there are more pairs to process */
        boolean pairsToParse = true;
        
        while (pairsToParse) {
            try {
                stringPair = scanner.next(); // this gives "(#,#)"
                int[] pairArray = stringPairToIntPair(stringPair);
                addPairsToAdjacencySearchList(pairArray);
            } catch (NoSuchElementException | NumberFormatException e) {
                pairsToParse = false;
            }
        }
        
        scanner.close();
    }

    
    /**
     * Parses a string pair like "(1,2)" and returns a fixed-size int array of length 2.
     * Removes parentheses and splits the string on comma to extract the two integers.
     * @param stringPair a string in the format "(num1,num2)" representing a node pair
     * @return an integer array of size 2 containing the two parsed numbers
     */
    private int[] stringPairToIntPair(String stringPair) {
        stringPair = stringPair.replace("(", "").replace(")", "");
        /** Array containing the two number strings after splitting on comma */
        String[] parts = stringPair.split(",");
        /** Result array to hold the two parsed integers */
        int[] result = new int[2];
        result[0] = Integer.parseInt(parts[0]);
        result[1] = Integer.parseInt(parts[1]);
        return result;
    }


    /**
     * Resets all mutable instance variables to their initial empty state.
     * Clears numNodes, all clusters, and the adjacency list map.
     */
    private void clearVars() {
        setNumNodes(0);        
        for (ArrayList<Integer> inner : clusters) {
            inner.clear(); // removes all elements from each inner list
        }
        clusters.clear();
        adjacencySearchList.clear();   
    }


    /**
     * Prints all clusters to standard output in a readable format.
     * Each cluster is printed as a sorted set of nodes, for example: {1,2,5} {3,4}
     */
    private void printClusters() {
        System.out.print("Clusters in this graph: ");
        for (ArrayList<Integer> cluster : clusters) {
            Collections.sort(cluster);
            int clusterSize = cluster.size();

            for (int nodeIndex = 0; nodeIndex < clusterSize; nodeIndex++) {
                if (nodeIndex == 0) {System.out.printf("{");}
                System.out.print(cluster.get(nodeIndex));
                if (nodeIndex == clusterSize - 1) {System.out.print("} ");}
                else {System.out.print(",");}
            }
        }
        System.out.println();
    }





    //------------------------------------------------------------
    // FINDING THE CLUSTERS 
    //------------------------------------------------------------

    /**
     * This uses DFS.
     * Recursively traverses connected nodes, adding them to the current cluster.
     * Visits each node's neighbors and removes visited nodes from the adjacency list.
     * @param nodes the list of neighbor nodes to explore from the current frontier
     * @param currentCluster the accumulating list representing the current cluster
     */
    private void recursivelyAddChildNodes(List<Integer> nodes, ArrayList<Integer> currentCluster) {
        for (int node : nodes) {
            List<Integer> childNodes = adjacencySearchList.get(node);
            if (childNodes == null) continue; // if this is null, then the vertex
                                            // has already been put into a cluster

            currentCluster.add(node);
            adjacencySearchList.remove(node);
            recursivelyAddChildNodes(childNodes, currentCluster);
        }
    }

    
    /**
     * Finds all clusters (connected components) in the current graph.
     * Iterates through all vertices, launching a DFS from unvisited nodes to build clusters.
     * Utilizes the classes adjacencySearchList and clusters
     */
    private void findClustersInGraph() {
        for (int vertex = 1; vertex <= numNodes; vertex++) {
            /** childNodes is the neighboring (connected) nodes of a given vertex */
            List<Integer> childNodes = adjacencySearchList.get(vertex);
            if (childNodes == null) continue; // if this is null, then the vertex
                                            // has already been put into a cluster

            // Otherwise this vertex hasn't been seen yet and should be added to a new cluster
            ArrayList<Integer> newCluster = new ArrayList<>();
            /** newCluster is to keep track of new connected node clusters */
            newCluster.add(vertex);
            adjacencySearchList.remove(vertex);
            recursivelyAddChildNodes(childNodes, newCluster);
            clusters.add(newCluster); 
        }
    }
    

    


    //------------------------------------------------------------
    // THE MAIN IDEA 
    //------------------------------------------------------------
    
    /**
     * Reads multiple graphs from an input file and processes each one.
     * For each line in the file, builds the adjacency list, finds clusters, and prints them.
     * @param filename the path to the input file containing one graph per line
     */
    public void travelMultipleGraphs(String filename) {
        // FIRST TURN THE FILE INTO A STRING 
        /** graphsAsString is meant to represent all the graphs in a file as a string */
        String graphsAsString = "";
        try {
            graphsAsString = Files.readString(Paths.get(filename)); // Into a string
                            // so that I can read the file, and then deal with each
                            // graph one at a time 
        } catch (java.io.IOException e) {
            System.out.println("Exiting");
            System.out.println(e);
            return;
        }
        
        // SECOND, TRAVERSE EACH GRAPH
        String[] lines = graphsAsString.split("\\R"); // split into array at newlines
        for (String singleGraphString : lines) {
            System.out.println("\n\nTraveling this graph: " + singleGraphString);
            clearVars(); // Ensures we're building from a clean slate
            makeAdjacencyList(singleGraphString);
            findClustersInGraph();               
            printClusters();
        }
    }    


    /**
     * Entry point of the program.
     * Determines the input filename, creates a GraphTraveler instance, 
     * and processes multiple graphs from the input file.
     * @param args Command-line arguments:
     *             - If provided, args[0] should be the input filename.
     *             - If not provided, a default filename ("SampleInput.txt") is used.
     * @return void
     */
    public static void main(String[] args) {
        // filename: stores the name of the input file to read graphs from
        String filename;

        if (args.length == 0) {
            filename = "SampleInput.txt";
        }
        else filename = args[0];

        GraphTraveler traveler = new GraphTraveler();
        traveler.travelMultipleGraphs(filename);
    }
}

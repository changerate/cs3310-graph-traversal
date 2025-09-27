/****************************************************************************/
/* Carlos Vargas                                                            */
/* Login ID: 017807647                                                      */
/* CS 3310, Fall 2025                                                       */
/* Programming Assignment 1                                                 */
/* Graph traveler class: traverses an undirected, unweighted graph.         */
/* Purpose: finds the 'clusters' – the parts o fthe graph that go together  */
/****************************************************************************/

import java.util.*;
import java.io.*;
import java.nio.file.*;

public class GraphTraveler {
    private int numNodes; 
    private Map<Integer, List<Integer>> adjacencySearchList = new HashMap<>();
    private ArrayList<ArrayList<Integer>> clusters = new ArrayList<>();    

    //----------------------------------------
    // SETTER FUNCTIONS 
    //----------------------------------------
    
    // TODO: make a method header 
    private void setNumNodes(int numNodes) {
        this.numNodes = numNodes;
    }
    

    // TODO: make a method header 
    private void setClusters(ArrayList<ArrayList<Integer>> clusters) {
        this.clusters = clusters;
    }
    

    // TODO: make a method header 
    private void buildEmptyAdjacencyList(int numNodes) {
        for (int i = 1; i <= numNodes; i++) {
            adjacencySearchList.put(i, new ArrayList<>());
        }
    }

    
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

    
    //----------------------------------------
    // GET FUNCTIONS 
    //----------------------------------------

    // TODO: make a method header 
    public int getNumNodes() {
        return numNodes;
    }
    
    // TODO: make a method header 
    public ArrayList<ArrayList<Integer>> getClusters() {
        return clusters;
    }
    
    
    // TODO: make a method header 
    public Map<Integer, List<Integer>> getAdjacencySearchList() {
        return adjacencySearchList;
    }
    
    
    
    
    
    
    //----------------------------------------
    // UTILITY FUNCTIONS 
    //----------------------------------------

    
    // TODO: make a method header
    private String saveInputFileAsString(String filename) throws IOException {
        return Files.readString(Paths.get(filename));
    }


    public void makeAdjacencyList(String graphString) {
        Scanner scanner = new Scanner(graphString);
        
        // get the number of nodes by getting the first integer of the line
        int fileNumNodes = scanner.nextInt();
        setNumNodes(fileNumNodes);
        System.out.println("numNodes: " + getNumNodes());
        
        // now set the number of nodes in the map 
        buildEmptyAdjacencyList(numNodes);
        
        // now fill out each vertex with it's adjacent nodes
        String stringPair = "";
        boolean pairsToParse = true;
        
        while (pairsToParse) {
            try {
                stringPair = scanner.next();
                int[] pairArray = stringPairToIntPair(stringPair);
                addPairsToAdjacencySearchList(pairArray);
            } catch (NoSuchElementException | NumberFormatException e) {
                pairsToParse = false;
            }
        }
        
        // System.out.println(getAdjacencySearchList());
        scanner.close();
    }

    

    // TODO: make a method header
    // Parses a string pair like "(1,2)" and returns a fixed-size int array of length 2
    private int[] stringPairToIntPair(String stringPair) {
        stringPair = stringPair.replace("(", "").replace(")", "");
        String[] parts = stringPair.split(",");
        int[] result = new int[2];
        result[0] = Integer.parseInt(parts[0]);
        result[1] = Integer.parseInt(parts[1]);
        return result;
    }


    private void clearVars() {
        setNumNodes(0);        
        for (ArrayList<Integer> inner : clusters) {
            inner.clear(); // removes all elements from each inner list
        }
        clusters.clear();
        adjacencySearchList.clear();   
    }



    // // TODO: make a method header 
    // // this method does not deal with wrong formatting gracefully 
    // public void digestAllGraphsFromFile(String filename) {
    //     try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
    //         String line = br.readLine();
    //         if (line == null) return;
    //         Scanner scanner = new Scanner(line);
            
    //         // Loop over each line in the file 
    //         while (true) {
    //             // get the number of nodes by getting the first integer of the file
    //             int fileNumNodes = scanner.nextInt();
    //             setNumNodes(fileNumNodes);
    //             System.out.println("Numnodes: " + getNumNodes());
                
    //             // now set the number of nodes in the map 
    //             buildEmptyAdjacencyList(numNodes);
                
    //             // now fill out each vertex with it's adjacent nodes
    //             String stringPair = "";
    //             boolean pairsToParse = true;
                
    //             while (pairsToParse) {
    //                 try {
    //                     stringPair = scanner.next();
    //                     int[] pairArray = stringPairToIntPair(stringPair);
    //                     addPairsToAdjacencySearchList(pairArray);
    //                 } catch (NoSuchElementException | NumberFormatException e) {
    //                     pairsToParse = false;
    //                 }
    //             }
                
    //             // System.out.println(getAdjacencySearchList());
                
    //             line = br.readLine();
    //             if (line == null) break;
    //             scanner = new Scanner(line);
    //         }
    //         scanner.close();
    //     } catch (IOException e) {
    //         System.err.println("Error reading file: " + e.getMessage());
    //     }
    // }




    //----------------------------------
    // FINDING THE CLUSTERS 
    //----------------------------------

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

    
    private void findClustersInGraph() {
        for (int vertex = 1; vertex <= numNodes; vertex++) {
            List<Integer> childNodes = adjacencySearchList.get(vertex);
            if (childNodes == null) continue; // if this is null, then the vertex
                                            // has already been put into a cluster

            // Otherwise this vertex hasn't been seen yet and should be added to a new cluster
            ArrayList<Integer> newCluster = new ArrayList<>();
            newCluster.add(vertex);
            adjacencySearchList.remove(vertex);
            recursivelyAddChildNodes(childNodes, newCluster);
            clusters.add(newCluster); 
        }
    }
    


    //----------------------------------------
    // THE MAIN IDEA 
    //----------------------------------------
    
    public void travelMultipleGraphs(String filename) {
        // FIRST TURN THE FILE INTO A STRING FOR SOME REASON??? i forgot 
        String graphsAsString = "";
        try {
            graphsAsString = saveInputFileAsString("SampleInput.txt");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        
        // SECOND, TRAVERSE EACH GRAPH
        String[] lines = graphsAsString.split("\\R");
        for (String singleGraphString : lines) {
            
            System.out.println("\n\nTraveling this graph: " + singleGraphString);
            clearVars(); // This resets the class vars to nulls
            makeAdjacencyList(singleGraphString);
            findClustersInGraph();               
            System.out.println("Clusters:   " + getClusters() + "\n");
        }
    }    
}

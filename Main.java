/****************************************************************************/
/* Carlos Vargas                                                            */
/* Login ID: 017807647                                                      */
/* CS 3310, Fall 2025                                                       */
/* Programming Assignment 1                                                 */
/* Main class: calls the main functionality of the graph traveler class.    */
/****************************************************************************/


public class Main {
    public static void main(String[] args) {
        GraphTraveler traveler = new GraphTraveler();
        traveler.travelMultipleGraphs("SampleInput.txt");
    }
}
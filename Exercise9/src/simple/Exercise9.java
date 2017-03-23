/*
 *	Keenan Johnstone
 *	11119412
 *	kbj182
 *	cmpt470 Exercise 9
 */

package simple;

import java.io.*;
import java.util.Scanner;

public interface Exercise9 {

    //statics
    int ROW_COUNT = 100;
    int COL_COUNT = 100;
    int REPORTING_FREQUENCY = 1000;
    int DEFAULT_ITERATION_COUNT = 10000;
    int CELL_COUNT = ROW_COUNT * COL_COUNT;

    int[] bufferEven = new int[CELL_COUNT];
    int[] bufferOdd = new int[CELL_COUNT];

    static void main(String Args[]) throws IOException {
        //REad from the file
        readState("input.txt", bufferEven);
        //run
        int[] finalState;
        finalState = runScenario(DEFAULT_ITERATION_COUNT);
        //return final State
        writeState("output.txt", finalState);
    }

    static void readState(String s, int[] bufferEven) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(s));
        String str = "";

        char[] charArr = new char[CELL_COUNT];

        for(int i = 0; i < ROW_COUNT; i++)
        {
            if(scanner.hasNextLine())
            {
                str += scanner.nextLine();

            }
        }
        charArr = str.toCharArray();
        for(int i = 0; i < charArr.length; i++)
        {
            if(charArr[i] == ' ')
                bufferEven[i] = 0;
            else
                bufferEven[i] = 1;
        }

    }

    static void writeState(String s, int[] finalState) throws IOException {
        BufferedWriter output;
        output = new BufferedWriter(new FileWriter(s));
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                output.write(textualEncodingForCellValue(finalState[indexForRowCol(row, col)]));
            }
            output.newLine();
        }
        output.flush();
        output.close();
    }

    static char textualEncodingForCellValue(int isLive) {
        if (isLive == 0)
            return ' ';
        else
            return '*';
    }

    // Run Conway's Game of Life for countSteps steps, on the initial state given by bufferEven, and returns a pointer to an array encoding the final state (in column-major order).
    // PRECONDITIONS:
    // 		countSteps >= 0
    //		bufferEven contains the initial state of the model (as a two uni-dimensional array encoded in row-major order, i.e., with succedssive columns for the same row arranged consecutively)
    // POSTCONDITIONS:
    //		returns a pointer to the final state of the model, encoded in the same row-major order as was bufferEven.  This final state was produced by
    //		running Conway's Game of Life for countSteps, starting with the initial state of the model
    static int[] runScenario(int countSteps) {
        int[] arrayLastState = new int[CELL_COUNT];
        for(int iTime = 0; iTime < countSteps; iTime++) {
            arrayLastState = updateSpace(iTime);

            if(iTime % REPORTING_FREQUENCY == 0)
                System.out.printf("Step%d\n", iTime);
        }
        return arrayLastState;
    }
    // Run a single iteration of Conway's Game of Life on the current space at time iTime (as determined by calling determineCurrentAndNextCells for iTime), and returns a pointer to an array encoding the final state (in column-major order)
    // PRECONDITIONS:
    // 		countSteps >= 0
    //		bufferEven contains the initial state of the model (as a two uni-dimensional array encoded in row-major order, i.e., with successive columns for the same row arranged consecutively),
    //			where the content of each patch is given by an integer, with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1.
    // POSTCONDITIONS:
    //		returns a pointer to the final state of the model, encoded in the same row-major order as was bufferEven.  This final state was produced by
    //		running Conway's Game of Life for a single step
    static int[] updateSpace(int iTime) {
        int[] currentCells;
        int[] nextCells;

        if(iTime % 2 == 0){
            currentCells = bufferEven;
            nextCells = bufferOdd;
        }
        else
        {
            currentCells = bufferOdd;
            nextCells = bufferEven;
        }
        for(int row = 0; row < ROW_COUNT; row++)
            for (int col = 0; col < ROW_COUNT; col++)
            {
                updateCell(currentCells, nextCells, row, col);
            }
        return(nextCells);
    }

    // Updates a particular patch (cell) using the rules of Conway's Game of Life, in a space whose current state is given by currentCells, updating the array nextState so as to encode the next value of that patch
    // For the cell at row row and column col in state given by currentCells, perform the rule for Conway's Game of Life and place the results in nextCells. Both cells are in row-major order).
    // PRECONDITIONS:
    // 		row >= 0, row < the number of rows in currentCells  (ROW_COUNT)
    //		col >= 0, col < the number of cols in currentCells  (COL_COUNT)
    //		length(currentCells) == length(nextCells)
    //		currentCells contains a contains a legal encoding (in row major form) of the matrix constituting the current state of the model of dimension ROW_COUNT x COL_COUNT, where the content of each patch is given by an integer,
    //			with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1.
    // POSTCONDITIONS:
    //		The patch at row row and column col in nextCells is updated to hold the result of applying the rule for Conway's Game of Life
    //
    static void updateCell(int[] currentCells, int[] nextCells, int row, int col) {
        int countSurroundingAlive = countSurroundingLiveCells(currentCells, row, col);
        int iCurrentCell = indexForRowCol(row, col);
        //Covert from any int to bool, where if it's 0 its false, else true
        boolean isCurrentCellAlive = (currentCells[iCurrentCell] != 0);

        boolean alive = computeLiveness(isCurrentCellAlive, countSurroundingAlive);
        if(alive)
            nextCells[iCurrentCell] = 1;
        else
            nextCells[iCurrentCell] = 0;
    }

    // Encodes the rule for Conway's Game of Life.
    // Given the current state of a patch and the count of neighbours that are live, returns true if the patch is occupied in the next timestep, false otherwise.
    // For this particular rule, a patch that is current empty becomes alive iff it is surrounded by exactly 3 live neighbors.  A patch that
    // is currently occupied survives iff and only iff it has between 2 and 3 live neighbours (in a Moore neighborhood)
    // PRECONDITIONS:
    // 		isCurrentCellAlive:  1 if the current cell is alive, 0 otherwise
    //		countSurroundingAlive: count of neighboring cells that are alive
    // POSTCONDITIONS:
    //		returns 1 if the cell is live in the next iteration, 0 otherwise

    static boolean computeLiveness(boolean isCurrentCellAlive, int countSurroundingAlive) {
        if (isCurrentCellAlive)
            return (countSurroundingAlive >= 2 && countSurroundingAlive <= 3);
        else
            return (countSurroundingAlive == 3);
    }

    // Returns 1 iff row and col are legal coordinates, 0 otherwise.  To be legal, we must have 0 <= row < ROW_COUNT and 0 <= col < COL_COUNT
    // PRECONDITIONS:
    //		None
    // POSTCONDITIONS:
    //		returns 1 iff 0 <= row < ROW_COUNT and 0 <= col < COL_COUNT
    static int indexForRowCol(int row, int col) {
        return row * ROW_COUNT + col;
    }

    static int countSurroundingLiveCells(int[] currentCells, int row, int col) {
        int countSurroundingAlive = 0;

        for (int deltaRow = -1; deltaRow <= 1; deltaRow++)
            for (int deltaCol = -1; deltaCol <= 1; deltaCol++)
            {
                if (deltaRow == 0 && deltaCol == 0)   // don't count the current cell of focus in the count of live neighbors!
                    continue;

                int checkingRow = row + deltaRow;
                int checkingCol = col + deltaCol;
                if (isLegalCoord(checkingRow, checkingCol))
                {
                    if (currentCells[indexForRowCol(checkingRow, checkingCol)] == 1)
                        countSurroundingAlive++;
                }
            }

        return(countSurroundingAlive);
    }

    static boolean isLegalCoord(int row, int col) {
        return ((row >= 0 && row < ROW_COUNT) && (col >= 0 && col < COL_COUNT));
    }

}

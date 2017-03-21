/*
 *	Keenan Johnstone
 *	11119412
 *	kbj182
 *	cmpt470 Exercise 9
 */

package simple;

import java.io.File;
import java.util.Scanner;

public class Exercise9 {

    //statics
    private static final int ROW_COUNT = 100;
    private static final int COL_COUNT = 100;
    private static final int REPORTING_FREQUENCY = 1000;
    private static final int DEFAULT_ITERATION_COUNT = 1000;
    private static final int CELL_COUNT = ROW_COUNT * COL_COUNT;

    private static int[] bufferEven = new int[CELL_COUNT];
    private static int[] bufferOdd = new int[CELL_COUNT];

    private static class conwaysGameOfLife {

        int runScenario(int countSteps)
        {
            return 0;
        }
    }

    public static void main(String Args[]) {
        //REad from the file...HOW?!
        Scanner scanner = new Scanner(new File("./src/input.txt"));
        int i = 0;
        while(scanner.hasNextInt()){
            bufferEven[i++] = scanner.nextInt();
        }
        
        int[] finalState = new int[CELL_COUNT];
        finalState = runScenario(DEFAULT_ITERATION_COUNT);
        //return final State... HOW?!
        
        
    }

    private static int[] runScenario(int countSteps) {
        int[] arrayLastState = new int[CELL_COUNT];

        for(int iTime = 0; iTime < countSteps; iTime++) {
            arrayLastState = updateSpace(iTime);

            if(iTime % REPORTING_FREQUENCY == 0)
                System.out.printf("Step%d\r", iTime);
        }
        return arrayLastState;
    }

    private static int[] updateSpace(int iTime) {

    }
}

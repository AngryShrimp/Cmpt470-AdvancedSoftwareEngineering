import java.util.Arrays;

/**
 * Created by Keenan on 2017-04-04.
 */
public aspect PrintStep {
    int REPORTING_FREQUENCY = 1000;

    pointcut step(): (call(* ConwaysGameOfLife.updateSpace(..)));

    after() returning(int [][]array): step(){
        //Get all of our Arguements
        for (Object arg: thisJoinPoint.getArgs())
        {
            //if Our arguement is an Integer
            if(arg instanceof Integer && (((Integer) arg).intValue() % REPORTING_FREQUENCY == 0)) {
                /*
                So here I felt it was better to subtract the number of dead cells as the original code (if I recall correctly)
                counted 0s as dead and everything else as alive, so in the unlikely event the something isnt a 1 or a 0
                This will still accurately count based on the code

                It'll do this by subtracting from the maximum size for the array each time it encounters a 0
                 */
                int numOfRows = array[0].length;
                int numOfCols = array.length;
                int liveCount = numOfCols*numOfRows;
                for (int i = 0; i < numOfCols; i++){
                    for(int j = 0; j < numOfRows; j++){
                        if(array[i][j] == 0)
                            liveCount--;
                    }
                }

                System.out.println("Step " + arg + ": " + liveCount + " cells alive.");
            }
        }
    }
}

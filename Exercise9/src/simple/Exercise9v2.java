package simple;

import java.io.IOException;

/**
 * Created by Keenan on 2017-03-22.
 */
public class Exercise9v2 implements Exercise9 {

    public boolean computeLiveness(boolean isCurrentCellAlive, int countSurroundingAlive) {
        System.out.println("Test2");
        if (isCurrentCellAlive)
            return (countSurroundingAlive >= 2 && countSurroundingAlive <= 4);
        else
            return (countSurroundingAlive == 3);
    }


}

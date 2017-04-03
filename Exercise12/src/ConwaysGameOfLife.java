import org.apache.commons.lang3.tuple.*;
import java.nio.*;
import java.nio.file.*;
import java.util.List;
import java.io.*;
import java.util.*;

interface ComputeLivenessRule
{
    public boolean apply(boolean isCurrentCellAlive, int countSurroundingAlive);
}


public class ConwaysGameOfLife
{
    final int ROW_COUNT=100;
    final int COL_COUNT=100;
    final int REPORTING_FREQUENCY=1000;
    final int DEFAULT_ITERATION_COUNT=1000;
    final int CELL_COUNT=(ROW_COUNT * COL_COUNT);

    int [][]bufferEven = new int[ROW_COUNT][COL_COUNT];
    int [][]bufferOdd = new int[ROW_COUNT][COL_COUNT];

    

    public static void main(String[] args)
    {
       ConwaysGameOfLife life = new ConwaysGameOfLife();
       
       life.simulate(args);
    }
    
    // runs Conway's game of life 

    // Based on command line arguments whose strings are given in argv,
    // and whose count is given in argc, return the count of iterations 
    // for which Conway's Game of Life should be run.
    // PRECONDITIONS:  
    // 		argc >= 1
    //		argv is of length argc
    // POSTCONDITIONS:
    //		if no command line argument is given (i.e., if argc==1), runs Conway's game of life for DEFAULT_ITERATION_COUNT iterations 
    //		if one command line argument is given, and it is a positive integer encodable as an integer, returns runs Conway's game of life for a count of iterations given by that integer
    //		if more than one command line argument is given, or if the single argument cannot be passed as an encodable integer, prints an error message giving proper syntax, and terminates without further output.
    //		For all of the cases where Conway's Game of Life are run, the initial state is read from "input.txt", and the final state is written out to "output.txt"
    public void simulate(String []args)
    {
        ImmutablePair<Integer, ComputeLivenessRule> iterationCountAndRulePair = determineArguments(args);
        
        if (iterationCountAndRulePair == null)
        {
            System.err.printf("Syntax:  'conway [iterations] [rule]', where iterations is a value integer >0.  If no iteration count is given, %d iterations are assumed, and 'rule' is one of 'Classic', 'Liveness2To4', 'Liveness2To3'.\n", DEFAULT_ITERATION_COUNT);
            System.exit(1);
        }
        else	
        {
            int countIterations = iterationCountAndRulePair.getLeft();
            ComputeLivenessRule livenessRule = iterationCountAndRulePair.getRight();
                
            readState("input.txt", bufferEven); 
            int [][]finalState = runScenario(countIterations, livenessRule);	
            writeState("output.txt", finalState); 
            
            System.exit(0);
        }
    }

    // Run Conway's Game of Life or variant (as specified by cell mapping function pFnComputeLiveness) for countSteps steps, on the initial state given by bufferEven, and returns a pointer to an array encoding the final state (in column-major order).
    // PRECONDITIONS:  
    // 		countSteps >= 0
    //		bufferEven contains the initial state of the model (as a two uni-dimensional array encoded in row-major order, i.e., with succedssive columns for the same row arranged consecutively)
    //		pFnComputeLiveness is a pointer to a function that -- given an indication if the current cell is alive, and the count of live surrounding neighbors -- returns true if the cell should stay alive, false otherwise
    // POSTCONDITIONS:
    //		returns a pointer to the final state of the model, encoded in the same row-major order as was bufferEven.  This final state was produced by 
    //		running Conway's Game of Life for countSteps, starting with the initial state of the model
    int [][]runScenario(int countSteps, ComputeLivenessRule ruleComputeLiveness)
    {
        int [][]arrayLatestState = null;

        for (int iTime = 0; iTime < countSteps; iTime++)
            {
            arrayLatestState = updateSpace(iTime, ruleComputeLiveness);
            
            if (iTime % REPORTING_FREQUENCY == 0)
                System.out.printf("Step %d\n", iTime);
            }
            
        return(arrayLatestState);
    }

    // Run a single iteration of Conway's Game of Life or variant (as specified by cell mapping function pFnComputeLiveness) on the current space at time iTime (as determined by calling determineCurrentAndNextCells for iTime), and returns a pointer to an array encoding the final state (in column-major order)  
    // PRECONDITIONS:  
    // 		countSteps >= 0
    //		bufferEven contains the initial state of the model (as a two uni-dimensional array encoded in row-major order, i.e., with succedssive columns for the same row arranged consecutively),
    //			where the content of each patch is given by an integer, with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1.
    //		pFnComputeLiveness is a pointer to a function that -- given an indication if the current cell is alive, and the count of live surrounding neighbors -- returns true if the cell should stay alive, false otherwise
    // POSTCONDITIONS:
    //		returns a pointer to the final state of the model, encoded in the same row-major order as was bufferEven.  This final state was produced by 
    //		running Conway's Game of Life for countSteps, starting with the initial state of the model
    int [][]updateSpace(int iTime, ComputeLivenessRule ruleComputeLiveness)
    {
        ImmutablePair<int [][], int [][]> currentNextCellsPair =  determineCurrentAndNextCells(iTime);

        int [][]currentCells = currentNextCellsPair.getLeft();
        int [][]nextCells = currentNextCellsPair.getRight();
        
        
        for (int row = 0; row < ROW_COUNT; row++)
            for (int col = 0; col < COL_COUNT; col++)
            {
                // update nextCells
                    updateCell(currentCells, nextCells, row, col, ruleComputeLiveness);	// another (less efficient, but still relatively clean) alternative would be to test here if it's an even or odd step, and to call "update cell" with different swapps first two arguments based on the outcome
            }

        return(nextCells);  // the cells updated in the final iteration
    }

    // Updates a particular patch (cell) using the rules of Conway's Game of Life or variant (as specified by cell mapping function pFnComputeLiveness) , in a space whose current state is given by currentCells, updating the array nextState so as to encode the next value of that patch
    // For the cell at row row and column col in state given by currentCells, perform the rule specified by cell mapping function pFnComputeLiveness  and place the results in nextCells. Both cells are in row-major order).
    // PRECONDITIONS:  
    // 		row >= 0, row < the number of rows in currentCells  (ROW_COUNT)
    //		col >= 0, col < the number of cols in currentCells  (COL_COUNT)
    //		length(currentCells) == length(nextCells) 
    //		currentCells contains a contains a legal encoding (in row major form) of the matrix constituting the current state of the model of dimension ROW_COUNT x COL_COUNT, where the content of each patch is given by an integer,
    //			with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1.
    //		pFnComputeLiveness is a pointer to a function that -- given an indication if the current cell is alive, and the count of live surrounding neighbors -- returns true if the cell should stay alive, false otherwise
    // POSTCONDITIONS:
    //		The patch at row row and column col in nextCells is updated to hold the result of applying the rule for Conway's Game of Life
    //			
    void updateCell(int [][]currentCells, int [][]nextCells, int row, int col, ComputeLivenessRule ruleComputeLiveness)
    {
        int countSurroundingAlive = countSurroundingLiveCells(currentCells, row, col);
        boolean isCurrentCellAlive = (currentCells[row][col] == 1);
        
        nextCells[row][col] = (ruleComputeLiveness.apply(isCurrentCellAlive, countSurroundingAlive) ? 1 : 0);
    }


    // Given the current cell at row row and column col in the state specified by array currentCells (in row major order, with each patch 
    // encoded by 1 if live and 0 if empty), indicate the total count of the cells that are occupied in the surrounding Moore Neighbourhood (i.e., in the 8 surrounding cells in the North, NorthEast, East, South East, South, South West, West, and North West directions)
    // PRECONDITIONS:  
    //		currentCells contains a contains a legal encoding (in row major form) of the matrix constituting the current state of the model of dimension ROW_COUNT x COL_COUNT, where the content of each patch is given by an integer,
    //			with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1.
    // 		0 <= row < the number of rows in currentCells  (ROW_COUNT)
    //		0 <= col < the number of cols in currentCells  (COL_COUNT)
    // POSTCONDITIONS:
    //		returns the total count of the cells that are occupied in the surrounding Moore Neighbourhood (i.e., in the 8 surrounding cells in the North, NorthEast, East, South East, South, South West, West, and North West directions) 
    int countSurroundingLiveCells(int [][]currentCells, int row, int col)
    {
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
                    if (currentCells[checkingRow][checkingCol] == 1)
                            countSurroundingAlive++;
                    }
                }
        
        return(countSurroundingAlive);
    }

    // Returns 1 iff row and col are legal coordinates, 0 otherwise.  To be legal, we must have 0 <= row < ROW_COUNT and 0 <= col < COL_COUNT
    // PRECONDITIONS:
    //		None
    // POSTCONDITIONS:
    //		returns 1 iff 0 <= row < ROW_COUNT and 0 <= col < COL_COUNT
    boolean isLegalCoord(int row, int col)
    {
        return ((row >= 0 && row < ROW_COUNT) &&
                (col >= 0 && col < COL_COUNT));
    }


    // Determines the arrays to use to encode the current state at time iTime.  Specifically, sets *pArrayCurrentCells to hold a pointer
    //   to the current array at that time (i.e., the array that holds the value of the current state at that time), and *pArrayNextCells to hold a pointer to the next array at that time (i.e., to the array that will hold the values for the next iteration)
    // PRECONDITIONS:
    // 		0 <= time
    // POSTCONDITIONS:
    //		*pArrayCurrentCells is set to hold a pointer to the current array at timestep iTime (i.e., the array that holds the value of the current state at that time)
    //		*pArrayNextCells is set to hold a pointer to the next array at at timestep iTime (i.e., to the array that will hold the values for the next iteration)
    ImmutablePair<int [][], int [][]> determineCurrentAndNextCells(int iTime)
    {
        boolean isEvenStep = (iTime % 2 == 0);
        
        int [][]currentSteps = isEvenStep ? bufferEven : bufferOdd;
        int [][]nextSteps = isEvenStep ? bufferOdd : bufferEven;
        
        // given that c doesn't support multiple return values, perhaps this is the least bad way of doing this

        return new ImmutablePair<int [][], int [][]>(currentSteps, nextSteps);
    }

    // Read a file (filename/path strFileName) encoding a state of a matrix of patches of dimension ROW_COUNT x COL_COUNT into array arrayCurrentCells and closes the file.
    // PRECONDITIONS:
    // 		the file whose filename/path is given by strFileName exists
    //		the file whose filename/path is given by strFileName legally encodes a current state of Conway's Game of Life, with a matrix of patches of dimension ROW_COUNT x COL_COUNT, and the contents of each patch encoded with a single character
    //		arrayCurrentCells points to an array of dimension ROW_COUNT x COL_COUNT 
    // POSTCONDITIONS:
    //		The contents of file strFileName are placed in arrayCurrentCells in row-major form 
    void readState(String strFileName, int [][]arrayCurrentCells)
    {
        try
            {
            List<String> lines = Files.readAllLines(Paths.get(strFileName));
            
            int row = 0;
            
            for (String strLine : lines)
                {
                if (strLine.length() != COL_COUNT)
                    {
                    System.err.printf("Error: Prematurely terminated file '%s'; expected a space of dimension %d x %d.  Terminating prematurely.\n", strFileName, ROW_COUNT, COL_COUNT);
                    System.exit(1);
                    }
                
                char lineArrayCharacters[] = strLine.toCharArray();

                for (int col = 0; col < COL_COUNT; col++)
                    {
                    char cEncoded = lineArrayCharacters[col];

                    if (!isLegalTextualEncoding(cEncoded))
                            {
                            System.err.printf("Error: Unexpected character at row %d, column %d in file %s.  Terminating prematurely.\n", row, col, strFileName);
                            System.exit(1);
                            }
                                    
                    arrayCurrentCells[row][col] = cellValueForTextualEncoding(cEncoded);	
                    }
                row++;
                }
                
            if (row != ROW_COUNT)
                    {
                    System.err.printf("Error: Prematurely terminated file '%s'; expected a space of dimension %d x %d.  Terminating prematurely.\n", strFileName, ROW_COUNT, COL_COUNT);
                    System.exit(1);
                    }

            }
        catch (Exception e)
            {
            System.err.printf("Error: could not open file '%s'.  Error is %s.  Aborting.\n", strFileName, e.getMessage());
            System.exit(1);
            }
    
            
    }

    // Writs the file with filename/path given by strFileName with an encoding of a state of a matrix of patches of dimension ROW_COUNT x COL_COUNT given by array arrayCurrentCells and closes the file.
    // PRECONDITIONS:
    //		the file whose filename/path is given by strFileName can be written
    //		arrayCurrentCells contains a contains a legal encoding (in row major form) of the matrix constituting the current state of the model of dimension ROW_COUNT x COL_COUNT, where the content of each patch is given by an integer,
    //			with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1.
    // POSTCONDITIONS:
    //		if the file whose filename/path is given by strFileName cannot be open for writing, the program terminates with an error message
    //		if the file whose filename/path is given by strFileName can be written, the program writes to that file an encoding of the state of the model given by arrayCurrentCells
    void  writeState(String strFileName, int [][]arrayCurrentCells)
    {
        try
            {
            OutputStream os = Files.newOutputStream(Paths.get(strFileName));
            for (int row = 0; row < ROW_COUNT; row++)
                {
                for (int col = 0; col < COL_COUNT; col++)
                    os.write(textualEncodingForCellValue(arrayCurrentCells[row][col]));

                os.write('\n');    // row separator
                }
            }
        catch (Exception e)
            {
            System.err.printf("Error: could not open file '%s' for writing.  Error is %s.  Aborting.\n", strFileName, e.getMessage());
            System.exit(1);
            }
    }

    // Given the boolean isLive that is true if the cell is live and false otherwise, returns a single-character encoding of that value
    // PRECONDITIONS:
    //		isLive is true if the cell is alive, false otherwise
    // POSTCONDITIONS:
    //		returns the character encoding the cell contents
    char textualEncodingForCellValue(int isLive)
    {
        if (isLive == 0)
            return ' ';
        else
            return '*';
    }

    // Determines if the given character is a legal encoding of a cell, as used in the files to encode spaces
    // Returns true if the given character is a legal encoding of a cell value, false otherwise
    // PRECONDITIONS:
    //		None
    // POSTCONDITIONS:
    //		Returns true if the given character is a legal encoding of a cell value, false otherwise
    boolean isLegalTextualEncoding(char cEncoded)
    {
        return(cEncoded == ' ' || cEncoded == '*');
    }

    // Determines the value in the encoding integer array for the given encoded cell value
    // PRECONDITIONS:
    //		cEncoded is a legal cell value (i.e., isLegalTextualEncoding(cEncoded) is true)
    // POSTCONDITIONS:
    //		Returns the integer value used to encode the cell (patch) value whose character encoding is given by cEncoded.
    int cellValueForTextualEncoding(char cEncoded)
    {
        return cEncoded == '*' ? 1 : 0;
    }	

    // Based on command line arguments whose strings are given in argv,
    // and whose count is given in argc, return the count of iterations 
    // for which Conway's Game of Life should be run.
    // PRECONDIITONS:  
    // 		argc >= 1
    //		argv is of length argc
    // POSTCONDITIONS:
    //		if no command line argument is given (i.e., if argc==1), returns DEFAULT_ITERATION_COUNT
    //		if one command line argument is given, and it is a positive integer encodable as an integer, returns that integer
    //		if more than one command line argument is given, or if the single argument cannot be passed as an encodable integer, return -1		
    ImmutablePair<Integer, ComputeLivenessRule>  determineArguments(String args[])
    {
        int countArgumentsNotIncludingFilename = args.length;
        
        if (countArgumentsNotIncludingFilename == 0)
        {
            // if there is no numeric argument specified, then use the
            // default iteration count.
            return(new ImmutablePair<Integer, ComputeLivenessRule>(DEFAULT_ITERATION_COUNT, new ComputeLivenessClassic()));
        }
        else 
        {
            try
            {
                String strIterations = args[0];

                int countIterations = Integer.parseInt(strIterations);  // Attempt to parse the first argument
                // return any iteration legal iteration count (i.e., count > 1)
                // NB: If the command line argument is not a number, it atoi will
                // return 0.
                // If no such legal iteration count is given, fall through
                if (countIterations <= 0)
                    return(null);  // indicate an illeal value
                    
                if (countArgumentsNotIncludingFilename == 1)
                    {
                    // ok, all we have is 1 command line argument.  Just use the standard rule
                    return(new ImmutablePair<Integer, ComputeLivenessRule>(countIterations, new ComputeLivenessClassic()));
                    }
                else if (countArgumentsNotIncludingFilename == 2)
                    {
                    // ok, after the iteration count, we have the rule specification
                    ComputeLivenessRule livenessRule = determineRuleFunctionForCommandLineArgument(args[1]);
                    if (livenessRule == null)
                        return null;
                    else
                        return(new ImmutablePair<Integer, ComputeLivenessRule>(countIterations, livenessRule));
                    }
                else
                    return null;		// signal that too many arguments are given
            }
            catch (Exception e)
            {
                return null;                    // signal that something is wrong with the arguments
            }
        }	
    }


    
    static Hashtable<String, ComputeLivenessRule> commandLineDispatchTable = new Hashtable<String, ComputeLivenessRule>();
    
    static
    {
        commandLineDispatchTable.put("Classic", new ComputeLivenessClassic());
        commandLineDispatchTable.put("Liveness2To4", new ComputeLivenessRemainAlive2Thru4());
        commandLineDispatchTable.put("Liveness1To3", new ComputeLivenessRemainAlive1Thru3());
    }
        
    // Returns the rule for the given command line argument.
    // If no matching rule is found, return NULL;
    // PRECONDITIONS:  
    //      strCommandLineArg is non-null
    // POSTCONDITIONS:
    //      returns the rule (as a ComputeLivenessRule) associated with the command line argument strCommandLineArg, and null if no such
    //      command line argument is recognized
    
    
    ComputeLivenessRule determineRuleFunctionForCommandLineArgument(String strCommandLineArg)
    { 
        return commandLineDispatchTable.get(strCommandLineArg);
    }
    
    
    
    
    static class ComputeLivenessClassic implements ComputeLivenessRule
    {
   // Encodes the classic rule for Conway's Game of Life.
    // Given the current state of a patch and the count of neighbours that are live, returns true if the patch is occupied in the next timestep, false otherwise.
    // For this particular rule, a patch that is current empty becomes alive iff it is surrounded by exactly 3 live neighbors.  A patch that 
    // is currently occupied survives iff and only iff it has between 2 and 3 live neighbours (in a Moore neighborhood)
    // PRECONDITIONS:  
    // 		isCurrentCellAlive:  1 if the current cell is alive, 0 otherwise
    //		countSurroundingAlive: count of neighboring cells that are alive
    // POSTCONDITIONS:
    //		returns 1 if the cell is live in the next iteration, 0 otherwise 
 
        public boolean apply(boolean isCurrentCellAlive, int countSurroundingAlive)
        {
            if (isCurrentCellAlive)
                return (countSurroundingAlive >= 2 && countSurroundingAlive <= 3);
            else
                return (countSurroundingAlive == 3);
            
        }
    }

    static class ComputeLivenessRemainAlive2Thru4 implements ComputeLivenessRule
    {
    // Encodes an alternative rule for Conway's Game of Life allowing survival of a live cell if have between 2 and 4 neighbors alive; the rule for colonization of empty patches remains unchanged.
    // Given the current state of a patch and the count of neighbours that are live, returns true if the patch is occupied in the next timestep, false otherwise.
    // For this particular rule, a patch that is current empty becomes alive iff it is surrounded by exactly 3 live neighbors.  A patch that 
    // is currently occupied survives iff and only iff it has between 2 and 4 live neighbours (in a Moore neighborhood)
    // PRECONDITIONS:  
    // 		isCurrentCellAlive:  1 if the current cell is alive, 0 otherwise
    //		countSurroundingAlive: count of neighboring cells that are alive
    // POSTCONDITIONS:
    //		returns 1 if the cell is live in the next iteration, 0 otherwise 
 
        public boolean apply(boolean isCurrentCellAlive, int countSurroundingAlive)
        {
            if (isCurrentCellAlive)
                return (countSurroundingAlive >= 2 && countSurroundingAlive <= 4);
            else
                return (countSurroundingAlive == 3);
        }
    }
    
 
    static class ComputeLivenessRemainAlive1Thru3 implements ComputeLivenessRule
    {
    // Encodes an alternative rule for Conway's Game of Life allowing survival of a live cell if have between 1 and 3 neighbors alive; the rule for colonization of empty patches remains unchanged.
    // Given the current state of a patch and the count of neighbours that are live, returns true if the patch is occupied in the next timestep, false otherwise.
    // For this particular rule, a patch that is current empty becomes alive iff it is surrounded by exactly 3 live neighbors.  A patch that 
    // is currently occupied survives iff and only iff it has between 1 and 3 live neighbours (in a Moore neighborhood)
    // PRECONDITIONS:  
    // 		isCurrentCellAlive:  1 if the current cell is alive, 0 otherwise
    //		countSurroundingAlive: count of neighboring cells that are alive
    // POSTCONDITIONS:
    //		returns 1 if the cell is live in the next iteration, 0 otherwise 
 
        public boolean apply(boolean isCurrentCellAlive, int countSurroundingAlive)
        {
            if (isCurrentCellAlive)
                return (countSurroundingAlive >= 1 && countSurroundingAlive <= 3);
            else
                return (countSurroundingAlive == 3);
        }
    }
    
    
}
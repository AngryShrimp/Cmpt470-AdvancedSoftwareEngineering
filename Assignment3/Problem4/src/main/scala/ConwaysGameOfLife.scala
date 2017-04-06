import scala.io.Source;
import java.time.temporal.WeekFields.ComputedDayOfField


object ConwaysGameOfLife  extends App
{

    val ruleClassic: (Boolean, Int) => Boolean =  
        (isCurrentCellAlive: Boolean , countSurroundingAlive: Int ) => {
            if(isCurrentCellAlive) {(countSurroundingAlive >= 2 && countSurroundingAlive <= 3);}
            else {(countSurroundingAlive == 3);}
        };
    val rule2to4: (Boolean, Int) => Boolean =  
        (isCurrentCellAlive: Boolean , countSurroundingAlive: Int ) => {
            if(isCurrentCellAlive) {(countSurroundingAlive >= 2 && countSurroundingAlive <= 4);}
            else {(countSurroundingAlive == 3);}
        };
    val rule1to3: (Boolean, Int) => Boolean =  
        (isCurrentCellAlive: Boolean , countSurroundingAlive: Int ) => {
            if(isCurrentCellAlive) {(countSurroundingAlive >= 1 && countSurroundingAlive <= 3);}
            else {(countSurroundingAlive == 3);}
        };


    val mapLivenessRuleForCommandLineArgument : Map[String, (Boolean, Int) => Boolean] = Map(
            "Classic" -> ruleClassic,
            "Liveness2To4"-> rule2to4,
            "Liveness1To3" -> rule1to3 
            )

    val ROW_COUNT=100
    val COL_COUNT=100
    val REPORTING_FREQUENCY=1000
    val DEFAULT_ITERATION_COUNT=10000
    val CELL_COUNT=(ROW_COUNT * COL_COUNT)

    private val bufferEven = Array.ofDim[Int](ROW_COUNT, COL_COUNT)
    private val bufferOdd = Array.ofDim[Int](ROW_COUNT, COL_COUNT)

    simulate(args)
    
    // runs Conway's game of life 

    // Based on command line arguments whose strings are given in args ,
    // perform Conway's Game of Life 
    // PRECONDITIONS:  
    // 		args is non-NULL
    // POSTCONDITIONS:
    //		if no command line argument is given (i.e., if argc==1), runs Conway's game of life for DEFAULT_ITERATION_COUNT iterations 
    //		if one command line argument is given, and it is a positive integer encodable as an integer, returns runs Conway's game of life for a count of iterations given by that integer
    //		if more than one command line argument is given, or if the single argument cannot be passed as an encodable integer, prints an error message giving proper syntax, and terminates without further output.
    //		For all of the cases where Conway's Game of Life are run, the initial state is read from "input.txt", and the final state is written out to "output.txt"
    def simulate(args: Array[String])
    {
        readState("input.txt", bufferEven);
        var finalState = Array.ofDim[Int](ROW_COUNT, COL_COUNT);
        try{
            finalState = runScenario(args(0).toInt, mapLivenessRuleForCommandLineArgument(args(1)));
        }
        catch {
            case e: Exception => finalState = runScenario(DEFAULT_ITERATION_COUNT, ruleClassic);
        }

        writeState("output.txt",finalState)
    }

    // Run Conway's Game of Life or variant (as specified by cell mapping function pFnComputeLiveness) for countSteps steps, on the initial state given by bufferEven, and returns a pointer to an array encoding the final state (in column-major order).
    // PRECONDITIONS:  
    // 		countSteps >= 0
    //		bufferEven contains the initial state of the model (as a two uni-dimensional array encoded in row-major order, i.e., with succedssive columns for the same row arranged consecutively)
    //		ruleComputeLiveness is is a function (closure) that -- given an indication if the current cell is alive, and the count of live surrounding neighbors -- returns true if the cell should stay alive, false otherwise
    // POSTCONDITIONS:
    //		returns a reference to the final state of the model, encoded in the same row-major order as was bufferEven.  This final state was produced by 
    //		running Conway's Game of Life for countSteps, starting with the initial state of the model
    def runScenario(countSteps: Int, ruleComputeLiveness: (Boolean, Int) => Boolean ) : Array[Array[Int]] =
    {
        var finalState = Array.ofDim[Int](ROW_COUNT, COL_COUNT);
        var iTime = 0;
        for(iTime <- 0 to countSteps){
            if(iTime % REPORTING_FREQUENCY == 0){
                println("Step " + iTime);
            }
            finalState = updateSpace(iTime, ruleComputeLiveness);
        }
        return finalState;
    }

    // Run a single iteration of Conway's Game of Life or variant (as specified by cell mapping function pFnComputeLiveness) on the current space at time iTime (as determined by calling determineCurrentAndNextCells for iTime), and returns a pointer to an array encoding the final state (in column-major order)  
    // PRECONDITIONS:  
    // 		countSteps >= 0
    //		bufferEven (if iTime % 2 == 0) or bufferOdd (if iTime % 2 != 0) contains the current state of the model (as a two uni-dimensional array encoded in row-major order, i.e., with succedssive columns for the same row arranged consecutively),
    //			where the content of each patch is given by an integer, with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1.
    //		ruleComputeLiveness is is a function (closure) that -- given an indication if the current cell is alive, and the count of live surrounding neighbors -- returns true if the cell should stay alive, false otherwise
    // POSTCONDITIONS:
    //		returns a reference to the final state of the model, encoded in the same row-major order as was bufferEven.  This final state was produced by 
    //		running Conway's Game of Life for countSteps, starting with the initial state of the model
    def updateSpace(iTime: Int, ruleComputeLiveness: (Boolean, Int) => Boolean ) : Array[Array[Int]] =
    {
        val currentAndNextCellsPair = determineCurrentAndNextCells(iTime);
        var currentCells = currentAndNextCellsPair._1;
        var nextCells = currentAndNextCellsPair._2;

        var i = 0;
        var j = 0;

        for(i <- 0 to ROW_COUNT-1){
            for(j <- 0 to COL_COUNT-1){
                updateCell(currentCells, nextCells, i, j, ruleComputeLiveness)
            }
        }

        return nextCells;
    }

    // Updates a particular patch (cell) using the rules of Conway's Game of Life or variant (as specified by cell mapping function pFnComputeLiveness) , in a space whose current state is given by currentCells, updating the array nextState so as to encode the next value of that patch
    // For the cell at row row and column col in state given by currentCells, perform the rule specified by cell mapping function pFnComputeLiveness  and place the results in nextCells. Both cells are in row-major order).
    // PRECONDITIONS:  
    // 		row >= 0, row < the number of rows in currentCells  (ROW_COUNT)
    //		col >= 0, col < the number of cols in currentCells  (COL_COUNT)
    //		size(currentCells) == size(nextCells) 
    //		size(currentCells(0)) == size(nextCells(0)) 
    //		currentCells contains a contains a legal encoding (in row major form) of the matrix constituting the current state of the model of dimension ROW_COUNT x COL_COUNT, where the content of each patch is given by an integer,
    //			with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1.
    //		ruleComputeLiveness is is a function (closure) that -- given an indication if the current cell is alive, and the count of live surrounding neighbors -- returns true if the cell should stay alive, false otherwise
    // POSTCONDITIONS:
    //		The patch at row row and column col in nextCells is updated to hold the result of applying the rule for Conway's Game of Life
    //			
    def updateCell(currentCells: Array[Array[Int]], nextCells: Array[Array[Int]], row: Int, col: Int, ruleComputeLiveness: (Boolean, Int) => Boolean )
    {
        var countSurroundingAlive = countSurroundingLiveCells(currentCells, row, col);
        var isCurrentCellAlive = (currentCells(row)(col) == 1)

        if(ruleComputeLiveness.apply(isCurrentCellAlive, countSurroundingAlive)){
            nextCells(row)(col) = 1;
        }
        else{
            nextCells(row)(col) = 0;
        }
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
    def countSurroundingLiveCells(currentCells : Array[Array[Int]], row: Int, col: Int) : Integer =
    {
        var countSurroundingAlive = 0;
        var deltaRow = 0;
        var deltaCol = 0;

        for(deltaRow <- (-1) to 1){
            for(deltaCol <- (-1) to 1){
                if(!(deltaCol == 0 && deltaRow == 0)){
                    val checkingRow = row + deltaRow;
                    val checkingCol = col + deltaCol;

                    if(isLegalCoord(checkingRow, checkingCol)){
                        if(currentCells(checkingRow)(checkingCol) == 1){
                            countSurroundingAlive = countSurroundingAlive + 1;
                        }
                    }
                }

            }
        }

        return countSurroundingAlive;
    }

    // Returns 1 iff row and col are legal coordinates, 0 otherwise.  To be legal, we must have 0 <= row < ROW_COUNT and 0 <= col < COL_COUNT// Returns 1 iff row and col are legal coordinates, 0 otherwise.  To be legal, we must have 0 <= row < ROW_COUNT and 0 <= col < COL_COUNT

    // PRECONDITIONS:
    //		None
    // POSTCONDITIONS:
    //		returns 1 iff 0 <= row < ROW_COUNT and 0 <= col < COL_COUNT
    def isLegalCoord(row: Int, col: Int): Boolean = (row >= 0 && row < ROW_COUNT) && (col >= 0 && col < COL_COUNT)

    // Determines the arrays to use to encode the current state at time iTime.  Specifically, returns a pair whose first element is 
    //   (a reference to) an array holding the value of the current state of the model, and whose second element is (a reference to)
    //  the next array (i.e., to the array that will hold the values for the next iteration)
    // PRECONDITIONS:
    // 		0 <= iTime
    // POSTCONDITIONS:
    //		returns a pair of 
    //            the current array at timestep iTime (i.e., the array that holds the value of the current state at that time)
    //            the next array at at timestep iTime (i.e., to the array that will hold the values for the next iteration)
    def determineCurrentAndNextCells(iTime: Int): (Array[Array[Int]], Array[Array[Int]]) =
    {
        var currentSteps = Array.ofDim[Int](ROW_COUNT, COL_COUNT);
        var nextSteps = Array.ofDim[Int](ROW_COUNT, COL_COUNT);

        if(iTime % 2 == 0){
            currentSteps = bufferEven;
            nextSteps = bufferOdd;
        }
        else{
            currentSteps = bufferOdd;
            nextSteps = bufferEven;
        }
        return (currentSteps, nextSteps);
    }

    // Read a file (filename/path strFileName) encoding a state of a matrix of patches of dimension ROW_COUNT x COL_COUNT into array arrayCurrentCells and closes the file.
    // PRECONDITIONS:
    // 		the file whose filename/path is given by strFileName exists
    //		the file whose filename/path is given by strFileName legally encodes a current state of Conway's Game of Life, with a matrix of patches of dimension ROW_COUNT x COL_COUNT, and the contents of each patch encoded with a single character
    //		arrayCurrentCells points to an array of dimension ROW_COUNT x COL_COUNT 
    // POSTCONDITIONS:
    //		The contents of file strFileName are placed in arrayCurrentCells in row-major form, with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1. 

    def readState(strFileName: String , arrayCurrentCells: Array[Array[Int]])
    {
      val linesIterator = scala.io.Source.fromFile(strFileName, "UTF-8").getLines
 
      var row = 0
            
      for (strLine <- linesIterator)
          {
          if (strLine.length() != COL_COUNT)
              {
              Console.err.printf(s"Error: Prematurely terminated file '$strFileName'; expected a space of dimension $ROW_COUNT x $COL_COUNT.  Terminating prematurely.\n");
              System.exit(1)
              }
          
          val lineArrayCharacters: Array[Char] = strLine.toCharArray()

          for (col <- 0 to COL_COUNT-1)
              {
              val cEncoded: Char = lineArrayCharacters(col)

              if (!isLegalTextualEncoding(cEncoded))
                      {
                      Console.err.printf(s"Error: Unexpected character at row $row, column $col in file $strFileName.  Terminating prematurely.\n")
                      System.exit(1)
                      }
                              
              arrayCurrentCells(row)(col) = cellValueForTextualEncoding(cEncoded)
              }
          row += 1
          }
          
      if (row != ROW_COUNT)
              {
              Console.err.printf(s"Error: Prematurely terminated file '$strFileName'; expected a space of dimension $ROW_COUNT x $COL_COUNT.  Terminating prematurely.\n")
              System.exit(1)
              }

    }

    // Writs the file with filename/path given by strFileName with an encoding of a state of a matrix of patches of dimension ROW_COUNT x COL_COUNT given by array arrayCurrentCells and closes the file.
    // PRECONDITIONS:
    //		the file whose filename/path is given by strFileName can be written
    //		arrayCurrentCells contains a contains a legal encoding (in row major form) of the matrix constituting the current state of the model of dimension ROW_COUNT x COL_COUNT, where the content of each patch is given by an integer, with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1.
    // POSTCONDITIONS:
    //		if the file whose filename/path is given by strFileName cannot be open for writing, the program terminates with an error message
    //		if the file whose filename/path is given by strFileName can be written, the program writes to that file an encoding of the state of the model given by arrayCurrentCells
    def writeState(strFileName: String , arrayCurrentCells: Array[Array[Int]])
    {
        try
            {
            val os = new java.io.PrintWriter(strFileName)
            for (row <- 0 to ROW_COUNT-1)
                {
                //Console.printf("Outputting line %d\n", row)

                for (col <- 0 to COL_COUNT-1)
                    os.write(textualEncodingForCellValue(arrayCurrentCells(row)(col)))

                os.write('\n')    // row separator
                }
            os.close()
            }
        catch 
            {
            case e: Exception =>
              val strExceptionMsg = e.getMessage()
              Console.err.printf(s"Error: could not open or write to file '$strFileName' for writing.  Error is $strExceptionMsg.  Aborting.\n")
              System.exit(1)
            }
      }

    // Given the boolean isLive that is true if the cell is live and false otherwise, returns a single-character encoding of that value
    // PRECONDITIONS:
    //		isLive is true if the cell is alive, false otherwise
    // POSTCONDITIONS:
    //		returns the character encoding the cell contents
    def textualEncodingForCellValue(isLive: Int): Char =  if (isLive == 0) ' ' else '*'

    // Determines if the given character is a legal encoding of a cell, as used in the files to encode spaces
    // Returns true if the given character is a legal encoding of a cell value, false otherwise
    // PRECONDITIONS:
    //		None
    // POSTCONDITIONS:
    //		Returns true if the given character is a legal encoding of a cell value, false otherwise
    def isLegalTextualEncoding(cEncoded: Char): Boolean = (cEncoded == ' ' || cEncoded == '*')
    

    // Determines the value in the encoding integer array for the given encoded cell value
    // PRECONDITIONS:
    //		cEncoded is a legal cell value (i.e., isLegalTextualEncoding(cEncoded) is true)
    // POSTCONDITIONS:
    //		Returns the integer value used to encode the cell (patch) value whose character encoding is given by cEncoded.
    def cellValueForTextualEncoding(cEncoded : Char) = if (cEncoded == '*') 1 else 0
   

    // Based on command line arguments whose strings are given in args,
    // return the count of iterations for which Conway's Game of Life should be run,
    // and the rule that should be used in that run.  In both cases, these return values are 
    // as characterized by the command-line arguments.
    // PRECONDIITONS:  
    // 		args is non-null
    // POSTCONDITIONS:
    //		if no command line argument is given (i.e., if argc==1), returns DEFAULT_ITERATION_COUNT
    //		if one command line argument is given, and it is a positive integer encodable as an integer, returns that integer
    //		if more than one command line argument is given, or if the single argument cannot be passed as an encodable integer, return -1		
   def determineArguments(args: Array[String]) :  (Integer, (Boolean, Int) => Boolean) =
    {
        val countArgumentsNotIncludingFilename: Int = args.length
        
        if (countArgumentsNotIncludingFilename == 0)
        {
            // if there is no numeric argument specified, then use the
            // default iteration count.
            (DEFAULT_ITERATION_COUNT, ruleClassic)
        }
        else 
        {
            try
            {
                val strIterations = args(0)

                val countIterations = Integer.parseInt(strIterations)  // Attempt to parse the first argument
                // return any iteration legal iteration count (i.e., count > 1)
                // NB: If the command line argument is not a number, it atoi will
                // return 0.
                // If no such legal iteration count is given, fall through
                if (countIterations <= 0)
                    (-1, null)  // indicate an illegal value                    
                else if (countArgumentsNotIncludingFilename == 1)
                    {
                    // ok, all we have is 1 command line argument.  Just use the standard rule
                    (countIterations, ruleClassic)
                    }
                else if (countArgumentsNotIncludingFilename == 2)
                    {
                    // ok, after the iteration count, we have the rule specification
                    val livenessRule = mapLivenessRuleForCommandLineArgument(args(1))
                    if (livenessRule == null)
                        (-1, null)
                    else
                        (countIterations, livenessRule)
                    }
                else
                    return null	// signal that too many arguments are given
            }
            catch 
            {
              case e: Exception =>
                null                    // signal that something is wrong with the arguments
            }
        }	
    }
}
    

  

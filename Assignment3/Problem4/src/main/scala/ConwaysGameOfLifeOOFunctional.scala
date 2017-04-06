

object ConwaysGameOfLifeOOFunctional extends App
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
    val DEFAULT_ITERATION_COUNT=1000
    val CELL_COUNT=(ROW_COUNT * COL_COUNT)

    simulate(args)

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
        var space = readState("input.txt");
        var finalState = Array.ofDim[Int](ROW_COUNT, COL_COUNT);
        try{
            finalState = runScenario(args(0).toInt, space, mapLivenessRuleForCommandLineArgument(args(1)));
        }
        catch {
            case e: Exception => finalState = runScenario(DEFAULT_ITERATION_COUNT, space, ruleClassic);
        }

        writeState("output.txt",finalState)
    }

    // Run Conway's Game of Life or variant (as specified by cell mapping function pFnComputeLiveness) for countSteps steps, on the initial state given by bufferEven, and returns a pointer to an array encoding the final state (in column-major order).
    // PRECONDITIONS:  
    // 		countSteps >= 0
    //		space is non-null
    //		space contains (a reference to) the initial state of the model (as a two uni-dimensional array encoded in row-major order, i.e., with successive columns for the same row arranged consecutively)
    //		ruleComputeLiveness is is a function (closure) that -- given an indication if the current cell is alive, and the count of live surrounding neighbors -- returns true if the cell should stay alive, false otherwise
    // POSTCONDITIONS:
    //		returns (a reference to) the updated space, encoded in the same row-major order as was space.  This final state was produced by 
    //		running Conway's Game of Life for countSteps, starting with the initial state of the model
    def runScenario(countSteps: Int, space:Array[Array[Int]], ruleComputeLiveness: (Boolean, Int) => Boolean ) : Array[Array[Int]]  =
    {
        var finalState = Array.ofDim[Int](ROW_COUNT, COL_COUNT);
        var iTime = 0;
        for(iTime <- 0 to countSteps){
            if(iTime % REPORTING_FREQUENCY == 0){
                println("Step " + iTime);
            }
            finalState = updateSpace(iTime, space, ruleComputeLiveness);
        }
        return finalState;
    }

    // Run a single iteration of Conway's Game of Life or variant (as specified by cell mapping function pFnComputeLiveness) on the current space at time iTime (as determined by calling determineCurrentAndNextCells for iTime), and returns a pointer to an array encoding the final state (in column-major order)  
    // PRECONDITIONS:  
    // 		countSteps >= 0
    //		space is non-null
    //		space contains the initial state of the model (as a two uni-dimensional array encoded in row-major order, i.e., with successive columns for the same row arranged consecutively)
    //		ruleComputeLiveness is is a function (closure) that -- given an indication if the current cell is alive, and the count of live surrounding neighbors -- returns true if the cell should stay alive, false otherwise
    // POSTCONDITIONS:
    //		returns (a reference to) the next state of the model, encoded in the same row-major order as was space.  This final state was produced by 
    //		running Conway's Game of Life for a single step from the context of space
    def updateSpace(iTime: Int, space:Array[Array[Int]], ruleComputeLiveness: (Boolean, Int) => Boolean) : Array[Array[Int]] = 
    {
        var i = 0;
        var j = 0;

        var newSpace = Array.ofDim[Int](ROW_COUNT, COL_COUNT)

        def updateCell(row: Int, col: Int) = computeIsAlive(row, col, space, ruleComputeLiveness);

        for(i <- 0 to ROW_COUNT-1){
            for(j <- 0 to COL_COUNT-1){
                newSpace = Array.tabulate(ROW_COUNT,COL_COUNT)((i, j) => updateCell(i, j))
            }
        }

        return newSpace;
    }

    // Determines if a particular patch (cell) is to remain alive in the next step using the rules of Conway's Game of Life or variant (as specified by cell mapping function pFnComputeLiveness) , in a space whose current state is given by space
    // For the cell at row row and column col in state given by currentCells, perform the rule specified by cell mapping function pFnComputeLiveness  and place the results in nextCells. Both cells are in row-major order).
    // PRECONDITIONS:  
    // 		row >= 0, row < the number of rows in currentCells  (ROW_COUNT)
    //		col >= 0, col < the number of cols in currentCells  (COL_COUNT)
    //		space is non-null
    //		space contains a contains a legal encoding (in row major form) of the matrix constituting the current state of the model of dimension ROW_COUNT x COL_COUNT, where the content of each patch is given by an integer, with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1.
    //		ruleComputeLiveness is a pointer to a function that -- given an indication if the current cell is alive, and the count of live surrounding neighbors -- returns true if the cell should stay alive, false otherwise
    // POSTCONDITIONS:
    //		The function returns the value (0 or 1) at the patch at row row and column col based on the result of applying the rule for Conway's Game of Life
    //			
    def computeIsAlive(row: Int, col: Int, space: Array[Array[Int]], ruleComputeLiveness: (Boolean, Int) => Boolean) : Int = 
    {
        var countSurroundingAlive = countSurroundingLiveCells(space, row, col);
        var isCurrentCellAlive = (space(row)(col) == 1)

        if(ruleComputeLiveness.apply(isCurrentCellAlive, countSurroundingAlive)){
            return 1;
        }
        else{
            return 0;
        }
    }


    // Given the current cell at row row and column col in the state specified by array currentCells (in row major order, with each patch 
    // encoded by 1 if live and 0 if empty), indicate the total count of the cells that are occupied in the surrounding Moore Neighbourhood (i.e., in the 8 surrounding cells in the North, NorthEast, East, South East, South, South West, West, and North West directions)
    // PRECONDITIONS:  
    //		currentCells contains a contains a legal encoding (in row major form) of the matrix constituting the current state of the model of dimension ROW_COUNT x COL_COUNT, where the content of each patch is given by an integer,
    //			with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1.
    // 		0 <= row < the number of rows in currentCells  (ROW_COUNT)
//		0 <= col < the number of cols in currentCells  (COL_COUNT)
    //		space is non-null
//		space contains a contains a legal encoding (in row major form) of the matrix constituting the current state of the model of dimension ROW_COUNT x COL_COUNT, where the content of each patch is given by an integer, with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1.
    // POSTCONDITIONS:
    //		returns the total count of the cells that are occupied in the surrounding Moore Neighbourhood (i.e., in the 8 surrounding cells in the North, NorthEast, East, South East, South, South West, West, and North West directions) 
    def countSurroundingLiveCells(space: Array[Array[Int]], row: Int, col: Int) : Integer =
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
                        if(space(checkingRow)(checkingCol) == 1){
                            countSurroundingAlive = countSurroundingAlive + 1;
                        }
                    }
                }

            }
        }

        return countSurroundingAlive;
    }

    // PRECONDITIONS:
    //		None
    // POSTCONDITIONS:
    //		returns 1 iff 0 <= row < ROW_COUNT and 0 <= col < COL_COUNT
    def isLegalCoord(row: Int, col: Int): Boolean = (row >= 0 && row < ROW_COUNT) && (col >= 0 && col < COL_COUNT)


    // Read and return a space from a file (filename/path strFileName) encoding a state of a matrix of patches of dimension ROW_COUNT x COL_COUNT, and close the file.
    // PRECONDITIONS:
    // 		the file whose filename/path is given by strFileName exists
    //		the file whose filename/path is given by strFileName legally encodes a current state of Conway's Game of Life, with a matrix of patches of dimension ROW_COUNT x COL_COUNT, and the contents of each patch encoded with a single character
    // POSTCONDITIONS:
    //		The contents of file strFileName returned in row-major form, with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1. 
    def readState(strFileName: String) : Array[Array[Int]] =
    {
      val linesIterator = scala.io.Source.fromFile(strFileName, "UTF-8").getLines
 
      var row = 0
      val arrayCurrentCells = Array.ofDim[Int](ROW_COUNT, COL_COUNT)
      
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

      arrayCurrentCells
    }

    // Writs the file with filename/path given by strFileName with an encoding of a state of a matrix of patches of dimension ROW_COUNT x COL_COUNT given by array arrayCurrentCells and closes the file.
    // PRECONDITIONS:
    //		the file whose filename/path is given by strFileName can be written
    //		arrayCurrentCells contains a contains a legal encoding (in row major form) of the matrix constituting the current state of the model of dimension ROW_COUNT x COL_COUNT, where the content of each patch is given by an integer, with an empty patch denoted by 0, and an occupied (i.e., live) patch indicated by 1.
    // POSTCONDITIONS:
    //		if the file whose filename/path is given by strFileName cannot be open for writing, the program terminates with an error message
    //		if the file whose filename/path is given by strFileName can be written, the program writes to that file an encoding of the state of the model given by arrayCurrentCells
    def writeState(strFileName: String, arrayCurrentCells: Array[Array[Int]])
    {
        try
            {
            val os = new java.io.PrintWriter(strFileName)
            for (row <- 0 to ROW_COUNT-1)
                {
                Console.printf("Outputting line %d\n", row)

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

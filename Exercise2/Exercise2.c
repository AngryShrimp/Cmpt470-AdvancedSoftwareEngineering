/*
 *	Keenan Johnstone
 *	11119412
 *	kbj182
 *	cmpt470 Exercise 1
 */


#define ROW_COUNT (100)
#define COL_COUNT (100)
#define REPORTING_FREQUENCY	(1000)
#include <stdio.h>
#include <assert.h>

typedef int bool;

int main();
int *runScenario(int countSteps);
int *updateSpace(int iTime);
void updateCell(int *currentCells, int *nextCells, int row, int col);
int computeLiveness(int isCurrentCellAlive, int countSurroundingAlive);
int countSurroundingLiveCells(int *currentCells, int row, int col);
int indexForRowCol(int row, int col);
void determineCurrentAndNextCells(int iTime, int **pArrayCurrentCells, int **pArrayNextCells);
bool isLegalCoord(int row, int col);
void readState(char *strFileName, int *arrayCurrentCells);
void  writeState(char *strFileName, int *arrayCurrentCells);
bool isLegalTextualEncoding(char cEncoded);
int cellValueForTextualEncoding(char cEncoded);
char textualEncodingForCellValue(int cellValue);
void exit(int);

# define CELL_COUNT	(ROW_COUNT * COL_COUNT)

int bufferEven[CELL_COUNT];
int bufferOdd[CELL_COUNT];

/*******************
 *	Name:
 *		-main
 *	Description:
 *		-Read the input file and print it to an output file
 *	PreConditions:
 *		-None
 *	PostConditions:
 *		-None
 ******************/

int main(int argc, char *argv[])
{
  readState("input.txt", bufferEven); 
  int *finalState = runScenario(999);	
  writeState("output.txt", finalState); 
}

/*******************
 *	Name:
 *		-runScenario
 *	Description:
 *		-Runs the 
 *	PreConditions:
 *		-countSteps is of the type Integer
 *	PostConditions:
 *		-The array returned is an array of Int
 ******************/

int *runScenario(int countSteps)
{
    int *arrayLatestState = NULL;

	for (int iTime  = 0; iTime < countSteps; iTime ++)
		{
		arrayLatestState = updateSpace(iTime);
		
		if (iTime % REPORTING_FREQUENCY == 0)
			printf("Step %d\n", iTime);
		}
	return(arrayLatestState);
}

/*******************
 *	Name:
 *		-updateSpace
 *	Description:
 *		-
 *	PreConditions:
 *		- iTime is of type integer
 *	PostConditions:
 *		-nextCells is a pointer to an integer of the cells to be updated in the final iteration
 ******************/

*int *updateSpace(int iTime)
{
	int *currentCells;
	int *nextCells;
	determineCurrentAndNextCells(iTime, &currentCells, &nextCells);

	for (int row = 0; row < ROW_COUNT; row++)
		for (int col = 0; col < COL_COUNT; col++)
		  {
		    // update nextCells
			updateCell(currentCells, nextCells, row, col);	// another (less efficient, but still relatively clean) alternative would be to test here if it's an even or odd step, and to call "update cell" with different swapps first two arguments based on the outcome
		  }

        return(nextCells);  // the cells updated in the final iteration
}

/*******************
 *	Name:
 *		-updateCell
 *	Description:
 *		- Updates the currentCell and updates nextCell to be it, the cell to be updated is given by a positive row and col calue
 *	PreConditions:
 *		-currentCells is a pointer to an Integer and is the cell to be updated
 *		-nextCells is a pointer to an Integer and is updated cell
 *		-row is of type int, >= 0 and is the current row being iterated over, 
 *		-col is of type int, >= 0 and is the current column being iterated over 
 *	PostConditions:
 *		-nextCells is updated based on it's neighbors and the computeLiveness function
 ******************/

void updateCell(int *currentCells, int *nextCells, int row, int col)
{
	int countSurroundingAlive = countSurroundingLiveCells(currentCells, row, col);
	int iCurrentCell = indexForRowCol(row, col);
	bool isCurrentCellAlive = currentCells[iCurrentCell];
	
	nextCells[iCurrentCell] = computeLiveness(isCurrentCellAlive, countSurroundingAlive);
}

/*******************
 *	Name:
 *		-computeLiveness
 *	Description:
 *		-Calculates whether or not the cell is alive or dead based on the number of surrounding cells
 *	PreConditions:
 *		-isCurrentCellAlive is of Type int and is either a 0 or a 1
 *		-countSurroundingAlive is of type Int and is the number of surrounding dead cells 
 *	PostConditions:
 *		-Returns 1 if alive and 0 if dead
 ******************/

int computeLiveness(int isCurrentCellAlive, int countSurroundingAlive)
{
	if (isCurrentCellAlive)
		return (countSurroundingAlive >= 2 && countSurroundingAlive <= 3);
	else
		return (countSurroundingAlive == 3);
}

/*******************
 *	Name:
 *		-countSurroundingLiveCells
 *	Description:
 *		-Counts the number of alive cells surrounding the cell at the row, col position
 *	PreConditions:
 *		-currentCells is an Integer reference to the curernt cell being updated
 *		-row is of type int, >= 0 and is the current row being iterated over, 
 *		-col is of type int, >= 0 and is the current column being iterated over 
 *	PostConditions:
 *		-countSurroundingAlive is of type int and countains the number of cells that are nearby and alive
 ******************/

int countSurroundingLiveCells(int *currentCells, int row, int col)
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
				if (currentCells[indexForRowCol(checkingRow, checkingCol)] == 1)
					countSurroundingAlive++;
				}
			}
	
	return(countSurroundingAlive);
}

/*******************
 *	Name:
 *		-isLegalCoord
 *	Description:
 *		-Checks if the row and colums are legal coordinates
 *	PreConditions:
 *		-row is of type int
 *		-col is of type int
 *	PostConditions:
 *		-retuens a boolean, true if the coords are elgal, false otherwise
 ******************/

bool isLegalCoord(int row, int col)
{
	return ((row >= 0 && row < ROW_COUNT) &&
			(col >= 0 && col < COL_COUNT));
}

/*******************
 *	Name:
 *		-indexForRowCol
 *	Description:
 *		-Calculates the 1D index for the simulated 2D array
 *	PreConditions:
 *		-row is of type int
 *		-col is of type int
 *	PostConditions:
 *		-returns an Int for the 1D array treated liek it was a 2D array
 ******************/

int indexForRowCol(int row, int col)
{
	return row * ROW_COUNT + col;
}

/*******************
 *	Name:
 *		-determineCurrentAndNextCells
 *	Description:
 *		-Finds the next cell to calculate
 *	PreConditions:
 *		-iTime is of type int
 *		-pArrayCurrentCells is the Array of Current cells and is a reference to it and is of type int
 *		-pArrayNextCells is the Array of Next cells and is a reference to it and is of type int
 *	PostConditions:
 *		-
 ******************/

void determineCurrentAndNextCells(int iTime, int **pArrayCurrentCells, int **pArrayNextCells)
{
	bool isEvenStep = (iTime % 2 == 0);

	// given that c doesn't support multiple return values, perhaps this is the least bad way of doing this
	*pArrayCurrentCells  = isEvenStep ? bufferEven : bufferOdd;
	*pArrayNextCells = isEvenStep ? bufferOdd : bufferEven;
}

/*******************
 *	Name:
 *		-readState
 *	Description:
 *		-Reads the given file and outputs it in an array
 *	PreConditions:
 *		-strFileName is a pointer to a character array that contains the desired file to be opened
 *		-arrayCurrentCells is a point to an empty array for all of tje values from the file to be dumped into
 *	PostConditions:
 *		-arrayCurrentCells will contain the contain the values from the file
 ******************/

void readState(char *strFileName, int *arrayCurrentCells)
{
  FILE *file = fopen(strFileName, "r");

  if (file == NULL)
    {
      fprintf(stderr, "Error: could not open file '%s'.  Aborting.\n", strFileName);
      exit(1);
    }

  for (int row = 0; row < ROW_COUNT; row++)
    {
    for (int col = 0; col < COL_COUNT; col++)
      {
        assert(!feof(file));
		char cEncoded = fgetc(file);

		assert(isLegalTextualEncoding(cEncoded));
		arrayCurrentCells[indexForRowCol(row, col)] = cellValueForTextualEncoding(cEncoded);	
      }
      char newLine = fgetc(file);
      assert(newLine == '\n');
    }
  fclose(file);
}

/*******************
 *	Name:
 *		-writeState
 *	Description:
 *		-Writes the given array to a txt file
 *	PreConditions:
 *		-strFileName is a cahracter array that contains the name of the location for the output file to be written
 *		-arrayCurrentCells is an array that has the final (or at least desired) array of cells to be written to file
 *	PostConditions:
 *		-
 ******************/

void  writeState(char *strFileName, int *arrayCurrentCells)
{
  FILE *file = fopen(strFileName, "w");

  for (int row = 0; row < ROW_COUNT; row++)
    {
    for (int col = 0; col < COL_COUNT; col++)
      {
		fputc(textualEncodingForCellValue(arrayCurrentCells[indexForRowCol(row, col)]), file);
      }

    fputc('\n', file);    // row separator
    }
  fclose(file);
}

/*******************
 *	Name:
 *		-textualEncodingForCellValue
 *	Description:
 *		-Converts any cells value to *
 *	PreConditions:
 *		-cellValue is of type int and isa a valid cellValue
 *	PostConditions:
 *		-returns a character that is either a space or a *
 ******************/

char textualEncodingForCellValue(int cellValue)
{
  if (cellValue == 0)
    return ' ';
  else
    return '*';
}

/*******************
 *	Name:
 *		-isLegalTextualEncoding
 *	Description:
 *		-Checks if the character is either space or *
 *	PreConditions:
 *		-cEncoded is of type Char
 *	PostConditions:
 *		-True if the character is a * or a ' ', false otherwise
 ******************/

bool isLegalTextualEncoding(char cEncoded)
{
  return(cEncoded == ' ' || cEncoded == '*');
}

/*******************
 *	Name:
 *		-cellValueForTextualEncoding
 *	Description:
 *		-checks if the character given is a * or not
 *	PreConditions:
 *		-cEncoded is of type Char
 *	PostConditions:
 *		-True if the character is a *, false otherwise
 ******************/

int cellValueForTextualEncoding(char cEncoded)
{
  return cEncoded == '*' ? 1 : 0;
}	

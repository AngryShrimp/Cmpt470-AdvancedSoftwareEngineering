/*
 *	Keenan Johnstone
 *	11119412
 *	kbj182
 *	cmpt470 Exercise 3
 */

PART A
------
Changes made:
-The following functions had an integer "ruleSet" added to allow the main function to pass in a ruleSet variable to control which ruleset to use:
	int *runScenario(int countSteps, int ruleSet);
	int *updateSpace(int iTime, int ruleSet);
	void updateCell(int *currentCells, int *nextCells, int row, int col, int ruleSet);
	int computeLiveness(int isCurrentCellAlive, int countSurroundingAlive, int ruleSet);

Inside computeLiveness a switch case was used to control which scenario should be run. More scenarios can be added easily
The switch defaults to the original game of life if the number given isnt recognized
See the comment header for computeLiveness for more info on the scenarios impletemented


PART B
------
Inside countSurroundingLiveCells, an input variable could be added titled along the lines of "neighborRange" 

neighborRange could be used to dictate the range of how far the countSurroundingLiveCells checks away from the center cell
It would look something like this (Look at the for loops):

int countSurroundingLiveCells(int *currentCells, int row, int col, int neighborRange)
{
	int countSurroundingAlive = 0;
	
	for (int deltaRow = -neighborRange; deltaRow <= neighborRange; deltaRow++)		<------ This would allow for a scalable range!
		for (int deltaCol = -neighborRange; deltaCol <= neighborRange; deltaCol++)	<------ This would allow for a scalable range!
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

This would allow for the function itself to scale the neighbor range check! Though to make it so main() has the abiklity to control it would simply require some passing of values around!
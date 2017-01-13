/*
 *	Keenan Johnstone
 *	11119412
 *	kbj182
 *	cmpt470 Exercise 1
 */

#include <stdio.h>
#include <stdlib.h>

//Global Variables
#define SIDE_LENGTH 100
#define AREA 10000

/*
 *THOUGHT: There is a way to make these non-global, but in C that requires pointers
 		   and my pointer game isnt... on POINT. Im sorry.
 */
int A[AREA]; 
int B[AREA];

/*THOUGHT: there has to be some way to simplify (OddCellCheck and EvenCellCheck) a bit, 
 *		   the code is strikingly similar but I can't seem to figure out a good way to do this.
 */

/*******************************************************************
* NAME :            void EvenCellCheck(a, b, cellCount)
*
* DESCRIPTION :     Modifies the A array based the number of nearby cells
*
* INPUTS :
*       PARAMETERS:
*           int a 			The current horzontal spot in the array
*			int b 			The current vertical spot in the array
*			int cellCount	The count for how many nearby cells there
*							are
*       GLOBALS :
*			int A[]			The arrays where the 'cells' for the game 
*							of life are stored.
*           
* OUTPUTS :
*       GLOBALS :
*           int A[]			Is modified based on how many nearby
*							cells there are
*       RETURN :
*			None
*            
********************************************************************/

void EvenCellCheck(int a, int b, int cellCount)
{
	if (a > 0)
	{
		if (b > 0) 
			cellCount += B[a * SIDE_LENGTH + b - (SIDE_LENGTH + 1)];
		cellCount +=  B[a * SIDE_LENGTH + b - SIDE_LENGTH];
		if (b < (SIDE_LENGTH - 1)) 
			cellCount += B[a * SIDE_LENGTH + b - (SIDE_LENGTH - 1)];
	}
	if (b > 0) 
		cellCount +=  B[a * SIDE_LENGTH + b - 1];
	if (b < (SIDE_LENGTH - 1)) 
		cellCount += B[a * SIDE_LENGTH + b + 1];
	if (a < (SIDE_LENGTH - 1))
	{
		if (b > 0) cellCount +=  B[a * SIDE_LENGTH + b + (SIDE_LENGTH - 1)];
		cellCount += B[a * SIDE_LENGTH + b + SIDE_LENGTH];
		if (b < (SIDE_LENGTH - 1)) cellCount += B[a * SIDE_LENGTH + b + (SIDE_LENGTH + 1)];
	}
	A[a * SIDE_LENGTH + b] = (cellCount == 3) || ((B[a * SIDE_LENGTH + b] == 1) && cellCount >= 2 && cellCount <= 3); 
}

/*******************************************************************
* NAME :            void OddCellCheck(a, b, cellCount)
*
* DESCRIPTION :     modifies the B array based on how many nearby cells 
*					there are
*
* INPUTS :
*       PARAMETERS:
*           int a 			The current horzontal spot in the array
*			int b 			The current vertical spot in the array
*			int cellCount	The count for how many nearby cells there
*							are
*       GLOBALS :
*			int B[]			The arrays where the 'cells' for the game 
*							of life are stored.
*           
* OUTPUTS :
*       GLOBALS :
*           int B[]			Are both modified based on how many nearby
*							cells there are
*       RETURN :
*			None
*            
********************************************************************/

void OddCellCheck(int a, int b, int cellCount)
{
	if (a > 0)
	{
		if (b > 0)
			cellCount += A[a * SIDE_LENGTH + b - (SIDE_LENGTH + 1)];
		cellCount += A[a * SIDE_LENGTH + b - SIDE_LENGTH];
		if (b < (SIDE_LENGTH - 1))
			cellCount += A[a * SIDE_LENGTH + b - (SIDE_LENGTH - 1)];
	}
	if (b > 0)
		cellCount += A[a * SIDE_LENGTH + b - 1];
	if (b < (SIDE_LENGTH - 1))
		cellCount += A[a * SIDE_LENGTH + b + 1];
	if (a < (SIDE_LENGTH - 1))
	{
		if (b > 0) cellCount += A[a * SIDE_LENGTH + b + (SIDE_LENGTH - 1)];
		cellCount += A[a * SIDE_LENGTH + b + SIDE_LENGTH];
		if (b < (SIDE_LENGTH - 1)) cellCount += A[a * SIDE_LENGTH + b + (SIDE_LENGTH + 1)];
	}
	B[a * SIDE_LENGTH + b] = (cellCount == 3) || ((A[a * SIDE_LENGTH + b] == 1) && cellCount >= 2 && cellCount <= 3);
}

/*******************************************************************
* NAME :            void GameOfLife()
*
* DESCRIPTION :     
*
* INPUTS :
*       PARAMETERS:
*           None
*       GLOBALS :
*			int A[], B[]	The arrays where the 'cells' for the game 
*							of life are stored.
*           
* OUTPUTS :
*       GLOBALS :
*           int A[], B[]	Are both modified based on how the OddCellCheck
*							and EvenCellCheck functions.
*       RETURN :
*			None
*            
********************************************************************/

void GameOfLife()
{
	for (int i = 0; i < AREA*10; i++)
	{
		if (i % 10 == 0)
		{
			//Using carriage return instead of new line to avoid flooding the console
			printf("Step %d\r", i);
		}
		//loop through each point on the 2D array
		for (int a = 0; a < SIDE_LENGTH; a++)
		{
			for (int b = 0; b < SIDE_LENGTH; b++)
			{
				//Count the number of neighbors and decide if this cell lives or dies
				int cellCount = 0;
				if (i % 2)
				{
					EvenCellCheck(a, b, cellCount);
				}
				else
				{
					OddCellCheck(a, b, cellCount);
				}
			}
		}
	}
}

/*******************************************************************
* NAME :           int main
*
* DESCRIPTION :     Takes the input from file and reads it into the 
*					GameOfLife function and then prints the result 
*					to output.txt
*            
********************************************************************/

int main()
{
	//attempt to open the file, if it doesnt exist, exit gracefully
	FILE *f = fopen("input.txt", "r");
	if(f == NULL)
	{
		printf("ERROR: File was not be found, exiting.");
		exit(0);
	}

	for (int i = 0; i <= AREA-1; i++)
	{
		if (i > 0 && i % SIDE_LENGTH == 0)
			fgetc(f);
		//Copy contents of input file to the array
		A[i] = (fgetc(f) == '*');
	}
	fclose(f);

	GameOfLife();

	f = fopen("output.txt", "w");  
	for (int i = 0; i <= AREA-1; i++) 
	{
		if (i > 0 && i % SIDE_LENGTH == 0) fputc('\n', f);
		fputc(A[i] == 1 ? '*' : ' ', f);
	}
	fputc('\n', f);
	fclose(f);
	printf("\nDone!\n");
}



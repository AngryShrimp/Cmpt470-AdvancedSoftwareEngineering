/*
 *	Keenan Johnstone
 *	11119412
 *	kbj182
 *	cmpt470 Exercise 12
 */

WHERES THE CODE?!
-----------------
In the src folder called MethodCallTrace.aj, the ConwaysGameOfLife.java was copied from the website solution to assignment 9.


What I did
----------
Created an IntelliJ project with AspectJ

1) Started by testing with a simple hello world test to make sure it was working (Spoiler it was)

2) Next I created a before() statement that caught and added each function call to a keyed HashMap<String, Int> to store and count how many times each function was called

3) I then caught after writeState (because its the last function to run) and print the dictionary results!

These were the results! (In no particular order) at DEFAULT_ITERATION_COUNT=1000

Call from ConwaysGameOfLife line 101 to ConwaysGameOfLife.updateSpace was called: 1000 times
Call from ConwaysGameOfLife line 291 to ConwaysGameOfLife.textualEncodingForCellValue was called: 10000 times
Call from ConwaysGameOfLife line 305 to ConwaysGameOfLife.isLegalTextualEncoding was called: 10000 times
Call from ConwaysGameOfLife.ComputeLivenessClassic line 418 to ConwaysGameOfLife$ComputeLivenessClassic.apply was called: 10000000 times
Call from ConwaysGameOfLife line 77 to ConwaysGameOfLife.runScenario was called: 1 times
Call from ConwaysGameOfLife line 131 to ConwaysGameOfLife.updateCell was called: 10000000 times
Call from ConwaysGameOfLife line 209 to ConwaysGameOfLife.readState was called: 1 times
Call from ConwaysGameOfLife line 330 to ConwaysGameOfLife.determineArguments was called: 1 times
Call from ConwaysGameOfLife line 315 to ConwaysGameOfLife.cellValueForTextualEncoding was called: 10000 times
Call from ConwaysGameOfLife line 27 to ConwaysGameOfLife.main was called: 1 times
Call from ConwaysGameOfLife line 47 to ConwaysGameOfLife.simulate was called: 1 times
Call from ConwaysGameOfLife line 149 to ConwaysGameOfLife.countSurroundingLiveCells was called: 10000000 times
Call from ConwaysGameOfLife line 266 to ConwaysGameOfLife.writeState was called: 1 times
Call from ConwaysGameOfLife line 190 to ConwaysGameOfLife.determineCurrentAndNextCells was called: 1000 times
Call from ConwaysGameOfLife line 176 to ConwaysGameOfLife.isLegalCoord was called: 80000000 times

So the following functions were called only once:
-runScenario
-readState
-writeState
-determineArguments
-main (Duuh)
-simulate

The following 1000 times (called once per iteration)
-determineCurrentAndNextCells
-updateSpace

The following were called 10000 times, once per cell on the 100x100 grid (these were called for the write states)
-textualEncodingForCellValue
-isLegalTextualEncoding
-cellValueForTextualEncoding

The following were called 10000000 times, or 10000 times per iteration, or rather once per cell on the 100x100 grid per iteration
-ConwaysGameOfLife$ComputeLivenessClassic.apply
-updateCell
-countSurroundingLiveCells

The following was called 80000000 or 8 times per cell on the 100x100 grid per iteration (To check if the surronding cells were in or out of the legal area)
-isLegalCoord
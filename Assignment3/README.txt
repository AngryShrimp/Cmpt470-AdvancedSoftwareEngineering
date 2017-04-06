/*
 *	Keenan Johnstone
 *	11119412
 *	kbj182
 *	cmpt470 Exercise 12
 */

WHERES THE CODE?!
=================
For the java and aspectj code I left them in their project directories on the off chance you have intellij (or maybe even eclipse could load it? idk) 
But I also pulled the source code out this time into folders, I ran all of my code in intelliJ, theres no reason it shouldnt work in command line? On the offchance it doesnt , where applicable I added text fiels with the ouptput

PROBLEM 1
---------
i. Is VectorPoint2D a plausible behavioral subtype of Point2D? Explain your reasoning.
Yes, because it only adds new functions without breaking the contract of the old ones

ii. Is MovablePoint2D a plausible behavioral subtype of Point2D? Explain your reasoning.
Yes, because the getters still function as inteneded and now we only have setters

iii. Is Point3DVariantA a plausible behavioral subtype of Point2D? Explain your reasoning.
Yes, Because the new function

iv. Is Point3DVariantB a plausible behavioral subtype of Point2D? Explain your reasoning.
No, because the new distance calculation overrides and breaks the contact of the distance calculation set in point 2D


PROBLEM 2 
---------

PROBLEM 3
---------

PART A : Problem3/src/PrintStep.aj
I wrote a short aspect that before updateCell occurs (as this is the start of each iteration) will print the step if it matches the reporting frequency

PART B : Problem3/src/MetaDataLogger.aj
I wrote some pointcuts that parse the appropriate information and print it to a textfile called ConwaysGoL-Logs.txt 
-Get start time and arguments when simulate is called (This is the begining, trying to start before main didnt work, probably because when I started running from main)
-Get EndTime after writeState Returns (the next line is system.exit, so this felt appropriate)
-Crunch total time with end time
-Send to file after writeState (again cause its the last thing to return before termination)

PART C : Problem3/src/PrintStep.aj
Inside the same step print statement on a "Reporting step" count how many cells are alive
-I can get the return value of updateCells by using the aspect returning(Object o) where object o can be whatever the return value is

PART D : Problem3/src/DisplayCells.aj
- I didnt use the canvas class and create my own graphics, instead I Simplely printed a string to the frame to visualize it, more so to prove I understood how to implement it in an aspectJ
With it being "crunch time" for this and other classes I ran out of time to be able to complete this part with any amount of sanity

PROBLEM 4
---------

PART A : Problem4/src/main/scala/ConwaysGameOfLife.scala
I wrote this one in a very similar fashion to the original java version, nothing really fancy ahppened here

PART B: Problem4/src/main/scala/ConwaysGameOfLifeOOFunctional.scala
So When I run this code it takes FOREVER, as in 10s per step (not per 1000 sterps, PER SINGLE STEP), which tells me I am doing something wrong somewhere

PROBLEM 5
---------
So for this Problem I started by creating three BiFunctions<Boolena, Integer, Boolean> inside the function determineArguements() that each looked like this:

BiFunction<Boolean, Integer, Boolean> classicFunc = (isCurrentCellAlive, countSurroundingAlive) -> {
    if (isCurrentCellAlive)
        return (countSurroundingAlive >= 2 && countSurroundingAlive <= 3);
    else
        return (countSurroundingAlive == 3);
    };

Basicilly I moved the Different rule sets from before to their own BiFunction and returned the appropriate BiFunction based on what command line arg was given like so:

if (args[1] == "Classic")
    return(new ImmutablePair<>(countIterations, classicFunc));

Then it was a matter of changing all of the functions that took a ComputerLivenessRule as a parameter to BiFunction<Boolean, Integer, Boolean> and presto!

FUNFACT: I didn't even have to change line where the functions where being applied, because Dr.Osgood's code was possibly set up with this in mind!
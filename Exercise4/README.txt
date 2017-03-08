/*
 *	Keenan Johnstone
 *	11119412
 *	kbj182
 *	cmpt470 Exercise 4
 */

 PART A
 ------

I started by rewriting the code to run 50k steps, larger run times lets me see small changes in code easier

Next I compiled with no Optimization to test the 'speed' of each code

 File Used       Part A
+---------------+-----------+
|Ugly           | 0m5.833s  |
|               |           |
|Refactored     | 0m35.695s |
|               |           |
|Flexible       | 0m36.021s |
+---------------+-----------+

In all cases the Ugly code is the fastest, this is likely because of the fact that the ugly code does not make use of functions and simply 'goes at it'

The flexible and refactored code are simlar, with the Flexible code taking slightly longer due to the passing of a whole extra paramater arond to change the rules of the game

PART B
------

See the attached gprof files.

PART C
------

Using -O2 and -O3 which are both forms of optimization where O3 is more thorough than O2 leading to more optimaztion where possible

 File Used       Part A      Part C O2   Part C O3
+---------------+-----------+-----------+----------+
|Ugly           | 0m5.833s  | 0m1.919s  | 0m1.959s |
|               |           |           |          |
|Refactored     | 0m35.695s | 0m8.366s  | 0m2.802s |
|               |           |           |          |
|Flexible       | 0m36.021s | 0m7.776s  | 0m3.192s |
+---------------+-----------+-----------+----------+

In O2, Flexible seems to be optimizaed much better than the refactored code. Perhaps this has to do with the way that O2 optimized the passing of variables to functions

In 03, the reverse is true with the Refactored code running slightly better than that of the Flexible code. This could be due to how because of the extra variable being pass the Flexible code can only get optimized up to s certain point.

For the Ugly code the difference between O2 and O3 is negligible as the code seems to have been optimized as much as possible.

While the ugly code wins in every case of compilation, reading it is a near impossiblity. So sacrificing a small amount of time for clean, easy to read code is a worthy tradeoff
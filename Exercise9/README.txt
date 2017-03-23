/*
 *	Keenan Johnstone
 *	11119412
 *	kbj182
 *	cmpt470 Exercise 9
 */

The plan:
---------
Create an interface that each variant that we want can implement differently

Problems I hit:
---------------
So I spent a few hours (~6) creating an interface based off of the C code given but I hit a few snags.

The way I wrote the interface it runs on it's own and properly does the game of life and it works great, but when I try to implement the interface, overriding seems to do nothing.

The issue I beleive lies in the fact that all of the methods are static, however I get the following error if I try to make any of them non-static then try to call them with a static method:

Error: java: non-static method cannot be referenced from a static context

And Im not sure how to get around this. I feel like this is something to do with me not having a strong grasp on Java8 yet. So hopefully I can brush up for the next exercise

What I did:
-----------
I left it so that the interface could run the regualr computeLiveness specs with a class that implements the interface, but doesnt seem to change anything

It works, but I'm wrestling with the syntax to implement the interface properly and I'm not 100% sure how to correctly do it, I know I want each variant for computingLiveness to override and use it's own computeLiveness.

So Im not sure If I approached the problem incorrectly, I know I'm not supposed to use subclasses, as that can break the contract and isn't good.
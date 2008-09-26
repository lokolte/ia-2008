AIMA JAVA Notes By Ravi(magesmail@yahoo.com)


version 0.9
***********
This release marks the pre final .
It  includes  the probability algorithms from chapters 15,16,17 and 21 (besides many bug fixes).

The latest  (and ever evolving) code can be found at http://code.google.com/p/aima-java/. if you notice a bug please try this version to see if it persists.
The official release can be found at http://aima.cs.berkeley.edu/java-overview.html.

The following people sent in excellent comments and bug reports.
 Thank you all

Don Cochrane from (?) University

Mike Angelotti  from Miami University

Chad Carff ,University of Western Florida .  EXCELLENT test cases . thank you .

Dr .Eman El-Sheikh, Ph.D.,University of Western Florida

Ravindra Guravannavar, Aztec Software,Bangalore

Cameron Jenkins,University Of New Orleans

Nils Knoblauch (Project Manager, Camline) - winner of the No Prize for the best bug report ! Thanks!

Phil Snowberger,Artificial Intelligence and Robotics Laboratory,University of Notre Dame



Build Instructions
******************
you can use 
    a)ANT(http://ant.apache.org/) 
    b)make 

The make file is *very*  elementary. All it does is compile the NON-Test Code into class files.I have tested the makefile on cygwin+Windows. i have NOT tested it on linux/unix.

The ANT file is more comprehensive and runs all the tests as part of the standard build.

To run the tests , you will need to put junit(www.junit.org) on the CLASSPATH.
so to build

With ant -- type ant at the command line
With make -- type make

I have included the eclipse.classpath and .projectfiles for those who use eclipse (www.eclipse.org).If you don't use eclipse presently consider getting it. It is free .And its awesome.

Code Navigation
***************
Rule 1 : To understand how a particular feature works , FIRST look at the demo files if they exist. As far as i am aware the only code that does NOT have demos is the "games" code .I will add this in the next release .
There are four main demo files  SearchDemo , LogicDemo ,ProbabilityDemo and LearningDemo.

2.If the Demo Files don't exist yet , look at the unit tests . they often cover much of how a particular feature works . 

3.If all else fails , write to me . Comprehensive documentation, both java doc and otherwise are in the pipeline , but will probably have to wait till I finish the code .


Notes on Search
***************

To solve a problem with (non CSP )Search .
   1.you need to write four classes .
	 a)a class that represents the Problem state .This class is independent of the framework and does NOT need to subclass anything . 
Let us, for the rest of these instruction, assume you are going to solve the NQueens problem . so in this step you need to write something like aima.search.nqueens.NQueensBoard . 
	b)a subclass of aima.search.framework.GoalTest.This implements only a single function ---boolean isGoalState(Object state);
The parameter state is an instance of the class you created in  step 1-a above
For the NQueensProblem you would need to wrte something like aima.search.nqueens.NqueensBoardTest
	c)a subclass of aima.search.framework.SuccessorFunction .This generates a stream of Successors where a Successor is an object that represents an (action, resultantState) pair. In this release of the code the action is  a String (something like "placeQueenAt4,4" and the resultantState is an instance of the class you create in step 1.a . An example is aima.search.nqueens.NQueensSuccessorFunction.
	    d)If you need to do an informed search, you should create a fourth class which subclasses aima.search.framework.HeuristicFunction. This implements a single function  int getHeuristicValue(Object state); keep in mind that the heuristic should DECREASE as the goal state comes nearer . 
For the NQueens problem, you need to write something like aima.search.nqueens.QueensToBePlacedHeuristic.


that is all you need to do (unless you plan to write a different search than is in the code base ).

To actually search you need to
   a) configure a problem instance
   b)select a search .Configure thsiwith Tree Search or GraphSearch if applicaple.
   c)instantiate a SerachAgent and 
   d)print any actions and metrics 

A good example (from the NQueens Demo ) is 
  private static void nQueensWithDepthFirstSearch() {
	  System.out.println("\nNQueensDemo DFS -->");
					    try {
							//Step a
								Problem problem =  new Problem(new NQueensBoard(8),new NQueensSuccessorFunction(), new NQueensGoalTest());
										//Step b
											Search search = new DepthFirstSearch(new GraphSearch());
												      //Step c
														SearchAgent agent = new SearchAgent(problem, search);
//Step d
		printActions(agent.getActions());
				printInstrumentation(agent.getInstrumentation());
					} catch (Exception e) {
						e.printStackTrace();
							}
							}

Search Inheritance Trees.
**************************
there are two nheritance trees in Search. one deals with "mechanism" of search.
This inheritance hirarchy look like 
     NodeExpander (encapsulates the Node expansionmechanism)
		  DepthLimitedSearch
			QueueSearch
					GraphSearch
							TreeSearch

The second tree deals with the search instances  you can use to solve a problem.
These implement the aima.search.framework.Search interface.

So if you see a declaration like 
"SimulatedAnnealingSearch extends NodeExpander implements Search" , do not be confused.
the first superclass(nodeExpander) provides the mechanism of the search and the second makes it suitable for use in solving actual problems like in the BreadthSearch above.

Searches like DepthFirstSearch which need to be used as a search (so implementing the Search interface) and can be configured with Graphseach or TreeSearch (the mechanism) have a  constructor like
	 public DepthFirstSearch(QueueSearch search) .

Again if you get confuded, look at the demos.


Logic Notes
***********
This section of the code is less stable than the search code. Use the demos as guidance.
The ONE thing you need to watch out for is that the Parsers are VERY finicky . if you get  a lexing or parsing error , there is an error in your logic string ,with 99% probability.

To use FirstOrder Logic, first you need to create a subclass of aima.logic.fol.FOLDomain which collects the constants, predicates, functions etc that you use to solve a particular problem.
  A parser (that understands the Grammar in figure 8.3 (page 247 in my copy) ) needs to be instantiated with this domain (eg: 
FOLDomain weaponsDomain = DomainFactory.weaponsDomain();
FOLParser parser = new FOLParser(weaponsDomain);  ).

the basic design of all the logic code is that the parser creates a Composite (Design Patterns by Gamma, et al) parse tree over which various Visitors(Design Patterns by Gamma, et al) traverse . the key difference between the Visitor elucidated in the GOF book and the code is that in the former the visit() methods have a void visit(ConcreteNode) signature while the vistors used in teh logic code have a Object visit(ConcreteNode,Object arg) signature. This makes testing easier and allows some recursive code that is hard withte former .

miscellany 
 - a DLKnowledgeBAse represents an instance of a datalog knowledge base which contains
 only data log rules or facts 
 -Standardise apart is NOT implemented  which means that for now , every variable ina databse needs to be different. On the other hand, a variable can be any string that starts with small letter so it shouldn't eb too hard to work around this limitation.

Probability Notes
*****************
Except elimination-ask, the rest of the algorithms from chapter 13 and 14 have been implemented.I have tried to make the code stick very closely to Dr.Norvig's' pseudocode . Looking at the demo and tests will reveal how to use the code . This code is relatively new and so i am most interested in any bug reports .If you notice something funny do NOT hesitate to write .

LearningNotes
*************
Main Classes and responsibilities
********************************

A <DataSet> is a collection of <Example>s .Wherever you see "examples" in plural in the text , the code uses a DataSet . This makes it easy to aggregate operations that work on collections of examples in one place.

An Example is a collection of Attributes. Each example is a data point for Supervised Learning .

DataSetSpecification and AttributeSpecification do some error checking on the attributes when they are read in from a file or string .At present there are two types of Attributes - A sring attribute, used for datasets like "restaurant" and a NUmeric Attribute which represents attributes which are numbers . These are presently modelled as Doubles.

A Numerizer specifies how a particular DataSet's examples may be converted to Lists of DOubles so they can be used in Neural Networks . There is presently one numerizer in the codebase (IrisDataSetNumerizer)  but it is trivial to write more by implementing the Numerizer interface.

here is how to apply learners .
The DecisionTreeLearner and DecisionList Learner work only on datasets with ordianl attributes (no numbers).Numbers are treated as separate strings .
The Perceptron and DecisionTreeLearners work on *numerized datasets* .If you intend to work with these ,you need to write a DataSetSpecific Numerizer by implementing the Numerizer interface.

1.To import a dataset into a system so that learners can be applied to it , first add a public static DataSet getXDataSet(where "x" is the name of the DataSet you want to import) to the DataSetFactory

2.Learners all implement the Learner interface with 3 method, train, predict and test .If you want to add a new type of Learner (a partitioning Decion Tree learner perhaps? )  you need to implement this interface .

 LearningDemo.java contain examples of how to use all the learners . LearnerTests may be of help too.besides there are specific test files for DEcison Trees, List and Neural networks.


#Probabilistic Decision Making and  reinforcement learning

#TODO
 
Final Thoughts
***************
If you need any help with the java code , do write to me at magesmail@yahoo.com.
i am happy to receive any mails/bug reports and generally respond within a day ,unless I am travelling .The only mails i do NOT respond to are those asking me to do your homework! Don't even try ! :-)These  mails are rejected without even reading them! 


Bug Reports are greatly appreciated! 

when you send in a bug report please include
     a)what you did to see the bug
     b)what you expected to see
    and c) what you actually saw . 
A bug report that says "there is some bug in Search " (i actually got a mail like this ) is next to useless .
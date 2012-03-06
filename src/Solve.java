import java.util.*;

/**
 * this class is the method for solving the given board
 * @author hs
 *
 */
public class Solve {

	private boolean foundSolution; //Whether or not a solution to the board has been found - includes the -1 array as a solution
	private int numberOfSolutions; //Number of solutions to this board
	private Board[] allSolutions; //Board array with all solutions.
	private int solutionIndex; //Which index we are storing the solution to in the array
	private int numberOfValidStates; //Number of valid states created to find all of the solutions
	private boolean firstMethod; //Whether or not the method is the first method of the recursive stack


	public Solve() {
		foundSolution = false;
		solutionIndex = 0;
		numberOfValidStates = 1;
		firstMethod = true;
	}


	/**
	 * Returns the number of all the possible solutions to the input board in
	 * @param in The board we are seeing how many solutions there are
	 * @return Number of solutions to the board in
	 * @throws NullBoardException When board is invalid or not.
	 */
	public int countSol(Board in) throws NullBoardException {
		if (in.ifGoal()) {
			return 1;
		}

		int solutions= 0;
		Board[] childArray= in.getChildren();
		for(int i= 0;i<childArray.length;i++) {
			//Add the number of solutions in each child to the total number of solutions
			solutions= solutions+countSol(childArray[i]);
		}

		return solutions;
	}

	/**
	 * Adds all instance of boards which are solutions to the solution array.
	 * @param in Board we are seeing if we can add in
	 * @throws NullBoardException Thrown when board is invalid or has no children.
	 */
	public void addSolBoards(Board in) throws NullBoardException {
		if (in.ifGoal()) {
			allSolutions[solutionIndex] = in;
			solutionIndex++;
			return;
		}

		Board[] childArray = in.getChildren();
		for (int i = 0; i < childArray.length; i++) {
			numberOfValidStates = numberOfValidStates + childArray.length;
			addSolBoards(childArray[i]);
		}
	}


	/**
	 * Solves the puzzle and simply prints solution
	 * @param in Board we are finding the solution to
	 * @throws NullBoardException Thrown when board is invalid or unsolvable
	 */
	public void printSolve (Board in) throws NullBoardException {
		boolean isFirst = firstMethod; //Whether or not this is the first method of the recursive stack
		try {
			if (firstMethod) { firstMethod = false; }
			if (foundSolution) { return; }
			if (in.ifGoal()) {
				in.printBoard();
				foundSolution = true;
				return;
			}
			//[This board state is not in the solved state]

			Board[] childArray = in.getChildren(); //Generate all valid children.

			for (int i = 0; i < childArray.length; i++) {
				printSolve(childArray[i]);
			}
		}
		//If it caught an invalid or unsolvable board print the -1 board array and return the correct message
		catch (NullBoardException nbe) {
			if (!foundSolution && isFirst) {
				String error = "";
				if (!in.validBoardState()) { error = "This is an invalid board."; }
				else { error = "This is unsolvable."; }
				Board a = new Board();
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						a.fill(i, j, -1);
					}
				}
				a.printBoard();
				foundSolution = true;
				System.out.println("This board cannot be solved because: " + error);
			}
		}
		//[The method went through all cases and may or may not have found a solution]
		finally {
			if (foundSolution) { return; }
			else if (isFirst) {
				Board a = new Board();
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						a.fill(i, j, -1);
					}
				}
				a.printBoard();
				throw new NullBoardException("This is unsolvable.");
			}
		}
	}


	/**
	 * Solve with statistics
	 * @param in Board we are solving the method for
	 * @return all valid solutions
	 * @throws NullBoardException If the board is invalid or unsolvable.
	 */
	public Board [] solveWithStats(Board in) throws NullBoardException {
		try { numberOfSolutions = countSol(in); } //Stores the number of solutions in the static variable.

		//If we get an exception because of calling the method above, we know that the board is invalid.
		catch (NullBoardException nbe) {
			if (numberOfSolutions == 0) {
				Board a = new Board();
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						a.fill(i, j, -1);
					}
				}
				Board[] b = new Board[1];
				b[0] = a;
				System.out.println("This board is unsolvable because it is invalid.");
				return b;
			}
		}

		//There still may be no solutions if the board is unsolvable
		if (numberOfSolutions == 0) {
			String error = "";
			error = "This is unsolvable.";
			Board a = new Board();
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					a.fill(i, j, -1);
				}
			}
			a.printBoard();
			throw new NullBoardException(error);
		}

		//[There is at least one solution]			
		allSolutions = new Board[numberOfSolutions]; //Creates a board of the size of the number of solutions.
		addSolBoards(in);
		System.out.println("The number of solutions this puzzle has is " + numberOfSolutions + ".");
		System.out.println("The number of valid states (children) generated to find all solutions was " + numberOfValidStates + ".");
		return allSolutions;
	}

	/**
	 * Solves the board in a smart fashion
	 * @param in Board we are looking to solve smartly
	 * @return A solved board otherwise throw an exception
	 * @throws NullBoardException
	 */
	public Board solveSmart(Board in) throws NullBoardException {
		boolean isFirst = firstMethod; //Determines if this method is the first in the recursive stack
		if (firstMethod) { firstMethod = false;	}
		if (in.ifGoal()) { //If the board is a solution, return the board
			foundSolution = true;
			return in;
		}
		Board[] childArray = in.getIntelligentChildren();
		for (int i = 0; i < childArray.length; i++) {
			Board solutionBoard = solveSmart(childArray[i]);
			if (solutionBoard != null) {
				return solutionBoard;
			}
		}

		//If this is the first method in the recursion and no solution was found return the -1 array
		if (isFirst && !foundSolution) {
			String error = "";
			if (!in.validBoardState()) { error = "This is an invalid board."; }
			else { error = "This is unsolvable."; }
			Board a = new Board();
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					a.fill(i, j, -1);
				}
			}
			a.printBoard();
			throw new NullBoardException(error);
		}

		return null;
	}


}
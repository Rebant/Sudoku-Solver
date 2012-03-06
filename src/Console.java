import java.io.*;
import java.util.*;

public class Console {

	//initialize the i/o

	public static void main(String [] args){

		String filename;
		Board board = null;
		boolean keepGoing = true;

		InputStreamReader iReader = new InputStreamReader(System.in);
		BufferedReader bReader = new BufferedReader(iReader);

		System.out.println("Welcome to the Sudoku Master!");
		System.out.println("Please input a filename:");

		boolean no_sucess = true;

		while(no_sucess){
			try{
				filename = bReader.readLine();
				no_sucess = false;
				Parser parse = new Parser(filename);
				board = parse.createBoard();
				board.printBoard();
			}
			catch(IOException e){
				System.out.println("There is a problem with your filename.");
				System.out.println("Please check your file path, then re-enter the filename.");
			}
		}

		while(keepGoing){
			System.out.println("Now what would you like to do?");
			System.out.println("(Please select a number)");
			System.out.println("1. Check valid state");
			System.out.println("2. Generate next moves");
			System.out.println("3. Solve this puzzle!");
			System.out.println("4. Solve & Generate Statistics.");
			System.out.println("5. Solve Intelligently!");
			System.out.println("6. Input new puzzle file");
			System.out.println("7. Quit");

			try{
				String choice = bReader.readLine();
				int ichoice = Integer.parseInt(choice);

				switch(ichoice){
				case 1:
					if(board.validBoardState()){
						System.out.println("This is a valid board state.");
					}
					else{
						System.out.println("This is an invalid board state");
					}
					break;
				case 2:
					try {
						Board [] childs = board.getChildren();

						System.out.println("These are possible children:");
						for (Board chil: childs){
							chil.printBoard();
						}
					}
					catch (NullBoardException nbe) {
						System.out.println("Cannot get children of the board because: " + nbe.getMessage());
					}
					break;
				case 3: 
					try{
						Solve sol = new Solve();
						sol.printSolve(board);
					}
					catch(NullBoardException ne){
						System.out.println("Cannot solve board because of following problem: " + ne.getMessage());
					}
					break;
				case 4:
					try{
						Solve sol = new Solve();
						Board [] solvedb = sol.solveWithStats(board);

						for(Board b: solvedb){
							b.printBoard();
						}
					}
					catch(NullBoardException ne){
						System.out.println("Cannot solve board because of following problem: " + ne.getMessage());
					}
					break;
				case 5:
					try{
						Solve sol = new Solve();
						Board solvedb = sol.solveSmart(board);
						System.out.println("The returned board looks like: ");

						solvedb.printBoard();

					}
					catch(NullBoardException ne){
						System.out.println("Cannot solve board because of following problem: " + ne.getMessage());
					}
					break;
				case 6: 
					System.out.println("Please enter the new filename:");
					no_sucess = true;

					while(no_sucess){
						try{
							filename = bReader.readLine();
							no_sucess = false;
							Parser parse = new Parser(filename);
							board = parse.createBoard();
							board.printBoard();
						}
						catch(IOException e){
							System.out.println("There is a problem with your filename.");
							System.out.println("Please check your file path, then re-enter the filename.");
						}
					}
					break;
				case 7:
					keepGoing = false;
					break;
				default:
					break;
				}

			}
			catch(Exception e){
				System.out.println("Please re-enter your choice");
			}
		}

	}

}

import java.io.*;

import java.util.*;
public class Parser {

	private List<String> fileInput;

	//only reads in the file info, need to call createBoard to get the GameBoard.
	public Parser(String filename){

		fileInput = new ArrayList<String>();

		try{ //only completes the task if file is found
			Scanner scanner = new Scanner(new File(filename));

			while(scanner.hasNextLine()){ 
				String newLine = scanner.nextLine();
				fileInput.add(newLine);
			}

			scanner.close();

		}
		catch(FileNotFoundException e){//Does nothing if this happens

			System.out.println("Could not find file. Please check given path");
		}
	}


	public Board createBoard(){

		Board board = new Board();

		for(int row=0; row<fileInput.size(); row++){
			String[] splits = fileInput.get(row).split(" ");
			for (int col=0; col< splits.length; col++){
				board.fill(row,col, Integer.parseInt(splits[col]));
			}
		}

		return board;

	}

}

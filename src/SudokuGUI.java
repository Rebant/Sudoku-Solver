import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SudokuGUI extends JFrame implements ActionListener {
 
 File filename;
 Board board = null;
 Container overall = this.getContentPane(); //Overall container
 JPanel puzzleGrid; //Puzzle part
 JPanel buttons; //Selection things
 Board solutions; //All solutions to the board
 
 public static void main(String[] args) {
   SudokuGUI a = new SudokuGUI();
 }
 
 public void run() {
   new SudokuGUI();
 }
 
 public SudokuGUI() {

  JDialog dialog = new JDialog();
  JLabel label = new JLabel("Please wait...");
  dialog.setLocationRelativeTo(null);
  dialog.add(label);
  dialog.setSize(100, 100);
  
  boolean success = false;
  //Choose the board file
  while (!success) {
   filename = chooseTheFile();
   if (filename == null) { JOptionPane.showMessageDialog(this, "No file selected. Starting application with no board."); break; }
   Parser parse = new Parser(filename.getPath());
   try { board = parse.createBoard(); success = true;}
   catch (Exception e) { JOptionPane.showMessageDialog(this, "This file is not a sudoku file. Please pick again."); }
  }
  
  dialog.setVisible(true);
  
  //Store local copy of solutions
  Solve sol = new Solve();
  try { solutions = sol.solveSmart(board); }
  catch (NullBoardException nbe) { notSolvable(); System.out.println("NBE"); solutions = null; }
  catch (NullPointerException npe) { System.out.println("NPE"); }
  
  overall.setLayout(new BorderLayout());
  
  puzzleGrid = new JPanel();
  if (board != null) { setupPuzzleGrid(board); }
  else { setupPuzzleGrid(); }
  
  buttons = new JPanel();
  setupButtons();
  
  overall.setLayout(new BorderLayout());
  overall.add(puzzleGrid, BorderLayout.WEST);
  overall.add(buttons, BorderLayout.CENTER);
  setSize(700, 500);
  
  dialog.setVisible(false);
  
  setVisible(true);
  setResizable(false);
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 }

 /**
  * @return File of the Sudoku file
  */
 public File chooseTheFile() {
  JFileChooser fc = new JFileChooser();
  int returnVal = fc.showOpenDialog(this);
  if (returnVal == JFileChooser.APPROVE_OPTION) { return fc.getSelectedFile(); } //Why have both of these...?
  else { return fc.getSelectedFile(); }
 }
 
 /**
  * Setup the puzzle grid without numbers.
  */
 public void setupPuzzleGrid() {
  puzzleGrid = new JPanel();
  puzzleGrid.setLayout(new GridLayout(9, 9));
  for (int i = 0; i < 81; i++) {
   JPanel a = new JPanel();
   puzzleGrid.add(a);
  }
  for (int i = 0; i < 81; i++) {
   JPanel a = (JPanel) puzzleGrid.getComponent(i);
   sudokuNumberButton w = new sudokuNumberButton("0");
   a.add(w);
   w.addActionListener(this);
  }
 }
 
 /**
  * Setup the puzzle grid with numbers from board.
  * @param board
  */
 public void setupPuzzleGrid(Board board) {
  puzzleGrid = new JPanel();
  puzzleGrid.setLayout(new GridLayout(9, 9));
  for (int i = 0; i < 81; i++) {
   JPanel a = new JPanel();
   puzzleGrid.add(a);
  }
  int j = 0;
  for (int i = 0; i < 81; i++) {
   JPanel a = (JPanel) puzzleGrid.getComponent(i);
   a.setLayout(new GridLayout(1, 1));
   int number = board.holes[i/9][j];
   sudokuNumberButton w;
   if (number - 1 <= -1) { w = new sudokuNumberButton("0"); }
   else { w = new sudokuNumberButton("" + number, true); }
   a.add(w);
   w.addActionListener(this);
   j = (j == 8 ? 0 : j + 1);
  }
 }
 
 public void setupButtons() {
  buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
  solveButtons newButton = new solveButtons("New Sudoku Puzzle from Board");
  newButton.setAlignmentX(Component.CENTER_ALIGNMENT);
  newButton.addActionListener(this);
  solveButtons solveButton = new solveButtons("Solve");
  solveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
  solveButton.addActionListener(this);
  solveButtons checkButton = new solveButtons("Check if I am doing fine");
  checkButton.setAlignmentX(Component.CENTER_ALIGNMENT);
  checkButton.addActionListener(this);
  solveButtons makeNewBoardButton = new solveButtons("Make this the board");
  makeNewBoardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
  makeNewBoardButton.addActionListener(this);
  buttons.add(newButton);
  buttons.add(solveButton);
  buttons.add(checkButton);
  buttons.add(makeNewBoardButton);
 }
 
 public void actionPerformed(ActionEvent e) {
  //If it is a Sudoku Number...
  if (e.getSource() instanceof sudokuNumberButton) {
   sudokuNumberButton a = (sudokuNumberButton) e.getSource();
   if (a.original == true) { return; }
   a.nextNumber();
   return;
  }
  if (e.getSource() instanceof solveButtons) {
   solveButtons a = (solveButtons) e.getSource();
   if (a.getText().equals("Solve")) { try {solveThePuzzle(); } catch(Exception asdf) { } }
   if (a.getText().equals("New Sudoku Puzzle from Board")) { newSudokuFile(); }
   if (a.getText().equals("Check if I am doing fine")) { checkIfGood(); }
   if (a.getText().equals("Make this the board")) { makeThisTheBoard(); }
  }
 }
 
 /**
  * When user clicks to make the current board the board, this is what happens.
  * @param board
  */
 public void newSudokuFile(int[][] numbers) {
  board = null;
  board = new Board();
  board.holes = numbers;
  
  JDialog dialog = new JDialog();
  JLabel label = new JLabel("Please wait...");
  dialog.setLocationRelativeTo(null);
  dialog.add(label);
  dialog.setTitle("Please wait...");
  dialog.setSize(150, 0);
  dialog.setResizable(false);
  
  setVisible(false); //Make invisible as we solve the puzzle.
  
  dialog.setVisible(true);
  
  //Store local copy of solutions
  solutions = null;
  Solve sol = new Solve();
  try { solutions = sol.solveSmart(board); }
  catch (NullBoardException nbe) { notSolvable(); solutions = null; }
  catch (NullPointerException npe) { }
  
  dialog.setVisible(false);
  
  overall.remove(puzzleGrid);
  puzzleGrid = new JPanel();
  if (board != null) { setupPuzzleGrid(board); }
  else { setupPuzzleGrid(); }
  overall.add(puzzleGrid, BorderLayout.WEST);
  setSize(700, 500);
  
  setVisible(true);
 }
 
 /**
  * When user clicks for a new Sudoku file, this is what happens.
  */
 public void newSudokuFile() {
  board = null;
  boolean success = false;
  while (!success) {
   filename = chooseTheFile();
   if (filename == null) { JOptionPane.showMessageDialog(this, "No file selected."); return; }
   Parser parse = new Parser(filename.getPath());
   try { board = parse.createBoard(); success = true;}
   catch (Exception e) { JOptionPane.showMessageDialog(this, "This file is not a sudoku file. Please pick again."); }
  }
  
  JDialog dialog = new JDialog();
  JLabel label = new JLabel("Please wait...");
  dialog.setLocationRelativeTo(null);
  dialog.add(label);
  dialog.setTitle("Please wait...");
  dialog.setSize(150, 0);
  dialog.setResizable(false);
  
  setVisible(false); //Make invisible as we solve the puzzle.
  
  dialog.setVisible(true);
  
  //Store local copy of solutions
  solutions = null;
  Solve sol = new Solve();
  try { solutions = sol.solveSmart(board); }
  catch (NullBoardException nbe) { notSolvable(); solutions = null; }
  catch (NullPointerException npe) { }
  
  dialog.setVisible(false);
  
  overall.remove(puzzleGrid);
  puzzleGrid = new JPanel();
  if (board != null) { setupPuzzleGrid(board); }
  else { setupPuzzleGrid(); }
  overall.add(puzzleGrid, BorderLayout.WEST);
  setSize(700, 500);
  
  setVisible(true);
 }
 
 /**
  * Solves the puzzle when the user clicks on the button.
  * @throws InterruptedException 
  */
 public void solveThePuzzle() throws InterruptedException {
  if (solutions == null) { notSolvable(); return; }
  int j = 0;
  boolean notFinished = true;
  while (notFinished) {
   notFinished = false;
   for (int i = 0; i < 81; i++) {
    JPanel a = (JPanel) puzzleGrid.getComponent(i);
    sudokuNumberButton currentButton = (sudokuNumberButton) a.getComponent(0);
    if (currentButton.original == true || currentButton.getText().equals("" + solutions.holes[i/9][j])) {
     j = (j == 8 ? 0 : j + 1);
     continue;
    }
    else {
     j = (j == 8 ? 0 : j + 1);
     currentButton.nextNumber();
     notFinished = true;
    }
   }
  }
 }
 
 /**
  * Checks to see if the board is heading in the right direction or not.
  */
 public void checkIfGood() {
  if (solutions == null) { notSolvable(); return; }
  int[][] usersBoard = new int[9][9];
  int j = 0;
  for (int i = 0; i < 81; i++) {
   JPanel a = (JPanel) puzzleGrid.getComponent(i);
   sudokuNumberButton currentButton = (sudokuNumberButton) a.getComponent(0);
   int number = Integer.parseInt(currentButton.getText());
   usersBoard[i/9][j] = (number == 0 ? -1 : Integer.parseInt(currentButton.getText()));
   j = (j == 8 ? 0 : j + 1);
  }
  for (int i = 0; i < 9; i++) {
   for (int h = 0; h < 9; h++) {
    if (usersBoard[i][h] != solutions.holes[i][h]) {
     getSpecificButton(i*9 + h).setForeground(Color.RED);
    }
    if (!getSpecificButton(i*9 + h).original && usersBoard[i][h] == solutions.holes[i][h]) {
     getSpecificButton(i*9 + h).setForeground(Color.GREEN);
    }
   }
  }
 }
 
 /**
  * @return Specific sudokuNumberButton representing the int specified.
  */
 public sudokuNumberButton getSpecificButton(int number) {
  JPanel a = (JPanel) puzzleGrid.getComponent(number);
  sudokuNumberButton currentButton = (sudokuNumberButton) a.getComponent(0);
  return currentButton;
 }
 
 public void notSolvable() {
  JOptionPane.showMessageDialog(this, "This board is unsolvable.");
 }
 
 public void makeThisTheBoard() {
  int[][] usersBoard = new int[9][9];
  int j = 0;
  for (int i = 0; i < 81; i++) {
   JPanel a = (JPanel) puzzleGrid.getComponent(i);
   sudokuNumberButton currentButton = (sudokuNumberButton) a.getComponent(0);
   int number = Integer.parseInt(currentButton.getText());
   usersBoard[i/9][j] = (number == 0 ? -1 : Integer.parseInt(currentButton.getText()));
   j = (j == 8 ? 0 : j + 1);
  }
  newSudokuFile(usersBoard);
 }

}

class sudokuNumberButton extends JButton {
 boolean original = false;
 
 /**
  * I don't know.
  */
 private static final long serialVersionUID = 1L;

 public sudokuNumberButton(String number) {
  super(number);
 }
 
 public sudokuNumberButton(String number, boolean original) {
  super(number);
  this.original = original;
  setForeground(Color.ORANGE);
 }
 
 public void nextNumber() {
  int currentNumber = 0;
  try { currentNumber = Integer.parseInt(this.getText()); }
  catch (NumberFormatException nfe) { setText("1"); }
  setText(currentNumber + 1 == 10 ? "1": "" + (currentNumber + 1));
  setForeground(Color.black);
 }
}

class solveButtons extends JButton {
 
 /**
  * I don't know.
  */
 private static final long serialVersionUID = 1L;

 public solveButtons(String name) {
  super(name);
 }
}
